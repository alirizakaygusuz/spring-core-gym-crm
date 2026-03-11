package com.alirizakaygusuz.gymcrm.security.ratelimit;

import com.alirizakaygusuz.gymcrm.security.web.MultiReadHttpServletRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            MultiReadHttpServletRequest wrapped = new MultiReadHttpServletRequest(httpRequest);
            httpRequest.setAttribute("cachedAuthRequest", wrapped);
            chain.doFilter(wrapped, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}