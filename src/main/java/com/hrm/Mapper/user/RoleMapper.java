package com.hrm.Mapper.user;

import com.hrm.Entity.user.Role;
import com.hrm.dto.request.role.RoleRequest;
import com.hrm.dto.response.user.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
