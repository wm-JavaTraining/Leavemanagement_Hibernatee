package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    @Autowired
    @Qualifier("employeeLeaveRepositoryImpl")
    private EmployeeLeaveRepository employeeLeaveRepository;


    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
        return employeeLeaveRepository.applyLeave(leaveRequest);
    }

    @Override
    public List<EmployeeLeave> getAppliedLeaves(int empId, LeaveRequestStatus status) throws ServerUnavailableException {
        return employeeLeaveRepository.getAppliedLeaves(empId, status);
    }

    @Override
    public EmployeeLeave acceptLeaveRequest(int leaveId) throws ServerUnavailableException {
        return employeeLeaveRepository.acceptLeaveRequest(leaveId);
    }

    @Override
    public LeaveRequest rejectLeaveRequest(int leaveId) throws ServerUnavailableException {
        return employeeLeaveRepository.rejectLeaveRequest(leaveId);
    }


    @Override
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds, LeaveRequestStatus status) throws ServerUnavailableException {
        return employeeLeaveRepository.getLeavesOfEmployees(employeeIds, status);
    }


    @Override
    public int getTotalNumberOfLeavesTaken(int empId, int leaveTypeId) throws SQLException {
        return employeeLeaveRepository.getTotalNumberOfLeavesTaken(empId, leaveTypeId);
    }

}
