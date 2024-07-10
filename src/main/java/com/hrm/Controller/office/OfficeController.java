package com.hrm.Controller.office;

import com.hrm.Service.office.OfficeService;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.office.OfficeRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}offices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OfficeController {
    OfficeService officeService;

    @PostMapping
    ApiResponse<OfficeRespone> create(@RequestBody OfficeRequest request){
        return ApiResponse.<OfficeRespone>builder()
                .result(officeService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<OfficeRespone>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<OfficeRespone>>builder()
                .result(officeService.getAll(pageNumber, 30))
                .build();
    }

    @GetMapping("/office")
    ApiResponse<OfficeRespone> getD(@RequestParam int officeId){
        return ApiResponse.<OfficeRespone>builder()
                .result(officeService.getById(officeId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<OfficeRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "status", required = false)  Integer status){
        return ApiResponse.<List<OfficeRespone>>builder()
                .result(officeService.searchAll(pageNumber, 30, name,status))
                .build();
    }

    @PutMapping
    ApiResponse<OfficeRespone> update(@RequestParam int officeId, @RequestBody OfficeRequest request){
        return ApiResponse.<OfficeRespone>builder()
                .result(officeService.update(officeId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int officeId){
        officeService.delete(officeId);
        return ApiResponse.<String>builder()
                .result("Office has been deleted")
                .build();
    }
}
