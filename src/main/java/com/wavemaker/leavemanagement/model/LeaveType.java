package com.wavemaker.leavemanagement.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="LEAVE_TYPE")
public class LeaveType {
    @Id
    @Column(name = "LEAVE_TYPE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int leaveTypeId;
    @Column(name = "TYPE_NAME")
    private String typeName;
    @Column(name = "LIMIT_FOR_LEAVES")
    private int limitForLeaves;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "GENDER")
    private String gender;

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getLimitForLeaves() {
        return limitForLeaves;
    }

    public void setLimitForLeaves(int limitForLeaves) {
        this.limitForLeaves = limitForLeaves;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        LeaveType leaveType = (LeaveType) o;
        return leaveTypeId == leaveType.leaveTypeId && limitForLeaves == leaveType.limitForLeaves && Objects.equals(typeName, leaveType.typeName) && Objects.equals(description, leaveType.description) && Objects.equals(gender, leaveType.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaveTypeId, typeName, limitForLeaves, description, gender);
    }

    @Override
    public String toString() {
        return "LeaveType{" +
                "leaveTypeId=" + leaveTypeId +
                ", typeName='" + typeName + '\'' +
                ", limitForLeaves=" + limitForLeaves +
                ", description='" + description + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
