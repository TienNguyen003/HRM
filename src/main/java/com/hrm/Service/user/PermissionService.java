package com.hrm.Service.user;

import com.hrm.Entity.user.Permission;
import com.hrm.Mapper.user.PermissionMapper;
import com.hrm.dto.request.permission.PermissionRequest;
import com.hrm.dto.response.user.PermissionResponse;
import com.hrm.repository.user.PermissionRepository;
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
