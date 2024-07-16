package com.hrm.Mapper.role;

import com.hrm.Entity.role.Role;
import com.hrm.dto.request.role.RoleRequest;
import com.hrm.dto.request.role.RoleUpdateRequest;
import com.hrm.dto.response.role.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    @Mapping(target = "permissions", ignore = true)
    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);
}
