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
import checkMyResearchOut.mongoModel.CMROUserAnswerRepository;
import checkMyResearchOut.mongoModel.CMROUserRepository;
import checkMyResearchOut.security.services.PasswordEncodingService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.mockito.AdditionalAnswers;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Rémi Venant
 */
public class CMROUserServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private CMROUserRepository userRepo;

    @Mock
    private CMROUserAnswerRepository answerRepo;

    @Mock
    private PasswordEncodingService passwordSvc;

    @Mock
    private MailSendingService mailSendingSvc;

    private CMROUserServiceImpl userSvcImpl;

    public CMROUserServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.mocks = MockitoAnnotations.openMocks(this);
        this.userSvcImpl = new CMROUserServiceImpl(userRepo, answerRepo, passwordSvc, mailSendingSvc, 30);
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.mocks.close();
    }

    /**
     * Test of createUser method, of class CMROUserServiceImpl.
     */
    @Test
    public void testCreateUserOK() {
        System.out.println("createUser");
        String mail = "mail@maIl.com";
        String lastname = "lnAmE  ";
        String firstname = " fnaMe";
        String clearPassword = "a password  ";
        String encodedPassword = "encodedPassword";

        given(this.passwordSvc.encodePassword(clearPassword)).willReturn("encodedPassword");
        given(this.userRepo.save(Mockito.any(CMROUser.class))).will(AdditionalAnswers.returnsFirstArg());

        CMROUser result = this.userSvcImpl.createUser(mail, lastname, firstname, clearPassword);

        Mockito.verify(this.passwordSvc, Mockito.times(1)).encodePassword(clearPassword);
        Mockito.verify(this.userRepo, Mockito.times(1)).save(result);
        assertThat(result).as("User has proper properties")
                .extracting("mail", "lastname", "firstname", "password", "validated", "admin")
                .containsExactly("mail@mail.com", "lname", "fname", encodedPassword, false, false);
    }

    /**
     * Test of getUserByMail method, of class CMROUserServiceImpl.
     */
    @Test
    public void testGetUserByMailOK() {
        System.out.println("getUserByMail");
        String mail = "mail@mail.com";
        CMROUser expectedUser = new CMROUser(mail, "lname", "fname", "encodedPass");

        given(this.userRepo.findByMail(mail)).willReturn(Optional.of(expectedUser));

        CMROUser result = this.userSvcImpl.getUserByMail(mail);
        assertThat(result).isSameAs(expectedUser);
    }

    /**
     * Test of sendValidationEmail method, of class CMROUserServiceImpl.
     */
    @Test
    public void testSendValidationEmailOK() throws Exception {
        System.out.println("sendValidationEmail");
        CMROUser user = new CMROUser("mail@mail.com", "lname", "fname", "encodedPass");
        user.setValidated(Boolean.FALSE);
        user.setValidationToken(null);
        user.setTokenEmissionDateTime(null);

        given(this.userRepo.save(Mockito.any(CMROUser.class))).will(AdditionalAnswers.returnsFirstArg());

        this.userSvcImpl.sendValidationEmail(user);

        Mockito.verify(this.userRepo, Mockito.times(1)).save(user);
        Mockito.verify(this.mailSendingSvc, Mockito.times(1)).sendAccountValidationMail(user);
        assertThat(user).as("User has not null validation info")
                .extracting("validationToken", "tokenEmissionDateTime")
                .doesNotContainNull();
        assertThat(user.getValidated()).as("User is still not validated").isFalse();
    }

    /**
     * Test of validate method, of class CMROUserServiceImpl.
     */
    @Test
    public void testValidateOK() {
        System.out.println("validate");
        String validationToken = "val-token";
        CMROUser user = new CMROUser("mail@mail.com", "lname", "fname", "encodedPass");
        user.setValidated(Boolean.FALSE);
        user.setValidationToken(validationToken);
        user.setTokenEmissionDateTime(LocalDateTime.now().minusMinutes(3));

        given(this.userRepo.save(Mockito.any(CMROUser.class))).will(AdditionalAnswers.returnsFirstArg());

        this.userSvcImpl.validate(user, validationToken);

        Mockito.verify(this.userRepo, Mockito.times(1)).save(user);
        assertThat(user).as("User has null validation info")
                .extracting("validationToken", "tokenEmissionDateTime")
                .containsOnlyNulls();
        assertThat(user.getValidated()).as("User is validated").isTrue();
    }

    /**
     * Test of sendChangePasswordEmail method, of class CMROUserServiceImpl.
     */
    @Test
    public void testSendChangePasswordEmailOK() throws Exception {
        System.out.println("sendChangePasswordEmail");
        CMROUser user = new CMROUser("mail@mail.com", "lname", "fname", "encodedPass");
        user.setValidated(Boolean.TRUE);
        user.setValidationToken(null);
        user.setTokenEmissionDateTime(null);

        given(this.userRepo.save(Mockito.any(CMROUser.class))).will(AdditionalAnswers.returnsFirstArg());

        this.userSvcImpl.sendChangePasswordEmail(user);

        Mockito.verify(this.userRepo, Mockito.times(1)).save(user);
        Mockito.verify(this.mailSendingSvc, Mockito.times(1)).sendPasswordRenewalMail(user);
        assertThat(user).as("User has not null validation info")
                .extracting("validationToken", "tokenEmissionDateTime")
                .doesNotContainNull();
    }

    /**
     * Test of changePassword method, of class CMROUserServiceImpl.
     */
    @Test
    public void testChangePasswordOK() {
        System.out.println("changePassword");
        String validationToken = "val-token";
        String newClearPassword = "newPAss";
        String newEncodedPassword = "newEncodedPass";
        CMROUser user = new CMROUser("mail@mail.com", "lname", "fname", "encodedPass");
        user.setValidated(Boolean.TRUE);
        user.setValidationToken(validationToken);
        user.setTokenEmissionDateTime(LocalDateTime.now().minusMinutes(3));

        given(this.passwordSvc.encodePassword(newClearPassword)).willReturn(newEncodedPassword);
        given(this.userRepo.save(Mockito.any(CMROUser.class))).will(AdditionalAnswers.returnsFirstArg());

        this.userSvcImpl.changePassword(user, validationToken, newClearPassword);

        Mockito.verify(this.passwordSvc, Mockito.times(1)).encodePassword(newClearPassword);
        Mockito.verify(this.userRepo, Mockito.times(1)).save(user);

        assertThat(user).as("User has null validation info")
                .extracting("validationToken", "tokenEmissionDateTime")
                .containsOnlyNulls();
        assertThat(user.getPassword()).as("User has changed password").isEqualTo(newEncodedPassword);
    }

    /**
     * Test of patchUser method, of class CMROUserServiceImpl.
     */
    @Test
    public void testPatchUserOK() {
        System.out.println("patchUser");
        CMROUser user = new CMROUser("mail@mail.com", "lname", "fname", "encodedPass");
        user.setValidated(Boolean.TRUE);

        String newLastname = " newLname";
        String newFirstname = null;
        String newClearPassword = "newClearPass";
        String newEncodedPassword = "newEncodedPass";

        given(this.passwordSvc.encodePassword(newClearPassword)).willReturn(newEncodedPassword);
        given(this.userRepo.save(Mockito.any(CMROUser.class))).will(AdditionalAnswers.returnsFirstArg());

        CMROUser result = this.userSvcImpl.patchUser(user, newLastname, newFirstname, newClearPassword);

        Mockito.verify(this.passwordSvc, Mockito.times(1)).encodePassword(newClearPassword);
        Mockito.verify(this.userRepo, Mockito.times(1)).save(user);

        assertThat(result).as("User has proper properties")
                .extracting("mail", "lastname", "firstname", "password")
                .containsExactly("mail@mail.com", "newlname", "fname", newEncodedPassword);
    }

    /**
     * Test of delete method, of class CMROUserServiceImpl.
     */
    @Test
    public void testDeleteUserOK() {
        System.out.println("delete");
        CMROUser user = new CMROUser("mail@mail.com", "lname", "fname", "encodedPass");
        user.setValidated(Boolean.TRUE);

        this.userSvcImpl.deleteUser(user);

        Mockito.verify(this.userRepo, Mockito.times(1)).delete(user);
        Mockito.verify(this.answerRepo, Mockito.times(1)).deleteByUser(user);
    }

}
