package com.hrm.Controller.role;

import com.hrm.Service.role.RoleService;
import com.hrm.dto.request.role.RoleUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.request.role.RoleRequest;
import com.hrm.dto.response.role.RoleResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PreAuthorize("@requiredPermission.checkPermission('PERM_ADD')")
    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody @Valid RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERM_VIEW')")
    @GetMapping()
    ApiResponse<List<RoleResponse>> searchAll(@RequestParam int pageNumber,
                                              @RequestParam(name = "name", required = false) String name){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.searchAll(name, pageNumber, 30))
                .page(roleService.getPagination(pageNumber, name))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERM_VIEW')")
    @GetMapping("/get")
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @GetMapping("/role")
    ApiResponse<RoleResponse> getRole(@RequestParam(name = "name", required = false) String name){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.getRole(name))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERM_EDIT')")
    @PutMapping()
    ApiResponse<RoleResponse> updateRole(@RequestBody @Valid RoleUpdateRequest request, @RequestParam String name){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(request, name))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERM_DELETE')")
    @DeleteMapping()
    ApiResponse<String> delete(@RequestParam(name = "name", required = false) String name){
        roleService.delete(name);
        return ApiResponse.<String>builder()
                .result("Role has been deleted")
                .build();
    }
}
