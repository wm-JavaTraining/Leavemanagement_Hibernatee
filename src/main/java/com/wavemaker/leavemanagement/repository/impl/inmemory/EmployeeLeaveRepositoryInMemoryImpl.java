package com.wavemaker.leavemanagement.repository.impl.inmemory;

import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.repository.LeaveTypeRepository;
import com.wavemaker.leavemanagement.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository("EmployeeLeaveRepositoryInMemory")
public class EmployeeLeaveRepositoryInMemoryImpl implements EmployeeLeaveRepository {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveRepositoryInMemoryImpl.class);
    private static ConcurrentHashMap<Integer, EmployeeLeave> employeeLeaveMap = new ConcurrentHashMap<>();
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
        int leaveId = -1;
        leaveId = getMaxLeaveId();
        leaveId += 1;
        leaveRequest.setLeaveId(leaveId);
        logger.debug("Generated new leave Request ID: {}", leaveId);

        logger.info("Adding leave request with ID: {}", leaveRequest.getLeaveId());

        if (employeeLeaveMap.containsKey(leaveRequest.getLeaveId())) {
            logger.error("Leave Request with  Leave ID {} already exists.", leaveRequest.getLeaveId());

        }
        employeeLeaveMap.put(leaveRequest.getLeaveId(),(EmployeeLeave) leaveRequest);
        logger.info("leave request  with ID {} added successfully.", leaveRequest.getLeaveId());
        return leaveRequest;
    }

    @Override
    public List<EmployeeLeave> getAppliedLeaves(int empId, LeaveRequestStatus status) throws ServerUnavailableException {
//        return new ArrayList<>(employeeLeaveMap.values());
        return employeeLeaveMap.values()
                .stream()
                .filter(leave -> leave.getEmployeeId() == empId && leave.getStatus().equals(String.valueOf(status)))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeLeave acceptLeaveRequest(int leaveId) throws ServerUnavailableException {
        EmployeeLeave employeeLeave = employeeLeaveMap.get(leaveId);
        employeeLeave.setStatus("APPROVED");
        employeeLeaveMap.put(leaveId, employeeLeave);

        return employeeLeave;
    }

    @Override
    public LeaveRequest rejectLeaveRequest(int leaveId) throws ServerUnavailableException {
        EmployeeLeave employeeLeave = employeeLeaveMap.get(leaveId);
        employeeLeave.setStatus("REJECTED");
        employeeLeaveMap.put(leaveId, employeeLeave);

        return employeeLeave;
    }

    @Override
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds, LeaveRequestStatus status) throws ServerUnavailableException {
        return employeeLeaveMap.values()
                .stream()
                .filter(leave -> employeeIds.contains(leave.getEmployeeId()) && leave.getStatus().equals(String.valueOf(status)))
                .collect(Collectors.toList());
    }


    @Override
    public int getTotalNumberOfLeavesTaken(int empId, int leaveTypeId) throws SQLException {
        int totalLeavesTaken = 0;
        List<EmployeeLeave> employeeLeaves = employeeLeaveMap.values()
                .stream()
                .filter(leave -> leave.getEmployeeId() == empId && leave.getLeaveTypeId() == leaveTypeId && leave.getStatus().equals("APPROVED"))
                .collect(Collectors.toList());
        for (EmployeeLeave employeeLeave : employeeLeaves) {
            Date fromDate = Date.valueOf(employeeLeave.getFromDate());
            Date toDate = Date.valueOf(employeeLeave.getToDate());
            int leaveDays = DateUtil.calculateTotalDaysExcludingWeekends(fromDate, toDate);
            totalLeavesTaken += leaveDays;
        }
        return totalLeavesTaken;

    }


    private int getMaxLeaveId() {
        int maxLeaveId = 0;
        for (int i : employeeLeaveMap.keySet()) {
            maxLeaveId = Math.max(maxLeaveId, i);
        }
        return maxLeaveId;
    }
}
