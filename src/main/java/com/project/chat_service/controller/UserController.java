package com.project.chat_service.controller;

import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.request.SignUpRequest;
import com.project.chat_service.service.UserService;
import com.project.chat_service.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                               BindingResult bindingResult, HttpServletResponse httpServletResponse) {
        log.info("[AuthController:registerUser]Signup Process Started for user:{}",signUpRequest.email());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser]Errors in user:{}",errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return ResponseEntity.ok(userService.registerUser(signUpRequest,httpServletResponse));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> signIn(Authentication authentication, HttpServletResponse response) {
        return ResponseEntity.ok(userService.getJwtTokensAfterAuthentication(authentication,response));
    }
}
