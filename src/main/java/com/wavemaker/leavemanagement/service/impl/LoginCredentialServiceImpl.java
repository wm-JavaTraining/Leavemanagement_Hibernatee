package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.repository.LoginCredentialRepository;
import com.wavemaker.leavemanagement.service.LoginCredentialService;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginCredentialServiceImpl implements LoginCredentialService {

    @Autowired
    private LoginCredentialRepository loginCredentialRepository;


    @Override
    public int isValidate(LoginCredential loginCredential) throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException {
        return loginCredentialRepository.isValidate(loginCredential);

    }

    @Override
    public LoginCredential addEmployeeLogin(LoginCredential loginCredential) {
        return loginCredentialRepository.addEmployeeLogin(loginCredential);
    }

    @Override
    public LoginCredential findByEmailId(String emailId) {
        return  loginCredentialRepository.findByEmailId(emailId);
    }
}
