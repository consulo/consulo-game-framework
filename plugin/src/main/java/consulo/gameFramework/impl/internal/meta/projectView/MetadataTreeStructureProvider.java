package consulo.gameFramework.impl.internal.meta.projectView;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.application.progress.ProgressManager;
import consulo.gameFramework.meta.MetadataFileType;
import consulo.gameFramework.meta.MetadataService;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.ui.view.tree.ProjectViewNode;
import consulo.project.ui.view.tree.TreeStructureProvider;
import consulo.project.ui.view.tree.ViewSettings;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.tree.TreeHelper;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
@ExtensionImpl
public class MetadataTreeStructureProvider implements TreeStructureProvider, DumbAware {
    private final MetadataService myMetadataService;

    @Inject
    public MetadataTreeStructureProvider(MetadataService metadataService) {
        myMetadataService = metadataService;
    }

    @Override
    @RequiredUIAccess
    public Collection<AbstractTreeNode> modify(AbstractTreeNode parent, Collection<AbstractTreeNode> children, ViewSettings settings) {
        return TreeHelper.calculateYieldingToWriteAction(() -> doModify(children, settings));
    }

    @Nonnull
    @RequiredReadAction
    private Collection<AbstractTreeNode> doModify(Collection<AbstractTreeNode> children, ViewSettings settings) {
        if (!myMetadataService.isAvailable()) {
            return children;
        }

        Boolean showMetaFiles = settings.getViewOption(MetadataProjectViewPaneOptionProvider.KEY);
        if (showMetaFiles == Boolean.TRUE) {
            return children;
        }

        List<AbstractTreeNode> nodes = new ArrayList<>(children.size());
        for (AbstractTreeNode child : children) {
            ProgressManager.checkCanceled();

            if (child instanceof ProjectViewNode projectViewNode) {
                VirtualFile virtualFile = projectViewNode.getVirtualFile();
                if (virtualFile != null && virtualFile.getFileType() instanceof MetadataFileType && haveOwnerFile(virtualFile)) {
                    continue;
                }
            }

            nodes.add(child);
        }
        return nodes;
    }

    public static boolean haveOwnerFile(VirtualFile virtualFile) {
        String nameWithoutExtension = virtualFile.getNameWithoutExtension();
        VirtualFile parent = virtualFile.getParent();
        return parent.findChild(nameWithoutExtension) != null;
    }
}