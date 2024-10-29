package com.wavemaker.leavemanagement.controller;


import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.*;
import com.wavemaker.leavemanagement.service.*;
import com.wavemaker.leavemanagement.util.GetCurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
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

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private LoginCredentialService loginCredentialService;


    private GetCurrentUserUtil getCurrentUserUtil = new GetCurrentUserUtil();

    @PostMapping
    public LeaveRequest applyEmployeeLeave(HttpServletRequest request, @RequestBody LeaveRequest leaveRequest,
                                           @RequestParam(value = "leaveType", required = false) String leaveType) throws ServerUnavailableException, SQLException {
        Integer loginId = getCurrentUser();
        LeaveRequest addLeaveRequest = null;
        EmployeeLeave employeeLeave = null;
        if (loginId != null) {

            Employee employee = employeeService.getEmployeeByLoginId(loginId);
            if (employee != null) {
                employeeLeave = new EmployeeLeave();
                employeeLeave.setEmployeeId(leaveRequest.getEmployeeId());
                employeeLeave.setLeaveId(leaveRequest.getLeaveId());
                employeeLeave.setLeaveTypeId(leaveRequest.getLeaveTypeId());
                employeeLeave.setFromDate(leaveRequest.getFromDate());
                employeeLeave.setToDate(leaveRequest.getToDate());
                employeeLeave.setReason(leaveRequest.getReason());
                employeeLeave.setStatus(leaveRequest.getStatus());
                employeeLeave.setManagerId(leaveRequest.getManagerId());
                employeeLeave.setComments(leaveRequest.getComments());
                employeeLeave.setDateOfApplication(leaveRequest.getDateOfApplication());
                int numberOfLeavesAllocated = leaveTypeService.getNumberOfLeavesAllocated(leaveType);
                logger.info("Final Leave limit for type '{}' is: {}", leaveType, numberOfLeavesAllocated);
                employeeLeave.setTypeLimit(numberOfLeavesAllocated);
                int totalNumberOfLeavesTaken = employeeLeaveService.getTotalNumberOfLeavesTaken(employee.getEmployeeId(), employeeLeave.getLeaveTypeId());
                logger.info("Total Leaves Taken: {}", totalNumberOfLeavesTaken);
                employeeLeave.setTotalEmployeeLeavesTaken(totalNumberOfLeavesTaken);

                addLeaveRequest = employeeLeaveService.applyLeave(leaveRequest);
                if (addLeaveRequest != null) {
                    EmployeeLeaveSummary employeeSummary = new EmployeeLeaveSummary();
                    employeeSummary.setEmployeeByEmployeeId(employee);
                    employeeSummary.setLeaveTypeByLeaveTypeId(addLeaveRequest.getLeaveTypes());
                    employeeSummary.setEmployeeId(addLeaveRequest.getEmployeeId());
                    employeeSummary.setLeaveType(leaveType);
                    employeeSummary.setLeaveTypeId(addLeaveRequest.getLeaveTypeId());
                    //employeeSummary.setTotalAllocatedLeaves(numberOfLeavesAllocated);
                    employeeSummary.setTotalLeavesTaken(totalNumberOfLeavesTaken);
                    employeeLeaveSummaryService.addEmployeeLeaveSummary(employeeSummary);


                }
            }

        }
        return addLeaveRequest;
    }

    @GetMapping("/appliedLeaves")
    public List<EmployeeLeave> getAppliedLeaves(HttpServletRequest request, @RequestParam(value = "status", required = true) String status) throws ServerUnavailableException {
        List<EmployeeLeave> leaveRequests = null;
        Integer loginId = getCurrentUser();
        if (loginId != null) {
            Employee employee = employeeService.getEmployeeByLoginId(loginId);
            if (employee != null) {
                int employeeId = employee.getEmployeeId();
                leaveRequests = employeeLeaveService.getAppliedLeaves(employeeId, LeaveRequestStatus.valueOf(status));
                return leaveRequests;
            }
        }

        return leaveRequests;
    }

    @GetMapping("/team-requests")
    public List<EmployeeLeave> getMyTeamRequests(HttpServletRequest request, @RequestParam(value = "status", required = true) String status) throws ServerUnavailableException {
        List<EmployeeLeave> employeeLeaves = null;
        Integer loginId = getCurrentUser();
        if (loginId != null) {
            Employee employee = employeeService.getEmployeeByLoginId(loginId);
            int managerId = employee.getEmployeeId();
            List<Integer> employeeIds = employeeService.getEmpIdUnderManager(managerId);
            employeeLeaves = employeeLeaveService.getLeavesOfEmployees(employeeIds, LeaveRequestStatus.valueOf(status));

        }
        return employeeLeaves;
    }

    @PutMapping("/acceptLeaveRequest")
    public EmployeeLeave acceptLeaveRequest(HttpServletRequest request, @RequestParam(value = "leaveId", required = true) int leaveId) throws ServerUnavailableException, SQLException {
        EmployeeLeave employeeLeave = null;
        Integer loginId = getCurrentUser();
        if (loginId != null) {

            employeeLeave = employeeLeaveService.acceptLeaveRequest(leaveId);
            if (employeeLeave != null) {
                EmployeeLeaveSummary employeeLeaveSummary = new EmployeeLeaveSummary();
                String leaveType = leaveTypeService.getLeaveType(employeeLeave.getLeaveTypeId());
                employeeLeaveSummary.setLeaveType(leaveType);
                employeeLeaveSummary.setLeaveTypeId(employeeLeave.getLeaveTypeId());
                employeeLeaveSummary.setEmployeeId(employeeLeave.getEmployeeId());
                employeeLeaveSummary.setLeaveTypeByLeaveTypeId(employeeLeave.getLeaveTypes());
                employeeLeaveSummary.setEmployeeByEmployeeId(employeeLeave.getEmployeesByEmployeeId());
                int numberOfLeavesAllocated = leaveTypeService.getNumberOfLeavesAllocated(leaveType);
                // employeeLeaveSummary.setTotalAllocatedLeaves(numberOfLeavesAllocated);
                int totalNumberOfLeavesTaken = employeeLeaveService.getTotalNumberOfLeavesTaken(employeeLeave.getEmployeeId(), employeeLeave.getLeaveTypeId());
                logger.info("Total Leaves Taken: {}", totalNumberOfLeavesTaken);
                employeeLeaveSummary.setTotalLeavesTaken(totalNumberOfLeavesTaken);
                employeeLeaveSummaryService.updateEmployeeLeaveSummary(employeeLeaveSummary);

            }
        }
        return employeeLeave;
    }

    @PutMapping("/rejectLeaveRequest")
    public LeaveRequest rejectLeaveRequest(HttpServletRequest request, @RequestParam(value = "leaveId", required = true) int leaveId) throws ServerUnavailableException {
        LeaveRequest leaveRequest = null;
        Integer loginId = getCurrentUser();
        if (loginId != null) {
            leaveRequest = employeeLeaveService.rejectLeaveRequest(leaveId);

        }
        return leaveRequest;
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
