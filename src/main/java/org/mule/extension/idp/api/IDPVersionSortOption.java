/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.api;

public enum IDPVersionSortOption {
    VERSION_DESC("version,desc"),
    VERSION_ASC("version,asc");

    private final String value;

    IDPVersionSortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}