package com.hrm.Controller.office;

import com.hrm.Service.office.OfficeService;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.office.OfficeRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}offices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OfficeController {
    OfficeService officeService;

    @PreAuthorize("@requiredPermission.checkPermission('OFF_ADD')")
    @PostMapping
    ApiResponse<OfficeRespone> create(@RequestBody OfficeRequest request){
        return ApiResponse.<OfficeRespone>builder()
                .result(officeService.create(request))
                .build();
    }

    @GetMapping("/office")
    ApiResponse<OfficeRespone> getD(@RequestParam int officeId){
        return ApiResponse.<OfficeRespone>builder()
                .result(officeService.getById(officeId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('OFF_VIEW')")
    @GetMapping
    ApiResponse<List<OfficeRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "status", required = false)  Integer status){
        return ApiResponse.<List<OfficeRespone>>builder()
                .result(officeService.searchAll(pageNumber, 30, name,status))
                .page(officeService.getPagination(pageNumber, 30, name,status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('OFF_VIEW')")
    @GetMapping("/ofc")
    ApiResponse<List<OfficeRespone>> getAll(){
        return ApiResponse.<List<OfficeRespone>>builder()
                .result(officeService.getAll())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('OFF_EDIT')")
    @PutMapping
    ApiResponse<OfficeRespone> update(@RequestParam int officeId, @RequestBody OfficeRequest request){
        return ApiResponse.<OfficeRespone>builder()
                .result(officeService.update(officeId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('OFF_EDIT')")
    @PutMapping("/stt")
    ApiResponse<String> updateStt(@RequestParam int id, @RequestParam int status){
        return ApiResponse.<String>builder()
                .result(officeService.updateStt(id, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('OFF_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int officeId){
        officeService.delete(officeId);
        return ApiResponse.<String>builder()
                .result("Office has been deleted")
                .build();
    }
}
