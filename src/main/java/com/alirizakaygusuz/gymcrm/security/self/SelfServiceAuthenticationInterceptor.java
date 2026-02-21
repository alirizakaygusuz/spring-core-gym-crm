package com.alirizakaygusuz.gymcrm.security.self;

import com.alirizakaygusuz.gymcrm.exception.AuthorizationFailedException;
import com.alirizakaygusuz.gymcrm.security.context.AuthContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

@Component
public class SelfServiceAuthenticationInterceptor implements MethodInterceptor {

    private final AuthContext authContext;

    public SelfServiceAuthenticationInterceptor(AuthContext authContext) {
        this.authContext = authContext;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        var annotation = invocation.getMethod().getAnnotation(SelfService.class);

        if (annotation == null) {
            return invocation.proceed();
        }

        String authenticatedUsername = authContext.getUsername();
        if (authenticatedUsername == null) {
            throw new AuthorizationFailedException("User is not authenticated");
        }

        Optional<String> targetUsername = extractUsernameFromParams(
            invocation.getMethod(), 
            invocation.getArguments(), 
            annotation
        );

        if (targetUsername.isEmpty() || !targetUsername.get().equals(authenticatedUsername)) {
            throw new AuthorizationFailedException("User is not authorized to access this resource");
        }

        return invocation.proceed();
    }

    private Optional<String> extractUsernameFromParams(Method method, Object[] args, SelfService annotation) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (annotation.usernameParam().equals(parameters[i].getName())) {
                return Optional.ofNullable((String) args[i]);
            }
        }
        return Optional.empty();
    }
}