package com.wavemaker.leavemanagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "EMPLOYEE")

public class Employee {
    @Id
    @Column(name = "EMPLOYEE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;
    @Column(name = "NAME")
    private String empName;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate DateOfBirth;
    @Column(name = "PHONE_NUMBER")
    private long phoneNumber;
    @Column(name = "GENDER")
    private String gender;
    @Column(name = "MANAGER_ID")
    private Integer managerId;
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID", referencedColumnName = "EMPLOYEE_ID", insertable = false, updatable = false)
    private Employee manager;

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        if (manager != null) {
            this.managerId = manager.getEmployeeId();
        }

        this.manager = manager;
    }


    public Employee() {

    }

    public Employee(Integer employeeId, String empName, String email, LocalDate dateOfBirth, String gender, Integer managerId, Employee manager, long phoneNumber) {
        this.employeeId = employeeId;
        this.empName = empName;
        this.email = email;
        DateOfBirth = dateOfBirth;
        this.gender = gender;
        this.managerId = managerId;
        this.manager = manager;
        this.phoneNumber = phoneNumber;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(LocalDate DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return phoneNumber == employee.phoneNumber && Objects.equals(employeeId, employee.employeeId) && Objects.equals(empName, employee.empName) && Objects.equals(email, employee.email) && Objects.equals(DateOfBirth, employee.DateOfBirth) && Objects.equals(gender, employee.gender) && Objects.equals(managerId, employee.managerId) && Objects.equals(manager, employee.manager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, empName, email, DateOfBirth, phoneNumber, gender, managerId, manager);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", empName='" + empName + '\'' +
                ", email='" + email + '\'' +
                ", DateOfBirth=" + DateOfBirth +
                ", phoneNumber=" + phoneNumber +
                ", gender='" + gender + '\'' +
                ", managerId=" + managerId +
                ", manager=" + manager +
                '}';
    }
}
