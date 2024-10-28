package com.shticell.engine.users.accessPermission;

import com.shticell.engine.sheet.api.Sheet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SheetUserAccessManager implements Serializable {
    private final Map <String, UserAccessPermission> userAccessPermissionMap;

    public SheetUserAccessManager() {
        this.userAccessPermissionMap = new HashMap<>();
    }

    public void addUserAccessPermission(UserAccessPermission userAccessPermission) {
        userAccessPermissionMap.put(userAccessPermission.getUsername(), userAccessPermission);
    }

    public void removeUserAccessPermission(String username) {
        userAccessPermissionMap.remove(username);
    }
    public UserAccessPermission getUserAccessPermission(String username) {
        return userAccessPermissionMap.get(username);
    }

    public Map <String, UserAccessPermission> getSheetUserAccessManager() {
        return userAccessPermissionMap;
    }

    public void setOwner(String userName) {
        addUserAccessPermission(new UserAccessPermission(userName, AccessPermisionType.OWNER, AccessPermissionStatus.APPROVED));
    }
}
