package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.Role;
import com.alirizakaygusuz.gymcrm.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);

}
