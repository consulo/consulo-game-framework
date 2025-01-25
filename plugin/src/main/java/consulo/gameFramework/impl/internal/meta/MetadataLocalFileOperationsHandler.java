package consulo.gameFramework.impl.internal.meta;

import consulo.gameFramework.meta.MetadataFileType;
import consulo.gameFramework.meta.MetadataProvider;
import consulo.gameFramework.meta.MetadataService;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.project.ProjectLocator;
import consulo.util.lang.function.ThrowableConsumer;
import consulo.virtualFileSystem.LocalFileOperationsHandler;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
public class MetadataLocalFileOperationsHandler implements LocalFileOperationsHandler {
    private interface FsExecutor {
        void execute(MetadataProvider metadataProvider, VirtualFile metaFile) throws IOException;
    }

    private static final Logger LOG = Logger.getInstance(MetadataFileSystemWatcher.class);

    private final ProjectLocator myProjectLocator;

    public MetadataLocalFileOperationsHandler(ProjectLocator projectLocator) {
        myProjectLocator = projectLocator;
    }

    @Override
    public boolean delete(VirtualFile file) throws IOException {
        return doActionOnMetaFile(file, (m, virtualFile) -> virtualFile.delete(null));
    }

    @Override
    public boolean move(VirtualFile file, final VirtualFile toDir) throws IOException {
        return doActionOnMetaFile(file, (m, virtualFile) -> virtualFile.move(null, toDir));
    }

    @Nullable
    @Override
    public File copy(VirtualFile file, VirtualFile toDir, String copyName) throws IOException {
        return null;
    }

    @Override
    public boolean rename(VirtualFile file, final String newName) throws IOException {
        return doActionOnMetaFile(file, (p, virtualFile) -> virtualFile.rename(null, newName + "." + p.getExtension()));
    }

    @Override
    public boolean createFile(VirtualFile dir, String name) throws IOException {
        return false;
    }

    @Override
    public boolean createDirectory(VirtualFile dir, String name) throws IOException {
        return false;
    }

    @Override
    public void afterDone(ThrowableConsumer<LocalFileOperationsHandler, IOException> invoker) {
    }

    private boolean doActionOnMetaFile(VirtualFile parentFile, FsExecutor consumer) {
        if (parentFile.getFileType() instanceof MetadataFileType) {
            return false;
        }

        Project project = myProjectLocator.guessProjectForFile(parentFile);
        if (project == null) {
            return false;
        }

        MetadataProvider provider = project.getInstance(MetadataService.class).findProvider();
        return provider != null && doActionOnSuffixFile(parentFile, consumer, provider);
    }

    private boolean doActionOnSuffixFile(VirtualFile parentFile, FsExecutor consumer, MetadataProvider metadataProvider) {
        VirtualFile parent = parentFile.getParent();
        if (parent == null) {
            return false;
        }
        VirtualFile metaFile = parent.findChild(parentFile.getName() + "." + metadataProvider.getExtension());
        if (metaFile != null) {
            try {
                consumer.execute(metadataProvider, metaFile);
            }
            catch (IOException e) {
                LOG.error(e);
            }
        }
        return false;
    }
}
