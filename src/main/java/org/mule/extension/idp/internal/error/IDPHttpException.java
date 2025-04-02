/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.error;

import org.mule.runtime.extension.api.exception.ModuleException;

public class IDPHttpException extends ModuleException {
    public IDPHttpException(String message, IDPError error) {
        super(message, error);
    }
}
