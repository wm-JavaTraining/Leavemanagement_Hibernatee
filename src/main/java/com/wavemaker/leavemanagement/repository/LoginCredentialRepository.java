package com.wavemaker.leavemanagement.repository;

import com.mysql.cj.log.Log;
import com.wavemaker.leavemanagement.model.LoginCredential;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

public interface LoginCredentialRepository {
    public int isValidate(LoginCredential loginCredential) throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException;

    public LoginCredential addEmployeeLogin(LoginCredential loginCredential);
    public LoginCredential findByEmailId(String emailId);
}
