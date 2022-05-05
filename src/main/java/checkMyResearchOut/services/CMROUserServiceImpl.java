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
import checkMyResearchOut.security.model.CMROSecurityUser;
import checkMyResearchOut.security.services.CurrentUserInformationService;
import checkMyResearchOut.security.services.PasswordEncodingService;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author Rémi Venant
 */
@Service
public class CMROUserServiceImpl implements CMROUserService {

    private final CMROUserRepository userRepo;

    private final CMROUserAnswerRepository answerRepo;

    private final PasswordEncodingService passwordSvc;

    private final MailSendingService mailSendSvc;

    private final CurrentUserInformationService currentUserInformationSvc;

    private final int tokenDurationMinutes;

    private final int minTimeBeforeNewMail;

    @Autowired
    public CMROUserServiceImpl(CMROUserRepository userRepo, CMROUserAnswerRepository answerRepo,
            PasswordEncodingService passwordSvc, MailSendingService mailSendSvc,
            CurrentUserInformationService currentUserInformationSvc,
            @Value("${checkMyResearchOut.server.token-duration:30}") int tokenDurationMinutes,
            @Value("${checkMyResearchOut.mail.mail-timeout:5}") int minTimeBeforeNewMail) {
        this.userRepo = userRepo;
        this.answerRepo = answerRepo;
        this.passwordSvc = passwordSvc;
        this.mailSendSvc = mailSendSvc;
        this.currentUserInformationSvc = currentUserInformationSvc;
        this.tokenDurationMinutes = tokenDurationMinutes;
        this.minTimeBeforeNewMail = minTimeBeforeNewMail;
    }

    @Override
    public CMROUser createUser(String mail, String lastname, String firstname, String clearPassword) throws IllegalArgumentException, DuplicateKeyException {
        if (!StringUtils.hasText(mail) || !StringUtils.hasText(lastname) || !StringUtils.hasText(firstname)) {
            throw new IllegalArgumentException("Email nor lastname nor firstname cannot be null.");
        }
        try {
            CMROUser user = new CMROUser(mail.trim().toLowerCase(),
                    lastname.trim().toLowerCase(),
                    firstname.trim().toLowerCase(),
                    this.passwordSvc.encodePassword(clearPassword));
            user.setValidated(Boolean.TRUE);
            user = this.userRepo.save(user);
            return user;
        } catch (ConstraintViolationException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public CMROUser getCurrentUser() throws IllegalArgumentException, NoSuchElementException {
        CMROSecurityUser secUser = this.currentUserInformationSvc.getUser();
        if (secUser == null) {
            throw new IllegalArgumentException("Current user absent");
        }
        return this.userRepo.findById(secUser.getUserId()).orElseThrow(() -> new NoSuchElementException("Unknown user id."));
    }

    @Override
    public CMROUser getUserById(String userId) throws IllegalArgumentException, NoSuchElementException {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("UserId cannot be blank.");
        }
        return this.userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("Unknown user id."));
    }

    @Override
    public CMROUser getUserByMail(String mail) throws IllegalArgumentException, NoSuchElementException {
        if (!StringUtils.hasText(mail)) {
            throw new IllegalArgumentException("Mail cannot be blank.");
        }
        return this.userRepo.findByMail(mail).orElseThrow(() -> new NoSuchElementException("Unknown email."));
    }

    @Override
    public boolean hasCurrentValidationProcess(CMROUser user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        return !user.getValidated()
                && user.getValidationToken() != null && user.getTokenEmissionDateTime() != null
                && user.getTokenEmissionDateTime().plusMinutes(this.tokenDurationMinutes).isAfter(LocalDateTime.now());
    }

    @Override
    public void sendValidationEmail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (user.getValidated()) {
            throw new IllegalArgumentException("User account already validated.");
        }
        if (user.getTokenEmissionDateTime() != null && user.getTokenEmissionDateTime()
                .plusMinutes(this.minTimeBeforeNewMail).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(String.format("A mail was already sent less than %d minutes ago. Please retry in a while.", minTimeBeforeNewMail));
        }
        user = this.generateAndSetToken(user);
        this.mailSendSvc.sendAccountValidationMail(user);
    }

    @Override
    public void validate(CMROUser user, String token) throws IllegalArgumentException, AccessDeniedException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (user.getValidated()) {
            throw new IllegalArgumentException("User account already validated.");
        }
        if (user.getValidationToken() == null || !user.getValidationToken().equals(token)) {
            throw new AccessDeniedException("Invalid token.");
        }
        if (user.getTokenEmissionDateTime().plusMinutes(this.tokenDurationMinutes)
                .isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("Outdated token.");
        }
        user.setValidated(true);
        user.setValidationToken(null);
        user.setTokenEmissionDateTime(null);
        this.userRepo.save(user);
    }

    @Override
    public boolean hasCurrentRenewalPasswordProcess(CMROUser user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        return user.getValidated()
                && user.getValidationToken() != null && user.getTokenEmissionDateTime() != null
                && user.getTokenEmissionDateTime().plusMinutes(this.tokenDurationMinutes).isAfter(LocalDateTime.now());
    }

    @Override
    public void sendChangePasswordEmail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (!user.getValidated()) {
            throw new IllegalArgumentException("User account not validated.");
        }
        if (user.getTokenEmissionDateTime() != null && user.getTokenEmissionDateTime()
                .plusMinutes(this.minTimeBeforeNewMail).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(String.format("A mail was already sent less than %d minutes ago. Please retry in a while.", minTimeBeforeNewMail));
        }
        user = this.generateAndSetToken(user);
        this.mailSendSvc.sendPasswordRenewalMail(user);
    }

    @Override
    public void changePassword(CMROUser user, String token, String clearPassword) throws IllegalArgumentException, AccessDeniedException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (!user.getValidated()) {
            throw new IllegalArgumentException("User account not validated.");
        }
        if (user.getValidationToken() == null || !user.getValidationToken().equals(token)) {
            throw new AccessDeniedException("Invalid token.");
        }
        if (user.getTokenEmissionDateTime().plusMinutes(this.tokenDurationMinutes)
                .isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("Outdated token.");
        }
        user.setPassword(this.passwordSvc.encodePassword(clearPassword));
        user.setValidationToken(null);
        user.setTokenEmissionDateTime(null);
        this.userRepo.save(user);
    }

    @Override
    public CMROUser patchUser(CMROUser user, String lastname, String firstname, String clearPassword) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (StringUtils.hasText(lastname)) {
            user.setLastname(lastname.trim().toLowerCase());
        }
        if (StringUtils.hasText(firstname)) {
            user.setFirstname(firstname.trim().toLowerCase());
        }
        if (StringUtils.hasText(clearPassword)) {
            final String encodedPassword = this.passwordSvc.encodePassword(clearPassword);
            user.setPassword(encodedPassword);
        }
        return this.userRepo.save(user);
    }

    @Override
    public void deleteUser(CMROUser user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        this.answerRepo.deleteByUser(user);
        this.userRepo.delete(user);
    }

    private CMROUser generateAndSetToken(CMROUser user) {
        final UUID token = UUID.randomUUID();
        user.setValidationToken(token.toString());
        user.setTokenEmissionDateTime(LocalDateTime.now());
        return this.userRepo.save(user);
    }

}
