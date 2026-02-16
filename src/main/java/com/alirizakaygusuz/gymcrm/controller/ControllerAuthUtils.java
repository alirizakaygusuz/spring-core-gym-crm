package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.exception.AuthorizationFailedException;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.security.context.AuthContextHolder;

import java.util.Optional;

public final class ControllerAuthUtils {

    private static String getAuthenticatedUsername() {
        Optional<String> username = AuthContextHolder.getAuthenticatedUsername();
        return username.orElseThrow(() ->
                new AuthenticationFailedException("Authenticated user not found in context"));
    }


    public static void verifyUserAccess(String requestedUsername) {
        String authenticatedUsername = getAuthenticatedUsername();
        if (!authenticatedUsername.equals(requestedUsername)) {
            throw new AuthorizationFailedException(
                    "User:" + authenticatedUsername +
                            " cannot access resource of:" + requestedUsername);
        }
    }

}
