package com.wavemaker.leavemanagement.controller;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeManager;
import com.wavemaker.leavemanagement.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping("/employee/leave/employeeDetails")
public class EmployeeServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServlet.class);
    private static ApplicationContext context;
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/addEmployee")
    private Employee addEmployee(HttpServletRequest request, HttpServletResponse response, @RequestBody Employee employee) throws IOException {
        return employeeService.addEmployee(employee);

    }


    @GetMapping("/getEmployeeName")
    private Employee getEmployeeName(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        Employee employee = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId != null) {
                employee = employeeService.getEmployeeByLoginId(loginId);
                if (employee != null) {
                    return employee;
                }
            }
        }
        return employee;
    }

    @GetMapping("/getEmployeeAndManagerDetails")
    private Employee getEmployeeAndManagerDetails(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        EmployeeManager employeeManager = null;
        Employee employee = null;
        String jsonResponse = "";

        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer loginId = (Integer) session.getAttribute("loginId");
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

        }
        return employeeManager;

    }

    @GetMapping("/getEmployeeDetailsAndLeaveSummary")
    private EmployeeLeave getEmployeeDetailsAndLeaveSummary(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "empId", required = true) int empId) throws ServerUnavailableException {
        EmployeeLeave employeeLeave = null;
        employeeLeave = employeeService.getEmployeeDetailsAndLeaveSummary(empId);
        return employeeLeave;


    }

}
