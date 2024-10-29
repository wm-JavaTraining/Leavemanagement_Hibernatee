package com.wavemaker.leavemanagement.util;

import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.service.LoginCredentialService;
import com.wavemaker.leavemanagement.service.impl.LoginCredentialServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class GetCurrentUserUtil {

    private LoginCredentialService loginCredentialService = new LoginCredentialServiceImpl();

    public int getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
           UserDetails userDetails  = (UserDetails) authentication.getPrincipal();
            if (userDetails != null) {
               LoginCredential  loginCredential1 = loginCredentialService.findByEmailId(userDetails.getUsername());
               return  loginCredential1.getLoginId();



            }
        }

        return 0;
    }

}
