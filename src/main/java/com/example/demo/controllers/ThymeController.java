package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThymeController {
    @RequestMapping("/home")
    public String getHomePage(){
        return "home";
    }
    @RequestMapping("/login")
    @GetMapping
    public String getLogin() {
//        log.info("Trying to get the login template");
        return "login";
    }

//    @RequestMapping("/signup")
    @GetMapping("/signup")
    public String getSignup() {
//        log.info("Trying to get the sign up template");
        return "signup";
    }

//    @PostMapping("/signup")
    public void createUser() {
        System.out.println("Thyme Controller processing user data");
    }

}
