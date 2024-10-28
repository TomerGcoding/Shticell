package dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SheetUsersAccessDTO implements Serializable {
    private Set<UserAccessDTO> usersAccess;

    public SheetUsersAccessDTO() {
        this.usersAccess = new HashSet<>();
    }

    public SheetUsersAccessDTO(Set<UserAccessDTO> usersAccess) {
        this.usersAccess = usersAccess;
    }

    public Set<UserAccessDTO> getUsersAccess() {
        return usersAccess;
    }

    public UserAccessDTO getUserAccess(String userName){
        for (UserAccessDTO user: usersAccess){
            if (user.getUserName().equals(userName))
                return user;

        }
        throw new IllegalArgumentException("User not found in sheet users");
    }
}
