package com.alirizakaygusuz.gymcrm.service.user;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterResponse;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.auth.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {

    private final AuthServiceImpl authServiceImpl;
    private final UserDao userDao;
    private final UserDomainService userDomainService;

    @Transactional
    public User createUserWithCredentials(UserRegisterResponse request) {
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
        return authServiceImpl.authenticateAndGetUser(login);
    }

    @Transactional
    public void applyProfileUpdate(User user, UserRegisterResponse req) {
        userDomainService.applyUpdate(user, req);
        userDao.update(user);
    }
}
