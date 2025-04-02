/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

import java.util.Optional;

public enum IDPError implements ErrorTypeDefinition<IDPError> {
    CONNECTIVITY(MuleErrors.CONNECTIVITY);

    private ErrorTypeDefinition<? extends Enum<?>> parent;

    IDPError(ErrorTypeDefinition<? extends Enum<?>> parent) {
        this.parent = parent;
    }

    IDPError() {
    }

    @Override
    public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
        return Optional.ofNullable(parent);
    }
}
