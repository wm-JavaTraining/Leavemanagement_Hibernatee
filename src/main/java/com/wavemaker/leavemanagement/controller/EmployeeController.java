package com.wavemaker.leavemanagement.controller;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeManager;
import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.service.EmployeeService;
import com.wavemaker.leavemanagement.service.LoginCredentialService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/employee/leave/employeeDetails")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private static ApplicationContext context;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LoginCredentialService loginCredentialService;

    @PostMapping("/addEmployee")
    private Employee addEmployee(HttpServletRequest request, HttpServletResponse response, @RequestBody Employee employee) throws IOException {
        return employeeService.addEmployee(employee);

    }


    @GetMapping("/getEmployeeName")
    private Employee getEmployeeName(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        Employee employee = null;
        Integer loginId = getCurrentUser();
        if (loginId != null) {
            employee = employeeService.getEmployeeByLoginId(loginId);
            if (employee != null) {
                return employee;
            }
        }
        return employee;
    }

    @GetMapping("/getEmployeeAndManagerDetails")
    private Employee getEmployeeAndManagerDetails(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        EmployeeManager employeeManager = null;
        Employee employee = null;
        String jsonResponse = "";
        Integer loginId = getCurrentUser();
        if (loginId != null) {
            employee = employeeService.getEmployeeByLoginId(loginId);
            if (employee != null) {
                int employeeId = employee.getEmployeeId();
                int managerId = employee.getManagerId();
                if (managerId != 0) {
                    employeeManager = employeeService.getEmployeeManagerDetails(employeeId);
                    return employeeManager;
                } else {
                    employee = employeeService.getEmployeeByLoginId(loginId);
                    return employee;
                }
            }


        }
        return employeeManager;

    }

    @GetMapping("/getEmployeeDetailsAndLeaveSummary")
    private EmployeeLeave getEmployeeDetailsAndLeaveSummary(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "empId", required = true) int empId) throws ServerUnavailableException {
        EmployeeLeave employeeLeave = null;
        employeeLeave = employeeService.getEmployeeDetailsAndLeaveSummary(empId);
        return employeeLeave;


    }

    private Integer getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails != null) {
                LoginCredential loginCredential1 = loginCredentialService.findByEmailId(userDetails.getUsername());
                return loginCredential1.getLoginId();


            }
        }

        return 0;

    }

}
