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

    public void apply(User u, String firstName, String lastName) {
        String username = credentialsGenerator.generateUniqueUsername(firstName, lastName);
        String password = credentialsGenerator.generateRandomPassword();

        u.setUsername(username);
        u.setPassword(password);
    }
}
