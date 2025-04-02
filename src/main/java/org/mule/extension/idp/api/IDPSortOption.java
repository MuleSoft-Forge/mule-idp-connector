/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.api;

public enum IDPSortOption {
    UPDATED_AT_DESC("updatedAt,desc"),
    UPDATED_AT_ASC("updatedAt,asc"),
    NAME_DESC("name,desc"),
    NAME_ASC("name,asc"),
    TYPE_DESC("type,desc"),
    TYPE_ASC("type,asc");

    private final String value;

    IDPSortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}