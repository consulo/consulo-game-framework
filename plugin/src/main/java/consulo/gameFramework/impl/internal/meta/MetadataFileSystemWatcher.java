package consulo.gameFramework.impl.internal.meta;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.disposer.Disposable;
import consulo.project.ProjectLocator;
import consulo.virtualFileSystem.LocalFileOperationsHandler;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFileManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
@Singleton
@ServiceAPI(value = ComponentScope.APPLICATION, lazy = false)
@ServiceImpl
public class MetadataFileSystemWatcher implements Disposable {
    private final LocalFileSystem myLocalFileSystem;

    private final LocalFileOperationsHandler myFileOperationsHandler;

    @Inject
    public MetadataFileSystemWatcher(ProjectLocator projectLocator, VirtualFileManager virtualFileManager) {
        myLocalFileSystem = LocalFileSystem.get(virtualFileManager);
        myFileOperationsHandler = new MetadataLocalFileOperationsHandler(projectLocator);
        myLocalFileSystem.registerAuxiliaryFileOperationsHandler(myFileOperationsHandler);
    }

    @Override
    public void dispose() {
        myLocalFileSystem.unregisterAuxiliaryFileOperationsHandler(myFileOperationsHandler);
    }
}
