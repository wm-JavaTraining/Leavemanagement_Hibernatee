package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeManager;
import com.wavemaker.leavemanagement.repository.EmployeeRepository;
import com.wavemaker.leavemanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.addEmployee(employee);
    }

    @Override
    public Employee getEmployeeByLoginId(int loginId) throws ServerUnavailableException {
        return employeeRepository.getEmployeeByLoginId(loginId);
    }

    @Override
    public List<Integer> getEmpIdUnderManager(int managerId) throws ServerUnavailableException {
        return employeeRepository.getEmpIdUnderManager(managerId);
    }

    @Override
    public EmployeeManager getEmployeeManagerDetails(int employeeId) throws ServerUnavailableException {
        return employeeRepository.getEmployeeManagerDetails(employeeId);

    }

    @Override
    public EmployeeLeave getEmployeeDetailsAndLeaveSummary(int empId) throws ServerUnavailableException {
        return employeeRepository.getEmployeeLeaveDetailsAndLeaveSummary(empId);
    }
}
