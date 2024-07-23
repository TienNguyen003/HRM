package com.hrm.Controller.user;

import com.hrm.Service.user.BankService;
import com.hrm.dto.request.user.BankRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.BankRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}bank_accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BankController {
    BankService bankService;

    @PostMapping
    ApiResponse<BankRespone> create(@RequestBody BankRequest request) {
        return ApiResponse.<BankRespone>builder()
                .result(bankService.createB(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<BankRespone>> getAll(@RequestParam("pageNumber") int pageNumber,
                                          @RequestParam(name = "name", required = false) String name,
                                          @RequestParam(name = "status", required = false) Integer status,
                                          @RequestParam(name = "priority", required = false) Integer priority,
                                          @RequestParam(name = "nameBank", required = false) String nameBank) {
        return ApiResponse.<List<BankRespone>>builder()
                .result(bankService.searchAllB(pageNumber, 30, name, nameBank, status, priority))
                .page(bankService.getPagination(pageNumber, name, nameBank, status, priority))
                .build();
    }

    @GetMapping("/bank")
    ApiResponse<BankRespone> getById(@RequestParam("id") int id) {
        return ApiResponse.<BankRespone>builder()
                .result(bankService.getById(id))
                .build();
    }

    @PutMapping
    ApiResponse<String> update(@RequestParam int id, @RequestBody BankRequest request) {
        bankService.updateB(id, request);
        return ApiResponse.<String>builder()
                .result("Update success")
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id) {
        bankService.deleteB(id);
        return ApiResponse.<String>builder()
                .result("This bank has been deleted")
                .build();
    }
}
