package com.hrm.Service.role;

import com.hrm.Entity.role.Permission;
import com.hrm.Mapper.role.PermissionMapper;
import com.hrm.dto.request.role.PermissionRequest;
import com.hrm.dto.response.role.PermissionResponse;
import com.hrm.repository.role.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        var permission = permissionRepository.findAll();
        return permission.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String name){
        permissionRepository.deleteById(name);
    }
}
