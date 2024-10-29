package com.wavemaker.leavemanagement.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name="COOKIE")
public class EmployeeCookie {

    @Id
    @Column(name = "COOKIE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cookieId;
    @Column(name = "COOKIE_NAME")
    private String cookieName;
    @Column(name = "COOKIE_VALUE")
    private String cookieValue;
    @Column(name = "LOGIN_ID")
    private Integer loginId;
    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryLocalDate;
    @ManyToOne
    @JoinColumn(name = "LOGIN_ID", referencedColumnName = "LOGIN_ID", insertable = false, updatable = false)
    private LoginCredential loginCredential;

    public LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public void setLoginCredential(LoginCredential loginCredential) {
        if(loginCredential != null) {
            this.loginId = loginCredential.getLoginId();
        }

        this.loginCredential = loginCredential;
    }

    // Default constructor
    public EmployeeCookie() {
    }


    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }

    public Integer getLoginId() {
        return loginId;
    }

    public Integer getCookieId() {
        return cookieId;
    }

    public void setCookieId(Integer cookieId) {
        this.cookieId = cookieId;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }


    public LocalDate getExpiryLocalDate() {
        return expiryLocalDate;
    }

    public void setExpiryLocalDate(LocalDate expiryLocalDate) {
        this.expiryLocalDate = expiryLocalDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeCookie that = (EmployeeCookie) o;
        return Objects.equals(cookieId, that.cookieId) && Objects.equals(cookieName, that.cookieName) && Objects.equals(cookieValue, that.cookieValue) && Objects.equals(loginId, that.loginId) && Objects.equals(expiryLocalDate, that.expiryLocalDate) && Objects.equals(loginCredential, that.loginCredential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cookieId, cookieName, cookieValue, loginId, expiryLocalDate, loginCredential);
    }

    @Override
    public String toString() {
        return "EmployeeCookie{" +
                "cookieId=" + cookieId +
                ", cookieName='" + cookieName + '\'' +
                ", cookieValue='" + cookieValue + '\'' +
                ", loginId=" + loginId +
                ", expiryLocalDate=" + expiryLocalDate +
                ", loginCredential=" + loginCredential +
                '}';
    }
}
