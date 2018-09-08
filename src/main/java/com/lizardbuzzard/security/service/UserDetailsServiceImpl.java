package com.lizardbuzzard.security.service;

import com.lizardbuzzard.persistence.dao.AuthorityRepository;
import com.lizardbuzzard.persistence.model.AuthorityEntity;
import com.lizardbuzzard.persistence.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    AuthorityRepository authorityRepository;

    public UserDetailsServiceImpl() {
        super();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<AuthorityEntity> authorities = authorityRepository.findByUsername(username);
        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Authorities for user \'%s\' are not found", username));
        }
        UserEntity user = authorities.get(0).getUser();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        authorities.forEach(a -> grantedAuthorities.add(new SimpleGrantedAuthority(a.getAuthority())));

        UserDetails userDetails = (UserDetails) new User(user.getUsername(), user.getPassword(), grantedAuthorities);

        return userDetails;
    }
}
