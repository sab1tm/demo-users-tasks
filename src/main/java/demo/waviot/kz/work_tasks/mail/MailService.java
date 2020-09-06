package demo.waviot.kz.work_tasks.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    private final Configuration templateConfiguration;

    @Value("${app.velocity.templates.location}")
    private String basePackagePath;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    public MailService(JavaMailSender mailSender, Configuration templateConfiguration) {
        this.mailSender = mailSender;
        this.templateConfiguration = templateConfiguration;
    }

    public void sendPasswordResetCode(String resetCode, String to)
            throws IOException, TemplateException, MessagingException {
        MailEntity mailEntity = new MailEntity();
        mailEntity.setSubject("Password Reset Code");
        mailEntity.setTo(to);
        mailEntity.setFrom(mailFrom);
        mailEntity.getModel().put("userName", to);
        mailEntity.getModel().put("resetCode", resetCode);

        templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
        Template template = templateConfiguration.getTemplate("reset-code.ftl");
        String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailEntity.getModel());
        mailEntity.setContent(mailContent);
        send(mailEntity);
    }

    public void send(MailEntity mailEntity) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(mailEntity.getTo());
        helper.setText(mailEntity.getContent(), true);
        helper.setSubject(mailEntity.getSubject());
        helper.setFrom(mailEntity.getFrom());
        mailSender.send(message);
    }

}
