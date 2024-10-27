package com.shticell.engine.accessPermission;

import com.shticell.engine.sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;

public class SheetUserAccessManager {
    private final Map <String, UserAccessPermission> userAccessPermissionMap;
    private final Sheet sheet;

    public SheetUserAccessManager(Sheet sheet) {
        this.userAccessPermissionMap = new HashMap<>();
        this.sheet = sheet;
    }

    public void addUserAccessPermission(UserAccessPermission userAccessPermission) {
        userAccessPermissionMap.put(userAccessPermission.getUsername(), userAccessPermission);
    }

    public void removeUserAccessPermission(String username) {
        userAccessPermissionMap.remove(username);
    }
}
