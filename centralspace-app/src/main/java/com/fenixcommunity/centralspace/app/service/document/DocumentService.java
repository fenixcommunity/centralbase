package com.fenixcommunity.centralspace.app.service.document;

import static com.fenixcommunity.centralspace.app.service.document.converter.HtmlPdfConverterStrategyType.BASIC;
import static com.fenixcommunity.centralspace.app.service.document.converter.HtmlPdfConverterStrategyType.THYMELEAF;
import static java.util.Collections.singletonMap;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import com.fenixcommunity.centralspace.app.configuration.restcaller.RestCallerStrategy;
import com.fenixcommunity.centralspace.app.service.security.SecurityService;
import com.fenixcommunity.centralspace.app.service.document.converter.BasicPdfConverter;
import com.fenixcommunity.centralspace.app.service.document.converter.HtmlPdfConverterStrategy;
import com.fenixcommunity.centralspace.app.service.document.converter.HtmlPdfConverterStrategyType;
import com.fenixcommunity.centralspace.app.service.document.converter.IPdfConverter;
import com.fenixcommunity.centralspace.app.service.document.converter.ThymeleafPdfConverter;
import com.fenixcommunity.centralspace.app.service.document.pdfcreator.IPdfCreator;
import com.fenixcommunity.centralspace.app.service.document.pdfcreator.ITextPdfCreator;
import com.fenixcommunity.centralspace.utilities.common.FileFormat;
import com.fenixcommunity.centralspace.utilities.resourcehelper.ResourceLoaderTool;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Log4j2
@Service
@AllArgsConstructor(access = PACKAGE) @FieldDefaults(level = PRIVATE, makeFinal = true)
public class DocumentService {
//todo to interface

    private final ResourceLoaderTool resourceTool;
    private final TemplateEngine templateEngine;
    private final SecurityService securityService;

    private final RestCallerStrategy restCallerStrategy;

    public void createPdf(final String pdfFileName) {
        final IPdfCreator pdfCreator = new ITextPdfCreator(pdfFileName, resourceTool);
        pdfCreator.createPdf();
    }

    public void convertPdfToImage(final String pdfFileName, final FileFormat fileFormat) {
        final IPdfConverter converter = new BasicPdfConverter(pdfFileName, resourceTool);
        converter.convertPdfToImage(fileFormat);
    }

    public void convertImageToPdfAsAdminByWebClientAndRestTemplate(final String imageFileName, final FileFormat fileFormat) {
        final IPdfConverter converter = new BasicPdfConverter(imageFileName, resourceTool);
        if (securityService.isValidSecurityRole()) {
            converter.convertImageToPdf(fileFormat, restCallerStrategy);
        }
    }

    public void convertHtmlToPdf(final String htmlFileName, final HtmlPdfConverterStrategyType strategyType) {
        if (THYMELEAF == strategyType) {
            final Map<String, String> thymeleafVariables = singletonMap("imageUrl",  resourceTool.getAbsoluteImagePath());
            final HtmlPdfConverterStrategy converter = new ThymeleafPdfConverter(htmlFileName, thymeleafVariables, templateEngine, resourceTool);
            converter.convertHtmlToPdf();
        } else if (BASIC == strategyType) {
            final HtmlPdfConverterStrategy converter = new BasicPdfConverter(htmlFileName, resourceTool);
            converter.convertHtmlToPdf();
        }
    }

    public void convertPdfToHtml(final String pdfFileName, final HtmlPdfConverterStrategyType strategyType) {
        if (THYMELEAF == strategyType) {
            final HtmlPdfConverterStrategy converter = new ThymeleafPdfConverter(pdfFileName, null, templateEngine, resourceTool);
            converter.convertPdfToHtml();
        } else if (BASIC == strategyType) {
            final HtmlPdfConverterStrategy converter = new BasicPdfConverter(pdfFileName, resourceTool);
            converter.convertPdfToHtml();
        }
    }

    public String getHtmlBody(final String htmlFileName, final HtmlPdfConverterStrategyType strategyType) {
        if (THYMELEAF == strategyType) {
            final Map<String, String> thymeleafVariables = singletonMap("imageUrl", resourceTool.getAbsoluteImagePath());
            final HtmlPdfConverterStrategy converter = new ThymeleafPdfConverter(htmlFileName, thymeleafVariables, templateEngine, resourceTool);
            return converter.getHtmlBody();
        } else if (BASIC == strategyType) {
            final HtmlPdfConverterStrategy converter = new BasicPdfConverter(htmlFileName, resourceTool);
            return converter.getHtmlBody();
        }
        return "";
    }

    public void convertPdfToText(final String pdfFileName) {
        final IPdfConverter converter = new BasicPdfConverter(pdfFileName, resourceTool);
        converter.convertPdfToText();
    }

    public void convertTextToPdf(final String textFileName) {
        final IPdfConverter converter = new BasicPdfConverter(textFileName, resourceTool);
        converter.convertTextToPdf();
    }

    public void convertPdfToDocx(final String pdfFileName) {
        final IPdfConverter converter = new BasicPdfConverter(pdfFileName, resourceTool);
        converter.convertPdfToDocx();
    }

}