/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.internal.component.resolution.failure.describer;

import org.gradle.api.internal.attributes.AttributesSchemaInternal;
import org.gradle.internal.component.resolution.failure.exception.ConfigurationSelectionException;
import org.gradle.internal.component.resolution.failure.type.ConfigurationNotConsumableFailure;

import java.util.List;
import java.util.Optional;

/**
 * A {@link ResolutionFailureDescriber} that describes a {@link ConfigurationNotConsumableFailure}.
 */
public abstract class ConfigurationNotConsumableFailureDescriber extends AbstractResolutionFailureDescriber<ConfigurationNotConsumableFailure> {
    @Override
    public ConfigurationSelectionException describeFailure(ConfigurationNotConsumableFailure failure, Optional<AttributesSchemaInternal> schema) {
        String message = buildConfigurationNotConsumableFailureMsg(failure);
        List<String> resolutions = buildResolutions(suggestReviewAlgorithm());
        return new ConfigurationSelectionException(message, failure, resolutions);
    }

    private String buildConfigurationNotConsumableFailureMsg(ConfigurationNotConsumableFailure failure) {
        return String.format("Selected configuration '" + failure.getRequestedName() + "' on '" + failure.getRequestedComponentDisplayName() + "' but it can't be used as a project dependency because it isn't intended for consumption by other components.");
    }
}
