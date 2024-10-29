package com.wavemaker.leavemanagement.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "LOGIN_CREDENTIAL")
public class LoginCredential {
    @Id
    @Column(name = "LOGIN_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer loginId;
    @Column(name = "EMAILID")
    private String emailId;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "EMPLOYEE_ID")
    private  Integer employeeId;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID", insertable = false, updatable = false)
    private Employee employee;

    public Integer getLoginId() {
        return loginId;
    }

    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        if(employee != null) {
            this.employeeId = employee.getEmployeeId();
        }
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginCredential that = (LoginCredential) o;
        return Objects.equals(loginId, that.loginId) && Objects.equals(emailId, that.emailId) && Objects.equals(password, that.password) && Objects.equals(employeeId, that.employeeId) && Objects.equals(employee, that.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginId, emailId, password, employeeId, employee);
    }

    @Override
    public String toString() {
        return "LoginCredential{" +
                "loginId=" + loginId +
                ", emailId='" + emailId + '\'' +
                ", password='" + password + '\'' +
                ", employeeId=" + employeeId +
                ", employee=" + employee +
                '}';
    }
}
