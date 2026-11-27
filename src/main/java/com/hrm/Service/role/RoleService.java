package com.hrm.Service.role;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.role.Role;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.role.RoleMapper;
import com.hrm.dto.request.role.RoleRequest;
import com.hrm.dto.request.role.RoleUpdateRequest;
import com.hrm.dto.response.role.RoleResponse;
import com.hrm.repository.role.PermissionRepository;
import com.hrm.repository.role.RoleRepository;
import com.hrm.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;
    RoleGuard roleGuard;
    UserRepository userRepository;

    public RoleResponse create(RoleRequest request) {
        if(roleRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);
        roleGuard.assertNameAssignable(request.getName());
        roleGuard.assertPermissionAssignable(request.getPermissions());
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public List<RoleResponse> searchAll(String name, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return roleRepository.findByName(name, pageable)
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public PageCustom getPagination(int pageNumber, String name){
        Pageable pageable = PageRequest.of(pageNumber - 1, 30);
        Page<Role> page = roleRepository.findByName(name, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    public RoleResponse getRole(String name){
        return roleMapper.toRoleResponse(roleRepository.findById(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)));
    }

    public RoleResponse updateRole(RoleUpdateRequest request, String name){
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleGuard.assertNameAssignable(name);
        roleGuard.assertPermissionAssignable(request.getPermissions());
        roleMapper.updateRole(role, request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        // Đổi quyền của role -> token cũ của mọi user thuộc role này phải vô hiệu ngay.
        userRepository.findByRole_Name(name).forEach(user -> {
            user.setTokenVersion(user.getTokenVersion() + 1);
            userRepository.save(user);
        });

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    /** Đảm bảo role có quyền super mà không ghi đè các quyền đã gán tay. */
    @org.springframework.transaction.annotation.Transactional
    public void ensureSuperPermission(String roleName) {
        Role role = roleRepository.findById(roleName).orElse(null);
        if (role == null) return;
        var permissions = role.getPermissions() == null
                ? new HashSet<com.hrm.Entity.role.Permission>()
                : new HashSet<>(role.getPermissions());
        permissionRepository.findById(PermissionCatalog.SUPER).ifPresent(permissions::add);
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    public void delete(String name){
        roleRepository.deleteById(name);
    }
}
