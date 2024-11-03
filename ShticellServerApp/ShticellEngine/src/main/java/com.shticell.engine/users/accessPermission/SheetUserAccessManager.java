package com.shticell.engine.users.accessPermission;

import dto.UserAccessDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        addUserAccessPermission(new UserAccessPermission(userName, AccessPermissionType.OWNER, AccessPermissionStatus.APPROVED));
    }

    public void requestAccessPermission(String userName, String requestedAccessPermission) {
        UserAccessPermission userAccessPermission = userAccessPermissionMap.get(userName);
        if (userAccessPermission == null) {
            throw new IllegalArgumentException("User " + userName + " does not have access to the sheet.");
        }
        try {
            userAccessPermission.setRequestedAccessPermissionType(
                    AccessPermissionType.valueOf(requestedAccessPermission.toUpperCase())
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid access permission type: " + requestedAccessPermission + "found in SheetAccessManager");
        }
    }

    public void approveAccessPermission(String owner, String userName, String requestedAccessPermission) {
        AccessPermissionType ownerAccessPermission = userAccessPermissionMap.get(owner).getAccessPermissionType();
        if (ownerAccessPermission != AccessPermissionType.OWNER) {
            throw new IllegalArgumentException("User " + owner + " is not the owner of the sheet. He can't approve access permission.");
        }
        UserAccessPermission userAccessPermission = userAccessPermissionMap.get(userName);
        if (userAccessPermission == null) {
            throw new IllegalArgumentException("User " + userName + " does not have access to the sheet.");
        }
        if (userAccessPermission.getRequestedAccessPermissionType() == null) {
            throw new IllegalArgumentException("User " + userName + " has not requested any access permission.");
        }
        if (requestedAccessPermission.equalsIgnoreCase(userAccessPermission.getRequestedAccessPermissionType().toString())) {
            userAccessPermission.setAccessPermissionType(userAccessPermission.getRequestedAccessPermissionType());
            userAccessPermission.setAccessPermissionStatus(AccessPermissionStatus.APPROVED);
        } else {
            throw new IllegalArgumentException("User " + userName + " has not requested access permission: " + requestedAccessPermission);
        }
    }

    public void rejectAccessPermission(String owner, String userName, String requestedAccessPermission) {
        AccessPermissionType ownerAccessPermission = userAccessPermissionMap.get(owner).getAccessPermissionType();
        if (ownerAccessPermission != AccessPermissionType.OWNER) {
            throw new IllegalArgumentException("User " + owner + " is not the owner of the sheet. He can't approve access permission.");
        }
        UserAccessPermission userAccessPermission = userAccessPermissionMap.get(userName);
        if (userAccessPermission == null) {
            throw new IllegalArgumentException("User " + userName + " does not have access to the sheet.");
        }
        if (userAccessPermission.getRequestedAccessPermissionType() == null) {
            throw new IllegalArgumentException("User " + userName + " has not requested any access permission.");
        }
        if (requestedAccessPermission.equalsIgnoreCase(userAccessPermission.getRequestedAccessPermissionType().toString())) {
            userAccessPermission.setAccessPermissionStatus(AccessPermissionStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("User " + userName + " has not requested access permission: " + requestedAccessPermission);
        }
    }

    public boolean isOwner(String ownerUserName) {
        UserAccessPermission userAccessPermission = userAccessPermissionMap.get(ownerUserName);
        if (userAccessPermission == null) {
            return false;
        }
        return userAccessPermission.getAccessPermissionType().equals(AccessPermissionType.OWNER);
    }

    public List<UserAccessDTO> getAllAccessRequests(String ownerUserName) {
        List<UserAccessDTO> userAccessDTOList = new ArrayList<>();
        for (UserAccessPermission userAccessPermission : userAccessPermissionMap.values()) {
            if (userAccessPermission.getRequestedAccessPermissionType() != null && userAccessPermission.getAccessPermissionStatus() == AccessPermissionStatus.PENDING) {
                if (userAccessPermission.getRequestedAccessPermissionType() != null)
                    userAccessDTOList.add(new UserAccessDTO(userAccessPermission.getUsername(), userAccessPermission.getRequestedAccessPermissionType().toString(), userAccessPermission.getAccessPermissionStatus().toString(), userAccessPermission.getRequestedAccessPermission().toString()));
                else
                    userAccessDTOList.add(new UserAccessDTO(userAccessPermission.getUsername(), userAccessPermission.getRequestedAccessPermissionType().toString(), userAccessPermission.getAccessPermissionStatus().toString(), null));
            }
        }
        return userAccessDTOList;
    }
}
