package com.hrm.Controller.role;

import com.hrm.Service.role.RoleService;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.request.role.RoleRequest;
import com.hrm.dto.response.role.RoleResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<RoleResponse>> searchAll(@RequestParam int pageNumber,
                                              @RequestParam(name = "name", required = false) String name){
        List<RoleResponse> roles = roleService.searchAll(name, pageNumber, 30);
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roles)
                .page(roleService.getPagination(pageNumber, roles.stream().count()))
                .build();
    }

    @GetMapping("/role")
    ApiResponse<RoleResponse> getRole(@RequestParam(name = "name", required = false) String name){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.getRole(name))
                .build();
    }

    @DeleteMapping("/{roleName}")
    ApiResponse<Void> delete(@PathVariable String roleName){
        roleService.delete(roleName);
        return ApiResponse.<Void>builder().build();
    }
}
