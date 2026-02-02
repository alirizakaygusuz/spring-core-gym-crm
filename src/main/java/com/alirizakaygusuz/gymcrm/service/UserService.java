package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.common.UserProfileData;
import com.alirizakaygusuz.gymcrm.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthService authService;
    private final UserDao userDao;
    private final UserDomainService userDomainService;

    @Transactional
    public User createUserWithCredentials(UserProfileData request) {
        User user = userDomainService.createWithCredentials(request);
        return userDao.save(user);
    }

    @Transactional
    public void changePassword(User user, String newPassword) {
        userDomainService.changePassword(user, newPassword);
        userDao.update(user);
    }

    @Transactional
    public void activate(User user) {
        userDomainService.activate(user);
        userDao.update(user);
    }

    @Transactional
    public void deactivate(User user) {
        userDomainService.deactivate(user);
        userDao.update(user);
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginRequest login) {
        return authService.authenticateAndGetUser(login);
    }

    @Transactional
    public void applyProfileUpdate(User user, UserProfileData req) {
        userDomainService.applyUpdate(user, req);
        userDao.update(user);
    }
}
