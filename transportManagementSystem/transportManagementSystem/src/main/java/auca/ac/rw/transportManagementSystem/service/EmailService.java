package auca.ac.rw.transportManagementSystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendPlainText(String to, String subject, String body) {
        // Always print email content to console so it's always visible
        String separator = "=".repeat(80);
        String emailOutput = "\n" + separator + "\n" +
                "📧 EMAIL TO: " + to + "\n" +
                "📧 SUBJECT: " + subject + "\n" +
                "📧 BODY:\n" + body + "\n" +
                separator + "\n";
        
        // Print to console (always visible)
        System.out.println(emailOutput);
        System.out.flush();
        
        // Also log it
        log.info("\n" + separator);
        log.info("📧 EMAIL TO: {}", to);
        log.info("📧 SUBJECT: {}", subject);
        log.info("📧 BODY:\n{}", body);
        log.info(separator + "\n");

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            // SMTP not configured - email content is logged above for visibility
            log.warn("⚠️  Email not sent (SMTP not configured). Email content is visible in logs above.");
            return;
        }

        try {
            // Try to send as HTML if body contains HTML, otherwise plain text
            if (body.contains("<html") || body.contains("<a href") || body.contains("http://") || body.contains("https://")) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                
                // If body already contains HTML tags, use as-is, otherwise wrap in basic HTML
                if (body.contains("<html")) {
                    helper.setText(body, true);
                } else {
                    // Convert plain text to HTML, making links clickable
                    String htmlBody = convertPlainTextToHtml(body);
                    helper.setText(htmlBody, true);
                }
                
                mailSender.send(mimeMessage);
                log.info("✅ HTML email sent successfully to {}", to);
            } else {
                // Plain text email
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(to);
                msg.setSubject(subject);
                msg.setText(body);
                mailSender.send(msg);
                log.info("✅ Plain text email sent successfully to {}", to);
            }
        } catch (Exception e) {
            // Don't break core flows if email fails in dev
            log.warn("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String convertPlainTextToHtml(String plainText) {
        // Convert plain text to HTML, making URLs clickable
        String html = plainText
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        
        // Make URLs clickable
        html = html.replaceAll("(https?://[^\\s]+)", "<a href=\"$1\" style=\"color: #0066cc; text-decoration: underline;\">$1</a>");
        
        return "<html><body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
               "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
               html +
               "</div></body></html>";
    }
}

