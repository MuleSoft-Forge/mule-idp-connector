/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.operation;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.mule.extension.idp.internal.connection.IDPAuthentication;
import org.mule.extension.idp.internal.connection.IDPConnection;
import org.mule.extension.idp.internal.error.IDPErrorProvider;
import org.mule.extension.idp.internal.metadata.IDPUtilsInputAnyJsonResolver;
import org.mule.extension.idp.internal.metadata.IDPUtilsOutputAnyJsonResolver;
import org.mule.extension.idp.internal.operation.utils.IDPOperationsUtils;
import org.mule.extension.idp.internal.operation.utils.IDPUtilsPdfOptions;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.TypeResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mule.runtime.api.meta.ExpressionSupport.REQUIRED;

public class IDPUtilsPDFOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(IDPUtilsPDFOperations.class);

    @DisplayName("Utils IDP - HTTP - Request")
    @Summary("Utils HTTP Request Reusing your Connectivity" +
            "<ul>" +
            "<li> Problem:; New resources available but connector not updated" +
            "so you have to use HTTP Request and redo Connectivity and maintain in two places" +
            "<li> Solution: Use this!" +
            "</ul>")
    @MediaType(value = MediaType.APPLICATION_JSON, strict = false) // https://docs.mulesoft.com/mule-sdk/latest/return-media-type#variable-media-type
    @Throws(IDPErrorProvider.class)
    @OutputResolver(output = IDPUtilsOutputAnyJsonResolver.class)
    public void utilsHttpRequest(@Connection IDPConnection connection,
                                   @NotNull
                                        @DisplayName("HTTP Method")
                                        @Example("POST")
                                        @Summary("Compose your http call by choosing what method to use")
                                        HttpConstants.Method httpMethods,
                                   @NotNull
                                        @DisplayName("Full URL")
                                        @Example("https://anypoint.mulesoft.com/idp/api/v1/organizations/01f79c97-9a83-4194-b922-15b64fd7305e/actions?page=0&size=20&sort=updatedAt%2Cdesc")
                                        @Summary("Compose your http call by providing the full url")
                                        String url,
                                   @Optional(defaultValue="#[payload]")
                                        @DisplayName("JSON Payload")
                                        @Expression(REQUIRED) @TypeResolver(IDPUtilsInputAnyJsonResolver.class) TypedValue<InputStream> jsonContents,
                                   CompletionCallback<InputStream, Void> completionCallback) throws ModuleException {

        Map<String, String> uriParameters = new HashMap<>();
        String uri = IDPOperationsUtils.createEndpoint(url, uriParameters);

        connection.sendRequestNonBlocking(requestBuilder -> {
                requestBuilder.addHeader("Content-Type", "application/json");
                if (httpMethods != HttpConstants.Method.GET) {
                    requestBuilder.entity(new InputStreamHttpEntity(jsonContents.getValue()));
                }
            }, uri, httpMethods, IDPAuthentication.BASIC_AUTH, "")
                .whenCompleteAsync(IDPOperationsUtils.createCompletionHandler(completionCallback, uri));
    }

    @DisplayName("Utils IDP - PDF - ExtractText")
    @MediaType(MediaType.TEXT_PLAIN)
    @Throws(IDPErrorProvider.class)
    public String utilsPdfExtractText(
            @DisplayName("PDF Pages to Include")
                @Example("1,2,4,6,12")
                @Optional
                @Summary("Comma separated list of pages to be included as new PDF") String pdfCsvPages,
            @Content TypedValue<InputStream> pdfFile
    ) throws IOException, NumberFormatException, IllegalArgumentException {

        LOGGER.info("Starting PDF text extraction...");

        StringBuilder extractedText = new StringBuilder();
        try {
            InputStream inputStream = pdfFile.getValue();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            LOGGER.debug("Reading InputStream into byte array...");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            LOGGER.info("Finished reading InputStream. PDF size: " + pdfBytes.length + " bytes");

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                LOGGER.info("PDF loaded successfully.");
                PDFTextStripper textStripper = new PDFTextStripper();

                if (pdfCsvPages != null && !pdfCsvPages.isEmpty()) {
                    String[] pageNumbers = pdfCsvPages.split(",");
                    for (String pageNumStr : pageNumbers) {
                        int pageNum = Integer.parseInt(pageNumStr.trim());
                        if (pageNum > 0 && pageNum <= document.getNumberOfPages()) {
                            textStripper.setStartPage(pageNum);
                            textStripper.setEndPage(pageNum);
                            extractedText.append(textStripper.getText(document));
                        } else {
                            throw new IllegalArgumentException("Invalid page number: " + pageNum);
                        }
                    }
                } else {
                    extractedText.append(textStripper.getText(document));
                }

                LOGGER.info("Extracted PDF Text:");
                LOGGER.debug(extractedText.toString());
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid page number format: " + e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }

        LOGGER.info("PDF text extraction completed successfully.");
        return extractedText.toString();
    }

    @DisplayName("Utils IDP - PDF - RemovePages")
    @MediaType(MediaType.APPLICATION_OCTET_STREAM)
    @Throws(IDPErrorProvider.class)
    public Result<InputStream, Map<String, Object>> utilsPdfRemovePages(
            @ParameterGroup(name = "Remove Options [Only one choice allowed]") IDPUtilsPdfOptions pdfOptions,
            @Content TypedValue<InputStream> pdfFile
    ) throws IOException, IllegalArgumentException {

        LOGGER.info("Starting PDF page removal...");

        try {
            InputStream inputStream = pdfFile.getValue();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            LOGGER.debug("Reading InputStream into byte array...");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            LOGGER.info("Finished reading InputStream. PDF size: " + pdfBytes.length + " bytes");

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                int originalPageCount = document.getNumberOfPages();
                LOGGER.info("Original page count: " + originalPageCount);

                if (Objects.equals(pdfOptions.getRemoveBlankPages(), "yes")) {
                    List<PDPage> blankPages = new ArrayList<>();
                    PDPageTree pages = document.getPages();

                    for (PDPage page : pages) {
                        if (isPageBlank(page, document)) {
                            blankPages.add(page);
                        }
                    }

                    for (PDPage page : blankPages) {
                        document.removePage(page);
                    }

                    int finalPageCount = document.getNumberOfPages();
                    int pagesRemoved = originalPageCount - finalPageCount;
                    LOGGER.info("Pages removed: " + pagesRemoved);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    document.save(outputStream);
                    LOGGER.info("Blank PDF pages removed successfully.");
                    return Result.<InputStream, Map<String, Object>>builder()
                            .output(new ByteArrayInputStream(outputStream.toByteArray()))
                            .attributes(createAttributes(originalPageCount, pagesRemoved))
                            .build();
                } else {
                    List<Integer> pagesToKeep = new ArrayList<>();
                    String pdfCsvPages = pdfOptions.getPdfCsvPages();
                    if (pdfCsvPages != null && !pdfCsvPages.isEmpty()) {
                        String[] pageNumbers = pdfCsvPages.split(",");
                        for (String pageNumStr : pageNumbers) {
                            int pageNum = Integer.parseInt(pageNumStr.trim());
                            if (pageNum > 0 && pageNum <= document.getNumberOfPages()) {
                                pagesToKeep.add(pageNum);
                            } else {
                                throw new IllegalArgumentException("Invalid page number: " + pageNum);
                            }
                        }
                    } else {
                        for (int i = 1; i <= document.getNumberOfPages(); i++) {
                            pagesToKeep.add(i);
                        }
                    }

                    List<PDPage> pagesToRemove = new ArrayList<>();
                    for (int i = 0; i < document.getNumberOfPages(); i++) {
                        if (!pagesToKeep.contains(i + 1)) {
                            pagesToRemove.add(document.getPage(i));
                        }
                    }

                    for (PDPage page : pagesToRemove) {
                        document.removePage(page);
                    }

                    int finalPageCount = document.getNumberOfPages();
                    int pagesRemoved = originalPageCount - finalPageCount;
                    LOGGER.info("Pages removed: " + pagesRemoved);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    document.save(outputStream);
                    LOGGER.info("Unwanted PDF pages removed successfully.");
                    return Result.<InputStream, Map<String, Object>>builder()
                            .output(new ByteArrayInputStream(outputStream.toByteArray()))
                            .attributes(createAttributes(originalPageCount, pagesRemoved))
                            .build();
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Invalid page number format: {}", e.getMessage(), e);
                throw e;
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            } catch (IOException e) {
                LOGGER.error("Error processing PDF: {}", e.getMessage(), e);
                throw e;
            }

        } catch (IOException e) {
            LOGGER.error("Error reading input stream: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static Map<String, Object> createAttributes(int originalPageCount, int pagesRemoved) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("originalPageCount", originalPageCount);
        attributes.put("pagesRemoved", pagesRemoved);
        return attributes;
    }

    private static boolean isPageBlank(PDPage page, PDDocument document) throws IOException {
        // Check for text content
        PDFTextStripper textStripper = new PDFTextStripper();
        int pageIndex = document.getPages().indexOf(page) + 1;
        textStripper.setStartPage(pageIndex);
        textStripper.setEndPage(pageIndex);
        String pageText = textStripper.getText(document);
        if (pageText != null && !pageText.trim().isEmpty()) {
            return false; // Page has text, not blank
        }

        // Check for images and other XObject resources
        if (page.getResources() != null && page.getResources().getXObjectNames() != null) {
            if (page.getResources().getXObjectNames().iterator().hasNext()) {
                return false; // Page has resources (e.g., images), not blank
            }
        }

        // ✅ Check for annotations (links, form fields, comments)
        if (page.getAnnotations() != null && !page.getAnnotations().isEmpty()) {
            return false; // Page has annotations, not blank
        }

        // If no text, resources, or annotations, we consider the page blank
        return true;
    }
}