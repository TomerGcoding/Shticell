package dto;

import java.io.Serializable;

public class UserAccessDTO implements Serializable {

    private String userName;
    private String accessPermission;
    private String accessPermissionStatus;

    public UserAccessDTO() {
        this.userName = null;
        this.accessPermission = null;
        this.accessPermissionStatus = null;
    }

    public UserAccessDTO(String userName, String accessPermission, String AccessPermissionStatus) {
        this.userName = userName;
        this.accessPermission = accessPermission;
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
}
