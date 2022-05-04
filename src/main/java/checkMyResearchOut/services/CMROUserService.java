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
import java.util.NoSuchElementException;
import javax.mail.MessagingException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author Rémi Venant
 */
public interface CMROUserService {

    /**
     * Create a user. User will not be validate
     *
     * @param mail
     * @param lastname
     * @param firstname
     * @param clearPassword
     * @return the create user
     * @throws IllegalArgumentException if mail, lastname, firstname or password is blank
     * (ConstraintViolationException)
     * @throws DuplicateKeyException if bookCopy if unknown or book copy does not belong to book
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    CMROUser createUser(String mail, String lastname, String firstname, String clearPassword) throws IllegalArgumentException, DuplicateKeyException;

    /**
     * Retrieve currently connected user
     *
     * @return
     * @throws IllegalArgumentException if no connected user
     * @throws NoSuchElementException if user is not found
     */
    @PreAuthorize("isAuthenticated()")
    CMROUser getCurrentUser() throws IllegalArgumentException, NoSuchElementException;

    /**
     * Get a user by its id
     *
     * @param userId
     * @return
     * @throws NoSuchElementException
     */
    //We use postauthorized since we do not know user mail before retrieving it
    @PreAuthorize("(hasRole('USER') and #userId == principal.userId) or hasRole('ADMIN')")
    CMROUser getUserById(String userId) throws IllegalArgumentException, NoSuchElementException;

    /**
     * Get a user by mail
     *
     * @param mail
     * @return
     * @throws NoSuchElementException
     */
    @PreAuthorize("permitAll") // since we use this service for account validation and password-renewal
    CMROUser getUserByMail(String mail) throws IllegalArgumentException, NoSuchElementException;

    /**
     * Check if there is a current valid validation process for the user
     *
     * @param user
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    boolean hasCurrentValidationProcess(CMROUser user) throws IllegalArgumentException;

    /**
     * Send a validation mail
     *
     * @param user
     * @throws IllegalArgumentException if user is null or user is already validated
     * @throws javax.mail.MessagingException
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    void sendValidationEmail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException;

    /**
     * Validate a user account
     *
     * @param user
     * @param token
     * @throws IllegalArgumentException if user if null or already validate
     * @throws AccessDeniedException if token is null or does not match or was emitted too long ago
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    void validate(CMROUser user, String token) throws IllegalArgumentException, AccessDeniedException;

    /**
     * Check if there is a current valid renewal password process for the user
     *
     * @param user
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    boolean hasCurrentRenewalPasswordProcess(CMROUser user) throws IllegalArgumentException;

    /**
     * Send a change password email
     *
     * @param user
     * @throws IllegalArgumentException if user is null or if account is not validate
     * @throws javax.mail.MessagingException
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    void sendChangePasswordEmail(CMROUser user) throws IllegalArgumentException, MailException, MessagingException;

    /**
     * Change the password of a user given a token
     *
     * @param user
     * @param token
     * @param clearPassword
     * @throws IllegalArgumentException if user or token or clearPassword is null
     * @throws AccessDeniedException if token does not match or was emitted too long ago
     */
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    void changePassword(CMROUser user, String token, String clearPassword) throws IllegalArgumentException, AccessDeniedException;

    /**
     * Patch a user. Change lastname, firstname or clearPassword if they are not blank
     *
     * @param user
     * @param lastname
     * @param firstname
     * @param clearPassword
     * @return the updated user
     * @throws IllegalArgumentException if user is null or if account is not validated
     */
    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
    CMROUser patchUser(CMROUser user, String lastname, String firstname, String clearPassword) throws IllegalArgumentException;

    /**
     * Delete a user, and any answer given
     *
     * @param user
     * @throws IllegalArgumentException
     */
    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
    void deleteUser(CMROUser user) throws IllegalArgumentException;
}
