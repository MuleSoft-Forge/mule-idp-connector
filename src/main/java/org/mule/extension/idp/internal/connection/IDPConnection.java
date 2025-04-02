/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.connection;

import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpRequestOptions;
import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mule.runtime.http.api.HttpHeaders.Names.AUTHORIZATION;

public class IDPConnection {
    private final HttpClient httpClient;
    private final String serviceBaseUrl;
    private final String organisationId;
    private final String platformBaseUrl;
    private final HttpAuthentication httpAuthentication;
    private ClientCredentialsState state;

    public IDPConnection(Builder builder) {
        this.httpClient = builder.httpClient;
        this.serviceBaseUrl = builder.serviceBaseUrl;
        this.organisationId = builder.organisationId;
        this.platformBaseUrl = builder.platformBaseUrl;
        this.httpAuthentication = builder.httpAuthentication;
        this.state = builder.state;
    }

    public String getServiceBaseUrl() {
        return serviceBaseUrl;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public String getPlatformBaseUrl() {
        return platformBaseUrl;
    }

    public HttpAuthentication getHttpAuthentication() {
        return httpAuthentication;
    }

    public ClientCredentialsState getClientCredentialsState() {
        return state;
    }

    public static class Builder {
        private HttpClient httpClient;
        private String serviceBaseUrl;
        private String organisationId;
        private String platformBaseUrl;
        private HttpAuthentication httpAuthentication;
        private ClientCredentialsState state;

        public Builder setHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder setServiceBaseUrl(String serviceBaseUrl) {
            this.serviceBaseUrl = serviceBaseUrl;
            return this;
        }

        public Builder setOrganisationId(String organisationId) {
            this.organisationId = organisationId;
            return this;
        }

        public Builder setPlatformBaseUrl(String platformBaseUrl) {
            this.platformBaseUrl = platformBaseUrl;
            return this;
        }

        public Builder setHttpAuthentication(HttpAuthentication httpAuthentication) {
            this.httpAuthentication = httpAuthentication;
            return this;
        }

        public Builder setClientCredentialsState(ClientCredentialsState state) {
            this.state = state;
            return this;
        }

        public IDPConnection build() {
            return new IDPConnection(this);
        }
    }

    public CompletableFuture<HttpResponse> sendRequestNonBlocking(Consumer<HttpRequestBuilder> httpRequestBuilderConsumer,
                                                                  String uri,
                                                                  HttpConstants.Method method,
                                                                  IDPAuthentication authentication,
                                                                  String accessTokenIfAuthenticationIsOAuth) {

        HttpRequestBuilder builder = HttpRequest.builder();

        if (httpRequestBuilderConsumer != null) {
            httpRequestBuilderConsumer.accept(builder);
        }

        HttpRequest request = null;
        HttpRequestOptions options = null;

        if (authentication == IDPAuthentication.BASIC_AUTH) {
            options = HttpRequestOptions.builder()
                    .authentication(httpAuthentication)
                    .build();
            request = builder.method(method)
                    .uri(uri)
                    .build();

            return httpClient.sendAsync(request, options);

        } else if (authentication == IDPAuthentication.OAUTH) {
            request = builder.method(method)
                    .uri(uri)
                    .addHeader(AUTHORIZATION, "Bearer " + accessTokenIfAuthenticationIsOAuth)
                    .build();

            return httpClient.sendAsync(request);

        } else {
            request = builder.method(method)
                    .uri(uri)
                    .build();
        }

        return httpClient.sendAsync(request);
    }

    public HttpResponse sendRequestBlocking(Consumer<HttpRequestBuilder> httpRequestBuilderConsumer,
                                            String uri,
                                            HttpConstants.Method method,
                                            IDPAuthentication authentication,
                                            String accessTokenIfAuthenticationIsOAuth) throws Throwable {

        HttpRequestBuilder builder = HttpRequest.builder();

        if (httpRequestBuilderConsumer != null) {
            httpRequestBuilderConsumer.accept(builder);
        }

        HttpRequest request = null;
        HttpRequestOptions options = null;

        if (authentication == IDPAuthentication.BASIC_AUTH) {
            options = HttpRequestOptions.builder()
                    .authentication(httpAuthentication)
                    .build();
            request = builder.method(method)
                    .uri(uri)
                    .build();

            return httpClient.send(request, options);

        } else if (authentication == IDPAuthentication.OAUTH) {
            request = builder.method(method)
                    .uri(uri)
                    .addHeader(AUTHORIZATION, "Bearer " + accessTokenIfAuthenticationIsOAuth)
                    .build();

            return httpClient.send(request);

        } else {
            request = builder.method(method)
                    .uri(uri)
                    .build();
        }

        return httpClient.send(request);
    }

    public void invalidate() {
        if (httpClient != null) {
            httpClient.stop();
        }
    }
}
