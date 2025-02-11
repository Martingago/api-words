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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not founded"));
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userModel.getRoleModelSet().forEach(
                role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole().name()))));

        userModel.getRoleModelSet().stream()
                .flatMap(role -> role.getPermissionModelSet().stream())
                        .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userModel.getUsername(),
                userModel.getPassword(),
                userModel.getIsEnabled(),
                userModel.getAccountNonExpired(),
                userModel.getCredentialNonExpired(),
                userModel.getAccountNonLocked(),
                authorityList);
    }
}
