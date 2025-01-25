/*
 * Copyright 2013-2017 consulo.io
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

package consulo.gameFramework.impl.internal.meta.index;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.gameFramework.meta.MetadataFileType;
import consulo.gameFramework.meta.MetadataProvider;
import consulo.index.io.*;
import consulo.index.io.data.DataExternalizer;
import consulo.language.psi.stub.FileBasedIndex;
import consulo.language.psi.stub.FileBasedIndexExtension;
import consulo.language.psi.stub.FileContent;
import consulo.language.psi.stub.PsiDependentFileContent;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;

import java.util.Map;

/**
 * @author VISTALL
 * @since 01-Sep-17
 */
@ExtensionImpl
public class MetadataIndexExtension extends FileBasedIndexExtension<String, Integer> {
    public static final ID<String, Integer> KEY = ID.create("metadata.file.index");

    private final Application myApplication;

    private final DataIndexer<String, Integer, FileContent> myIndexer;

    private final EnumeratorIntegerDescriptor myDescriptor = new EnumeratorIntegerDescriptor();
    
    private final EnumeratorStringDescriptor myKeyDescriptor = new EnumeratorStringDescriptor();

    @Inject
    public MetadataIndexExtension(Application application) {
        myApplication = application;
        myIndexer = fileContent -> {
            PsiDependentFileContent dep = (PsiDependentFileContent) fileContent;

            Map.Entry<String, String> metaInfo = myApplication.getExtensionPoint(MetadataProvider.class).computeSafeIfAny(it -> {
                String id = it.extractIdForIndex(dep);
                if (id == null) {
                    return null;
                }
                return Map.entry(id, it.getExtension());
            });

            if (metaInfo == null) {
                return Map.of();
            }

            String metaId = metaInfo.getKey();

            String metaExtension = metaInfo.getValue();

            VirtualFile file = fileContent.getFile();

            VirtualFile parent = file.getParent();

            String ownerFileName = StringUtil.trimEnd(file.getName(), "." + metaExtension);
            VirtualFile owner = parent.findChild(ownerFileName);

            if (owner == null) {
                return Map.of();
            }

            int fileId = FileBasedIndex.getFileId(owner);
            
            return Map.of(metaId, fileId);
        };
    }

    @Nonnull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return (project, virtualFile) -> virtualFile.getFileType() instanceof MetadataFileType;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Nonnull
    @Override
    public ID<String, Integer> getName() {
        return KEY;
    }

    @Nonnull
    @Override
    public DataIndexer<String, Integer, FileContent> getIndexer() {
        return myIndexer;
    }

    @Nonnull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @Nonnull
    @Override
    public DataExternalizer<Integer> getValueExternalizer() {
        return myDescriptor;
    }

    @Override
    public int getVersion() {
        int[] version = new int[]{10};
        myApplication.getExtensionPoint(MetadataProvider.class).forEachExtensionSafe(it -> version[0] += it.getVersion());
        return version[0];
    }
}
