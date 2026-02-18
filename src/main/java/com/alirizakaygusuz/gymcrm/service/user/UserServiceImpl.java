package com.alirizakaygusuz.gymcrm.service.user;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.common.UserUpdateRequest;
import com.alirizakaygusuz.gymcrm.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserDomainService userDomainService;

    @Transactional
    public User createUserWithCredentials(UserRegisterRequest request) {
        User user = userDomainService.createWithCredentials(request);
        return userDao.save(user);
    }

    @Transactional
    public void changePassword(User user, String newPassword) {
        userDomainService.changePassword(user, newPassword);
        userDao.update(user);
    }


    @Transactional
    public void setActiveStatus(User user, boolean desiredActive) {
        userDomainService.setActiveStatus(user, desiredActive);
        userDao.update(user);

    }


    @Transactional
    public void applyProfileUpdate(User user, UserUpdateRequest req) {
        userDomainService.applyUpdate(user, req);
        userDao.update(user);
    }
}
