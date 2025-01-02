package com.project.chat_service.controller;

import com.project.chat_service.models.request.SignInRequest;
import com.project.chat_service.models.request.SignUpRequest;
import com.project.chat_service.security.utils.AuthUtils;
import com.project.chat_service.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthUtils authUtils;

    public UserController(UserService userService, AuthUtils authUtils) {
        this.userService = userService;
        this.authUtils = authUtils;
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
    public String signIn(@Valid @ModelAttribute SignInRequest sign, HttpServletResponse response) {
        String token = authUtils.auth(sign);

        Cookie jwtCookie = new Cookie("Authorization", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);

        response.addCookie(jwtCookie);

        return "redirect:/api/interface";
    }

}
