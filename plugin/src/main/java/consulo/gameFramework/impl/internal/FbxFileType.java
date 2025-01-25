/*
 * Copyright 2013-2025 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.gameFramework.impl.internal;

import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2025-01-24
 */
public class FbxFileType implements FileType {
    public static final FbxFileType INSTANCE = new FbxFileType();

    @Nonnull
    @Override
    public String getId() {
        return "FBX";
    }

    @Nonnull
    @Override
    public String getDefaultExtension() {
        return "fbx";
    }

    @Nonnull
    @Override
    public LocalizeValue getDescription() {
        return LocalizeValue.localizeTODO("Adaptable File Formats for 3D Animation Software");
    }

    @Nullable
    @Override
    public Image getIcon() {
        return PlatformIconGroup.filetypesBinary();
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
