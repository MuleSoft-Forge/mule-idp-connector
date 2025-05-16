/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.valueprovider;

import org.mule.extension.idp.api.IDPSortOption;
import org.mule.extension.idp.internal.connection.IDPConnection;
import org.mule.extension.idp.internal.operation.IDPPlatformOperations;
import org.mule.extension.idp.internal.operation.utils.IDPPageable;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.util.Reference;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static java.util.Collections.emptyMap;

public class IDPActionIdValueProvider implements ValueProvider {

    private static final String EXPRESSION = "%dw 2.0\n" +
            "output application/java\n" +
            "---\n" +
            "payload.actions map { ($.id) : $.name } reduce ($$ ++ $) ";

    @Connection
    private IDPConnection connection;

    @Inject
    private SchedulerService schedulerService;

    @Inject
    private ExpressionManager expressionManager;

    @Override
    public Set<Value> resolve() throws ValueResolvingException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Reference<Set<Value>> valuesReference = new Reference<>(new HashSet<>());
        Reference<Throwable> throwableReference = new Reference<>();

        try {
            new IDPPlatformOperations().listActions(connection, new IDPPageable(0,0, IDPSortOption.UPDATED_AT_DESC), createCallbackHandler(countDownLatch, valuesReference, throwableReference, EXPRESSION));
        } catch (ModuleException e) {
            throw new RuntimeException(e);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new ValueResolvingException("Unexpected Error", "UNKNOWN", e);
        }

        if (throwableReference.get() != null) {
            throw new ValueResolvingException("Unexpected Error", "UNKNOWN", throwableReference.get());
        }

        return valuesReference.get();
    }

    @Override
    public String getId() {
        return ValueProvider.super.getId();
    }

    private CompletionCallback<InputStream, Void> createCallbackHandler(CountDownLatch countDownLatch,
                                                                        Reference<Set<Value>> values,
                                                                        Reference<Throwable> throwableReference,
                                                                        String expression) {
        return new CompletionCallback<InputStream, Void>() {

            @Override
            public void success(Result<InputStream, Void> result) {
                CountDownLatch transformationLatch = new CountDownLatch(1);
                Scheduler scheduler = schedulerService.cpuLightScheduler();
                scheduler.execute(() -> {
                    try {
                        TypedValue<?> payload =
                                expressionManager.evaluate(expression, BindingContext.builder()
                                        .addBinding("payload", getTypedValue(result))
                                        .build());

                        Map<String, String> value = (Map<String, String>) payload.getValue();
                        values.set(ValueBuilder.getValuesFor(value == null ? emptyMap() : value));
                    } finally {
                        transformationLatch.countDown();
                    }
                });
                try {
                    transformationLatch.await();
                } catch (InterruptedException e) {
                    throwableReference.set(e);
                } finally {
                    scheduler.stop();
                    scheduler = null;

                    countDownLatch.countDown();
                }
            }

            @Override
            public void error(Throwable throwable) {
                throwableReference.set(throwable);
                countDownLatch.countDown();
            }
        };
    }

    private TypedValue getTypedValue(Result<InputStream, Void> result) {
        InputStream output = result.getOutput();
        String s = IOUtils.toString(output);
        return new TypedValue<>(s, DataType.builder().type(InputStream.class).mediaType(result.getMediaType().get()).build());
    }
}
