package consulo.gameFramework.meta;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.PsiDependentFileContent;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface MetadataProvider {
    @Nonnull
    String getExtension();

    @RequiredReadAction
    boolean isAvailable(@Nonnull Project project);

    @Nullable
    @RequiredReadAction
    String extractIdForIndex(@Nonnull PsiDependentFileContent content);

    @Nullable
    @RequiredReadAction
    String extractId(@Nonnull PsiFile file);

    @Nonnull
    MetadataFileType getFileType();

    default int getVersion() {
        return 1;
    }
}
