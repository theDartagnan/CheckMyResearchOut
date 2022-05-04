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

import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.security.model.CMROSecurityUser;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Rémi Venant
 */
public class MongoUserDetailsService implements UserDetailsService {

    private static final Log LOG = LogFactory.getLog(MongoUserDetailsService.class);

    private final MongoTemplate mongoTemplate;

    public MongoUserDetailsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        LOG.debug("Load user for mail " + mail);
        final CMROUser cmroUser = this.mongoTemplate.findOne(Query.query(Criteria.where("mail").is(mail.toLowerCase())), CMROUser.class);
        if (cmroUser == null) {
            throw new UsernameNotFoundException("Unknown user");
        }
        LOG.debug("User retrieved.");
        final boolean accountEnabled = cmroUser.getValidated() != null && cmroUser.getValidated();
        final boolean isAdmin = cmroUser.getAdmin() != null && cmroUser.getAdmin();
        final List<SimpleGrantedAuthority> authorities = Stream.concat(
                Stream.of(new SimpleGrantedAuthority("ROLE_USER")), isAdmin ? Stream.of(new SimpleGrantedAuthority("ROLE_ADMIN")) : Stream.empty()
        ).collect(Collectors.toList());
        return new CMROSecurityUser(cmroUser.getId(), cmroUser.getMail(), cmroUser.getPassword(), accountEnabled, true, true, true, authorities);
    }

}
