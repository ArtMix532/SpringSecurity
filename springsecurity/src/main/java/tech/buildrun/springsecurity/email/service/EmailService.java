package tech.buildrun.springsecurity.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // O Spring injeta as duas ferramentas automaticamente
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEmailConfirmacao(String to, String nome, String linkConfirmacao) {
        try {
            Context context = new Context();
            context.setVariable("nome", nome);
            context.setVariable("linkConfirmacao", linkConfirmacao);

            String htmlBody = templateEngine.process("Verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("NavyStore - Confirme seu e-mail \uD83D\uDCE7");
            helper.setText(htmlBody, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar o e-mail de confirmação", e);
        }
    }
}