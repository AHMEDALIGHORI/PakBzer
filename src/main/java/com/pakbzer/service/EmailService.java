package com.pakbzer.service;

import com.pakbzer.config.SendGridProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final SendGridProperties properties;

    public EmailService(SendGridProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    public void sendOrderConfirmation(String toEmail, String subject, String body) {
        if (!isEnabled() || toEmail == null || toEmail.isBlank()) {
            return;
        }

        Email from = new Email(properties.getFrom());
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        try {
            SendGrid client = new SendGrid(properties.getApiKey());
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = client.api(request);
            if (response.getStatusCode() >= 400) {
                log.warn("SendGrid returned {} for order confirmation to {}", response.getStatusCode(), toEmail);
            }
        } catch (IOException ex) {
            log.warn("Failed to send order confirmation to {}: {}", toEmail, ex.getMessage());
        }
    }
}
