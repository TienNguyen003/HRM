package com.hrm.Service.day_off;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.day_off.SabbaticalLogsMapper;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import com.hrm.repository.day_off.SabLogsRepository;
import com.hrm.repository.user.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SabLogsService {
    SabLogsRepository sabLogsRepository;
    SabbaticalLogsMapper sabbaticalLogsMapper;
    EmployeeRepository employeeRepository;

    // thêm
    public SabbaticalLogsResponse create(SabbaticalLogsRequest request){

        SabbaticalLeaveLogs sabbaticalLeaveLogs = sabbaticalLogsMapper.toSabLogs(request);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        sabbaticalLeaveLogs.setEmployee(employee);

        return sabbaticalLogsMapper.toSabLogsResponse(sabLogsRepository.save(sabbaticalLeaveLogs));
    }

    // tìm kiếm
    public List<SabbaticalLogsResponse> searchAllSab(int pageNumber, int pageSize, String name, Integer id){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return sabLogsRepository.findByNameContaining(name, id, pageable)
                .stream().map(sabbaticalLogsMapper::toSabLogsResponse).toList();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer id){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<SabbaticalLeaveLogs> page = sabLogsRepository.findByNameContaining(name, id, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }
}
