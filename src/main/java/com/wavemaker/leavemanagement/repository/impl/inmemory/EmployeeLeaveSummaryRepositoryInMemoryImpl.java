package com.wavemaker.leavemanagement.repository.impl.inmemory;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository("employeeLeaveSummaryRepositoryInMemory")
public class EmployeeLeaveSummaryRepositoryInMemoryImpl implements EmployeeLeaveSummaryRepository {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryRepositoryInMemoryImpl.class);
    private static ConcurrentHashMap<Integer, EmployeeLeaveSummary> employeeLeaveSummaryMap = new ConcurrentHashMap<>();

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException {
        return employeeLeaveSummaryMap.values()
                .stream()
                .filter(leave -> leave.getEmployeeId() == employeeId)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
        List<Integer> summaryIds = employeeLeaveSummaryMap.values()
                .stream()
                .filter(leave -> leave.getEmployeeId() == employeeLeaveSummary.getEmployeeId() &&
                        leave.getLeaveTypeId() == employeeLeaveSummary.getLeaveTypeId())
                .map(EmployeeLeaveSummary::getSummaryId) // Map to summaryId
                .collect(Collectors.toList());
        for (int i : summaryIds) {
            updateEmployeeLeaveSummary(employeeLeaveSummary);
            return employeeLeaveSummary;// Collect into a list
        }
        int summaryId = -1;
        summaryId = getMaxLeaveSummaryId();
        summaryId += 1;
        employeeLeaveSummary.setSummaryId(summaryId);
        logger.debug("Generated new summary Id: {}", summaryId);

        logger.info("Adding employee summary with ID: {}", employeeLeaveSummary.getSummaryId());

        if (employeeLeaveSummaryMap.containsKey(employeeLeaveSummary.getSummaryId())) {
            logger.error("employee leave summary with  summary id {} already exists.", employeeLeaveSummary.getSummaryId());

        }
        employeeLeaveSummaryMap.put(employeeLeaveSummary.getSummaryId(), employeeLeaveSummary);
        logger.info("Employee leave summary  with ID {} added successfully.", employeeLeaveSummary.getSummaryId());
        return employeeLeaveSummary;

    }

    @Override
    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
        int pendingLeaves = employeeLeaveSummary.getLeaveTypeByLeaveTypeId().getLimitForLeaves() - employeeLeaveSummary.getTotalLeavesTaken();
        employeeLeaveSummary.setPendingLeaves(pendingLeaves);
        List<Integer> summaryIds = employeeLeaveSummaryMap.values()
                .stream()
                .filter(leave -> leave.getEmployeeId() == employeeLeaveSummary.getEmployeeId() &&
                        leave.getLeaveTypeId() == employeeLeaveSummary.getLeaveTypeId())
                .map(EmployeeLeaveSummary::getSummaryId) // Map to summaryId
                .collect(Collectors.toList());
        for (int i : summaryIds) {
            employeeLeaveSummary.setSummaryId(i);
            break;// Collect into a list
        }
        employeeLeaveSummaryMap.put(employeeLeaveSummary.getSummaryId(), employeeLeaveSummary);
        return true;
    }

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException {
        return employeeLeaveSummaryMap.values()
                .stream()
                .filter(leave -> employeeIds.contains(leave.getEmployeeId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Holiday> getPersonalHolidays(int employeeId) throws ServerUnavailableException {
        return new ArrayList<>();
    }

    private int getMaxLeaveSummaryId() {
        int maxLeaveSummaryId = 0;
        for (int i : employeeLeaveSummaryMap.keySet()) {
            maxLeaveSummaryId = Math.max(maxLeaveSummaryId, i);
        }
        return maxLeaveSummaryId;
    }
}
