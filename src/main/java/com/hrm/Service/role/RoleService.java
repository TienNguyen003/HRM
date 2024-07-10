package com.hrm.Service.role;

import com.hrm.Entity.PageCustom;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.role.RoleMapper;
import com.hrm.dto.request.role.RoleRequest;
import com.hrm.dto.response.role.RoleResponse;
import com.hrm.repository.role.PermissionRepository;
import com.hrm.repository.role.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    public RoleResponse create(RoleRequest request) {
        if(roleRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> searchAll(String name, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return roleRepository.findByName(name, pageable)
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public PageCustom getPagination(int pageNumber, long count){
        String totalItems = String.valueOf(roleRepository.totalItems());
        String totalPages = String.valueOf(roleRepository.totalPages());
        return PageCustom.builder()
                .totalPages(totalPages)
                .totalItems(totalItems)
                .totalItemsPerPage(String.valueOf(count))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    public RoleResponse getRole(String name){
        return roleMapper.toRoleResponse(roleRepository.findById(name).orElseThrow(() -> new RuntimeException("Role not found")));
    }

    public void delete(String name){
        roleRepository.deleteById(name);
    }
}
