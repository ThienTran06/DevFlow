package com.hamy.devflow.user;

import lombok.Getter;

import java.util.Set;

import static com.hamy.devflow.user.UserPermissions.*;

@Getter
public enum UserRole {
    USER(
            Set.of(CREATE_TASK, DELETE_TASK, UPDATE_TASK, VIEW_TASK,CREATE_GOAL, DELETE_GOAL, UPDATE_GOAL, VIEW_GOAL)
    ),
    ADMIN(
            Set.of(UserPermissions.values())
    );
    private final Set<UserPermissions> userPermissions;
    UserRole(Set<UserPermissions> userPermissions) {
        this.userPermissions = userPermissions;
    }
}
