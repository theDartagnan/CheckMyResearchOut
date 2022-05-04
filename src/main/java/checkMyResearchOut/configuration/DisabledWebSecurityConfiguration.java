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
package checkMyResearchOut.configuration;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author Rémi Venant
 */
@Profile("disable-security")
@Configuration
@EnableWebSecurity
public class DisabledWebSecurityConfiguration {

    private static final Log LOG = LogFactory.getLog(DisabledWebSecurityConfiguration.class);

    private final String restBasePath = "/api/**";

    @PostConstruct
    public void init() {
        for (int i = 0; i < 10; i++) {
            LOG.warn("SECURITY DISABLED!!!!");
        }
    }

    @Bean
    SecurityFilterChain disabledSecFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(httpBasicCustomizer -> httpBasicCustomizer.realmName("no-auth"))
                .csrf(csrfCustomizer -> csrfCustomizer.disable()) // Disable CSRF protection
                .cors().and() // Enable CORS filtering
                .authorizeRequests(authorizeRequestsCustomizer -> authorizeRequestsCustomizer
                .antMatchers(this.restBasePath).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.GET).permitAll()
                .anyRequest().denyAll()) // Any other request refused
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*", "moz-extension://*"));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("content-type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
