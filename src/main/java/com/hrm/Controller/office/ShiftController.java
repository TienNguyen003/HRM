package com.hrm.Controller.office;

import com.hrm.Service.office.ShiftService;
import com.hrm.dto.request.office.ShiftRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.office.ShiftResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}shift")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShiftController {
    ShiftService shiftService;

    @PostMapping
    ApiResponse<ShiftResponse> create(@RequestBody @Valid ShiftRequest request){
        return ApiResponse.<ShiftResponse>builder()
                .result(shiftService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ShiftResponse>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<ShiftResponse>>builder()
                .result(shiftService.getAll(pageNumber, 30))
                .build();
    }

    @PutMapping
    ApiResponse<ShiftResponse> update(@RequestParam int shiftId,@RequestBody @Valid ShiftRequest request){
        return ApiResponse.<ShiftResponse>builder()
                .result(shiftService.update(shiftId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int shiftId){
        shiftService.delete(shiftId);
        return ApiResponse.<String>builder()
                .result("Shift has been deleted")
                .build();
    }
}
