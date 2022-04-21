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
package checkMyResearchOut.security.filters;

import checkMyResearchOut.security.model.CredentialInput;
import static checkMyResearchOut.security.services.RESTTokenBasedRememberMeServices.REMEMBERME_REQUEST_ATTRIBUTE_NAME;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author Rémi Venant
 */
public class RESTUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Log LOG = LogFactory.getLog(UsernamePasswordAuthenticationFilter.class);

    private final ObjectMapper mapper;

    @Autowired
    public RESTUsernamePasswordAuthenticationFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LOG.debug("Attempting authentication...");
        // Post only
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try {
            final CredentialInput credential = this.mapper.readValue(request.getInputStream(), CredentialInput.class);
            request.setAttribute(REMEMBERME_REQUEST_ATTRIBUTE_NAME, credential.isRememberMe()); // SET REMEMBER ME INFO FOR REMEMBER ME SERVICE
            LOG.debug(String.format("Attempting authentication for user %s...", Objects.toString(credential.getMail())));
            final UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                    credential.getMail().trim(),
                    credential.getPassword());
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException ex) {
            LOG.debug("Authentication failed on JSON parsing: " + ex.getMessage());
            throw new AuthenticationServiceException("Invalid credential: " + ex.getMessage());
        }
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        throw new IllegalStateException("obtainUsername method should not be called");
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        throw new IllegalStateException("obtainPassword method should not be called");
    }
}
