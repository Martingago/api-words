package com.martingago.words.service.user;

import com.martingago.words.model.UserModel;
import com.martingago.words.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not founded"));
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userModel.getRoleModelSet().forEach(
                role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole().name())))
        );

        return null;
    }
}
