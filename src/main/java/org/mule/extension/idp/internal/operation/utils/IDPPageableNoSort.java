/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.utils;

import org.mule.extension.idp.api.IDPSortOption;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * Parameter Group that contains all the parameter related to Pageable
 */
public class IDPPageableNoSort {

  public IDPPageableNoSort() {}

  public IDPPageableNoSort(int page, int size) {
    this.page = page;
    this.size = size;
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


  public int getPage() {return page;}

  public int getSize() {return size;}
}
