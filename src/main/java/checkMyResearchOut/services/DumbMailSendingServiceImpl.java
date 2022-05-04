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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Rémi Venant
 */
@Profile("fake-mail")
@Service
public class DumbMailSendingServiceImpl implements MailSendingService {

    private static final Log LOG = LogFactory.getLog(DumbMailSendingServiceImpl.class);

    private final String accountValidationUrl;

    private final String passwordRenewalUrl;

    @Autowired
    public DumbMailSendingServiceImpl(
            @Value("${checkMyResearchOut.mail.account-validation-url}") String accountValidationUrl,
            @Value("${checkMyResearchOut.mail.password-renewal-url}") String passwordRenewalUrl) {
        this.accountValidationUrl = accountValidationUrl;
        this.passwordRenewalUrl = passwordRenewalUrl;
    }

    @Override
    public void sendAccountValidationMail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        if (user.getValidationToken() == null) {
            throw new IllegalArgumentException("No validation token present for user.");
        }
        final String accessUrl = this.forgeUrl(this.accountValidationUrl, user.getMail(), user.getValidationToken());
        LOG.info(String.format("---- SENDING VALIDATION ACCOUNT MAIL TO %s ----", user.getMail()));
        LOG.info("url is: " + accessUrl);
        LOG.info("-----------------------------------------------");
    }

    @Override
    public void sendPasswordRenewalMail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        if (user.getValidationToken() == null) {
            throw new IllegalArgumentException("No renewal password token present for user.");
        }
        final String accessUrl = this.forgeUrl(this.passwordRenewalUrl, user.getMail(), user.getValidationToken());
        LOG.info(String.format("---- SENDING PASSWORD RENEWAL MAIL TO %s ----", user.getMail()));
        LOG.info("url is: " + accessUrl);
        LOG.info("-----------------------------------------------");
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

}
