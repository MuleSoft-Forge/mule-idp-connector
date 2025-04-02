/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.metadata;

import com.mulesoft.connectivity.rest.commons.api.datasense.metadata.input.FileInputMetadataResolver;
import org.mule.metadata.api.model.MetadataFormat;

public class IDPStandardFileBinaryInputResolver extends FileInputMetadataResolver {

  private static final String OCTET_STREAM = "application/octet-stream";
  private static final String BINARY_NAME = "Binary";

  @Override
  public String getCategoryName() {
    return "Binary File Input"; // More descriptive category
  }

  @Override
  public MetadataFormat getFormat() {
    return new MetadataFormat(OCTET_STREAM, BINARY_NAME, OCTET_STREAM); // Corrected format
  }
}