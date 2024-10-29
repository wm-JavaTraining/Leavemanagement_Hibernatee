package com.wavemaker.leavemanagement.config;

import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.service.LoginCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

    //    @Autowired
//    private EmployeePasswordService employeePasswordService;
    @Autowired
    private LoginCredentialService loginCredentialService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       LoginCredential loginCredential = loginCredentialService.findByEmailId(email);
        if (loginCredential == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new User(loginCredential.getEmailId(), loginCredential.getPassword(), Collections.emptyList());
    }
}