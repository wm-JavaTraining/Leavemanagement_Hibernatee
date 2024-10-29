package com.wavemaker.leavemanagement.repository.impl.indb;

import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.repository.LoginCredentialRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public class LoginCredentialRepositoryImpl implements LoginCredentialRepository {
    private static final Logger logger = LoggerFactory.getLogger(LoginCredentialRepositoryImpl.class);

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public int isValidate(LoginCredential loginCredential) {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();

        String hql = "FROM LoginCredential lc WHERE lc.emailId = :email AND lc.password = :password";
        try {
            Query<LoginCredential> query = session.createQuery(hql, LoginCredential.class);
            query.setParameter("email", loginCredential.getEmailId());
            query.setParameter("password", loginCredential.getPassword());

            LoginCredential result =  query.uniqueResult();

            if (result != null) {
                int i=result.getLoginId();
                return i;

            }

        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

        return -1;
    }

    @Override
    @Transactional
    public LoginCredential addEmployeeLogin(LoginCredential loginCredential) {
        Serializable serializable = hibernateTemplate.save(loginCredential);
        return (LoginCredential) serializable;
    }

    @Override
    @Transactional
    public LoginCredential findByEmailId(String emailId) {
        String hql = "FROM LoginCredential lc WHERE lc.emailId = :emailId";
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        try {
            Query<LoginCredential> query = session.createQuery(hql, LoginCredential.class);
            query.setParameter("emailId", emailId);
            LoginCredential result = query.uniqueResult();
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            logger.error("Error validating user", e);
        }
        return null;
    }
}
