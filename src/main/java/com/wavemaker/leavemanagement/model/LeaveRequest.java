package com.wavemaker.leavemanagement.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "LEAVE_REQUEST")
public class LeaveRequest {
    @Id
    @Column(name = "LEAVE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveId;
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;
    @Column(name = "LEAVE_TYPE_ID ")
    private Integer leaveTypeId;
    @Column(name = "FROM_DATE")
    private LocalDate fromDate;
    @Column(name = "TO_DATE")
    private LocalDate toDate;
    @Column(name = "REASON")
    private String reason;
    @Column(name = "STATUS")
    private String status;

    @Column(name = "MANAGER_ID")
    private Integer managerId;

    @Column(name = "COMMENTS")
    private String comments;
    @Column(name = "DATE_OF_APPLICATION")
    private LocalDate dateOfApplication;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID", insertable = false, updatable = false)
    private Employee employeesByEmployeeId;

    @ManyToOne
    @JoinColumn(name = "MANAGER_ID", referencedColumnName = "EMPLOYEE_ID", insertable = false, updatable = false)
    private Employee employeesByManagerId;

    @ManyToOne
    @JoinColumn(name = "LEAVE_TYPE_ID", referencedColumnName = "LEAVE_TYPE_ID", insertable = false, updatable = false)
    private LeaveType leaveTypes;


    // Default constructor
    public LeaveRequest() {
    }

    public LeaveRequest(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LeaveRequest(LocalDate fromDate, LocalDate toDate, LeaveType leaveTypes, String reason) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.leaveTypes = leaveTypes;
        this.reason = reason;

    }

    public LeaveRequest(Integer leaveId, Integer employeeId, Integer leaveTypeId, LocalDate fromDate, LocalDate toDate, String reason, String status, Integer managerId, LocalDate currentDate, Employee employeesByEmployeeId, Employee employeesByManagerId, LeaveType leaveTypes, String comments) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = status;
        this.managerId = managerId;
        this.dateOfApplication = currentDate;
        this.employeesByEmployeeId = employeesByEmployeeId;
        this.employeesByManagerId = employeesByManagerId;
        this.leaveTypes = leaveTypes;
        this.comments = comments;
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDate getDateOfApplication() {
        return dateOfApplication;
    }

    public void setDateOfApplication(LocalDate dateOfApplication) {
        this.dateOfApplication = dateOfApplication;
    }

    public Employee getEmployeesByEmployeeId() {
        return employeesByEmployeeId;
    }

    public void setEmployeesByEmployeeId(Employee employeesByEmployeeId) {
        this.employeesByEmployeeId = employeesByEmployeeId;
    }

    public Employee getEmployeesByManagerId() {
        return employeesByManagerId;
    }

    public void setEmployeesByManagerId(Employee employeesByManagerId) {
        this.employeesByManagerId = employeesByManagerId;
    }

    public LeaveType getLeaveTypes() {
        return leaveTypes;
    }

    public void setLeaveTypes(LeaveType leaveTypes) {
        this.leaveTypes = leaveTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaveRequest that = (LeaveRequest) o;
        return Objects.equals(leaveId, that.leaveId) && Objects.equals(employeeId, that.employeeId) && Objects.equals(leaveTypeId, that.leaveTypeId) && Objects.equals(fromDate, that.fromDate) && Objects.equals(toDate, that.toDate) && Objects.equals(reason, that.reason) && Objects.equals(status, that.status) && Objects.equals(managerId, that.managerId) && Objects.equals(comments, that.comments) && Objects.equals(dateOfApplication, that.dateOfApplication) && Objects.equals(employeesByEmployeeId, that.employeesByEmployeeId) && Objects.equals(employeesByManagerId, that.employeesByManagerId) && Objects.equals(leaveTypes, that.leaveTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaveId, employeeId, leaveTypeId, fromDate, toDate, reason, status, managerId, comments, dateOfApplication, employeesByEmployeeId, employeesByManagerId, leaveTypes);
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "leaveId=" + leaveId +
                ", employeeId=" + employeeId +
                ", leaveTypeId=" + leaveTypeId +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", managerId=" + managerId +
                ", comments='" + comments + '\'' +
                ", currentDate=" + dateOfApplication +
                ", employeesByEmployeeId=" + employeesByEmployeeId +
                ", employeesByManagerId=" + employeesByManagerId +
                ", leaveTypes=" + leaveTypes +
                '}';
    }
}
