/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.mule.extension.idp.internal.error.IDPError;
import org.mule.extension.idp.internal.error.IDPHttpException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON;

public class IDPOperationsUtils {

    public static BiConsumer<HttpResponse, Throwable> createCompletionHandler(CompletionCallback<InputStream, Void> completionCallback, String uri) {
        return ((httpResponse, throwable) -> {
            if (throwable != null) {
                completionCallback.error(new RuntimeException("Unexpected Error Occurred", throwable));
            } else {
                int statusCode = httpResponse.getStatusCode();
                String reason = httpResponse.getReasonPhrase();
                if (statusCode >= 400) {
                    completionCallback.error(new IDPHttpException(createHttpResponseMessage("IDP Error Occurred", reason, statusCode, uri), IDPError.CONNECTIVITY) );
                } else {
                    completionCallback.success(Result.<InputStream, Void>builder().output(httpResponse.getEntity().getContent())
                            .mediaType(APPLICATION_JSON)
                            .build());
                }
            }
        });
    }

    private static String createHttpResponseMessage(String message, String reason, int statusCode, String uri) {
        JsonObject jsonObjects = new JsonObject();
        jsonObjects.addProperty("message", message);
        jsonObjects.addProperty("reason", reason);
        jsonObjects.addProperty("code", statusCode);
        jsonObjects.addProperty("resource", uri);

        String jsonMessage = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObjects);

        return jsonMessage;
    }

    public static String createEndpoint(String endpointTemplate, Map<String, String> uriParameters) {
        String endpoint = endpointTemplate;
        for (String uriParameter : uriParameters.keySet()) {
            endpoint = endpoint.replace("{" + uriParameter + "}", uriParameters.get(uriParameter));
        }
        return endpoint;
    }

}
