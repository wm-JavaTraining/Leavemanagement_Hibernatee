package com.wavemaker.leavemanagement.repository.impl.indb;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.LeaveType;
import com.wavemaker.leavemanagement.repository.LeaveTypeRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LeaveTypeRepositoryImpl implements LeaveTypeRepository {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    private static final Logger logger = LoggerFactory.getLogger(LeaveTypeRepositoryImpl.class);
    private static final String GET_NUMBER_OF_LEAVES_ALLOCATED =
            "FROM LeaveType lt WHERE lt.typeName = :typeName";
    private static final String SELECT_LEAVE_TYPE_QUERY =
            "FROM LeaveType lt WHERE lt.leaveTypeId = :leaveTypeId";
    private static final String SELECT_LEAVE_TYPE_ID_QUERY =
            "FROM LeaveType lt WHERE lt.typeName = :typeName";


    @Override
    @Transactional
    public int getNumberOfLeavesAllocated(String leaveType) {
        int leaveLimit = 0;
        String leaveTypeName = leaveType.trim();
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        try {
            Query<LeaveType> query = session.createQuery(GET_NUMBER_OF_LEAVES_ALLOCATED, LeaveType.class);
            query.setParameter("typeName", leaveTypeName);
            LeaveType result = query.uniqueResult();

            if (result != null) {
                return result.getLimitForLeaves();

            }

        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

        return leaveLimit;

    }

    @Override
    @Transactional
    public int getLeaveTypeId(String leaveType) {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        try {
            Query<LeaveType> query = session.createQuery(SELECT_LEAVE_TYPE_ID_QUERY, LeaveType.class);
            query.setParameter("typeName", leaveType);
            LeaveType result = query.uniqueResult();

            if (result != null) {
                return result.getLeaveTypeId();
            }

        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

        return -1;
    }

    @Override
    @Transactional
    public String getLeaveType(int leaveTypeId) throws ServerUnavailableException {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        try {
            Query<LeaveType> query = session.createQuery(SELECT_LEAVE_TYPE_QUERY , LeaveType.class);
            query.setParameter("typeName", leaveTypeId);
            LeaveType result = query.uniqueResult();

            if (result != null) {
                return result.getTypeName();
            }

        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

        return "";
    }

}
