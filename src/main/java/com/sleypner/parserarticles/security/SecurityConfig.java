package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.exceptions.CustomAuthenticationFailureHandler;
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
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
                .cors((cors) -> cors.configurationSource(apiConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/",
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
                        .anyRequest()
                        .authenticated())
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
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(conf -> conf.accessDeniedPage("/access-denied"));
        return http.build();
    }
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/**","/content/**","/admin/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    private UrlBasedCorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(env.getProperty("frontend.url")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
