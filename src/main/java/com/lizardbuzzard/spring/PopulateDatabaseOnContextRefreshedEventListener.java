package com.lizardbuzzard.spring;

import com.lizardbuzzard.persistence.dao.AuthorityRepository;
import com.lizardbuzzard.persistence.dao.UserRepository;
import com.lizardbuzzard.persistence.model.AuthorityEntity;
import com.lizardbuzzard.persistence.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@Component
public class PopulateDatabaseOnContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private int numberOfUsers = 5;
    private String length = Integer.toString((int) Math.ceil(Math.log10(numberOfUsers + 0.5)));
    private String format = String.format("user%%0%sd", length);

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    @Qualifier("passwordEncoder")
    PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        createUserWithRoles("admin", "ROLE_ADMIN");

        for(int i=1; i<=numberOfUsers; i++) {
            createUserWithRoles(String.format(format, i), "ROLE_USER");
        }

        createUserWithRoles("joker", "ROLE_ADMIN", "ROLE_USER");
    }

    private void createUserWithRoles(String userLoginName, String ... userRoles) {
        UserEntity user = createUser(userLoginName);
        Arrays.stream(userRoles).forEach(r->createUserRole(user, r));
    }

    @Transactional
    private UserEntity createUser(String userLoginName) {
        UserEntity rezultUser = userRepository.findByUsername(userLoginName).orElseGet(
                () -> {
                    UserEntity user = new UserEntity();
                    user.setUsername(userLoginName);
                    user.setPassword(passwordEncoder.encode(userLoginName + "123"));
                    user.setEnabled((short) 1);
                    userRepository.save(user);
                    return user;
                });
        return rezultUser;
    }

    @Transactional
    private void createUserRole(UserEntity user, @NotNull String userRole) {
        AuthorityEntity authority = authorityRepository.findByUser(user)
                .stream().filter(a -> userRole.equals(a.getAuthority())).findFirst()
                .orElseGet(() -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUsername(user.getUsername());
                    ae.setAuthority(userRole);
                    ae.setUser(user);
                    authorityRepository.save(ae);
                    return ae;
                });
    }

}