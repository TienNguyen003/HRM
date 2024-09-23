package com.hrm.Service.user;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Contracts;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.ContractsMapper;
import com.hrm.dto.request.user.contract.ContractsRequest;
import com.hrm.dto.request.user.contract.ContractsUpdateRequest;
import com.hrm.dto.response.user.ContractsRespone;
import com.hrm.repository.user.ContractsRepository;
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
public class ContractsService {
    ContractsRepository contractsRepository;
    ContractsMapper contractsMapper;
    EmployeeRepository employeeRepository;

    // thêm danh sách
    public ContractsRespone createB(ContractsRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        Contracts contracts = contractsMapper.toContracts(request);
        contracts.setEmployee(employee);

        return contractsMapper.toContractsRespone(contractsRepository.save(contracts));
    }

    // cập nhật
    public void updateB(int contractsId, ContractsUpdateRequest request) {
        Contracts contracts = contractsRepository.findById(contractsId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_EXISTED));

        contractsMapper.updateContractsUp(contracts, request);

        contractsRepository.save(contracts);
    }

    public String updateStt(int id, int status) {
        Contracts contracts = contractsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_EXISTED));

        contracts.setStatus(status);
        contractsMapper.toContractsRespone(contractsRepository.save(contracts));

        return "Update success";
    }

    // lấy theo id
    public ContractsRespone getById(int contractsId) {
        return contractsMapper.toContractsRespone(contractsRepository.findById(contractsId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_EXISTED)));
    }

    // tìm kiếm
    public List<ContractsRespone> searchAllB
    (int pageNumber, int pageSize, String name, Integer status) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return contractsRepository.findByName(name, status, pageable)
                .stream().map(contractsMapper::toContractsRespone).toList();
    }

    public PageCustom getPagination(int pageNumber, String name, Integer status) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 30);
        Page<Contracts> page = contractsRepository.findByName(name, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void deleteB(int contractsId) {
        contractsRepository.deleteById(contractsId);
    }
}
