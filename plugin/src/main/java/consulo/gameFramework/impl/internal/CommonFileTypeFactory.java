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

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2025-01-24
 */
@ExtensionImpl
public class CommonFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@Nonnull FileTypeConsumer consumer) {
        // register exr file type as binary, do not try index it
        consumer.consume(ExrImageFileType.INSTANCE);

        // register fbx file type as binary, do not try index it
        consumer.consume(FbxFileType.INSTANCE);
    }
}
