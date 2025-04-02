/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation.utils;

import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;

/**
 * Parameter Group that contains all the parameter related to Submit IDP Doc
 */
public class IDPSubmitDocOptions {

  public IDPSubmitDocOptions() {}

  public IDPSubmitDocOptions(String fileName, String callbackUrl, String headerXSfdcCoreTenantId) {
    this.fileName = fileName;
    this.callbackUrl = callbackUrl;
    this.headerXSfdcCoreTenantId = headerXSfdcCoreTenantId;
  }

  /**
   * Filename
   * Filename must have the file extension
   */
  @Parameter
  @Expression(SUPPORTED)
  @DisplayName("Filename [File Extension Required]")
  @Example("myFileName.pdf")
  @Summary("Filename must have the file extension")
  private String fileName;

  @Parameter
  @Expression(SUPPORTED)
  @DisplayName("Callback URL")
  @Example("https://my-cloudhub.io/api/v1/idp/callback?context=myCaseId&apikey=MzDUdfsfg")
  @Summary("Optional - You can define a callback URL when you call IDP to execute your document actions." +
          " If defined, IDP calls the callback URL when the document action execution finishes with states" +
          " https://docs.mulesoft.com/idp/automate-document-processing-with-the-idp-api#callback-url")
  @Optional
  private String callbackUrl;

  /**
   * Override Salesforce Org ID
   * Optional - If you have multiple Salesforce Orgs connected with your Anypoint Platform organization,
   * you can specify the Org to use when executing your document actions.
   * <a href="https://docs.mulesoft.com/idp/automate-document-processing-with-the-idp-api#use-a-different-salesforce-org-to-execute-your-document-actions">...</a>
   */
  @Parameter
  @Expression(SUPPORTED)
  @DisplayName("Override Salesforce Org ID")
  @Example("18-char-sfdc-org-id")
  @Summary("Optional - If you have multiple Salesforce Orgs connected with your Anypoint Platform organization," +
          " you can specify the Org to use when executing your document actions. " +
          "https://docs.mulesoft.com/idp/automate-document-processing-with-the-idp-api#use-a-different-salesforce-org-to-execute-your-document-actions")
  @Optional
  private String headerXSfdcCoreTenantId;



  public String getFileName() {return fileName;}

  public String getCallbackUrl() {return callbackUrl;}

  public String getHeaderXSfdcCoreTenantId() {return headerXSfdcCoreTenantId;}

}
