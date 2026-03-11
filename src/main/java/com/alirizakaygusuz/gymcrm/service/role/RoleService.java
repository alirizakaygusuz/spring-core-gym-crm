package com.alirizakaygusuz.gymcrm.service.role;

import com.alirizakaygusuz.gymcrm.model.RoleType;
import com.alirizakaygusuz.gymcrm.model.User;

public interface RoleService {

    void assignRoleToUser(User user, RoleType roleType);
}
