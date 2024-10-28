package com.shticell.engine.users.accessPermission;

import java.io.Serializable;

public class UserAccessPermission implements Serializable {
    private final String username;
    private  AccessPermisionType accessPermisionType;
    private  AccessPermissionStatus accessPermissionStatus;

    public UserAccessPermission(String username, AccessPermisionType accessPermisionType, AccessPermissionStatus accessPermissionStatus) {
        this.username = username;
        this.accessPermisionType = accessPermisionType;
        this.accessPermissionStatus = accessPermissionStatus;
    }

    public String getUsername() {
        return username;
    }

    public AccessPermisionType getAccessPermisionType() {
        return accessPermisionType;
    }

    public AccessPermissionStatus getAccessPermissionStatus() {
        return accessPermissionStatus;
    }

    public void setAccessPermissionType(AccessPermisionType accessPermisionType) {
        this.accessPermisionType = accessPermisionType;
    }

    public void setAccessPermissionStatus(AccessPermissionStatus accessPermissionStatus) {
        this.accessPermissionStatus = accessPermissionStatus;
    }

}
