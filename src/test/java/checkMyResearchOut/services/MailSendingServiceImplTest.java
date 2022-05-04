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
import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Rémi Venant
 */
@ActiveProfiles({"mail-test", "disable-security"})
@SpringBootTest(classes = {MailSendingServiceImpl.class, MailSenderAutoConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MailSendingServiceImplTest {

    private static SimpleSmtpServer server;

    @Autowired
    private JavaMailSender mailSender;

    private MailSendingServiceImpl mailSendingSvc;

    @Value("${checkMyResearchOut.mail.account-validation-url}")
    private String accountValidationUrl;

    @Value("${checkMyResearchOut.mail.password-renewal-url}")
    private String passwordRenewalUrl;

    @Value("${checkMyResearchOut.mail.reply-to}")
    private String replyTo;

    @Value("${spring.application.name}")
    private String applicationName;

    public MailSendingServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() throws IOException {
        server = SimpleSmtpServer.start(3333);
    }

    @AfterAll
    public static void tearDownClass() {
        server.stop();
    }

    @BeforeEach
    public void setUp() {
        this.mailSendingSvc = new MailSendingServiceImpl(mailSender, accountValidationUrl, passwordRenewalUrl, replyTo, applicationName);
    }

    @AfterEach
    public void tearDown() {
        server.reset();
    }

    /**
     * Test of sendAccountValidationMail method, of class MailSendingServiceImpl.
     */
    @Test
    public void testSendAccountValidationMail() throws Exception {
        System.out.println("sendAccountValidationMail");
        final String lastname = "bon";
        final String firstname = "jean";
        final String mail = "jean.bon@mail.com";
        final String token = "a-token-to-validate";

        CMROUser user = new CMROUser(mail, lastname, firstname, "encpass");
        user.setValidated(Boolean.FALSE);
        user.setValidationToken(token);
        user.setTokenEmissionDateTime(LocalDateTime.now());

        System.out.println("Send account validation mail");
        this.mailSendingSvc.sendAccountValidationMail(user);
        System.out.println("Check if mail was received");
        assertThat(server.getReceivedEmails()).hasSize(1);
        System.out.println("Check mail information");
        SmtpMessage messageRecevied = server.getReceivedEmails().get(0);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(this.replyTo);
        assertThat(messageRecevied.getHeaderValue("To")).isEqualTo(mail);
        assertThat(messageRecevied.getHeaderValue("Cc")).isNull();
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo("Validation de compte - " + this.applicationName);
        System.out.println("Account validation Mail body:");
        System.out.println(messageRecevied.getBody());
    }

    /**
     * Test of sendPasswordRenewalMail method, of class MailSendingServiceImpl.
     */
    @Test
    public void testSendPasswordRenewalMail() throws Exception {
        System.out.println("sendPasswordRenewalMail");
        final String lastname = "bon";
        final String firstname = "jean";
        final String mail = "jean.bon@mail.com";
        final String token = "a-token-to-validate";

        CMROUser user = new CMROUser(mail, lastname, firstname, "encpass");
        user.setValidated(Boolean.TRUE);
        user.setValidationToken(token);
        user.setTokenEmissionDateTime(LocalDateTime.now());

        System.out.println("Send password renewal mail");
        this.mailSendingSvc.sendPasswordRenewalMail(user);
        System.out.println("Check if mail was received");
        assertThat(server.getReceivedEmails()).hasSize(1);
        System.out.println("Check mail information");
        SmtpMessage messageRecevied = server.getReceivedEmails().get(0);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(this.replyTo);
        assertThat(messageRecevied.getHeaderValue("To")).isEqualTo(mail);
        assertThat(messageRecevied.getHeaderValue("Cc")).isNull();
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo("Renouvellement de mot de passe - " + this.applicationName);
        System.out.println("Password renewl mail body:");
        System.out.println(messageRecevied.getBody());
    }

}
