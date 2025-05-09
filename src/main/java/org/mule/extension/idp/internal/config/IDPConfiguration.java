/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.config;

import org.mule.extension.idp.internal.connection.provider.IDPConnectionProvider;
import org.mule.extension.idp.internal.operation.IDPPlatformOperations;
import org.mule.extension.idp.internal.operation.IDPServiceOperations;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Configuration(name = "config")
@ConnectionProviders(IDPConnectionProvider.class)
@Operations({IDPPlatformOperations.class, IDPServiceOperations.class})
public class IDPConfiguration {
}
