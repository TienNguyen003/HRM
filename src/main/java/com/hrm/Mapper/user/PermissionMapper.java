package com.hrm.Mapper.user;

import com.hrm.Entity.user.Permission;
import com.hrm.dto.request.permission.PermissionRequest;
import com.hrm.dto.response.user.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);

    PermissionResponse toPermissionResponse(Permission permission);
}
