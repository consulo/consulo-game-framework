package consulo.gameFramework.impl.internal.meta.projectView;

import consulo.annotation.component.ExtensionImpl;
import consulo.gameFramework.meta.MetadataFileType;
import consulo.gameFramework.meta.MetadataService;
import consulo.project.Project;
import consulo.project.ui.view.tree.ProjectViewNode;
import consulo.project.ui.view.tree.ProjectViewNodeDecorator;
import consulo.ui.ex.SimpleTextAttributes;
import consulo.ui.ex.tree.PresentationData;
import consulo.virtualFileSystem.VirtualFile;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
@ExtensionImpl
public class MetadataProjectViewNodeDecorator implements ProjectViewNodeDecorator {
    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        Project project = node.getProject();
        if (project == null) {
            return;
        }

        VirtualFile virtualFile = node.getVirtualFile();
        if (virtualFile == null || !(virtualFile.getFileType() instanceof MetadataFileType)) {
            return;
        }

        MetadataService metadataService = project.getInstance(MetadataService.class);

        if (!metadataService.isAvailable()) {
            return;
        }

        if (MetadataTreeStructureProvider.haveOwnerFile(virtualFile)) {
            return;
        }

        data.clearText();
        data.addText(virtualFile.getName(), SimpleTextAttributes.GRAYED_BOLD_ATTRIBUTES);
        String nameWithoutExtension = virtualFile.getNameWithoutExtension();
        data.setTooltip("File(directory) '" + nameWithoutExtension + "' is not exists, meta file can be deleted.");
    }
}
