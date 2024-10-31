package dto;

import java.io.Serializable;

public class UserAccessDTO implements Serializable {

    private String userName;
    private String accessPermission;
    private String accessPermissionStatus;
    private String requestedAccessPermission;

    public UserAccessDTO() {
        this.userName = null;
        this.accessPermission = null;
        this.accessPermissionStatus = null;
        this.requestedAccessPermission = null;
    }

    public UserAccessDTO(String userName, String accessPermission, String AccessPermissionStatus, String requestedAccess) {
        this.userName = userName;
        this.accessPermission = accessPermission;
        this.requestedAccessPermission = requestedAccess;
        this.accessPermissionStatus = AccessPermissionStatus;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccessPermission() {
        return accessPermission;
    }

    public String getAccessPermissionStatus() {
        return accessPermissionStatus;
    }

    public String getRequestedAccessPermission() {
        return requestedAccessPermission;
    }
}
