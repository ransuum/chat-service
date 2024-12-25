package com.project.chat_service.utils.permission;

import com.project.chat_service.models.enums.Roles;

import java.util.Set;

public interface Permissions {
    Set<String> fillPermissions();
    Roles getRole();
}
