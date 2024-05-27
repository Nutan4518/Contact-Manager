package com.example.smartcontactmanager.service;

import org.springframework.stereotype.Service;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

@Service
public class EmailService {

    public  void sendEmail(String subject, String message, String to){

        String from ="nutansingh4518@gmail.com";

        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        System.out.println("properties: "+properties);
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session



    }
}
