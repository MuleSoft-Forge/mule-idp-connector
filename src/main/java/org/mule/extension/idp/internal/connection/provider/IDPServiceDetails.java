/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.connection.provider;

import org.mule.extension.idp.internal.connection.valueprovider.IDPAnypointIdpHostValueProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.values.OfValues;

public class IDPServiceDetails {

    @Parameter
    @Placement(order = 1)
    @DisplayName("IDP Service Host")
    @OfValues(IDPAnypointIdpHostValueProvider.class)
    protected String serviceBaseUrl;

    @Parameter
    @Placement(order = 2)
    @DisplayName("Organization Id")
    @Summary("MuleSoft Organization Id (Org Id) https://help.salesforce.com/s/articleView?id=001115130")
    protected String organisationId;

    public String getServiceBaseUrl() {
        return serviceBaseUrl;
    }

    public String getOrganisationId() {
        return organisationId;
    }
}
