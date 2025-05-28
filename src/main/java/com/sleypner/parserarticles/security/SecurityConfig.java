package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.exceptions.CustomAuthenticationFailureHandler;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.List;

import static com.sleypner.parserarticles.security.DefaultUser.createDefaultUserIfNotExist;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private Environment env;
    private final Oauth2ClientRegistrations oauth2ClientRegistrations;
    private final CustomOauth2UserService customOauth2UserService;

    public SecurityConfig(
            CustomOauth2UserService customOauth2UserService, Oauth2ClientRegistrations oauth2ClientRegistrations,
            Environment env) {
        this.customOauth2UserService = customOauth2UserService;
        this.oauth2ClientRegistrations = oauth2ClientRegistrations;
        this.env = env;
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CustomUserDetailsService customUserDetailsService() {
        createDefaultUserIfNotExist();
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
//                .cors(cors -> cors.configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowedOrigins(List.of("https://sleypner.dev"));
//                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowCredentials(true);
//                    return config;
//                }))
//                .csrf(csrf -> csrf
//                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()::handle)
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers(
                                "/**",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/registration",
                                "/registration-process",
                                "/email-verification",
                                "/resend-verification",
                                "/verify-code",
                                "/verify-email",
                                "/error"
                        ).permitAll()
                        .anyRequest().denyAll() // По умолчанию запрещаем всё, что не разрешено явно
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/",true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .clientRegistrationRepository(oauth2ClientRegistrations.clientRegistrationRepository())
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOauth2UserService.oauth2UserService())
                                .oidcUserService(customOauth2UserService.oidcUserService()
                                )
                        )
                )
                .logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}
