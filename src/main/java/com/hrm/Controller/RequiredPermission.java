package com.hrm.Controller;

import com.hrm.Service.role.RoleService;
import com.hrm.dto.response.role.PermissionResponse;
import com.hrm.dto.response.role.RoleResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequiredPermission {
    RoleService roleService;

    public boolean checkPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) return false;

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // role ADMIN (super admin) được truy cập mọi endpoint
        if (authorities.contains("ROLE_ADMIN")) return true;
        // quyền ALL được phép truy cập mọi endpoint
        if (authorities.contains("ALL")) return true;
        if (authorities.contains(permission)) return true;

        // fallback: đọc từ DB (trường hợp token cũ chưa có scope mới)
        GrantedAuthority firstAuthority = authentication.getAuthorities().stream()
                .findFirst()
                .orElse(null);
        if (firstAuthority == null) return false;

        String role = firstAuthority.getAuthority().replace("ROLE_", "");
        if (role.equals("NHÂN")) role = "NHÂN VIÊN";
        RoleResponse roleResponse = roleService.getRole(role.replace("_", " "));
        Set<String> permissionNames = roleResponse.getPermissions().stream()
                .map(PermissionResponse::getName)
                .collect(Collectors.toSet());
        // quyền ALL được phép truy cập mọi endpoint
        if (permissionNames.contains("ALL")) return true;
        // role ADMIN (super admin) được truy cập mọi endpoint
        if ("ADMIN".equals(roleResponse.getName())) return true;
        return permissionNames.contains(permission);
    }
}
