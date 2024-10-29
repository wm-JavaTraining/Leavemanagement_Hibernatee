package com.wavemaker.leavemanagement.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "EMPLOYEE_LEAVE_SUMMARY")
public class EmployeeLeaveSummary {

    @Id
    @Column(name = "SUMMARY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int summaryId;
    @Column(name = "EMPLOYEE_ID")
    private int employeeId;
    @Column(name = "LEAVE_TYPE_ID")
    private int leaveTypeId;
    @Column(name = "LEAVE_TYPE")
    private String leaveType;
    @Column(name = "PENDING_LEAVES")
    private int pendingLeaves;
    @Column(name = "TOTAL_LEAVES_TAKEN")
    private int totalLeavesTaken;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID", insertable = false, updatable = false)
    private  Employee employeeByEmployeeId;

    @ManyToOne
    @JoinColumn(name = "LEAVE_TYPE_ID", referencedColumnName = "LEAVE_TYPE_ID", insertable = false, updatable = false)
    private LeaveType leaveTypeByLeaveTypeId;

    public EmployeeLeaveSummary() {
    }

    public EmployeeLeaveSummary(int summaryId, int employeeId, int leaveTypeId, String leaveType, int pendingLeaves, int totalLeavesTaken, Employee employeeByEmployeeId, LeaveType leaveTypeByLeaveTypeId) {
        this.summaryId = summaryId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.leaveType = leaveType;
        this.pendingLeaves = pendingLeaves;
        this.totalLeavesTaken = totalLeavesTaken;
        this.employeeByEmployeeId = employeeByEmployeeId;
        this.leaveTypeByLeaveTypeId = leaveTypeByLeaveTypeId;
    }
    public EmployeeLeaveSummary(int summaryId, int employeeId, int leaveTypeId, String leaveType, int pendingLeaves, long totalLeavesTaken) {
        this.summaryId = summaryId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.leaveType = leaveType;
        this.pendingLeaves = pendingLeaves;
        this.totalLeavesTaken =(int) totalLeavesTaken;

    }

    public Employee getEmployeeByEmployeeId() {
        return employeeByEmployeeId;
    }

    public void setEmployeeByEmployeeId(Employee employeeByEmployeeId) {
        if(employeeByEmployeeId != null){
            this.employeeId = employeeByEmployeeId.getEmployeeId();
        }
        this.employeeByEmployeeId = employeeByEmployeeId;
    }

    public LeaveType getLeaveTypeByLeaveTypeId() {
        return leaveTypeByLeaveTypeId;
    }

    public void setLeaveTypeByLeaveTypeId(LeaveType leaveTypeByLeaveTypeId) {
        if(leaveTypeByLeaveTypeId != null){
            this.leaveTypeId = leaveTypeByLeaveTypeId.getLeaveTypeId();
        }
        this.leaveTypeByLeaveTypeId = leaveTypeByLeaveTypeId;
    }


    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }


    // Getter and Setter for employeeId
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    // Getter and Setter for leaveTypeId
    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    // Getter and Setter for leaveType
    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    // Getter and Setter for pendingLeaves
    public int getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(int pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    // Getter and Setter for totalLeavesTaken
    public int getTotalLeavesTaken() {
        return totalLeavesTaken;
    }

    public void setTotalLeavesTaken(int totalLeavesTaken) {
        this.totalLeavesTaken = totalLeavesTaken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeLeaveSummary that = (EmployeeLeaveSummary) o;
        return summaryId == that.summaryId && employeeId == that.employeeId && leaveTypeId == that.leaveTypeId && pendingLeaves == that.pendingLeaves && totalLeavesTaken == that.totalLeavesTaken && Objects.equals(leaveType, that.leaveType)  && Objects.equals(employeeByEmployeeId, that.employeeByEmployeeId) && Objects.equals(leaveTypeByLeaveTypeId, that.leaveTypeByLeaveTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaryId, employeeId, leaveTypeId, leaveType, pendingLeaves, totalLeavesTaken, employeeByEmployeeId, leaveTypeByLeaveTypeId);
    }

    @Override
    public String toString() {
        return "EmployeeLeaveSummary{" +
                "summaryId=" + summaryId +
                ", employeeId=" + employeeId +
                ", leaveTypeId=" + leaveTypeId +
                ", leaveType='" + leaveType + '\'' +
                ", pendingLeaves=" + pendingLeaves +
                ", totalLeavesTaken=" + totalLeavesTaken +
                ", employeeByEmployeeId=" + employeeByEmployeeId +
                ", leaveTypeByLeaveTypeId=" + leaveTypeByLeaveTypeId +
                '}';
    }
}
