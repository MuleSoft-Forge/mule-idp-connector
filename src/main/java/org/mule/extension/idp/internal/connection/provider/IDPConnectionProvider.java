/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.connection.provider;


import org.mule.extension.idp.internal.config.IDPProxyConfiguration;
import org.mule.extension.idp.internal.connection.IDPAuthentication;
import org.mule.extension.idp.internal.connection.IDPConnection;
import org.mule.extension.idp.internal.error.IDPError;
import org.mule.extension.idp.internal.error.IDPHttpException;
import org.mule.extension.idp.internal.operation.utils.IDPOperationsUtils;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.ClientCredentials;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.mule.sdk.api.annotation.Alias;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import static org.mule.extension.idp.internal.operation.utils.IDPEndpoints.PLATFORM_BASE;
import static org.mule.extension.idp.internal.operation.utils.IDPEndpoints.PLATFORM_IDP_LIST_ACTIONS;
import static org.mule.runtime.extension.api.annotation.param.display.Placement.SECURITY_TAB;

@Alias("connection")
@ClientCredentials(tokenUrl = "https://anypoint.mulesoft.com/accounts/api/v2/oauth2/token")
public class IDPConnectionProvider implements CachedConnectionProvider<IDPConnection>, Initialisable, Startable, Stoppable {

    @ParameterGroup(name = "MuleSoft Intelligent Document Processing Service")
    IDPServiceDetails serviceEndpoint;

    @ParameterGroup(name = "MuleSoft Anypoint Platform Account for Config Introspection and Platform API")
    IDPAnypointAccountDetails accountLoginDetails;

    @Parameter
    @Placement(tab=SECURITY_TAB)
    @DisplayName("TLS")
    @Optional
    private TlsContextFactory tlsContextFactory;

    @Parameter
    @Placement(tab="Proxy")
    @DisplayName("Proxy")
    @Optional
    private IDPProxyConfiguration proxyConfiguration;

    private TlsContextFactory getTlsContextFactory() {
        return tlsContextFactory;
    }

    private IDPProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    @Inject
    protected HttpService httpService;

    protected HttpClient httpClient;

    @RefName
    protected String configName;

    private ClientCredentialsState state;

    @Override
    public void initialise() throws InitialisationException {
        if (getTlsContextFactory() != null) {
            ((Initialisable) getTlsContextFactory()).initialise();
        }
    }

    @Override
    public void start() throws MuleException {
        httpClient = createHttpClient(httpService);
    }

    @Override
    public void stop() throws MuleException {
        if (httpClient != null) {
            httpClient.stop();
        }
    }

    private HttpClient createHttpClient(HttpService httpService) {
        HttpClient httpClient = httpService.getClientFactory().create(new HttpClientConfiguration.Builder()
                .setName(configName)
                .setTlsContextFactory(getTlsContextFactory())
                .setProxyConfig(getProxyConfiguration())
                .build());
        httpClient.start();
        return httpClient;
    }

    @Override
    public IDPConnection connect() throws ConnectionException {
        HttpAuthentication httpAuthentication = HttpAuthentication.basic(accountLoginDetails.getUsername(), accountLoginDetails.getPassword()).build();

        return new IDPConnection.Builder()
                .setServiceBaseUrl(serviceEndpoint.serviceBaseUrl)
                .setOrganisationId(serviceEndpoint.organisationId)
                .setPlatformBaseUrl(accountLoginDetails.getPlatformBaseUrl())
                .setHttpAuthentication(httpAuthentication)
                .setClientCredentialsState(state)
                .setHttpClient(httpClient)
                .build();
    }

    @Override
    public void disconnect(IDPConnection connection) {
        connection.invalidate();
    }

    @Override
    public ConnectionValidationResult validate(IDPConnection connection) {
        Map<String, String> uriParameters = new HashMap<>();
        uriParameters.put("organizationId", connection.getOrganisationId());

        String uri = connection.getPlatformBaseUrl() + IDPOperationsUtils.createEndpoint(PLATFORM_BASE + PLATFORM_IDP_LIST_ACTIONS, uriParameters);

        try {
            HttpResponse httpResponse = connection.sendRequestBlocking(requestBuilder -> {
            }, uri, HttpConstants.Method.GET, IDPAuthentication.BASIC_AUTH, "");

            int statusCode = httpResponse.getStatusCode();
            String reason = httpResponse.getReasonPhrase();
            if (statusCode >= 400) {
                return ConnectionValidationResult.failure("could not list actions!", new IDPHttpException("Status code: " + statusCode + " " + reason, IDPError.CONNECTIVITY));
            }
            return ConnectionValidationResult.success();
        } catch (Throwable e) {
            return ConnectionValidationResult.failure("could not list actions", new IDPHttpException(e.getMessage(), IDPError.CONNECTIVITY));
        }
    }
}
