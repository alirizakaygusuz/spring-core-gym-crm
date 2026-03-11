package com.alirizakaygusuz.gymcrm.service.user;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.common.UserUpdateRequest;
import com.alirizakaygusuz.gymcrm.model.RoleType;
import com.alirizakaygusuz.gymcrm.model.User;

public interface UserService {
    User createUserWithCredentials(UserRegisterRequest request , RoleType role);

    void applyProfileUpdate(User user, UserUpdateRequest req);

    void setActiveStatus(
            User user,
            boolean desiredActive
    );
}
