package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSeedMapperApplier {

    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public void apply(User u, long id, String firstName, String lastName, boolean isActive) {

        String username = credentialsGenerator.generateUniqueUsername(firstName, lastName);
        String password = credentialsGenerator.generateRandomPassword();

        u.setId(id);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setUsername(username);
        u.setPassword(password);
        u.setActive(isActive);

    }
}
