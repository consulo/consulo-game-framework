package consulo.gameFramework.impl.internal.meta;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ServiceImpl;
import consulo.application.util.LowMemoryWatcher;
import consulo.disposer.Disposable;
import consulo.gameFramework.impl.internal.meta.index.MetadataIndexExtension;
import consulo.gameFramework.meta.MetadataProvider;
import consulo.gameFramework.meta.MetadataService;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.PsiModificationTrackerListener;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.FileBasedIndex;
import consulo.project.Project;
import consulo.util.lang.ObjectUtil;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
@Singleton
@ServiceImpl
public class MetadataServiceImpl implements MetadataService, Disposable {
    private final Project myProject;

    private Map<Integer, Object> myGUIDs = new ConcurrentHashMap<>();

    @Inject
    public MetadataServiceImpl(Project project) {
        myProject = project;
        myProject.getMessageBus().connect().subscribe(PsiModificationTrackerListener.class, () -> myGUIDs.clear());

        LowMemoryWatcher.register(() -> myGUIDs.clear(), this);
    }

    @Override
    @RequiredReadAction
    public MetadataProvider findProvider() {
        return myProject.
            getApplication()
            .getExtensionPoint(MetadataProvider.class)
            .findFirstSafe(metadataProvider -> metadataProvider.isAvailable(myProject));
    }

    @RequiredReadAction
    @Nullable
    @Override
    public String getFileIdFromMetadata(@Nonnull VirtualFile virtualFile) {
        String name = virtualFile.getName();

        VirtualFile parent = virtualFile.getParent();
        if (parent == null) {
            return null;
        }

        int targetId = FileBasedIndex.getFileId(virtualFile);

        Object idFromMap = myGUIDs.get(targetId);
        if (idFromMap != null) {
            return idFromMap instanceof String s ? s: null;
        }

        String fileId = findFileId(parent, name);
        if (fileId == null) {
            myGUIDs.putIfAbsent(targetId, ObjectUtil.NULL);
            return null;
        }

        myGUIDs.putIfAbsent(targetId, fileId);

        return fileId;
    }

    @RequiredReadAction
    private String findFileId(VirtualFile parent, String name) {
        MetadataProvider provider = findProvider();
        if (provider == null) {
            return null;
        }

        VirtualFile child = parent.findChild(name + "." + provider.getExtension());
        if (child != null) {
            PsiFile file = PsiManager.getInstance(myProject).findFile(child);
            if (file != null) {
                return provider.extractId(file);
            }
        }

        return null;
    }

    @RequiredReadAction
    @Nullable
    @Override
    public VirtualFile findFileByMetadataId(@Nonnull String id) {
        List<Integer> values = FileBasedIndex.getInstance().getValues(MetadataIndexExtension.KEY, id, GlobalSearchScope.allScope(myProject));
        if (values.isEmpty()) {
            return null;
        }

        return FileBasedIndex.getInstance().findFileById(myProject, values.get(0));
    }

    @Override
    public void dispose() {
        myGUIDs.clear();
    }
}
