package com.wavemaker.leavemanagement.repository.impl.indb;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
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

@Repository("employeeLeaveSummaryRepositoryInDb")
public class EmployeeLeaveSummaryRepositoryImpl implements EmployeeLeaveSummaryRepository {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryRepositoryImpl.class);

    private static final String INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "INSERT INTO EMPLOYEE_LEAVE_SUMMARY (EMPLOYEE_ID, LEAVE_TYPE_ID, PENDING_LEAVES, TOTAL_LEAVES_TAKEN, LEAVE_TYPE) " +
                    "VALUES (?, ?, ?, ?, ?);";

    private static final String UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "UPDATE EmployeeLeaveSummary e SET e.totalLeavesTaken = :totalLeavesTaken, e.pendingLeaves = :pendingLeaves " +
                    "WHERE e.employeeId = :employeeId AND e.leaveTypeId = :leaveTypeId";

    private static final String CHECK_EMPLOYEE_LEAVE_SUMMARY_EXIST_QUERY =
            "SELECT COUNT(*) FROM EMPLOYEE_LEAVE_SUMMARY WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?;";

    private static final String SELECT_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "SELECT new com.wavemaker.leavemanagement.model.EmployeeLeaveSummary( " +
                    "els.summaryId, " +
                    "els.employeeId, " +
                    "els.leaveTypeId, " +
                    "lt.typeName, " +
                    "COALESCE(MAX(els.pendingLeaves), 0), " +
                    "COALESCE(SUM(els.totalLeavesTaken), 0) " +
                    ") " +
                    "FROM EmployeeLeaveSummary els " +
                    "LEFT JOIN els.employeeByEmployeeId e on els.employeeId = e.employeeId " +
                    "LEFT JOIN els.leaveTypeByLeaveTypeId lt on els.leaveTypeId = lt.leaveTypeId " +
                    "WHERE e.employeeId = :employeeId " +
                    "GROUP BY els.summaryId, els.employeeId, els.leaveTypeId, lt.typeName";


    private static final String SELECT_TEAM_LEAVE_SUMMARY_QUERY =
            "SELECT new com.wavemaker.leavemanagement.model.EmployeeLeaveSummary( " +
                    "els.summaryId, " +
                    "els.employeeId, " +
                    "els.leaveTypeId, " +
                    "lt.typeName, " +
                    "COALESCE(MAX(els.pendingLeaves), 0), " +
                    "COALESCE(SUM(els.totalLeavesTaken), 0) " +
                    ") " +
                    "FROM EmployeeLeaveSummary els " +
                    "LEFT JOIN els.employeeByEmployeeId e " +
                    "LEFT JOIN els.leaveTypeByLeaveTypeId lt " +
                    "WHERE e.employeeId IN (:employeeIds) " +
                    "GROUP BY els.summaryId, els.employeeId, els.leaveTypeId, lt.typeName";
    private static final String COUNT_APPROVED_LEAVES_BY_TYPE_QUERY =
            "SELECT new com.wavemaker.leavemanagement.model.LeaveRequest( " +
                    "lr.fromDate, lr.toDate) " +
                    "FROM LeaveRequest lr " +
                    "WHERE lr.employeesByEmployeeId.employeeId = :employeeId " +
                    "AND lr.status = 'APPROVED' " +
                    "AND lr.leaveTypes.leaveTypeId = :leaveTypeId ";
    private static final String SELECT_PERSONAL_HOLIDAYS_QUERY =
            "SELECT new com.wavemaker.leavemanagement.model.LeaveRequest(lr.fromDate, lr.toDate, lt, lr.reason) " +
                    "FROM LeaveRequest lr " +
                    "JOIN lr.leaveTypes lt " +
                    "WHERE lr.employeeId = :employeeId " +
                    "AND lr.status = 'APPROVED'";


    @Override
    @Transactional
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException {
        List<EmployeeLeaveSummary> leaveSummaryList = new ArrayList<>();
        Session session = hibernateTemplate.getSessionFactory().openSession();
        try {
            Query<EmployeeLeaveSummary> query = session.createQuery(SELECT_EMPLOYEE_LEAVE_SUMMARY_QUERY, EmployeeLeaveSummary.class);
            query.setParameter("employeeId", employeeId);
            leaveSummaryList = query.list();
        } catch (Exception e) {
            logger.error("Error while fetching leave summary for Employee ID: {}", employeeId, e);
        }
        return leaveSummaryList;

    }

    @Override
    @Transactional
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException {
        if (employeeIds == null || employeeIds.isEmpty()) {
            throw new IllegalArgumentException("Employee IDs list cannot be null or empty");
        }

        List<EmployeeLeaveSummary> leaveSummaryList;
        try (Session session = hibernateTemplate.getSessionFactory().openSession()) {
            Query<EmployeeLeaveSummary> query = session.createQuery(SELECT_TEAM_LEAVE_SUMMARY_QUERY, EmployeeLeaveSummary.class);
            query.setParameterList("employeeIds", employeeIds); // Bind the list of employee IDs

            logger.debug("Fetching leave summaries for Employee IDs: {}", employeeIds);
            leaveSummaryList = query.getResultList();

            logger.debug("Retrieved {} leave summaries for employee IDs: {}", leaveSummaryList.size(), employeeIds);
        } catch (Exception e) {
            logger.error("Error fetching leave summaries for employee IDs: {}", employeeIds, e);
            throw new ServerUnavailableException("Server is unavailable to fetch leave summaries for employees", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveSummaryList;
    }

    @Override
    public List<Holiday> getPersonalHolidays(int employeeId) throws ServerUnavailableException {
        List<Holiday> holidays = new ArrayList<>();
        Session session = hibernateTemplate.getSessionFactory().openSession();
        try {
            Query<LeaveRequest> query = session.createQuery(SELECT_PERSONAL_HOLIDAYS_QUERY, LeaveRequest.class);
            query.setParameter("employeeId", employeeId);
            List<LeaveRequest> leaveRequests = query.list();
            for (LeaveRequest lr : leaveRequests) {
                Holiday holiday = new Holiday();
                holiday.setHolidayStartDate(lr.getFromDate());
                holiday.setHolidayEndDate(lr.getToDate());
                holiday.setHolidayName(lr.getLeaveTypes().getTypeName());
                holiday.setReason(lr.getReason());
                holidays.add(holiday);
            }
        } catch (Exception e) {
            logger.error("Error fetching personal holidays", e);
            throw new ServerUnavailableException("Server is unavailable to fetch personal holidays", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return holidays;
    }


    @Override
    @Transactional
    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws
            ServerUnavailableException {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        boolean isRecordExists = false;

        int totalLeaves = 0;
        try {
            Query<LeaveRequest> query = session.createQuery(COUNT_APPROVED_LEAVES_BY_TYPE_QUERY, LeaveRequest.class);
            query.setParameter("employeeId", employeeLeaveSummary.getEmployeeId());
            query.setParameter("leaveTypeId", employeeLeaveSummary.getLeaveTypeId());
            List<LeaveRequest> leaveRequests = query.list();

            for (LeaveRequest leaveRequest : leaveRequests) {
                Date fromDate = Date.valueOf((leaveRequest.getFromDate()));
                Date toDate = Date.valueOf((leaveRequest.getToDate()));
                if (fromDate != null && toDate != null) {
                    int leaveDays = DateUtil.calculateTotalDaysExcludingWeekends(fromDate, toDate);
                    totalLeaves += leaveDays;
                } else {
                    logger.warn("Encountered null date values for employee ID {}", employeeLeaveSummary.getEmployeeId());
                }
            }
            String getCountQuery = "select count(els) from EmployeeLeaveSummary els where els.employeeId = :employeeId and els.leaveTypeId = :leaveTypeId";
            Query<Long> countQuery = session.createQuery(getCountQuery, Long.class);
            countQuery.setParameter("employeeId", employeeLeaveSummary.getEmployeeId());
            countQuery.setParameter("leaveTypeId", employeeLeaveSummary.getLeaveTypeId());
            Long resultCount = countQuery.uniqueResult();

            if (resultCount > 0) {
                logger.debug("Record exists for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                isRecordExists = true;

            } else {
                isRecordExists = false;
            }
            int pendingLeaves = employeeLeaveSummary.getLeaveTypeByLeaveTypeId().getLimitForLeaves() - totalLeaves;

            if (isRecordExists) {
                int rowsAffected = session.createQuery(UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY)
                        .setParameter("totalLeavesTaken", totalLeaves)
                        .setParameter("pendingLeaves", pendingLeaves)
                        .setParameter("employeeId", employeeLeaveSummary.getEmployeeId())
                        .setParameter("leaveTypeId", employeeLeaveSummary.getLeaveTypeId())
                        .executeUpdate();

                if (rowsAffected > 0) {
                    logger.debug("Successfully updated leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                } else {
                    logger.error("Failed to update leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                    throw new ServerUnavailableException("Failed to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }

            } else {
                employeeLeaveSummary.setTotalLeavesTaken(totalLeaves);
                employeeLeaveSummary.setPendingLeaves(pendingLeaves);
                session.save(employeeLeaveSummary);
                logger.debug("Successfully inserted leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
            }

        } catch (Exception e) {
            logger.error("Error while adding or updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
            throw new ServerUnavailableException("Server is unavailable to add or update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employeeLeaveSummary;
    }


    @Override
    @Transactional
    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws
            ServerUnavailableException {
        logger.debug("Updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        int totaLeavesTaken = 0;

        try {
            Query<LeaveRequest> query = session.createQuery(COUNT_APPROVED_LEAVES_BY_TYPE_QUERY, LeaveRequest.class);
            query.setParameter("employeeId", employeeLeaveSummary.getEmployeeId());
            query.setParameter("leaveTypeId", employeeLeaveSummary.getLeaveTypeId());
            List<LeaveRequest> leaveRequests = query.list();

            for (LeaveRequest leaveRequest : leaveRequests) {
                Date fromDate = Date.valueOf((leaveRequest.getFromDate()));
                Date toDate = Date.valueOf((leaveRequest.getToDate()));
                if (fromDate != null && toDate != null) {
                    int leaveDays = DateUtil.calculateTotalDaysExcludingWeekends(fromDate, toDate);
                    totaLeavesTaken += leaveDays;
                } else {
                    logger.warn("Encountered null date values for employee ID {}", employeeLeaveSummary.getEmployeeId());
                }
            }
            int totalLeavesTaken = getTotalLeavesTaken(employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
            int pendingLeaves = employeeLeaveSummary.getLeaveTypeByLeaveTypeId().getLimitForLeaves() - totalLeavesTaken;
            Query<EmployeeLeaveSummary> updateQuery = session.createQuery(UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY);
            updateQuery.setParameter("totalLeavesTaken", totalLeavesTaken);
            updateQuery.setParameter("pendingLeaves", pendingLeaves);
            updateQuery.setParameter("employeeId", employeeLeaveSummary.getEmployeeId());
            updateQuery.setParameter("leaveTypeId", employeeLeaveSummary.getLeaveTypeId());
            int count = updateQuery.executeUpdate();
            if (count > 0) {
                logger.debug("Successfully updated leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                return true;
            } else {
                logger.error("Failed to update leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                throw new ServerUnavailableException("Failed to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            logger.error("Error while updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
            throw new ServerUnavailableException("Server is unavailable to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private int getTotalLeavesTaken(int employeeId, int leaveTypeId) throws SQLException {
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
}
