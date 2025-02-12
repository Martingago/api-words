package com.martingago.words.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String API_PRIVATE_KEY;

    @Value("${security.jwt.user.generator}")
    private String USER_GENERATOR;


    /**
     * Método que genera un JWT en la aplicación
     * @param authentication
     * @return
     */
    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.API_PRIVATE_KEY);
        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // READ, WRITE, DELETE

        return JWT.create()
                .withIssuer(this.USER_GENERATOR)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    /**
     * Comprueba que un JWT enviado por el usuario en los headers de una petición sea valido en el servidor
     * @param token
     * @return
     */
    public DecodedJWT validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.API_PRIVATE_KEY);
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(this.USER_GENERATOR)
                    .build();
            return jwtVerifier.verify(token);
        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token invalid, not authorized");
        }
    }

    public String extractUser(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
