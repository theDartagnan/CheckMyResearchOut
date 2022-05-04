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

import checkMyResearchOut.security.filters.RESTUsernamePasswordAuthenticationFilter;
import checkMyResearchOut.security.services.MongoUserDetailsService;
import checkMyResearchOut.security.services.PasswordEncodingService;
import checkMyResearchOut.security.services.RESTTokenBasedRememberMeServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author Rémi Venant
 */
@Profile("!disable-security")
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private static final Log LOG = LogFactory.getLog(WebSecurityConfiguration.class);

    @PostConstruct
    public void init() {
        LOG.warn("SECURITY ENABLED !!!!");
    }

    @Bean
    public MongoUserDetailsService userDetailsService(MongoTemplate mongoTemplate) {
        return new MongoUserDetailsService(mongoTemplate);
    }

    @Bean
    public PasswordEncoder passwordEncoder(PasswordEncodingService passwordEncodingService) {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence cs) {
                return passwordEncodingService.encodePassword(cs);
            }

            @Override
            public boolean matches(CharSequence cs, String string) {
                return passwordEncodingService.verifyPassword(cs, string);
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(MongoUserDetailsService mongoUserDetailService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(mongoUserDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public ProviderManager providerManager(DaoAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public RESTTokenBasedRememberMeServices tokenBasedRememberMeServices(
            MongoUserDetailsService userDetailsService,
            ObjectMapper jsonMapper,
            @Value("${checkMyResearchOut.server.remember-me-cookie.name:REMEMBERME}") String rememberMeCookieName,
            @Value("${checkMyResearchOut.server.remember-me-cookie.key:remembermekey}") String rememberMeKey) {
        RESTTokenBasedRememberMeServices t = new RESTTokenBasedRememberMeServices(rememberMeKey, userDetailsService, jsonMapper);
        t.setCookieName(rememberMeCookieName);
        t.setAlwaysRemember(false);
        t.setUseSecureCookie(false);
        return t;
    }

    @Bean
    public RESTUsernamePasswordAuthenticationFilter restUsernamePasswordAuthenticationFilter(ObjectMapper jsonMapper,
            AuthenticationManager authenticationManager, RESTTokenBasedRememberMeServices tokenBasedRememberMeServices) {
        final RESTUsernamePasswordAuthenticationFilter filter = new RESTUsernamePasswordAuthenticationFilter(jsonMapper);
        filter.setFilterProcessesUrl("/api/v1/rest/login");
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            }
        });
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
        filter.setAuthenticationManager(authenticationManager);
        filter.setRememberMeServices(tokenBasedRememberMeServices);
        return filter;
    }

    @Bean
    public SecurityFilterChain enabledSecfilterChain(HttpSecurity http,
            RESTUsernamePasswordAuthenticationFilter restUsernamePasswordAuthenticationFilter,
            @Value("${server.servlet.session.cookie.name:JSESSIONID}") String cookieName,
            RESTTokenBasedRememberMeServices tokenBasedRememberMeServices) throws Exception {
        return http
                .exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .logout(logoutCustomizer -> logoutCustomizer
                .logoutUrl("/api/v1/rest/logout")
                .deleteCookies(cookieName)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()))
                .addFilterAt(restUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .rememberMe(rememberMeCustomizer -> rememberMeCustomizer
                .rememberMeServices(tokenBasedRememberMeServices))
                .csrf(csrfCustomizer -> csrfCustomizer.disable()) // Disable CSRF protection
                .cors(corsCustomizer -> corsCustomizer.configurationSource(this.corsConfigurationSource())) // Enable CORS filtering
                .authorizeRequests(authorizeRequestsCustomizer -> authorizeRequestsCustomizer
                .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v*/rest/users").anonymous()
                .antMatchers(HttpMethod.HEAD, "/api/v*/rest/users-accounts/validation", "/api/v*/rest/users-accounts/password-renewal").anonymous()
                .antMatchers(HttpMethod.POST, "/api/v*/rest/users-accounts/validation", "/api/v*/rest/users-accounts/validate",
                        "/api/v*/rest/users-accounts/password-renewal", "/api/v*/rest/users-accounts/password-renew").anonymous()
                .antMatchers("/api/v*/**").authenticated()
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()
                .anyRequest().denyAll()) // Any other request refused

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost", "http://127.0.0.1:*", "moz-extension://*"));
        //configuration.setAllowedOrigins(List.of("*"));
//        configuration.setAllowedOriginPatterns(Arrays.asList(
//                "http://localhost:*", "http://127.0.0.1:*", "moz-extension://*", "http://172.30.1.210:*"
//        ));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("content-type", "x-requested-with"));
        configuration.setAllowCredentials(Boolean.TRUE);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
