/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation;

import org.mule.extension.idp.internal.connection.IDPAuthentication;
import org.mule.extension.idp.internal.connection.IDPConnection;
import org.mule.extension.idp.internal.error.IDPErrorProvider;
import org.mule.extension.idp.internal.operation.utils.IDPOperationsUtils;
import org.mule.extension.idp.internal.operation.utils.IDPPageable;
import org.mule.extension.idp.internal.operation.utils.IDPPageableVersion;
import org.mule.extension.idp.internal.operation.valueprovider.IDPActionIdValueProvider;
import org.mule.extension.idp.internal.operation.valueprovider.IDPVersionIdValueProvider;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.fixed.InputJsonType;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputJsonType;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.values.OfValues;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mule.extension.idp.internal.operation.utils.IDPEndpoints.*;
import static org.mule.runtime.api.meta.ExpressionSupport.REQUIRED;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

public class IDPPlatformOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(IDPPlatformOperations.class);

    @DisplayName("Platform IDP - Actions - List")
    @Summary("List All IDP Document Actions for a MuleSoft Organisation Id" +
            "<ul>" +
            "<li> Use of MuleSoft Anypoint Platform API" +
            "<li> Anypoint Account login must have IDP permissions" +
            "</ul>")
    @MediaType(value = APPLICATION_JSON, strict = false)
    @Throws(IDPErrorProvider.class)
    @OutputJsonType(schema = "api/response/platform/PlatformActionsList.json")
    public void listActions(@Connection IDPConnection connection,
                            @ParameterGroup(name = "Pageable [optional]") IDPPageable pageable,
                            CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_LIST_ACTIONS, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                    if (pageable.getPage() > 0) {
                        requestBuilder.addQueryParam("page", String.valueOf(pageable.getPage()));
                    }
                    if (pageable.getSize() > 0) {
                        requestBuilder.addQueryParam("size", String.valueOf(pageable.getSize()));
                    }
                    if (pageable.getSort() != null) {
                        requestBuilder.addQueryParam("sort", String.valueOf(pageable.getSort()));
                    }
                }, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH)
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Platform IDP - Action Versions - List")
    @Summary("List All IDP Document Action Versions for a MuleSoft Organisation Id" +
            "<ul>" +
            "<li> Use of MuleSoft Anypoint Platform API" +
            "<li> Anypoint Account login must have IDP permissions" +
            "</ul>")
    @MediaType(value = APPLICATION_JSON, strict = false)
    @Throws(IDPErrorProvider.class)
    @OutputJsonType(schema = "api/response/platform/PlatformActionVersionsList.json")
    public void listActionVersions(@Connection IDPConnection connection,
                                   @OfValues(IDPActionIdValueProvider.class) String actionId,
                                   @ParameterGroup(name = "Pageable [optional]") IDPPageableVersion pageable,
                                   CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_LIST_ACTION_VERSIONS, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                    if (pageable.getPage() > 0) {
                        requestBuilder.addQueryParam("page", String.valueOf(pageable.getPage()));
                    }
                    if (pageable.getSize() > 0) {
                        requestBuilder.addQueryParam("size", String.valueOf(pageable.getSize()));
                    }
                    if (pageable.getSort() != null) {
                        requestBuilder.addQueryParam("sort", String.valueOf(pageable.getSort()));
                    }
                }, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH)
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }


    @DisplayName("Platform IDP - Action Version - Retrieve")
    @Summary("Retrieve a IDP Document Action Version for a MuleSoft Organisation Id" +
            "<ul>" +
            "<li> Use of MuleSoft Anypoint Platform API" +
            "<li> Anypoint Account login must have IDP permissions" +
            "</ul>")
    @MediaType(value = APPLICATION_JSON, strict = false)
    @Throws(IDPErrorProvider.class)
    @OutputJsonType(schema = "api/response/platform/PlatformActionVersionGet.json")
    public void getActionVersion(@Connection IDPConnection connection,
                                 @OfValues(IDPActionIdValueProvider.class) String actionId,
                                 @OfValues(IDPVersionIdValueProvider.class) String versionSemantic,
                                 CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);
        uriParameters.put("versionSemantic", versionSemantic);

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_RETRIEVE_ACTION_VERSION, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {}, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH)
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Platform IDP - Action Detail - Retrieve")
    @Summary("Retrieve a IDP Document Action Detail for a MuleSoft Organisation Id" +
            "<ul>" +
            "<li> Use of MuleSoft Anypoint Platform API" +
            "<li> Anypoint Account login must have IDP permissions" +
            "</ul>")
    @MediaType(value = APPLICATION_JSON, strict = false)
    @Throws(IDPErrorProvider.class)
    @OutputJsonType(schema = "api/response/platform/PlatformActionVersionGet.json")
    public void getActionDetails(@Connection IDPConnection connection,
                                 @OfValues(IDPActionIdValueProvider.class) String actionId,
                                 @OfValues(IDPVersionIdValueProvider.class) String versionSemantic,
                                 CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);
        uriParameters.put("versionSemantic", versionSemantic);

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_RETRIEVE_ACTION_DETAIL, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {}, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH)
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Platform IDP - Action Reviewers - List")
    @Summary("List All IDP Document Actions Reviewers for a MuleSoft Organisation Id" +
            "<ul>" +
            "<li> Use of MuleSoft Anypoint Platform API" +
            "<li> Anypoint Account login must have IDP permissions" +
            "</ul>")
    @MediaType(value = APPLICATION_JSON, strict = false)
    @Throws(IDPErrorProvider.class)
    @OutputJsonType(schema = "api/response/platform/PlatformActionReviewersList.json")
    public void listActionReviewers(@Connection IDPConnection connection,
                                    @OfValues(IDPActionIdValueProvider.class) String actionId,
                                    CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_LIST_ACTION_REVIEWERS, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {}, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH)
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Platform IDP - Action Reviewers - Update")
    @Summary("Update IDP Document Action Reviewers for a MuleSoft Organisation Id" +
            "<ul>" +
            "<li> Use of MuleSoft Anypoint Platform API" +
            "<li> Anypoint Account login must have IDP permissions" +
            "</ul>")
    @MediaType(value = APPLICATION_JSON, strict = false)
    @Throws(IDPErrorProvider.class)
    @OutputJsonType(schema = "api/response/platform/PlatformActionReviewersUpdate.json")
    public void updateActionReviewers(@Connection IDPConnection connection,
                                      @OfValues(IDPActionIdValueProvider.class) String actionId,
                                      @Expression(REQUIRED) @Optional(defaultValue="#[payload]") @InputJsonType(schema = "api/request/platform/PlatformActionReviewersUpdate.json") TypedValue<InputStream> contents,
                                      CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_UPDATE_ACTION_REVIEWERS, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.entity(new InputStreamHttpEntity(contents.getValue()));
                }, uri, HttpConstants.Method.PATCH, IDPAuthentication.BASIC_AUTH)
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }
}
