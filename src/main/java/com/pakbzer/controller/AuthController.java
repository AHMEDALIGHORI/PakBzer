package com.pakbzer.controller;

import com.pakbzer.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final String googleRedirectUri;

    public AuthController(UserService userService,
                          @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.userService = userService;
        this.googleRedirectUri = baseUrl + "/login/oauth2/code/google";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("googleRedirectUri", googleRedirectUri);
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model,
                           RedirectAttributes ra) {
        try {
            userService.register(fullName.trim(), email.trim().toLowerCase(), password);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            return "register";
        }
        ra.addFlashAttribute("message", "Account created! Please sign in.");
        return "redirect:/login";
    }
}
