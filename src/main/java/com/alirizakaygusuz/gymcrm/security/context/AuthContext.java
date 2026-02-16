package com.alirizakaygusuz.gymcrm.security.context;

public interface AuthContext {
    boolean isAuthenticated();
    String getUsername();
}
