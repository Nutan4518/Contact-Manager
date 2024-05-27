package com.example.smartcontactmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotPasswordController {

    Random random =new Random(1000);

    @RequestMapping("/forgot-password")
    public String forgotPassword(){
        return "forgot_password";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email){
        System.out.println("email: "+ email);

//        generating 4digit otp

       int otp =  random.nextInt(999999);
        System.out.println("otp"+ otp);
        return "verify_otp";
    }

}
