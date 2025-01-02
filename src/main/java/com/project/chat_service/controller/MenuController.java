package com.project.chat_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class MenuController {

    @GetMapping("/interface")
    public String interfacePage() {
        return "menu";
    }
}
