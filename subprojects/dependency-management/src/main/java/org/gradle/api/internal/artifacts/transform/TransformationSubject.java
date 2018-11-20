/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.transform;

import com.google.common.collect.ImmutableList;
import org.gradle.api.Describable;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Optional;

/**
 * Subject which is transformed or the result of a transformation.
 */
public abstract class TransformationSubject implements Describable {

    public static TransformationSubject failure(String displayName, Throwable failure) {
        return new TransformationFailedSubject(displayName, failure);
    }

    public static TransformationSubject initial(File file) {
        return new InitialFileTransformationSubject(file);
    }

    public static TransformationSubject initial(ComponentArtifactIdentifier artifactId, File file, ArtifactTransformDependenciesProvider dependencies) {
        return new InitialArtifactTransformationSubject(artifactId, file, dependencies);
    }

    /**
     * The files which should be transformed.
     */
    public abstract ImmutableList<File> getFiles();

    public abstract String getInitialFileName();

    public abstract Optional<ProjectComponentIdentifier> getProducer();

    /**
     * Gives access to the artifacts of the dependencies of the subject of the transformation
     */
    public abstract ArtifactTransformDependenciesProvider getArtifactDependenciesProvider();

    /**
     * Records the failure to transform a previous subject.
     */
    @Nullable
    public abstract Throwable getFailure();


    public TransformationSubject transformationFailed(Throwable failure) {
        return failure(getDisplayName(), failure);
    }

    public TransformationSubject transformationSuccessful(ImmutableList<File> result) {
        return new SubsequentTransformationSubject(
            this,
            result
        );
    }

    private static class TransformationFailedSubject extends TransformationSubject {
        private final String displayName;
        private final Throwable failure;

        public TransformationFailedSubject(String displayName, Throwable failure) {
            this.displayName = displayName;
            this.failure = failure;
        }

        @Override
        public ImmutableList<File> getFiles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArtifactTransformDependenciesProvider getArtifactDependenciesProvider() {
            throw new UnsupportedOperationException();
        }

        public String getInitialFileName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<ProjectComponentIdentifier> getProducer() {
            return Optional.empty();
        }

        @Nullable
        @Override
        public Throwable getFailure() {
            return failure;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }

    private static abstract class AbstractInitialTransformationSubject extends TransformationSubject {
        private final File file;

        public AbstractInitialTransformationSubject(File file) {
            this.file = file;
        }

        @Override
        public ImmutableList<File> getFiles() {
            return ImmutableList.of(file);
        }

        public File getFile() {
            return file;
        }

        @Nullable
        @Override
        public Throwable getFailure() {
            return null;
        }

        @Override
        public String getInitialFileName() {
            return getFile().getName();
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }

    private static class InitialFileTransformationSubject extends AbstractInitialTransformationSubject {

        public InitialFileTransformationSubject(File file) {
            super(file);
        }

        @Override
        public ArtifactTransformDependenciesProvider getArtifactDependenciesProvider() {
            return ArtifactTransformDependenciesProvider.EMPTY;
        }

        @Override
        public String getDisplayName() {
            return "file " + getFile();
        }

        @Override
        public Optional<ProjectComponentIdentifier> getProducer() {
            return Optional.empty();
        }
    }

    private static class InitialArtifactTransformationSubject extends AbstractInitialTransformationSubject {
        private final ComponentArtifactIdentifier artifactId;
        private final ArtifactTransformDependenciesProvider dependencies;

        public InitialArtifactTransformationSubject(ComponentArtifactIdentifier artifactId, File file, ArtifactTransformDependenciesProvider dependencies) {
            super(file);
            this.artifactId = artifactId;
            this.dependencies = dependencies;
        }

        @Override
        public ArtifactTransformDependenciesProvider getArtifactDependenciesProvider() {
            return dependencies;
        }

        @Override
        public String getDisplayName() {
            return "artifact " + artifactId.getDisplayName();
        }

        @Override
        public Optional<ProjectComponentIdentifier> getProducer() {
            ComponentIdentifier componentIdentifier = artifactId.getComponentIdentifier();
            if (componentIdentifier instanceof ProjectComponentIdentifier) {
                return Optional.of((ProjectComponentIdentifier) componentIdentifier);
            }
            return Optional.empty();
        }
    }

    private static class SubsequentTransformationSubject extends TransformationSubject {
        private final TransformationSubject previous;
        private final ImmutableList<File> files;

        public SubsequentTransformationSubject(TransformationSubject previous, ImmutableList<File> files) {
            this.previous = previous;
            this.files = files;
        }

        @Override
        public ImmutableList<File> getFiles() {
            return files;
        }

        @Override
        public ArtifactTransformDependenciesProvider getArtifactDependenciesProvider() {
            return previous.getArtifactDependenciesProvider();
        }

        public String getInitialFileName() {
            return previous.getInitialFileName();
        }

        @Override
        public Optional<ProjectComponentIdentifier> getProducer() {
            return previous.getProducer();
        }

        @Override
        public Throwable getFailure() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return previous.getDisplayName();
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }
}
