package com.nhynhi.models;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String username;
    private String password;
    private String role;

    private String displayName;

    private boolean saved;

    public UserAccount() {
    }

    public UserAccount(String username, String password, String role, String displayName, boolean saved) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.saved = saved;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    @Override
    public String toString() {
        return "UserAccount{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", displayName='" + displayName + '\'' +
                ", saved=" + saved +
                '}';
    }
}
