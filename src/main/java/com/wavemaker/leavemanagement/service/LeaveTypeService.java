package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;

public interface LeaveTypeService {
    int getNumberOfLeavesAllocated(String leaveType);

    public int getLeaveTypeId(String leaveType) throws ServerUnavailableException;

    public String getLeaveType(int leaveTypeId) throws ServerUnavailableException;

}
