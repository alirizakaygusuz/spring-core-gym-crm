package com.alirizakaygusuz.gymcrm.security.blacklist;

public interface TokenBlacklistService {

    void blacklist(String token);


    boolean isBlacklisted(String token);
}
