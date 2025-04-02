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
import org.mule.extension.idp.internal.metadata.*;
import org.mule.extension.idp.internal.operation.utils.IDPOperationsUtils;
import org.mule.extension.idp.internal.operation.utils.IDPPageable;
import org.mule.extension.idp.internal.operation.utils.IDPSubmitDocOptions;
import org.mule.extension.idp.internal.operation.valueprovider.IDPActionIdValueProvider;
import org.mule.extension.idp.internal.operation.valueprovider.IDPVersionIdValueProvider;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.TypeResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.values.OfValues;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.entity.multipart.HttpPart;
import org.mule.runtime.http.api.domain.entity.multipart.MultipartHttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mule.extension.idp.internal.operation.utils.IDPEndpoints.*;
import static org.mule.runtime.api.meta.ExpressionSupport.REQUIRED;

public class IDPServiceOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(IDPServiceOperations.class);

    @DisplayName("Service IDP - Execution - Submit")
    @MediaType(MediaType.APPLICATION_JSON)
    @Throws(IDPErrorProvider.class)
    @OutputResolver(output = IDPServiceExecutionSubmitOutputResolver.class)
    public void submitExecution(@Connection IDPConnection connection,
                                @OfValues(IDPActionIdValueProvider.class) String actionId,
                                @OfValues(IDPVersionIdValueProvider.class) String versionSemantic,
                                @ParameterGroup(name = "Submission Options") IDPSubmitDocOptions SubmitDocOptions,
                                @Content TypedValue<InputStream> fileContent,
                                CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {
        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);
        uriParameters.put("version", versionSemantic);

        String uri = connection.getServiceBaseUrl() + IDPOperationsUtils.createEndpoint(IDP_BASE + IDP_SUBMIT_DOC_EXECUTION, uriParameters);
        connection.sendRequestNonBlocking(requestBuilder -> {
            if (SubmitDocOptions.getHeaderXSfdcCoreTenantId() != null) {
                requestBuilder.addHeader("x-sfdc-core-tenant-id", SubmitDocOptions.getHeaderXSfdcCoreTenantId());
            }

            requestBuilder.addHeader("accept", "application/json");

            ArrayList multiPartsFormData = new ArrayList<HttpEntity>();

            {   //CALLBACK
                String partName = "callback";
                byte[] content = new String("{\"noAuthUrl\": \"" + SubmitDocOptions.getCallbackUrl() + "\"}").getBytes();
                String contentType = "application/json";
                multiPartsFormData.add(new HttpPart(partName, content, contentType, content.length));
            }
            {   //FILE
                String partName = "file";
                String fileName = SubmitDocOptions.getFileName();
                byte[] content = null;
                try {
                    content = IOUtils.toByteArray(fileContent.getValue());
                } catch (IOException e) {
                    throw new MuleRuntimeException(e.getCause());
                }
                String contentType = "application/octet-stream";
                multiPartsFormData.add(new HttpPart(partName, fileName, content, contentType, content.length));
            }

            requestBuilder.entity(new MultipartHttpEntity(multiPartsFormData));

        }, uri, HttpConstants.Method.POST, IDPAuthentication.OAUTH, connection.getClientCredentialsState().getAccessToken())
        .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Service IDP - Execution Result - Retrieve")
    @MediaType(MediaType.APPLICATION_JSON)
    @Throws(IDPErrorProvider.class)
    @OutputResolver(output = IDPServiceExecutionResultRetrieveOutputResolver.class)
    public void retrieveExecutionResult(@Connection IDPConnection connection,
                                        @OfValues(IDPActionIdValueProvider.class) String actionId,
                                        @OfValues(IDPVersionIdValueProvider.class) String versionSemantic,
                                        @NotNull String executionId,
                                        @NotNull boolean valueOnly,
                                        CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);
        uriParameters.put("version", versionSemantic);
        uriParameters.put("executionId", executionId);

        String uri = connection.getServiceBaseUrl() + IDPOperationsUtils.createEndpoint(IDP_BASE + IDP_RETRIEVE_EXECUTION_RESULT, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                    requestBuilder.addQueryParam("valueOnly", String.valueOf(valueOnly));

                }, uri, HttpConstants.Method.GET, IDPAuthentication.OAUTH, connection.getClientCredentialsState().getAccessToken())
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Service IDP - Review Tasks - List")
    @MediaType(MediaType.APPLICATION_JSON)
    @Throws(IDPErrorProvider.class)
    @OutputResolver(output = IDPServiceReviewTasksListOutputResolver.class)
    public void listReviewTasks(@Connection IDPConnection connection,
                                @ParameterGroup(name = "Pageable [optional]") IDPPageable pageable,
                                CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());

        String uri = connection.getServiceBaseUrl() + IDPOperationsUtils.createEndpoint(IDP_BASE + IDP_LIST_PENDING_REVIEW_TASKS, uriParameters);

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
                }, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH, "")
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Service IDP - Review Task - Retrieve")
    @MediaType(MediaType.APPLICATION_JSON)
    @Throws(IDPErrorProvider.class)
    @OutputResolver(output = IDPServiceReviewTaskGetOutputResolver.class)
    public void retrieveReviewTask(@Connection IDPConnection connection,
                                   @OfValues(IDPActionIdValueProvider.class) String actionId,
                                   @NotNull String executionId,
                                   CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);
        uriParameters.put("executionId", executionId);

        String uri = connection.getServiceBaseUrl() + IDPOperationsUtils.createEndpoint(IDP_BASE + IDP_RETRIEVE_REVIEW_TASK, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                }, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH, "")
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Service IDP - Review Task - Delete")
    @MediaType(MediaType.APPLICATION_JSON)
    @Throws(IDPErrorProvider.class)
    public void deleteReviewTask(@Connection IDPConnection connection,
                                 @OfValues(IDPActionIdValueProvider.class) String actionId,
                                 @NotNull String executionId,
                                 CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", actionId);
        uriParameters.put("executionId", executionId);

        String uri = connection.getServiceBaseUrl() + IDPOperationsUtils.createEndpoint(IDP_BASE + IDP_DELETE_REVIEW_TASK, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                }, uri, HttpConstants.Method.DELETE, IDPAuthentication.BASIC_AUTH, "")
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Service IDP - Review Task - Update")
    @MediaType(MediaType.APPLICATION_JSON)
    @Throws(IDPErrorProvider.class)
    @OutputResolver(output = IDPServiceReviewTaskUpdateOutputResolver.class)
    public void updateReviewTask(@Connection IDPConnection connection,
                                 @OfValues(IDPActionIdValueProvider.class) String actionId,
                                 @NotNull String executionId,
                                 @Expression(REQUIRED) @Optional(defaultValue="#[payload]") @TypeResolver(IDPServiceReviewTaskUpdateInputResolver.class) TypedValue<InputStream> contents,
                                 CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());
        uriParameters.put("actionId", executionId);
        uriParameters.put("executionId", executionId);

        String uri = connection.getServiceBaseUrl() + IDPOperationsUtils.createEndpoint(IDP_BASE + IDP_UPDATE_REVIEW_TASK, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.entity(new InputStreamHttpEntity(contents.getValue()));
                }, uri, HttpConstants.Method.PATCH, IDPAuthentication.BASIC_AUTH, "")
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

}
