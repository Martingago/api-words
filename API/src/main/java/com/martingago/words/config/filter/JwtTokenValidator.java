package com.martingago.words.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.martingago.words.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {


    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jwtToken != null){
            jwtToken = jwtToken.substring(7); // Elimina textos del header
            DecodedJWT decodedJWT =  jwtUtils.validateToken(jwtToken); // Valida el token

            String username = jwtUtils.extractUser(decodedJWT);

            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            //Obtiene lista de GrantedAuthorities
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);
            SecurityContext securityContext = SecurityContextHolder.getContext();

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request,response);
    }
}
