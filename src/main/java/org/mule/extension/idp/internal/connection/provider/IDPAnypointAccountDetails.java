/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.connection.provider;

import org.mule.extension.idp.internal.connection.valueprovider.IDPAnypointPlatformHostValueProvider;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.*;
import org.mule.runtime.extension.api.annotation.values.OfValues;


public class IDPAnypointAccountDetails {

  @Parameter
  @Placement(order = 1)
  @DisplayName("Host")
  @OfValues(IDPAnypointPlatformHostValueProvider.class)
  @Optional
  protected String platformBaseUrl;

  @Parameter
  @Placement(order = 2)
  @DisplayName("Username")
  @Optional
  private String username;

  @Parameter
  @Placement(order = 3)
  @DisplayName("Password")
  @Password
  @Optional
  private String password;

  public String getPlatformBaseUrl() {
    return platformBaseUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}

