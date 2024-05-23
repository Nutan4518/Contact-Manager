package com.example.smartcontactmanager.config;

import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;


public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        fetching user form database
        User user = userRepository.getUserByUserName(username);
        if(user==null) {
            throw new UsernameNotFoundException("Could Not Found User!!");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;

    }
}
