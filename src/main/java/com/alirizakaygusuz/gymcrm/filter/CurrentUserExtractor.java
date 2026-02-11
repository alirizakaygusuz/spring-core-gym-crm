package com.alirizakaygusuz.gymcrm.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUserExtractor {

    public String currentUser(HttpServletRequest request) {

        Object value = request.getAttribute(AuthenticationFilter.AUTH_USER_ATTR);
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated request");
        }
        return value.toString();
    }
}
