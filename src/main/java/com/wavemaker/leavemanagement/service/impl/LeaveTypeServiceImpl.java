package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.repository.LeaveTypeRepository;
import com.wavemaker.leavemanagement.service.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Override
    public int getNumberOfLeavesAllocated(String leaveType) {
        return leaveTypeRepository.getNumberOfLeavesAllocated(leaveType);
    }

    @Override
    public int getLeaveTypeId(String leaveType) throws ServerUnavailableException {
       return  leaveTypeRepository.getLeaveTypeId(leaveType);
    }

    @Override
    public String getLeaveType(int leaveTypeId) throws ServerUnavailableException {
       return leaveTypeRepository.getLeaveType(leaveTypeId);
    }
}
