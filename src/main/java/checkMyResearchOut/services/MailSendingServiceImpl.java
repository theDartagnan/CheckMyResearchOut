/*
 * Copyright (C) 2022 ATIEF.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package checkMyResearchOut.services;

import checkMyResearchOut.mongoModel.CMROUser;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author Rémi Venant
 */
@Service
public class MailSendingServiceImpl implements MailSendingService {

    private static final Log LOG = LogFactory.getLog(MailSendingServiceImpl.class);

    private final JavaMailSender mailSender;

    private final String accountValidationUrl;

    private final String passwordRenewalUrl;

    private final String replyToAddress;

    @Autowired
    public MailSendingServiceImpl(JavaMailSender mailSender,
            @Value("${checkMyResearchOut.mail.account-validation-url}") String accountValidationUrl,
            @Value("${checkMyResearchOut.mail.password-renewal-url}") String passwordRenewalUrl,
            @Value("${checkMyResearchOut.mail.reply-to}") String replyToAddress) {
        this.mailSender = mailSender;
        this.accountValidationUrl = accountValidationUrl;
        this.passwordRenewalUrl = passwordRenewalUrl;
        this.replyToAddress = replyToAddress;
    }

    @Override
    public void sendAccountValidationMail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        if (user.getValidationToken() == null) {
            throw new IllegalArgumentException("No validation token present for user.");
        }
        try {
            final String accessUrl = this.forgeUrl(this.accountValidationUrl, user.getMail(), user.getValidationToken());
            final String mailSubject = String.format("Validation de compte - Viens voir mes recherches");
            final String body = BODY_HTML_HEADER
                    + String.format("<p>Bonjour %s %s,</p>", StringUtils.capitalize(user.getFirstname()), StringUtils.capitalize(user.getLastname()))
                    + "<p>Merci de rejoindre ce jeu en ligne, veuillez trouver ici le lien de validation de votre compte :<br/>"
                    + String.format("<a href=\"%s\">%s</a>.", accessUrl, accessUrl)
                    + "</p>"
                    + "<p>Ce lien est valide pendant 30 minutes. Au delai de ce délai, il faudra le renouveller.</p>"
                    + BODY_HTML_FOOTER;
            this.mailSender.send(this.createRawMessage(user.getMail(), this.replyToAddress, mailSubject, body));
        } catch (MessagingException ex) {
            LOG.error("Unable to create validation mail.", ex);
            throw ex;
        } catch (MailException ex) {
            LOG.error("Unable to send validation mail.", ex);
            throw ex;
        }
    }

    @Override
    public void sendPasswordRenewalMail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        if (user.getValidationToken() == null) {
            throw new IllegalArgumentException("No renewal password token present for user.");
        }
        try {
            final String accessUrl = this.forgeUrl(this.passwordRenewalUrl, user.getMail(), user.getValidationToken());
            final String mailSubject = String.format("Renouvellement de mot de passe - Viens voir mes recherches");
            final String body = BODY_HTML_HEADER
                    + String.format("<p>Bonjour %s %s,</p>", StringUtils.capitalize(user.getFirstname()), StringUtils.capitalize(user.getLastname()))
                    + "<p>Vous avez demandé de renouveller votre mot de passe, veuillez trouver ici le lien d&lsquo;accès :<br/>"
                    + String.format("<a href='%s'>%s</a>.", accessUrl, accessUrl)
                    + "</p>"
                    + "<p>Si vous n&lsquo;êtes pas à l&lsquot;origine de cette demande, merci de ne pas en tenir compte.</p>"
                    + "<p>Ce lien est valide pendant 30 minutes. Au delai de ce délai, il faudra le renouveller.</p>"
                    + BODY_HTML_FOOTER;
            this.mailSender.send(this.createRawMessage(user.getMail(), this.replyToAddress, mailSubject, body));
        } catch (MessagingException ex) {
            LOG.error("Unable to create validation mail.", ex);
            throw ex;
        } catch (MailException ex) {
            LOG.error("Unable to send validation mail.", ex);
            throw ex;
        }
    }

    private String forgeUrl(String url, String mail, String token) throws MessagingException {
        try {
            String urlBase = url;
            if (urlBase.endsWith("/")) {
                urlBase = urlBase.substring(0, urlBase.length() - 1);
            }
            String encodedMail = URLEncoder.encode(mail, StandardCharsets.UTF_8.toString());
            return String.format("%s?m=%s&t=%s", urlBase, encodedMail, token);
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Cannot forge url", ex);
            throw new MessagingException("Cannot encode validation url");
        }
    }

    private MimeMessage createRawMessage(String recipient, String replyTo, String subject,
            String body) throws MessagingException {
        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setTo(recipient);
        helper.setFrom(replyTo);
        helper.setSubject(subject);
        helper.setText(body, true);
        return message;
    }

    private static final String BODY_HTML_HEADER = "<!DOCTYPE html>"
            + "<html lang=\"fr\">"
            + "<head>"
            + "<meta charset=\"utf-8\">"
            + "</head>"
            + "<body>";

    private static final String BODY_HTML_FOOTER = "<p>"
            + "Cordialement,"
            + "<br/><br/>"
            + "L&lsquo;application Viens voir mes recherches."
            + "</p>"
            + "</body>"
            + "</html>";

}
