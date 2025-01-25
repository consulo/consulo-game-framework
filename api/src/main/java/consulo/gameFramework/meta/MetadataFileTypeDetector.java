package consulo.gameFramework.meta;

import consulo.application.Application;
import consulo.util.io.ByteSequence;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.fileType.FileTypeDetector;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
public abstract class MetadataFileTypeDetector implements FileTypeDetector {
    private final MetadataProvider myMetadataProvider;

    private final byte[] myPrefixBytes;

    private final String myDotExtension;

    public MetadataFileTypeDetector(@Nonnull Application application,
                                    @Nonnull Class<? extends MetadataProvider> metaProvider,
                                    @Nonnull String prefix) {
        myMetadataProvider = application.getExtensionPoint(MetadataProvider.class).findExtensionOrFail(metaProvider);
        myPrefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
        myDotExtension = "." + myMetadataProvider.getExtension();
    }

    @Nullable
    @Override
    public FileType detect(@Nonnull VirtualFile file, @Nonnull ByteSequence firstBytes, @Nullable CharSequence firstCharsIfText) {
        if (StringUtil.endsWith(file.getNameSequence(), myDotExtension) && checkContent(firstBytes, firstCharsIfText)) {
            return myMetadataProvider.getFileType();
        }
        return null;
    }

    protected boolean checkContent(@Nonnull ByteSequence firstBytes, @Nullable CharSequence firstCharsIfText) {
        return hasPrefix(firstBytes);
    }

    private boolean hasPrefix(ByteSequence byteSequence) {
        int length = byteSequence.length();
        if (length < myPrefixBytes.length) {
            return false;
        }

        for (int i = 0; i < myPrefixBytes.length; i++) {
            if (myPrefixBytes[i] != byteSequence.byteAt(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getDesiredContentPrefixLength() {
        return myPrefixBytes.length;
    }

    @Nullable
    @Override
    public Collection<? extends FileType> getDetectedFileTypes() {
        return List.of(myMetadataProvider.getFileType());
    }

    @Override
    public int getVersion() {
        return myMetadataProvider.getVersion();
    }
}
