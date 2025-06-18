package dev.sleypner.asparser.service.core.auth.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    Environment env;

    public EmailServiceImpl(Environment env) {
        this.env = env;
    }

    public boolean sendCode(String email, int code) {

        MimeMessage message = new MimeMessage(getMailSession(getMailProperties()));
        try {
            String from = env.getProperty("smtp.mail.from");
            message.setFrom(new InternetAddress(from == null ? "AsParser" : from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(getSubject());

            String msg = getMessage("", code);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            Transport.send(message);

            return true;
        } catch (MessagingException e) {

            logger.info(e.getMessage());

            return false;
        }

    }

    public Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", env.getProperty("spring.mail.host"));
        props.put("mail.smtp.port", env.getProperty("spring.mail.port"));
//        props.put("mail.smtp.ssl.trust", env.getProperty("sandbox.smtp.mailtrap.io"));
        return props;
    }

    public Session getMailSession(Properties props) {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        env.getProperty("spring.mail.username"),
                        env.getProperty("spring.mail.password")
                );
            }
        });
    }

    public String getSubject() {
        return "Your verification code";
    }

    public String getMessage(String username, int code) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Your AsParser Verification Code</title>\n" +
                "</head>\n" +
                "<body style=\"background-color: #f7f7f8; margin: 0; padding: 40px 0; font-family: 'Open Sans',Helvetica,Arial,sans-serif;\">\n" +
                "    <div style=\"max-width: 500px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); overflow: hidden;\">\n" +
                "        <div style=\"text-align: center; padding: 20px 0 10px 0; border-bottom: 3px solid #4da300;\">\n" +
                "           <img src=\"https://asparser.com/logo.png\" alt=\"AsParser Logo\" style=\"max-width: 150px;\">" +
                "        </div>\n" +
                "        <div style=\"padding: 25px;\">\n" +
                "            <h1 style=\"color: #2c3e50; margin-top: 0;\">AsParser</h1>" +
                "            <h2 style=\"color: #2c3e50; margin-top: 0;\">Verification needed</h2>" +
                "            <p style=\"line-height: 1.6; color:black; font-size:16px;\" style>Please confirm your sign-up request</p>" +
                "            <p style=\"line-height: 1.6; color:#6d6f70; font-size:16px;\">Thank you for signing up on <strong>AsParser</strong>! Here's your 6-digit verification code:</p>\n" +
                "            <div style=\"font-size: 30px; letter-spacing: 5px; background: #f5f5f5; padding: 15px; text-align: center; margin: 20px 0; border-radius: 4px; font-weight: bold; color: #2c3e50;\">\n" +
                "                " + code + "\n" +
                "            </div>\n" +
                "            <p style=\"line-height: 1.6; color:#6d6f70; font-size:16px\">Enter this code on the <a href=\"#\" style=\"color: #4da300; text-decoration: none; font-size:16px font-weight: bold;\">verification page</a> to complete your registration. The code will expire in 30 minutes.</p>\n" +
                "            <p style=\"line-height: 1.6; color:#6d6f70;  font-size:16px; font-style: italic;\">If you didn't request this, please <a href=\"#\" style=\"color: #4da300; font-size:16px text-decoration: none; font-weight: bold;\">contact support</a>.</p>\n" +
                "        </div>\n" +
                "        <div style=\"margin-top: 20px; font-size: 14px; color: #777; text-align: center; padding: 15px; background: #f9f9f9;\">\n" +
                "            <p style=\"margin: 0;\">Best regards,<br>The AsParser Team</p>\n" +
                "            <p style=\"margin: 10px 0 0 0;\">© 2025 AsParser. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
