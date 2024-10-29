package com.wavemaker.leavemanagement.repository.impl.indb;

import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.util.DateUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository("employeeLeaveRepositoryImpl")
public class EmployeeLeaveRepositoryImpl implements EmployeeLeaveRepository {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveRepositoryImpl.class);
    private static final String UPDATE_LEAVE_STATUS_TO_APPROVED_QUERY = "UPDATE LeaveRequest lr SET lr.status = 'APPROVED' " +
            "WHERE lr.leaveId =:leaveId";
    private static final String UPDATE_LEAVE_STATUS_TO_REJECTED_QUERY = "UPDATE LeaveRequest lr SET lr.status = 'REJECTED' " +
            "WHERE lr.leaveId =:leaveId";
    private static final String GET_LEAVE_REQUEST_QUERY = "FROM LeaveRequest lr WHERE lr.leaveId =:leaveId";
    private static final String INSERT_LEAVE_REQUEST_QUERY = "INSERT INTO LEAVE_REQUEST (EMPLOYEE_ID, LEAVE_TYPE_ID," +
            " FROM_DATE, TO_DATE, REASON, STATUS, MANAGER_ID, COMMENTS,DATE_OF_APPLICATION) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
    private static final String COUNT_APPROVED_LEAVES_BY_TYPE_QUERY =
            "SELECT new com.wavemaker.leavemanagement.model.LeaveRequest( " +
                    "lr.fromDate, lr.toDate) " +
                    "FROM LeaveRequest lr " +
                    "WHERE lr.employeesByEmployeeId.employeeId = :employeeId " +
                    "AND lr.status = 'APPROVED' " +
                    "AND lr.leaveTypes.leaveTypeId = :leaveTypeId ";
    private static final String SELECT_LEAVE_TYPE_ID_BY_LEAVEID_QUERY =
            "SELECT LEAVE_TYPE_ID FROM LEAVE_REQUEST WHERE LEAVE_ID = ?";


    @Override
    @Transactional
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
        hibernateTemplate.save(leaveRequest);
        return leaveRequest;

    }

    @Override
    @Transactional
    public List<EmployeeLeave> getAppliedLeaves(int employeeId, LeaveRequestStatus status) throws ServerUnavailableException {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        List<EmployeeLeave> employeeLeaves = new ArrayList<>();
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        String hql;
        if (status == LeaveRequestStatus.ALL) {
            hql = "SELECT DISTINCT lr " +
                    "FROM LeaveRequest lr " +
                    "JOIN lr.employeesByEmployeeId e " +
                    "JOIN lr.leaveTypes lt " +
                    "WHERE e.id = :employeeId " +
                    "ORDER BY CASE WHEN lr.status = 'PENDING' THEN 1 ELSE 2 END, lr.dateOfApplication";
        } else {
            hql = "SELECT DISTINCT lr " +
                    "FROM LeaveRequest lr " +
                    "JOIN lr.employeesByEmployeeId e " +
                    "JOIN lr.leaveTypes lt " +
                    "WHERE e.id = :employeeId " +
                    "AND lr.status = :status " +
                    "ORDER BY CASE WHEN lr.status = 'PENDING' THEN 1 ELSE 2 END, lr.dateOfApplication";
        }
        try {
            Query<LeaveRequest> query = session.createQuery(hql, LeaveRequest.class);
            query.setParameter("employeeId", employeeId);
            if (status != LeaveRequestStatus.ALL) {
                query.setParameter("status", status.name());
            }
            leaveRequests = query.list();
            if (leaveRequests != null) {

                for (LeaveRequest leaveRequest : leaveRequests) {
                    EmployeeLeave employeeLeave = new EmployeeLeave();
                    employeeLeave.setLeaveId(leaveRequest.getLeaveId());
                    employeeLeave.setEmployeeId(leaveRequest.getEmployeeId());
                    employeeLeave.setManagerId(leaveRequest.getManagerId());
                    employeeLeave.setEmployeesByEmployeeId(leaveRequest.getEmployeesByEmployeeId());
                    employeeLeave.setEmployeesByManagerId(leaveRequest.getEmployeesByManagerId());
                    employeeLeave.setLeaveType(leaveRequest.getLeaveTypes().getTypeName());
                    employeeLeave.setLeaveTypes(leaveRequest.getLeaveTypes());
                    employeeLeave.setFromDate(leaveRequest.getFromDate());
                    employeeLeave.setToDate(leaveRequest.getToDate());
                    employeeLeave.setReason(leaveRequest.getReason());
                    employeeLeave.setStatus(leaveRequest.getStatus());
                    employeeLeave.setComments(leaveRequest.getComments());
                    employeeLeave.setEmpName(leaveRequest.getEmployeesByEmployeeId().getEmpName());
                    employeeLeave.setTypeLimit(leaveRequest.getLeaveTypes().getLimitForLeaves());
                    employeeLeave.setDateOfApplication(leaveRequest.getDateOfApplication());
                    employeeLeave.setLeaveTypeId(leaveRequest.getLeaveTypes().getLeaveTypeId());
                    int totalLeavesTaken = getTotalNumberOfLeavesTaken(employeeId, employeeLeave.getLeaveTypeId());
                    employeeLeave.setTotalEmployeeLeavesTaken(totalLeavesTaken);
                    int pendingLeaves = employeeLeave.getTypeLimit() - totalLeavesTaken;
                    employeeLeave.setPendingLeaves(pendingLeaves);
                    employeeLeaves.add(employeeLeave);
                }
            }


        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

        return employeeLeaves;

    }

    @Override
    @Transactional
    public EmployeeLeave acceptLeaveRequest(int leaveId) throws ServerUnavailableException {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        try {
            Query<LeaveRequest> query = session.createQuery(UPDATE_LEAVE_STATUS_TO_APPROVED_QUERY);
            query.setParameter("leaveId", leaveId);
            int rows = query.executeUpdate();
            if (rows > 0) {
                Query<LeaveRequest> query1 = session.createQuery(GET_LEAVE_REQUEST_QUERY, LeaveRequest.class);
                query1.setParameter("leaveId", leaveId);
                LeaveRequest leaveRequest = query1.getSingleResult();
                return mapResultSetToLeaveRequest(leaveRequest);
            }
        } catch (Exception e) {
            logger.error("Error accepting leave request", e);
        }
        return null;

    }

    @Override
    @Transactional
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds, LeaveRequestStatus status) throws ServerUnavailableException {
        List<EmployeeLeave> employeeLeaves = new ArrayList<>();
        if (employeeIds == null || employeeIds.isEmpty()) {
            return employeeLeaves;
        }

        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        StringBuilder hql = new StringBuilder();

        hql.append("SELECT DISTINCT lr ")
                .append("FROM LeaveRequest lr ")
                .append("JOIN lr.employeesByEmployeeId  e ")
                .append("JOIN lr.leaveTypes lt ")
                .append("WHERE e.employeeId IN :employeeIds ");

        if (status != LeaveRequestStatus.ALL) {
            hql.append("AND lr.status = :status ");
        }

        hql.append("ORDER BY CASE WHEN lr.status = 'PENDING' THEN 1 ELSE 2 END, lr.dateOfApplication");

        try {
            Query<LeaveRequest> query = session.createQuery(hql.toString(), LeaveRequest.class);
            query.setParameter("employeeIds", employeeIds);

            if (status != LeaveRequestStatus.ALL) {
                query.setParameter("status", status.name());
            }

            List<LeaveRequest> leaveRequests = query.list();

            for (LeaveRequest leaveRequest : leaveRequests) {
                EmployeeLeave employeeLeave = new EmployeeLeave();
                employeeLeave.setLeaveId(leaveRequest.getLeaveId());
                employeeLeave.setEmployeeId(leaveRequest.getEmployeeId());
                employeeLeave.setManagerId(leaveRequest.getManagerId());
                employeeLeave.setFromDate(leaveRequest.getFromDate());
                employeeLeave.setToDate(leaveRequest.getToDate());
                employeeLeave.setReason(leaveRequest.getReason());
                employeeLeave.setStatus(leaveRequest.getStatus());
                employeeLeave.setComments(leaveRequest.getComments());
                employeeLeave.setDateOfApplication(leaveRequest.getDateOfApplication());
                employeeLeave.setLeaveTypeId(leaveRequest.getLeaveTypeId());
                employeeLeave.setEmpName(leaveRequest.getEmployeesByEmployeeId().getEmpName());
                employeeLeave.setLeaveType(leaveRequest.getLeaveTypes().getTypeName());
                employeeLeave.setTypeLimit(leaveRequest.getLeaveTypes().getLimitForLeaves());
                employeeLeave.setEmployeesByEmployeeId(leaveRequest.getEmployeesByEmployeeId());
                employeeLeave.setLeaveTypes(leaveRequest.getLeaveTypes());
                int totalLeavesTaken = getTotalNumberOfLeavesTaken(employeeLeave.getEmployeeId(), employeeLeave.getLeaveTypeId());
                employeeLeave.setTotalEmployeeLeavesTaken(totalLeavesTaken);
                int pendingLeaves = employeeLeave.getTypeLimit() - totalLeavesTaken;
                employeeLeave.setPendingLeaves(pendingLeaves);
                employeeLeaves.add(employeeLeave);
            }

        } catch (Exception e) {
            logger.error("Error fetching leave details for employees", e);
            throw new ServerUnavailableException("Server is unavailable to fetch the leaves of team requests", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return employeeLeaves;
    }

    @Override
    @Transactional
    public int getTotalNumberOfLeavesTaken(int employeeId, int leaveTypeId) {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        int totalLeaves = 0;
        int totalLeavesTaken = 0;
        try {
            Query<LeaveRequest> query = session.createQuery(COUNT_APPROVED_LEAVES_BY_TYPE_QUERY, LeaveRequest.class);
            query.setParameter("employeeId", employeeId);
            query.setParameter("leaveTypeId", leaveTypeId);
            List<LeaveRequest> leaveRequests = query.list();

            for (LeaveRequest leaveRequest : leaveRequests) {
                Date fromDate = Date.valueOf((leaveRequest.getFromDate()));
                Date toDate = Date.valueOf((leaveRequest.getToDate()));
                if (fromDate != null && toDate != null) {
                    int leaveDays = DateUtil.calculateTotalDaysExcludingWeekends(fromDate, toDate);
                    totalLeaves += leaveDays;
                } else {
                    logger.warn("Encountered null date values for employee ID {}", employeeId);
                }
            }


        } catch (Exception e) {
            logger.error("Error fetching leave details for employees", e);
        }
        return totalLeaves;
    }

    @Override
    @Transactional
    public LeaveRequest rejectLeaveRequest(int leaveId) throws ServerUnavailableException {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        try {
            Query<LeaveRequest> query = session.createQuery(UPDATE_LEAVE_STATUS_TO_REJECTED_QUERY);
            query.setParameter("leaveId", leaveId);
            int rows = query.executeUpdate();
            if (rows > 0) {
                Query<LeaveRequest> query1 = session.createQuery(GET_LEAVE_REQUEST_QUERY, LeaveRequest.class);
                query1.setParameter("leaveId", leaveId);
                LeaveRequest leaveRequest = query1.getSingleResult();
                return mapResultSetToLeaveRequest(leaveRequest);
            }
        } catch (Exception e) {
            logger.error("Error accepting leave request", e);
        }
        return null;


    }

    private EmployeeLeave mapResultSetToLeaveRequest(LeaveRequest leaveRequest) throws SQLException {
        EmployeeLeave employeeLeave = new EmployeeLeave();
        employeeLeave.setLeaveId(leaveRequest.getLeaveId());
        employeeLeave.setEmployeeId(leaveRequest.getEmployeeId());
        employeeLeave.setLeaveTypeId(leaveRequest.getLeaveTypeId());
        employeeLeave.setFromDate(leaveRequest.getFromDate());
        employeeLeave.setToDate(leaveRequest.getToDate());
        employeeLeave.setReason(leaveRequest.getReason());
        employeeLeave.setStatus(leaveRequest.getStatus());
        employeeLeave.setManagerId(leaveRequest.getManagerId());
        employeeLeave.setComments(leaveRequest.getComments());
        employeeLeave.setLeaveTypes(leaveRequest.getLeaveTypes());
        employeeLeave.setEmployeesByEmployeeId(leaveRequest.getEmployeesByEmployeeId());
        employeeLeave.setEmployeesByManagerId(leaveRequest.getEmployeesByManagerId());
        employeeLeave.setDateOfApplication(leaveRequest.getDateOfApplication());
        return employeeLeave;

    }

}
