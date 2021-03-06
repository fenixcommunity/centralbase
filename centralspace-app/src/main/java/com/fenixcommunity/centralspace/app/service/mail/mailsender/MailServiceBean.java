package com.fenixcommunity.centralspace.app.service.mail.mailsender;

import static com.fenixcommunity.centralspace.app.service.document.converter.pdfconverter.HtmlPdfConverterStrategyType.THYMELEAF;
import static com.fenixcommunity.centralspace.utilities.validator.ValidatorType.MAIL;
import static lombok.AccessLevel.PRIVATE;

import com.fenixcommunity.centralspace.app.service.document.DocumentService;
import com.fenixcommunity.centralspace.utilities.common.FileFormat;
import com.fenixcommunity.centralspace.utilities.mail.MailBuilder;
import com.fenixcommunity.centralspace.utilities.mail.template.MailMessageTemplate;
import com.fenixcommunity.centralspace.utilities.resourcehelper.InternalResource;
import com.fenixcommunity.centralspace.utilities.resourcehelper.InternalResourceLoader;
import com.fenixcommunity.centralspace.utilities.validator.Validator;
import com.fenixcommunity.centralspace.utilities.validator.ValidatorFactory;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//TODO in all places should be interface with IBean?
@Service
@FieldDefaults(level = PRIVATE)
public class MailServiceBean implements MailService {
    private static final String TEMPLATE_BASIC_MAIL = "template_basic_mail";

    private final ApplicationContext context;
    private final JavaMailSender mailSender;
    private final Validator validator;
    private final InternalResourceLoader internalResourceLoader;
    private final DocumentService documentService;

    @Autowired @Qualifier("basicMailMessage")
    private MailMessageTemplate basicMailMessage;

    @Autowired @Qualifier("registrationMailMessage")
    private MailMessageTemplate registrationMailMessage;

    @Autowired
    MailServiceBean(final JavaMailSender mailSender, final ValidatorFactory validatorFactory,
                    final InternalResourceLoader internalResourceLoader, final DocumentService documentService,
                    final ApplicationContext context) {
        this.mailSender = mailSender;
        this.validator = validatorFactory.getInstance(MAIL);
        this.internalResourceLoader = internalResourceLoader;
        this.documentService = documentService;
        this.context = context;
    }

    @Async
    @Override
    public void sendBasicMail(@NonNull final String to) throws MailException {
        final MailMessageTemplate basicTemplate =  context.getBean("basicMailMessage", MailMessageTemplate.class);  // example
        final MailBuilder mailBuilder = new MailBuilder(basicMailMessage);
        mailBuilder.addTo(to);
        if (mailBuilder.isHtmlBody()) {
            mailBuilder.setBody(documentService.getHtmlBody(TEMPLATE_BASIC_MAIL, THYMELEAF));
        }
        sendBasicMail(mailBuilder);
    }

    @Async
    @Override
    public void sendRegistrationMailWithAttachment(@NonNull final String to) throws MailException {
        final MailBuilder mailBuilder = new MailBuilder(registrationMailMessage);
        mailBuilder.addTo(to);
        final InternalResource attachment = getRegistrationAttachment();
        sendMailWithAttachment(mailBuilder, attachment);
    }

    private void sendBasicMail(final MailBuilder mailBuilder) throws MailException {
        validateMailContent(mailBuilder);
        final MimeMessagePreparator messagePreparator = mimeMessage -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(mailBuilder.getToArray());
            helper.setSubject(mailBuilder.getSubject());
            helper.setText(mailBuilder.getBody(), mailBuilder.isHtmlBody());
            helper.setFrom(mailBuilder.getFrom());

        };

        mailSender.send(messagePreparator);
    }

    private void sendMailWithAttachment(final MailBuilder mailBuilder, final InternalResource attachment) throws MailException {
        validateMailContent(mailBuilder);
        final MimeMessagePreparator messagePreparator = mimeMessage -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(mailBuilder.getToArray());
            helper.setSubject(mailBuilder.getSubject());
            helper.setText(mailBuilder.getBody(), mailBuilder.isHtmlBody());
            helper.setFrom(mailBuilder.getFrom());

            if (attachment.fileExists()) {
                helper.addAttachment("Attachment", attachment.getContent().getFile());
            }
        };

        mailSender.send(messagePreparator);
    }

    private InternalResource getRegistrationAttachment() {
        final InternalResource attachment = InternalResource.resourceByNameAndType("attachment", FileFormat.PDF);
        attachment.setContent(internalResourceLoader.loadResourceFile(attachment));
        return attachment;
    }

    private void validateMailContent(final MailBuilder mailBuilder) {
        validator.validateWithException(mailBuilder);
    }
}
