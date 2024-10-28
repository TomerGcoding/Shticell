package dto;

import java.io.Serializable;

public class UserAccessDTO implements Serializable {

    private String userName;
    private String accessPermission;
    private String AccessPermissionStatus;

    public UserAccessDTO() {
        this.userName = null;
        this.accessPermission = null;
        this.AccessPermissionStatus = null;
    }

    public UserAccessDTO(String userName, String accessPermission, String AccessPermissionStatus) {
        this.userName = userName;
        this.accessPermission = accessPermission;
        this.accessPermission = AccessPermissionStatus;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccessPermission() {
        return accessPermission;
    }

    public String getAccessPermissionStatus() {
        return AccessPermissionStatus;
    }
}
