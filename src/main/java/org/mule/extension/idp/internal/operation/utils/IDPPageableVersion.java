/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.utils;

import org.mule.extension.idp.api.IDPVersionSortOption;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * Parameter Group that contains all the parameter related to Pageable
 */
public class IDPPageableVersion {

  public IDPPageableVersion() {}

  public IDPPageableVersion(int page, int size, IDPVersionSortOption sort) {
    this.page = page;
    this.size = size;
    this.sort = sort;
  }

  /**
   * Page
   */
  @Parameter
  @DisplayName("Page (Zero Based)")
  @Example("0")
  @Summary("Pagination starting with the first page numbered 0")
  @Optional
  private int page;

  /**
   * size
   */
  @Parameter
  @DisplayName("Paging Size")
  @Example("20")
  @Summary("Number of records per page")
  @Optional
  private int size;

  /**
   * sort
   */
  @Parameter
  @DisplayName("Sort By")
  @Optional
  private IDPVersionSortOption sort;

  public int getPage() {return page;}

  public int getSize() {return size;}

  public String getSort() {
    if (sort != null) {
      return sort.getValue();
    } else {
      return null; // Or an empty string, or some default value
    }
  }

}
