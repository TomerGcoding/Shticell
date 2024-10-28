package com.shticell.engine.users;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class UserManager {

    private final Set<String> usersSet;
   // private final Map<String, User> userNameToUser;

    public UserManager() {
        System.out.println("UserManager constructor called");
        usersSet = new HashSet<>();
     //   userNameToUser = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        usersSet.add(username);
      //  userNameToUser.put(username, new User(username));
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }

//    public AccessPermission getPermissionForUser (String userName, String sheetName){
//        if (usersSet.contains(userName)){
//            return userNameToUser.get(userName).getSheetPermission(sheetName);
//        }
//        throw new IllegalArgumentException("User does not exist");
//    }
//
//    public void addPermissionForUser (String userName, String sheetName, AccessPermission permission){
//        if (usersSet.contains(userName)){
//            userNameToUser.get(userName).addSheetPermission(sheetName, permission);
//        }
//        throw new IllegalArgumentException("User does not exist");
//    }

}
