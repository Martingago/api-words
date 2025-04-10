package com.martingago.words.controller.authentication;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.authentication.AuthLoginRequestDTO;
import com.martingago.words.dto.authentication.AuthResponseDTO;
import com.martingago.words.domain.service.user.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Hidden
public class AuthenticationController {

    @Autowired
    UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> loginUser(@RequestBody @Valid AuthLoginRequestDTO authLoginRequestDTO){
        AuthResponseDTO authResponseDTO = userDetailService.loginUser(authLoginRequestDTO);
        return  ApiResponseDTO.build(
                true,
                "Successfully login",
                HttpStatus.OK.value(),
                authResponseDTO,
                HttpStatus.OK
        );
    }
}
