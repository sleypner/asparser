package com.sleypner.parserarticles.security;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Service;

@Service
public class Oauth2ClientRegistrations {
    Environment environment;

    public Oauth2ClientRegistrations(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                this.googleClientRegistration(),
                this.discordClientRegistration(),
                this.yandexClientRegistration()
        );
    }
    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(environment.getProperty("oauth2.client.google.client-id"))
                .clientSecret(environment.getProperty("oauth2.client.google.client-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(environment.getProperty("oauth2.client.google.redirect-uri"))
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(environment.getProperty("oauth2.client.google.attribute-key"))
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }
    private ClientRegistration discordClientRegistration() {
        return ClientRegistration.withRegistrationId("discord")
                .clientId(environment.getProperty("oauth2.client.discord.client-id"))
                .clientSecret(environment.getProperty("oauth2.client.discord.client-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(environment.getProperty("oauth2.client.discord.redirect-uri"))
                .scope("identify", "email")
                .authorizationUri("https://discordapp.com/api/oauth2/authorize")
                .tokenUri("https://discordapp.com/api/oauth2/token")
                .userInfoUri("https://discordapp.com/api/users/@me")
                .userNameAttributeName(environment.getProperty("oauth2.client.discord.attribute-key"))
                .clientName("Discord")
                .build();
    }
    private ClientRegistration yandexClientRegistration() {
        return ClientRegistration.withRegistrationId("yandex")
                .clientId(environment.getProperty("oauth2.client.yandex.client-id"))
                .clientSecret(environment.getProperty("oauth2.client.yandex.client-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(environment.getProperty("oauth2.client.yandex.redirect-uri"))
                .authorizationUri("https://oauth.yandex.com/authorize")
                .tokenUri("https://oauth.yandex.com/token")
                .userInfoUri("https://login.yandex.ru/info?")
                .userNameAttributeName(environment.getProperty("oauth2.client.yandex.attribute-key"))
                .clientName("Yandex")
                .build();
    }
}
