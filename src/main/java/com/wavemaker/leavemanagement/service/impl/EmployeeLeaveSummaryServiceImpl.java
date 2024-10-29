package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.leavemanagement.service.EmployeeLeaveSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeLeaveSummaryServiceImpl implements EmployeeLeaveSummaryService {
    @Autowired
    @Qualifier("employeeLeaveSummaryRepositoryInDb")
    private EmployeeLeaveSummaryRepository employeeLeaveSummaryRepository;

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.getEmployeeLeaveSummaryByEmpId(employeeId);
    }

    @Override
    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeSummary) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.addEmployeeLeaveSummary(employeeSummary);
    }

    @Override
    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.updateEmployeeLeaveSummary(employeeLeaveSummary);
    }

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.getEmployeeLeaveSummaryByEmpIds(employeeIds);
    }

    @Override
    public List<Holiday> getPersonalHolidays(int employeeId) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.getPersonalHolidays(employeeId);
    }
}
