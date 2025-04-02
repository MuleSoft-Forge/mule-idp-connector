/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.utils;

import org.mule.extension.idp.api.IDPUtilsRemoveBlankOption;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * Parameter Group that contains all the parameter related to Pageable
 */
@ExclusiveOptionals(isOneRequired = true)
public class IDPUtilsPdfOptions {

  public IDPUtilsPdfOptions() {}

  public IDPUtilsPdfOptions(IDPUtilsRemoveBlankOption removeBlankPages, String pdfCsvPages) {
    this.removeBlankPages = removeBlankPages;
    this.pdfCsvPages = pdfCsvPages;
  }

  /**
   * Remove Blank Pages
   */
  @Parameter
  @DisplayName("Remove Blank Pages")
  @Summary("Remove Blank Pages using PDFBox testing for text and images present on each page")
  @Optional
  private IDPUtilsRemoveBlankOption removeBlankPages;

  /**
   * PDF Pages to Include
   */
  @Parameter
  @DisplayName("PDF Pages to Include")
  @Example("1,2,4,6,12")
  @Summary("Comma separated list of pages to be included in the new PDF")

  @Optional
  private String pdfCsvPages;

  public String getPdfCsvPages() {return pdfCsvPages;}

  public String getRemoveBlankPages() {
    if (removeBlankPages != null) {
      return removeBlankPages.getValue();
    } else {
      return null; // Or an empty string, or some default value
    }
  }

}

