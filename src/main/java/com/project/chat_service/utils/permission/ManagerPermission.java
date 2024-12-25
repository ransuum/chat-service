package com.project.chat_service.utils.permission;

import com.project.chat_service.models.enums.Roles;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ManagerPermission implements Permissions {
    @Override
    public Set<String> fillPermissions() {
        //logic
        return Set.of("READ", "WRITE", "DELETE");
    }

    @Override
    public Roles getRole() {
        return Roles.ROLE_MANAGER;
    }
}
