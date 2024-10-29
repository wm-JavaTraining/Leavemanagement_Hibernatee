package com.wavemaker.leavemanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;
import com.wavemaker.leavemanagement.service.EmployeeLeaveSummaryService;
import com.wavemaker.leavemanagement.service.EmployeeService;
import com.wavemaker.leavemanagement.util.LocalDateAdapter;
import com.wavemaker.leavemanagement.util.LocalTimeAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/employee/leave")
public class EmployeeLeaveController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveController.class);

    @Autowired
    private EmployeeLeaveService employeeLeaveService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeLeaveSummaryService employeeLeaveSummaryService;

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    @PostMapping("/apply")
    public ResponseEntity<String>  applyEmployeeLeave(HttpServletRequest request , @RequestBody EmployeeLeave employeeLeave) {
        HttpSession session =null;
        LeaveRequest addLeaveRequest = null;
        String leaveType = employeeLeave.getLeaveType();
        if (leaveType == null || leaveType.isEmpty()) {
            return ResponseEntity.badRequest().body("Leave type is required.");
        }

        try {
            session = request.getSession();
            Integer loginId = (Integer)session.getAttribute("loginId");
            if (loginId == null) {
                return ResponseEntity.status(401).body("User is not logged in.");
            }

            Employee employee = employeeService.getEmployeeByLoginId(loginId);
            if (employee == null) {
                return ResponseEntity.status(404).body("Employee not found.");
            }

            int numberOfLeavesAllocated = employeeLeaveService.getNumberOfLeavesAllocated(leaveType);
            logger.info("Final Leave limit for type '{}' is: {}", leaveType, numberOfLeavesAllocated);
            employeeLeave.setTypeLimit(numberOfLeavesAllocated);
            int totalNumberOfLeavesTaken = employeeLeaveService.getTotalNumberOfLeavesTaken(employee.getEmployeeId(), employeeLeave.getLeaveTypeId());
            logger.info("Total Leaves Taken: {}", totalNumberOfLeavesTaken);
            employeeLeave.setTotalEmployeeLeavesTaken(totalNumberOfLeavesTaken);

             addLeaveRequest = employeeLeaveService.applyLeave(employeeLeave);
            if (addLeaveRequest == null) {
                return ResponseEntity.status(500).body("Failed to apply leave request.");
            }

            EmployeeLeaveSummary employeeSummary = new EmployeeLeaveSummary();
            employeeSummary.setEmployeeId(addLeaveRequest.getEmployeeId());
            employeeSummary.setLeaveType(leaveType);
            employeeSummary.setLeaveTypeId(addLeaveRequest.getLeaveTypeId());
            employeeSummary.setTotalAllocatedLeaves(numberOfLeavesAllocated);
            employeeSummary.setTotalLeavesTaken(totalNumberOfLeavesTaken);
            employeeLeaveSummaryService.addEmployeeLeaveSummary(employeeSummary);

            return ResponseEntity.ok(gson.toJson(employeeLeave));

        } catch (ServerUnavailableException e) {
            return ResponseEntity.status(503).body("Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while inserting the leave request: " + e.getMessage());
        }
    }

    @GetMapping("/applied")
    public ResponseEntity<String> getAppliedLeaves(HttpSession session, @RequestParam String status) {
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Status is empty.");
        }

        try {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId == null) {
                return ResponseEntity.status(401).body("User is not logged in.");
            }

            Employee employee = employeeService.getEmployeeByLoginId(loginId);
            if (employee == null) {
                return ResponseEntity.status(404).body("Employee not found.");
            }

            int employeeId = employee.getEmployeeId();
            List<EmployeeLeave> leaveRequests = employeeLeaveService.getAppliedLeaves(employeeId, LeaveRequestStatus.valueOf(status));
            return ResponseEntity.ok(gson.toJson(leaveRequests));

        } catch (ServerUnavailableException e) {
            return ResponseEntity.status(503).body("Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while fetching leaves for the logged-in employee: " + e.getMessage());
        }
    }

    @GetMapping("/team-requests")
    public ResponseEntity<String> getMyTeamRequests(HttpSession session, @RequestParam String status) {
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Status is empty.");
        }

        try {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId == null) {
                return ResponseEntity.status(401).body("User is not logged in.");
            }

            Employee employee = employeeService.getEmployeeByLoginId(loginId);
            int managerId = employee.getEmployeeId();
            List<Integer> employeeIds = employeeService.getEmpIdUnderManager(managerId);
            List<EmployeeLeave> employeeLeaves = employeeLeaveService.getLeavesOfEmployees(employeeIds, LeaveRequestStatus.valueOf(status));
            return ResponseEntity.ok(gson.toJson(employeeLeaves));

        } catch (ServerUnavailableException e) {
            return ResponseEntity.status(503).body("Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving team leave requests: " + e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptLeaveRequest(HttpSession session, @RequestParam int leaveId) {
        try {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId == null) {
                return ResponseEntity.status(401).body("User is not logged in.");
            }

            EmployeeLeave employeeLeave = employeeLeaveService.acceptLeaveRequest(leaveId);
            if (employeeLeave != null) {
                EmployeeLeaveSummary employeeLeaveSummary = new EmployeeLeaveSummary();
                String leaveType = employeeLeaveService.getLeaveType(employeeLeave.getLeaveTypeId());
                employeeLeaveSummary.setLeaveType(leaveType);
                employeeLeaveSummary.setLeaveTypeId(employeeLeave.getLeaveTypeId());
                employeeLeaveSummary.setEmployeeId(employeeLeave.getEmployeeId());
                int numberOfLeavesAllocated = employeeLeaveService.getNumberOfLeavesAllocated(leaveType);
                employeeLeaveSummary.setTotalAllocatedLeaves(numberOfLeavesAllocated);
                int totalNumberOfLeavesTaken = employeeLeaveService.getTotalNumberOfLeavesTaken(employeeLeave.getEmployeeId(), employeeLeave.getLeaveTypeId());
                logger.info("Total Leaves Taken: {}", totalNumberOfLeavesTaken);
                employeeLeaveSummary.setTotalLeavesTaken(totalNumberOfLeavesTaken);
                employeeLeaveSummaryService.updateEmployeeLeaveSummary(employeeLeaveSummary);
                return ResponseEntity.ok(gson.toJson(employeeLeave));
            } else {
                return ResponseEntity.status(404).body("Leave request not found or already accepted.");
            }
        } catch (ServerUnavailableException e) {
            return ResponseEntity.status(503).body("Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while accepting the leave request: " + e.getMessage());
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectLeaveRequest(HttpSession session, @RequestParam int leaveId) {
        try {
            Integer loginId = (Integer) session.getAttribute("loginId");
            if (loginId == null) {
                return ResponseEntity.status(401).body("User is not logged in.");
            }

            LeaveRequest leaveRequest = employeeLeaveService.rejectLeaveRequest(leaveId);
            if (leaveRequest != null) {
                return ResponseEntity.ok(gson.toJson(leaveRequest));
            } else {
                return ResponseEntity.status(404).body("Leave request not found.");
            }
        } catch (ServerUnavailableException e) {
            return ResponseEntity.status(503).body("Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while rejecting the leave request: " + e.getMessage());
        }
    }
}
