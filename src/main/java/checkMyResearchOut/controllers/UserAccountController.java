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
package checkMyResearchOut.controllers;

import checkMyResearchOut.controllers.model.ValidAccPassRenewInput;
import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.services.CMROUserService;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Rémi Venant
 */
@RestController
@RequestMapping("/api/v1/rest/users-accounts")
public class UserAccountController {

    private final CMROUserService userSvc;

    @Autowired
    public UserAccountController(CMROUserService userSvc) {
        this.userSvc = userSvc;
    }

    @RequestMapping(value = "validation", method = RequestMethod.HEAD)
    public ResponseEntity<?> checkAccountValidationProcess(@RequestParam(name = "m") String encodeUserMail)
            throws UnsupportedEncodingException {
        final String mail = URLDecoder.decode(encodeUserMail, StandardCharsets.UTF_8.toString());
        final CMROUser user = this.userSvc.getUserByMail(mail);
        if (!this.userSvc.hasCurrentValidationProcess(user)) {
            throw new NoSuchElementException("No current validation process");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("validation")
    public ResponseEntity<?> startAccountValidationProcess(@RequestBody ValidAccPassRenewInput validationInput)
            throws UnsupportedEncodingException, MessagingException {
        if (!StringUtils.hasText(validationInput.getEncodedMail())) {
            throw new IllegalArgumentException("missing mail");
        }
        final String mail = URLDecoder.decode(validationInput.getEncodedMail(), StandardCharsets.UTF_8.toString());
        final CMROUser user = this.userSvc.getUserByMail(mail);
        this.userSvc.sendValidationEmail(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("validate")
    public ResponseEntity<?> validateAccount(@RequestBody ValidAccPassRenewInput validationInput)
            throws UnsupportedEncodingException {
        if (!StringUtils.hasText(validationInput.getEncodedMail())
                || !StringUtils.hasText(validationInput.getToken())) {
            throw new IllegalArgumentException("missing mail or token");
        }
        final String mail = URLDecoder.decode(validationInput.getEncodedMail(), StandardCharsets.UTF_8.toString());
        final CMROUser user = this.userSvc.getUserByMail(mail);
        this.userSvc.validate(user, validationInput.getToken());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "password-renewal", method = RequestMethod.HEAD)
    public ResponseEntity<?> checkPasswordRenewalProcess(@RequestParam(name = "m") String encodeUserMail)
            throws UnsupportedEncodingException {
        final String mail = URLDecoder.decode(encodeUserMail, StandardCharsets.UTF_8.toString());
        final CMROUser user = this.userSvc.getUserByMail(mail);
        if (!this.userSvc.hasCurrentValidationProcess(user)) {
            throw new NoSuchElementException("No current validation process");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("password-renewal")
    public ResponseEntity<?> startPasswordRenewalProcess(@RequestBody ValidAccPassRenewInput validationInput)
            throws UnsupportedEncodingException, MessagingException {
        if (!StringUtils.hasText(validationInput.getEncodedMail())) {
            throw new IllegalArgumentException("missing mail");
        }
        final String mail = URLDecoder.decode(validationInput.getEncodedMail(), StandardCharsets.UTF_8.toString());
        final CMROUser user = this.userSvc.getUserByMail(mail);
        this.userSvc.sendChangePasswordEmail(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("password-renew")
    public ResponseEntity<?> renewPassword(@RequestBody ValidAccPassRenewInput validationInput)
            throws UnsupportedEncodingException {
        if (!StringUtils.hasText(validationInput.getEncodedMail())
                || !StringUtils.hasText(validationInput.getToken())
                || !StringUtils.hasText(validationInput.getPassword())) {
            throw new IllegalArgumentException("missing mail or token or password");
        }
        final String mail = URLDecoder.decode(validationInput.getEncodedMail(), StandardCharsets.UTF_8.toString());
        final CMROUser user = this.userSvc.getUserByMail(mail);
        this.userSvc.changePassword(user, validationInput.getToken(), validationInput.getPassword());
        return ResponseEntity.noContent().build();
    }
}
