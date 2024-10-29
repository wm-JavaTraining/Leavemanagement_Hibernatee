package com.wavemaker.leavemanagement.repository;


import com.wavemaker.leavemanagement.exception.ServerUnavailableException;

public interface LeaveTypeRepository {
    int getNumberOfLeavesAllocated(String leaveType);

    int getLeaveTypeId(String leaveType);

    String getLeaveType(int leaveTypeId) throws ServerUnavailableException;
}
