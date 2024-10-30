package com.wavemaker.leavemanagement.repository.impl.indb;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.*;
import com.wavemaker.leavemanagement.repository.EmployeeRepository;
import com.wavemaker.leavemanagement.util.DbConnection;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    private static Logger logger = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);
    private static final String FIND_EMPLOYEES_BY_MANAGER_QUERY =
            "SELECT * FROM EMPLOYEE WHERE MANAGER_ID = ?";

    private static final String INSERT_EMPLOYEE_QUERY =
            "INSERT INTO EMPLOYEE (EMPLOYEE_ID, NAME, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, MANAGER_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String CHECK_MANAGER_QUERY =
            "SELECT COUNT(*) FROM EMPLOYEE WHERE EMAIL = ? AND EMPLOYEE_ID IN " +
                    "(SELECT DISTINCT MANAGER_ID FROM EMPLOYEES WHERE MANAGER_ID IS NOT NULL)";

    private static final String GET_EMPLOYEE_BY_LOGIN_ID_QUERY =
            "SELECT EMPLOYEE_ID FROM LOGIN_CREDENTIAL WHERE LOGIN_ID = ?";

    private static final String GET_EMPLOYEE_DETAILS_QUERY =
            "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_ID = ?";

    private static final String GET_EMPLOYEE_IDS_UNDER_MANAGER_QUERY =
            "SELECT EMPLOYEE_ID FROM EMPLOYEE WHERE MANAGER_ID = ?";

    private static final String GET_EMPLOYEE_MANAGER_DETAILS_QUERY =
            "SELECT e.EMPLOYEE_ID, e.NAME, e.EMAIL, e.DATE_OF_BIRTH, e.PHONE_NUMBER, e.MANAGER_ID, e.GENDER, " +
                    "m.NAME AS MANAGER_NAME, " +
                    "m.EMAIL AS MANAGER_EMAIL, " +
                    "m.DATE_OF_BIRTH AS MANAGER_DATE_OF_BIRTH, " +
                    "m.PHONE_NUMBER AS MANAGER_PHONE_NUMBER, " +
                    "m.GENDER AS MANAGER_GENDER " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN EMPLOYEE m ON e.MANAGER_ID = m.EMPLOYEE_ID " +
                    "WHERE e.EMPLOYEE_ID = ?";
    private static final String GET_EMPLOYEE_DETAILS_AND_LEAVE_SUMMARY_QUERY =
            "SELECT " +
                    "    e.NAME AS employeeName, " +
                    "    e.PHONE_NUMBER AS phoneNumber, " +
                    "    e.EMAIL AS emailId, " +
                    "    lt.TYPE_NAME AS leaveType, " +
                    "    COALESCE(els.TOTAL_LEAVES_TAKEN, 0) AS totalLeavesTaken, " +
                    "    lt.LIMIT_FOR_LEAVES AS leaveTypeLimit " +
                    "FROM " +
                    "    EMPLOYEE e " +
                    "LEFT JOIN " +
                    "    EMPLOYEE_LEAVE_SUMMARY els ON e.EMPLOYEE_ID = els.EMPLOYEE_ID " +
                    "LEFT JOIN " +
                    "    LEAVE_TYPE lt ON els.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE " +
                    "    e.EMPLOYEE_ID = ? " +
                    "ORDER BY " +
                    "    lt.TYPE_NAME";


    @Override
    @Transactional
    public Employee addEmployee(Employee employee) {
        return (Employee) hibernateTemplate.save(employee);
    }

    @Override
    @Transactional
    public Employee getEmployeeByLoginId(int loginId) throws ServerUnavailableException {
        Employee employee = null;
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();

        String hql = "from LoginCredential lc where lc.loginId=:loginId";
        try {
            Query<LoginCredential> query = session.createQuery(hql, LoginCredential.class);
            query.setParameter("loginId", loginId);

            LoginCredential result = query.uniqueResult();

            if (result != null) {
                int employeeId = result.getEmployeeId();
                employee = hibernateTemplate.get(Employee.class, employeeId);
                return employee;
            }

        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

     return  employee;

    }

    @Override
    @Transactional
    public List<Integer> getEmpIdUnderManager(int managerId) throws ServerUnavailableException {
        List<Integer> employeeIdsList = new ArrayList<>();
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();

        String hql = "SELECT e.employeeId FROM Employee e WHERE e.managerId = :managerId";
        try {
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("managerId", managerId);

            employeeIdsList = query.list(); // Directly get the list of employee IDs

            return employeeIdsList;

        } catch (Exception e) {
            logger.error("Error validating user", e);
        }

        return employeeIdsList;

    }
    @Override
    public EmployeeManager getEmployeeManagerDetails(int employeeId) throws ServerUnavailableException {
        EmployeeManager employeeManager = null;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_MANAGER_DETAILS_QUERY)) {

            preparedStatement.setInt(1, employeeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    employeeManager = new EmployeeManager();

                    // Employee details
                    employeeManager.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employeeManager.setEmpName(resultSet.getString("NAME"));
                    employeeManager.setEmail(resultSet.getString("EMAIL"));
                    employeeManager.setDateOfBirth(resultSet.getDate("DATE_OF_BIRTH").toLocalDate());
                    employeeManager.setPhoneNumber(resultSet.getLong("PHONE_NUMBER"));
                    employeeManager.setManagerId(resultSet.getInt("MANAGER_ID"));
                    employeeManager.setGender(resultSet.getString("GENDER"));

                    // Manager details
                    employeeManager.setManagerName(resultSet.getString("MANAGER_NAME"));
                    employeeManager.setManagerEmail(resultSet.getString("MANAGER_EMAIL"));
                    employeeManager.setManagerDateOfBirth(resultSet.getDate("MANAGER_DATE_OF_BIRTH").toLocalDate());
                    employeeManager.setManagerPhoneNumber(resultSet.getLong("MANAGER_PHONE_NUMBER"));
                    employeeManager.setManagerGender(resultSet.getString("MANAGER_GENDER"));
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("unavailable to accept leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employeeManager;
    }

    @Override
    public EmployeeLeave getEmployeeLeaveDetailsAndLeaveSummary(int empId) throws ServerUnavailableException {
        EmployeeLeave employeeLeave = new EmployeeLeave();
        List<EmployeeLeaveSummary> employeeLeaveSummaries = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_DETAILS_AND_LEAVE_SUMMARY_QUERY)) {

            preparedStatement.setInt(1, empId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int totalLeavesTaken = resultSet.getInt("totalLeavesTaken");
                    int leaveTypeLimit = resultSet.getInt("leaveTypeLimit");
                    int pendingLeaves = leaveTypeLimit - totalLeavesTaken;
                    if (employeeLeave.getEmpName() == null) {
                        employeeLeave.setEmpName(resultSet.getString("employeeName"));
                        employeeLeave.setPhoneNumber(resultSet.getLong("phoneNumber")); // Make sure PHONE_NUMBER is of type Long in your DB
                        employeeLeave.setEmail(resultSet.getString("emailId"));
                    }
                    EmployeeLeaveSummary employeeLeaveSummary = new EmployeeLeaveSummary();
                    employeeLeaveSummary.setLeaveType(resultSet.getString("leaveType"));
                    employeeLeaveSummary.setTotalLeavesTaken(totalLeavesTaken);
                    employeeLeaveSummary.setPendingLeaves(pendingLeaves);
                    employeeLeaveSummaries.add(employeeLeaveSummary);
                }

                employeeLeave.setEmployeeLeaveSummaries(employeeLeaveSummaries);
            }

        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to fetch employee leave details", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employeeLeave;
    }

}

