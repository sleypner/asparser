package com.sleypner.parserarticles.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
@Setter
public class CustomOauth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final String clientName;
    private Map<String, Object> attributes;

    public CustomOauth2User(OAuth2User oauth2User, String clientName) {
        this.oauth2User = oauth2User;
        this.clientName = clientName;
        this.attributes = getAttributes();
    }


    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> customAttributes = new HashMap<>();
        Map<String, Object> attributes = oauth2User.getAttributes();
        System.out.println();
        if (Objects.equals(clientName, "Yandex")){
            LinkedHashMap<String,Object> phoneMap = (LinkedHashMap<String,Object>) attributes.get("default_phone");
            String phone = (String) phoneMap.get("default_phone");
            customAttributes.put("name", attributes.get("first_name"));
            customAttributes.put("external_id", attributes.get("id"));
            customAttributes.put("login", attributes.get("login"));
            customAttributes.put("last_name", attributes.get("last_name"));
            customAttributes.put("sex", attributes.get("sex"));
            customAttributes.put("email", attributes.get("default_email"));
            customAttributes.put("birthday", attributes.get("birthday"));
            customAttributes.put("phone", phone);
            customAttributes.put("client_id", attributes.get("client_id"));
        } else if (Objects.equals(clientName, "Discord")){

            customAttributes.put("name", attributes.get("global_name"));
            customAttributes.put("external_id", attributes.get("id"));
            customAttributes.put("login", attributes.get("email"));
            customAttributes.put("last_name", attributes.get(""));
            customAttributes.put("sex", attributes.get(""));
            customAttributes.put("email", attributes.get("email"));
            customAttributes.put("birthday", attributes.get(""));
            customAttributes.put("phone", "");
            customAttributes.put("client_id", attributes.get(""));
        }
        return customAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return this.attributes.get("name").toString();
    }
}
