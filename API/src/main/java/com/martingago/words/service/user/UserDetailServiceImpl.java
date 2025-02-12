package com.martingago.words.service.user;

import com.martingago.words.dto.authentication.AuthLoginRequestDTO;
import com.martingago.words.dto.authentication.AuthResponseDTO;
import com.martingago.words.model.UserModel;
import com.martingago.words.repository.UserRepository;
import com.martingago.words.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

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

    public AuthResponseDTO loginUser(AuthLoginRequestDTO authLoginRequestDTO){
        String username = authLoginRequestDTO.email();
        String password = authLoginRequestDTO.password();
        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponseDTO authResponseDTO = new AuthResponseDTO(username, "user logged successfully", accessToken, true);
        return authResponseDTO;
    }

    /**
     * Función que valida si un usuario y contraseña son correctos y devuelve sus datos de authentication
     * @param username
     * @param password
     * @return
     */
    public Authentication authenticate(String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);
        if(userDetails == null){
            throw  new BadCredentialsException("Invalid username or password");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw  new BadCredentialsException("Password is not correct");
        }
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

}
