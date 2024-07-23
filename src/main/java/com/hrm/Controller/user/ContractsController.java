package com.hrm.Controller.user;

import com.hrm.Service.user.BankService;
import com.hrm.Service.user.ContractsService;
import com.hrm.dto.request.user.BankRequest;
import com.hrm.dto.request.user.ContractsRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.BankRespone;
import com.hrm.dto.response.user.ContractsRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}contracts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContractsController {
    ContractsService contractsService;

    @PostMapping
    ApiResponse<ContractsRespone> create(@RequestBody ContractsRequest request) {
        return ApiResponse.<ContractsRespone>builder()
                .result(contractsService.createB(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ContractsRespone>> getAll(@RequestParam("pageNumber") int pageNumber,
                                               @RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "status", required = false) Integer status) {
        return ApiResponse.<List<ContractsRespone>>builder()
                .result(contractsService.searchAllB(pageNumber, 30, name, status))
                .page(contractsService.getPagination(pageNumber, name, status))
                .build();
    }

    @GetMapping("/contracts")
    ApiResponse<ContractsRespone> getById(@RequestParam("contractsId") int contractsId) {
        return ApiResponse.<ContractsRespone>builder()
                .result(contractsService.getById(contractsId))
                .build();
    }

    @PutMapping
    ApiResponse<String> update(@RequestParam int contractsId, @RequestBody ContractsRequest request) {
        contractsService.updateB(contractsId, request);
        return ApiResponse.<String>builder()
                .result("Update success")
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id) {
        contractsService.deleteB(id);
        return ApiResponse.<String>builder()
                .result("This contracts has been deleted")
                .build();
    }
}
