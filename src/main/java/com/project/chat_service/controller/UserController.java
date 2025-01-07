package com.project.chat_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.request.SignInRequest;
import com.project.chat_service.models.request.SignUpRequest;
import com.project.chat_service.security.utils.AuthUtils;
import com.project.chat_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("signUp", SignUpRequest.empty());
        return "registration";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("signIn", SignInRequest.empty());
        return "login";
    }

    @PostMapping("/sign-up")
    public String registerUser(@Valid @ModelAttribute SignUpRequest signUpRequest,
                               BindingResult bindingResult, HttpServletResponse httpServletResponse) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return "error";
        }

        userService.registerUser(signUpRequest, httpServletResponse);
        return "redirect:/user/login";
    }

    @PostMapping("/sign-in")
    public String signIn(@Valid @ModelAttribute SignInRequest sign, HttpServletResponse response, HttpServletRequest request) {
        try {
            SignInRequest signInRequest;
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Basic ")) {
                String base64Credentials = authHeader.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                String[] values = credentials.split(":", 2);
                signInRequest = new SignInRequest(values[0], values[1]);
            } else signInRequest = sign;


            AuthResponse authResponse = userService.authenticate(signInRequest, response);

            return "redirect:/api/interface";
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            return "redirect:/user/login?error=true";
        }
    }

}
