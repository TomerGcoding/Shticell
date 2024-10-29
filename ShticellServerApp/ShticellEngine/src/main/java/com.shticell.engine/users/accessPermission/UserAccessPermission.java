package com.shticell.engine.users.accessPermission;

import java.io.Serializable;

public class UserAccessPermission implements Serializable {
    private final String username;
    private AccessPermissionType accessPermissionType;
    private AccessPermissionType requestedType;
    private  AccessPermissionStatus requestStatus;

    public UserAccessPermission(String username, AccessPermissionType accessPermissionType, AccessPermissionStatus accessPermissionStatus) {
        this.username = username;
        this.accessPermissionType = accessPermissionType;
        this.requestStatus = accessPermissionStatus;
        this.requestedType = null;
    }

    public String getUsername() {
        return username;
    }

    public AccessPermissionType getAccessPermissionType() {
        return accessPermissionType;
    }

    public AccessPermissionStatus getAccessPermissionStatus() {
        return requestStatus;
    }

    public void setAccessPermissionType(AccessPermissionType accessPermissionType) {
        this.accessPermissionType = accessPermissionType;
    }

    public void setAccessPermissionStatus(AccessPermissionStatus accessPermissionStatus) {
        this.requestStatus = accessPermissionStatus;
    }

    public AccessPermissionType getRequestedAccessPermissionType() {
        return requestedType;
    }

    public void setRequestedAccessPermissionType(AccessPermissionType requestedType) {
        this.requestedType = requestedType;
    }

}
