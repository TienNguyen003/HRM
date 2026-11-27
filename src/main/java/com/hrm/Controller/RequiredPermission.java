package com.hrm.Controller;

import com.hrm.Service.role.PermissionCatalog;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Kiểm tra quyền theo một nguồn chân lý duy nhất: authority trong JWT.
 *
 * Không còn đoán role từ authority đầu tiên và không tra DB lúc kiểm tra:
 * - mọi tên (legacy lẫn domain-action) được chuẩn hóa qua {@link PermissionCatalog};
 * - quyền ALL là super permission, không gắn cứng với tên role;
 * - token cũ chứa ROLE_ADMIN vẫn được hiểu là ALL để tương thích ngược.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequiredPermission {

    public boolean checkPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities() == null) return false;

        String required = PermissionCatalog.canonical(permission);
        if (required.isEmpty()) return false;

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(PermissionCatalog::canonical)
                .collect(Collectors.toSet());

        return authorities.contains(PermissionCatalog.SUPER) || authorities.contains(required);
    }
}
