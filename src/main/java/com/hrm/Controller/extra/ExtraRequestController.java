package com.hrm.Controller.extra;

import com.hrm.Service.extra.ExtraRequestService;
import com.hrm.dto.request.extra.ExtraRequestCreate;
import com.hrm.dto.request.extra.ExtraStatusUpdate;
import com.hrm.dto.response.ApiResponse;
import com.hrm.Entity.extra.ExtraRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}extra_requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExtraRequestController {
    ExtraRequestService extraRequestService;

    @PreAuthorize("@requiredPermission.checkPermission('REQ_ADD')")
    @PostMapping
    ApiResponse<ExtraRequest> create(@RequestBody @Valid ExtraRequestCreate request) {
        return ApiResponse.<ExtraRequest>builder().result(extraRequestService.create(request)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_EDIT')")
    @PutMapping
    ApiResponse<ExtraRequest> update(@RequestParam int id, @RequestBody @Valid ExtraRequestCreate request) {
        return ApiResponse.<ExtraRequest>builder().result(extraRequestService.update(id, request)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_APPROVALS')")
    @PutMapping("/status")
    ApiResponse<ExtraRequest> updateStatus(@RequestParam int id, @RequestBody @Valid ExtraStatusUpdate request) {
        return ApiResponse.<ExtraRequest>builder().result(extraRequestService.updateStatus(id, request)).build();
    }

    @GetMapping("/detail")
    ApiResponse<ExtraRequest> getDetail(@RequestParam int id) {
        return ApiResponse.<ExtraRequest>builder().result(extraRequestService.getById(id)).build();
    }

    @PreAuthorize(
            "@requiredPermission.checkPermission('REQ_VIEW') or "
                    + "@requiredPermission.checkPermission('REQ_ADD') or "
                    + "@requiredPermission.checkPermission('REQ_APPROVALS')")
    @GetMapping
    ApiResponse<List<ExtraRequest>> search(@RequestParam("pageNumber") int pageNumber,
                                           @RequestParam(name = "name", required = false) String name,
                                           @RequestParam(name = "type", required = false) Integer type,
                                           @RequestParam(name = "status", required = false) Integer status,
                                           @RequestParam(name = "employeeId", required = false) Integer employeeId) {
        return ApiResponse.<List<ExtraRequest>>builder()
                .result(extraRequestService.search(pageNumber, 30, name, type, status, employeeId))
                .page(extraRequestService.getPagination(pageNumber, 30, name, type, status, employeeId))
                .build();
    }

    // giờ + tiền tăng ca đã duyệt của 1 tháng (dùng cho bảng lương)
    @PreAuthorize("@requiredPermission.checkPermission('SALA_ADD') or @requiredPermission.checkPermission('SALA_VIEW')")
    @GetMapping("/overtime")
    ApiResponse<Map<String, Object>> overtime(@RequestParam int employeeId, @RequestParam String month) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(extraRequestService.overtimeInfo(employeeId, month))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id) {
        extraRequestService.delete(id);
        return ApiResponse.<String>builder().result("Extra request has been deleted").build();
    }
}