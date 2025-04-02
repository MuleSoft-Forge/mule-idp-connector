/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.metadata;

import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.json.api.JsonTypeLoader;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.core.api.util.IOUtils;

import java.io.InputStream;

public abstract class IDPBaseStaticJsonTypeResolver implements OutputTypeResolver {

  @Override
  public String getCategoryName() {
    return getJsonTypeName();
  }

  @Override
  public String getResolverName() {
    return getJsonTypeName();
  }

  @Override
  public MetadataType getOutputType(MetadataContext metadataContext, Object o) {
    return getMetadataFrom(getJsonLocation(), getJsonTypeName());
  }

  abstract String getJsonTypeName();

  abstract String getJsonLocation();

  public static MetadataType getMetadataFrom(String resource, String typeAlias) {
    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    return new JsonTypeLoader(IOUtils.toString(resourceAsStream)).load(null, typeAlias).get();
  }
}
