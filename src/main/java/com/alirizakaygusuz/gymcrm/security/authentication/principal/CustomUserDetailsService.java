package com.alirizakaygusuz.gymcrm.security.authentication.principal;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        User user = userDao.findByUsernameWithDetails(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));


        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(
                        userRole.getRole().getName().asAuthority()
                ))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
