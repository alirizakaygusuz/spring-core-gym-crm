package com.alirizakaygusuz.gymcrm.service.validator;

import com.alirizakaygusuz.gymcrm.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class SelfAccessValidator {

    public void assertSelfAccess(String currentUsername, String targetUsername) {
        if (!currentUsername.equals(targetUsername)) {
            throw new AccessDeniedException("You can only access your own profile.");
        }
    }

}
