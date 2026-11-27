package com.hrm.Service.role;

import com.hrm.Controller.RequiredPermission;
import com.hrm.Entity.role.Role;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Chặn leo thang đặc quyền khi gán role/permission từ request body.
 * Role có quyền super (hoặc tên ADMIN) chỉ được gán bởi người đã có quyền super.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleGuard {

    RequiredPermission requiredPermission;

    public void assertAssignable(Role role) {
        if (role == null) return;
        if (isPrivileged(role) && !hasSuper()) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public void assertPermissionAssignable(Iterable<String> permissionNames) {
        if (permissionNames == null) return;
        boolean containsSuper = false;
        for (String name : permissionNames) if (PermissionCatalog.isSuper(name)) { containsSuper = true; break; }
        if (containsSuper && !hasSuper()) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public void assertNameAssignable(String roleName) {
        if ("ADMIN".equalsIgnoreCase(roleName) && !hasSuper())
            throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    /** Không có principal = luồng nội bộ (job/test); mọi request HTTP đều có principal. */
    private boolean hasSuper() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return true;
        return requiredPermission.checkPermission(PermissionCatalog.SUPER);
    }

    public boolean hasPermission(String permission) {
        return requiredPermission.checkPermission(permission);
    }

    public boolean isPrivileged(Role role) {
        if (role == null) return false;
        if ("ADMIN".equalsIgnoreCase(role.getName())) return true;
        if (role.getPermissions() == null) return false;
        return role.getPermissions().stream()
                .anyMatch(p -> PermissionCatalog.isSuper(p.getName()));
    }
}
