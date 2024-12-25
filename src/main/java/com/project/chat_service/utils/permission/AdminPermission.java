package com.project.chat_service.utils.permission;

import com.project.chat_service.models.enums.Roles;

import java.util.Set;

public class AdminPermission implements Permissions{
    @Override
    public Set<String> fillPermissions() {
        //logic
        return Set.of("READ", "WRITE", "DELETE");
    }

    @Override
    public Roles getRole() {
        return Roles.ROLE_ADMIN;
    }
}
