package com.hrm.Mapper.role;

import com.hrm.Entity.role.Permission;
import com.hrm.dto.request.role.PermissionRequest;
import com.hrm.dto.response.role.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);

    PermissionResponse toPermissionResponse(Permission permission);
}
