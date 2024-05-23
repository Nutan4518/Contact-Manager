package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.dao.UserRepository;

import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private BindingResult result1;
    private Boolean agreement;
    private Model model;
    private HttpSession session;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }


    @RequestMapping("/signup")
    public String signUp(Model model, HttpSession session){
        model.addAttribute("title","Signup - Smart Contact Manager");
        model.addAttribute("user", new User());

        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }
        return "signup";
    }

    @RequestMapping(value="/do_register", method= RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result1,
                               @RequestParam(value="agreement",
                                       defaultValue = "false")
                               Boolean agreement,
                               Model model,
                               HttpSession session){

        try {

            if(!agreement)
            {
                System.out.println("You have Not Agreed To terms and Conditions");
                throw new Exception("You have Not Agreed To terms and Conditions");
            }
            if(result1.hasErrors())
            {
                System.out.println("Error "+result1.toString());
                model.addAttribute("user",user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println("Agreement " + agreement);
            System.out.println("User"+user);
            User result= this.userRepository.save(user);
            model.addAttribute("user",new User());
            session.setAttribute("message", new Message("Successfully registered","alert-success"));
            return "signup";
        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("user");
            model.addAttribute("user", user);
//            System.out.println("User exception"+e);
            session.setAttribute("message", new Message("Something went wrong" + e.getMessage(), "alert-danger") );
            return "signup";
        }
    }


    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login - Smart Contact Manager");
        return "login";
    }

}
