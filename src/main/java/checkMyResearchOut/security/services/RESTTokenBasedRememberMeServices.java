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
package checkMyResearchOut.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 *
 * @author Rémi Venant
 */
public class RESTTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

    public static final String REMEMBERME_REQUEST_ATTRIBUTE_NAME = "rememberme";

    private static final Log LOG = LogFactory.getLog(UsernamePasswordAuthenticationFilter.class);

    private final ObjectMapper mapper;

    private boolean alwaysRemember = false;

    public RESTTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, ObjectMapper mapper) {
        super(key, userDetailsService);
        this.mapper = mapper;
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        if (this.alwaysRemember) {
            LOG.debug("Remember me requested by force.");
            return true;
        }
        try {
            final Boolean remembermeAttr = (Boolean) request.getAttribute(REMEMBERME_REQUEST_ATTRIBUTE_NAME);
            if (remembermeAttr) {
                LOG.debug("Remember me requested by user request.");
                return true;
            }
            return false;
        } catch (ClassCastException ex) {
            LOG.debug("Remember me not requested by error: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void setAlwaysRemember(boolean alwaysRemember) {
        super.setAlwaysRemember(alwaysRemember);
        this.alwaysRemember = alwaysRemember;
    }

}
