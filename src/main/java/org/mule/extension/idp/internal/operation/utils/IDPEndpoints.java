/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.utils;

public interface IDPEndpoints {
    String IDP_BASE = "/api/v1/organizations/{organizationId}";
    String IDP_SUBMIT_DOC_EXECUTION = "/actions/{actionId}/versions/{version}/executions";
    String IDP_RETRIEVE_EXECUTION_RESULT = "/actions/{actionId}/versions/{version}/executions/{executionId}";
    String IDP_LIST_PENDING_REVIEW_TASKS = "/reviews";
    String IDP_DELETE_REVIEW_TASK = "/actions/{actionId}/reviews/{executionId}";
    String IDP_RETRIEVE_REVIEW_TASK = "/actions/{actionId}/reviews/{executionId}";
    String IDP_UPDATE_REVIEW_TASK = "/actions/{actionId}/reviews/{executionId}";

    String PLATFORM_BASE = "/idp/api/v1/organizations/{organizationId}";
    String PLATFORM_IDP_LIST_ACTIONS = "/actions";
    String PLATFORM_IDP_LIST_ACTION_VERSIONS = "/actions/{actionId}/versions";
    String PLATFORM_IDP_RETRIEVE_ACTION_VERSION = "/actions/{actionId}/versions/{versionSemantic}";
    String PLATFORM_IDP_RETRIEVE_ACTION_DETAIL = "/actions/{actionId}";
    String PLATFORM_IDP_LIST_ACTION_REVIEWERS = "/actions/{actionId}/reviewers";
    String PLATFORM_IDP_UPDATE_ACTION_REVIEWERS = "/actions/{actionId}/reviewers";
}
