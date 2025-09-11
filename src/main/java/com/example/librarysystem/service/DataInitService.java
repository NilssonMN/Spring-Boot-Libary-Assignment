package com.example.librarysystem.service;

import com.example.librarysystem.entity.User;
import com.example.librarysystem.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitService
{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initData()
    {
        if (userRepository.findByEmailIgnoreCase("user@test.com").isEmpty())
        {
            User user = new User();
            user.setEmail("user@test.com");
            user.setFirstName("User");
            user.setLastName("Usersson");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole("USER");
            userRepository.save(user);
        }

        if (userRepository.findByEmailIgnoreCase("admin@test.com").isEmpty())
        {
            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setFirstName("Admin");
            admin.setLastName("Adminson");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }


        System.out.println("ADMIN ÄR SKAPAD");
        System.out.println("USER ÄR SKAPAPAPADPADPA");
    }
}
