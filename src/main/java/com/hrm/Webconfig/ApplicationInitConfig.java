package com.hrm.Webconfig;

import com.hrm.Entity.role.Permission;
import com.hrm.Entity.role.Role;
import com.hrm.Entity.user.User;
import com.hrm.repository.role.PermissionRepository;
import com.hrm.repository.role.RoleRepository;
import com.hrm.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_ROLE = "ADMIN";

    // quyền cho module lọc CV (đảm bảo luôn tồn tại để gán vào role)
    static final List<Permission> CV_PERMISSIONS = List.of(
            Permission.builder().name("CV_VIEW").des("Xem danh sách ứng viên").build(),
            Permission.builder().name("CV_ADD").des("Lọc CV / thêm bộ tiêu chí").build(),
            Permission.builder().name("CV_EDIT").des("Duyệt / chỉnh sửa ứng viên").build(),
            Permission.builder().name("CV_DELETE").des("Xóa bộ tiêu chí").build()
    );

    static final List<String> HRM_PERMISSIONS = List.of(
            "SELF_VIEW","SELF_UPDATE","ATT_CHECKIN","LEAVE_CREATE",
            "TEAM_VIEW","LEAVE_APPROVE","PERF_VIEW","PERF_ADD",
            "EMP_VIEW","EMP_ADD","EMP_EDIT","EMP_DELETE",
            "ATT_VIEW","LEAVE_VIEW","REC_VIEW","REC_ADD","REC_EDIT","REC_DELETE",
            "ORG_VIEW","PERM_ADD","PERM_VIEW","USER_ADD","USER_VIEW","USER_EDIT","USER_DELETE",
            "COMP_VIEW","OFF_VIEW","OT_VIEW","OT_CREATE","OT_APPROVE",
            "PAYROLL_VIEW","PAYROLL_RUN","PAYSLIP_VIEW",
            "ONBOARDING_VIEW","ONBOARDING_MANAGE","LEAVE_POLICY_VIEW","LEAVE_POLICY_MANAGE",
            "SHIFT_VIEW","SHIFT_MANAGE","BENEFIT_VIEW","BENEFIT_MANAGE",
            "HR_REQUEST_VIEW","HR_REQUEST_CREATE","HR_REQUEST_MANAGE","ALL"
    );

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        PermissionRepository permissionRepository,
                                        RoleRepository roleRepository) {
        return args -> {
            // seed quyền CV_* nếu chưa có trong DB
            CV_PERMISSIONS.forEach(permission -> {
                if (!permissionRepository.existsById(permission.getName()))
                    permissionRepository.save(permission);
            });
            // seed quyền HRM_* nếu chưa có trong DB
            HRM_PERMISSIONS.forEach(name -> {
                if (!permissionRepository.existsById(name))
                    permissionRepository.save(Permission.builder().name(name).des(name).build());
            });

            if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()) {
                Role role = roleRepository.findById(ADMIN_ROLE).orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(ADMIN_ROLE)
                                .des("Quản trị hệ thống")
                                .build()));

                User user = User.builder()
                        .username(ADMIN_USERNAME)
                        .password(passwordEncoder.encode("123321"))
                        .status(1)
                        .role(role)
                        .build();

                userRepository.save(user);
            }

            // tài khoản admin luôn có toàn bộ quyền hiện có (kể cả các quyền mới thêm sau này
            // như CV_VIEW/CV_ADD...) -> tránh trường hợp admin đăng nhập được mà không vào được module
            userRepository.findByUsername(ADMIN_USERNAME).ifPresent(user -> {
                if (user.getRole() == null) return;

                roleRepository.findById(user.getRole().getName()).ifPresent(role -> {
                    var permissions = new HashSet<>(permissionRepository.findAll());
                    if (permissions.isEmpty()) return;

                    role.setPermissions(permissions);
                    roleRepository.save(role);
                });
            });
        };
    }
}

