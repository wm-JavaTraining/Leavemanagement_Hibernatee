package com.wavemaker.leavemanagement.controller;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;
import com.wavemaker.leavemanagement.service.EmployeeLeaveSummaryService;
import com.wavemaker.leavemanagement.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/employee/leave/summary")
public class EmployeeLeaveSummaryServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryServlet.class);
    private static ApplicationContext context;
    @Autowired
    private EmployeeLeaveService employeeLeaveService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeLeaveSummaryService employeeLeaveSummaryService;

    @GetMapping("/getEmployeeLeaveSummary")
    private List<EmployeeLeaveSummary> getEmployeeLeaveSummary(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        Employee employee = null;
        String jsonResponse = null;
        List<EmployeeLeaveSummary> employeeLeaveSummary = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId != null) {
                employee = employeeService.getEmployeeByLoginId(loginId);
                if (employee != null) {
                    int employeeId = employee.getEmployeeId();
                    employeeLeaveSummary = employeeLeaveSummaryService.getEmployeeLeaveSummaryByEmpId(employeeId);

                }
            }
        }
        return employeeLeaveSummary;
    }

    @GetMapping("/getTeamLeaveSummary")
    private List<EmployeeLeaveSummary> getTeamLeaveSummary(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        Employee manager = null;
        String jsonResponse = null;
        List<EmployeeLeaveSummary> employeeLeaveSummaries = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId != null) {
                manager = employeeService.getEmployeeByLoginId(loginId);
                if (manager != null) {
                    int managerId = manager.getEmployeeId();
                    List<Integer> employeeIds = employeeService.getEmpIdUnderManager(managerId);
                    if (employeeIds != null && !employeeIds.isEmpty()) {
                        employeeLeaveSummaries = employeeLeaveSummaryService.getEmployeeLeaveSummaryByEmpIds(employeeIds);
                    }
                }
            }

        }
        return employeeLeaveSummaries;
    }

    @GetMapping("/getLeaveLimitsForLeaveType")
    private EmployeeLeave getLeaveLimitsForLeaveType(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="leaveType",required = true) String leaveType) throws ServerUnavailableException, SQLException {
        Employee employee = null;
        EmployeeLeave leaveDetails = null;
        String jsonResponse = null;
        if (leaveType != null && !leaveType.trim().isEmpty()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    employee = employeeService.getEmployeeByLoginId(loginId);
                    if (employee != null) {
                        int employeeId = employee.getEmployeeId();
                        int leaveLimit = employeeLeaveService.getNumberOfLeavesAllocated(leaveType);
                        int leaveTypeId = employeeLeaveService.getLeaveTypeId(leaveType);
                        int leavesTaken = employeeLeaveService.getTotalNumberOfLeavesTaken(employeeId, leaveTypeId);
                        leaveDetails = new EmployeeLeave();
                        leaveDetails.setEmployeeId(employeeId);
                        leaveDetails.setTypeLimit(leaveLimit);
                        leaveDetails.setLeaveTypeId(leaveTypeId);
                        leaveDetails.setTotalEmployeeLeavesTaken(leavesTaken);

                    }

                }
            }
        }
        return leaveDetails;

    }

    @GetMapping("/getPersonalHolidays")
    private List<Holiday> getPersonalHolidays(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        Employee employee = null;
        String jsonResponse = null;
        List<Holiday> holidays = null;

        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId != null) {
                employee = employeeService.getEmployeeByLoginId(loginId);
                if (employee != null) {
                    int employeeId = employee.getEmployeeId();
                    holidays = employeeLeaveSummaryService.getPersonalHolidays(employeeId);
                }
            }
        }
        return holidays;

    }

}
