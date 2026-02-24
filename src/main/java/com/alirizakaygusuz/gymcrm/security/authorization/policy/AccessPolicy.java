package com.alirizakaygusuz.gymcrm.security.authorization.policy;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("accessPolicy")
public class AccessPolicy {

    public boolean isSelf(Authentication authentication, String username) {
        return authentication != null
                && username != null
                && username.equals(authentication.getName());
    }
}