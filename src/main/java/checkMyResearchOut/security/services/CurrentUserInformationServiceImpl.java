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

import checkMyResearchOut.security.model.CMROSecurityUser;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Rémi Venant
 */
@Service
public class CurrentUserInformationServiceImpl implements CurrentUserInformationService {

    private static final Log LOG = LogFactory.getLog(CurrentUserInformationServiceImpl.class);

    @Override
    public CMROSecurityUser getUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CMROSecurityUser.class::isInstance)
                .map(CMROSecurityUser.class::cast)
                .orElse(null);
    }

    @Override
    public String getUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .orElse(null);
    }

    @Override
    public boolean hasOneOfRoles(String... roles) {
        final Set<? extends GrantedAuthority> roleGatSet = Arrays.asList(roles).stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                .collect(Collectors.toSet());
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(a -> a.getAuthorities().stream())
                .orElse(Stream.empty())
                .filter(ga -> roleGatSet.contains(ga))
                .findAny().isPresent();
    }

    @Override
    public boolean hasRole(String role) {
        final SimpleGrantedAuthority roleGa = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(a -> a.getAuthorities().stream())
                .orElse(Stream.empty())
                .filter(ga -> ga.equals(roleGa))
                .findAny().isPresent();
    }

}
