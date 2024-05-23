package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.dao.ContactRepository;
import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    //    Method for Common data
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("userName: " + userName);
        User user = userRepository.getUserByUserName(userName);
        System.out.println("user: " + user);
        model.addAttribute("user", user);
    }

    //    dashboard home
    @GetMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    //    open add form handler
    @GetMapping("/add-contact")
    public String openAddContactHome(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    //    processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

//            processing and uploading file
            if(file.isEmpty()){
                System.out.println("file is missing!!");
            }
            else{
//                upload file to folder and update the date to contact
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is Successfully uploaded");
            }
            user.getContacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);
            System.out.println("added to db");

//            success msg
//            session.setAttribute("message", new Message("Your Contact is Added.. Add more","success"));
            redirectAttributes.addFlashAttribute("message", new Message("Your contact is added. Add more.", "success"));

            System.out.println("Data" + contact.toString());

        }
        catch (Exception e) {
            e.printStackTrace();
//            error msg
//            session.setAttribute("message", new Message("something went wrong!! try again","danger"));
            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong! Try again.", "danger"));

            System.out.println("Error" + e.getMessage());
        }
        return "redirect:/user/add-contact";
    }


//    per page
//    current page
    @GetMapping("/view-contacts/{page}")
    public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Show User Contact");

//        send list of contacts
        String userName= principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

//        below is one method
//        List<Contact> contacts=user.getContacts();

//        second method by contactRepositroy;
        Pageable pageable = PageRequest.of(page,5);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
        model.addAttribute("contacts",contacts);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",contacts.getTotalPages());

        return "normal/show_contacts";
    }
}
