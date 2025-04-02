/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.connection.valueprovider;

import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.ValueProvider;

import java.util.HashSet;
import java.util.Set;

import static org.mule.runtime.extension.api.values.ValueBuilder.newValue;

public class IDPAnypointPlatformHostValueProvider implements ValueProvider {

  @Override
  public Set<Value> resolve() {
    Set<Value> values = new HashSet<>();
    values.add(newValue("https://anypoint.mulesoft.com").withDisplayName("https://anypoint.mulesoft.com (US)").build());
    values.add(newValue("https://eu1.anypoint.mulesoft.com").withDisplayName("https://eu1.anypoint.mulesoft.com (EU)").build());
    return values;
  }
}
