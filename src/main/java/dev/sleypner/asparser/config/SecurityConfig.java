package dev.sleypner.asparser.config;

import dev.sleypner.asparser.exceptions.CustomAuthenticationFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final Environment env;
    private final Oauth2ClientRegistrations oauth2ClientRegistrations;
    private final CustomOauth2UserService customOauth2UserService;
    private final DefaultUser defaultUser;

    public SecurityConfig(
            CustomOauth2UserService customOauth2UserService, Oauth2ClientRegistrations oauth2ClientRegistrations,
            Environment env, DefaultUser defaultUser) {
        this.customOauth2UserService = customOauth2UserService;
        this.oauth2ClientRegistrations = oauth2ClientRegistrations;
        this.env = env;
        this.defaultUser = defaultUser;
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CustomUserDetailsService customUserDetailsService() {
        defaultUser.createDefaultUserIfNotExist();
        return new CustomUserDetailsService();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
//                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
//                .headers(headers -> headers
//                        .httpStrictTransportSecurity(hsts -> hsts
//                                .includeSubDomains(true)
//                                .preload(true)
//                                .maxAgeInSeconds(31536000)
//                        )
//                )
//                .headers(headers -> headers
//                        .contentSecurityPolicy(csp -> csp
//                                .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' https://trusted.cdn.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:;")
//                        )
//                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//                        .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
//                )
//                .addFilterBefore(new RateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
//                .securityContext(context -> context.requireExplicitSave(false))
//                .servletApi(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
//                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()::handle)
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(
                                "/**",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/auth/**",
                                "/error"
                        ).permitAll()
                        .anyRequest().denyAll()
                )
                .formLogin(login -> login
                        .loginPage("/auth/signin")
                        .loginProcessingUrl("/auth/signin")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/signin?error=true")
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/signin")
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/auth/oauth2/authorization/")
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/auth/signin/oauth2/code/*")
                        )
                        .clientRegistrationRepository(oauth2ClientRegistrations.clientRegistrationRepository())
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOauth2UserService.oauth2UserService())
                                .oidcUserService(customOauth2UserService.oidcUserService()
                                )
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/signin?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}
