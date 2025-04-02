/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.metadata;

import com.mulesoft.connectivity.rest.commons.api.datasense.metadata.input.JsonInputMetadataResolver;

public class IDPServiceReviewTaskUpdateInputResolver extends JsonInputMetadataResolver {
    @Override
    public String getSchemaPath() {
        return "api/request/service/ServiceReviewTaskUpdate.json";
    }

    @Override
    public String getCategoryName() {
        return "idp-service-review-task-update-type-resolver";
    }

    @Override
    public String getResolverName() {
        return this.getClass().getName();
    }
}
