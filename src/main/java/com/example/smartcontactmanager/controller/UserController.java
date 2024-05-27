package com.example.smartcontactmanager.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.smartcontactmanager.dao.ContactRepository;
import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //    Method for Common data
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
//        System.out.println("userName: " + userName);
        User user = userRepository.getUserByUserName(userName);
//        System.out.println("user: " + user);
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
//            if(file.isEmpty()){
//                System.out.println("file is missing!!");
//            }
//            else{
////                upload file to folder and update the date to contact
//                contact.setImage(file.getOriginalFilename());
//                File saveFile = new ClassPathResource("static/img").getFile();
//                System.out.println("saveFile" + saveFile);
//                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
//                System.out.println("path"+path
//                );
//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Image is Successfully uploaded");
//            }
            if (!file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                // Save file to the static/img directory
                Path uploadPath = Paths.get("static/img/"); // Adjust path as necessary
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }
                contact.setImage(fileName);
                System.out.println("Image is successfully uploaded: " + fileName);
            } else {
                contact.setImage("contact.png");
                System.out.println("File is missing!!");
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

//    showing particluar contact details
    @GetMapping("/{cId}/contact/")
    public String showContactDetails(@PathVariable("cId") Integer cId,
                                     Model model,
                                     Principal principal){

        Optional<Contact> contactOptioal =  this.contactRepository.findById(cId);
        Contact contact = contactOptioal.get();

        String userName=principal.getName();

        User loggedInUser = this.userRepository.getUserByUserName(userName);

        if(loggedInUser.getId() == contact.getUser().getId())
        {
            model.addAttribute("contact",contact);
            model.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }

    //    open add form handler
    @PostMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId, Model model,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Contact> contactOptional = this.contactRepository.findById(cId);
            Contact contact = contactOptional.get();
//            System.out.println("contact:::::"+ contact);
            String userName = principal.getName();
//            System.out.println("Username:::::"+userName);
            User loggedInUser = this.userRepository.getUserByUserName(userName);

//            below method is not deleting the entries from the database
//            if (loggedInUser.getId() == contact.getUser().getId()) {
//
//                contact.setUser(null);
////                remove img contact.getImage
//               this.contactRepository.delete(contact);
//                redirectAttributes.addFlashAttribute("message", new Message("Your contact is deleted. successfully ", "success"));
//
//            }
            loggedInUser.getContacts().remove(contact);
            this.userRepository.save(loggedInUser);
        } catch (Exception e) {
            e.printStackTrace();
          redirectAttributes.addFlashAttribute("message", new Message("Something went wrong! Try again.", "danger"));
        }
        return "redirect:/user/view-contacts/0";
    }

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") int cid, Model model){
        model.addAttribute("title","update contact form");

        Contact contact=this.contactRepository.findById(cid).get();
        model.addAttribute("contact",contact);
        return "normal/update_form";
    }

    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                Model model,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try{
            Contact oldContactDetails= this.contactRepository.findById(contact.getcId()).get();

            if(!file.isEmpty()){

//                upload new file

//                check it, pic is not deleting;
//                delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, String.valueOf(oldContactDetails));
                file1.delete();

//                update new photo
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            }else {
                contact.setImage(oldContactDetails.getImage());
            }
//            System.out.println("Contact name::"+ contact.getName());
//            System.out.println("contact id:::::"+ contact.getcId());
            User user=this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
            redirectAttributes.addFlashAttribute("message", new Message("Your contact is updated successfully!!!! ", "success"));

        }catch(Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", new Message(" Ohh no something went Wrong ", "danger"));

        }

        return "redirect:/user/"+contact.getcId()+"/contact/";
    }

//    your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title","profilePage");
        return "normal/profile";
    }



//    open settings handler
    @GetMapping("/settings")
    public String openSettings(Model model){
        model.addAttribute("title","settingsPage");
        return "normal/settings";
    }

//    change password handler

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            Principal principal,
            RedirectAttributes redirectAttributes
    ){

//            System.out.println("old password:::  " + oldPassword);
//            System.out.println("new password:::  " + newPassword);

            String userName = principal.getName();
            User currentUser = this.userRepository.getUserByUserName(userName);

            if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
                currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                User save = this.userRepository.save(currentUser);
                redirectAttributes.addFlashAttribute("message", new Message("Your Password is changed successfully!!!! ", "success"));

            }
            else{
                redirectAttributes.addFlashAttribute("message", new Message(" Your Password Could not change....Something went wrong ", "danger"));
                return "redirect:/user/settings";
            }

        return "redirect:/user/index";
    }
}
