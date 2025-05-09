/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.extension;

import org.mule.extension.idp.internal.config.IDPConfiguration;
import org.mule.extension.idp.internal.config.IDPProxyConfiguration;
import org.mule.extension.idp.internal.error.IDPError;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;
import org.mule.sdk.api.annotation.Configurations;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.annotation.MinMuleVersion;

import static org.mule.sdk.api.meta.JavaVersion.*;
/**
 * MuleSoft IDP Universal Connector
 *
 * @author Open Source
 */
@Extension(name="MuleSoft IDP", category = Category.COMMUNITY)
@ErrorTypes(IDPError.class)
@Xml(prefix ="mule-idp")
@Configurations(IDPConfiguration.class)
@JavaVersionSupport({JAVA_8, JAVA_11})
@RequiresEnterpriseLicense(allowEvaluationLicense = false)
@MinMuleVersion("4.6.0")
public class IDPConnector {
}
