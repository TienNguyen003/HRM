package com.hrm.Service.user;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Bank;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.BankMapper;
import com.hrm.dto.request.user.BankRequest;
import com.hrm.dto.response.user.BankRespone;
import com.hrm.repository.user.BankRepository;
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
public class BankService {
    BankRepository bankRepository;
    BankMapper bankMapper;
    EmployeeRepository employeeRepository;

    // thêm danh sách
    public BankRespone createB(BankRequest request) {
        if (bankRepository.existsByNameAndAccount(request.getNameBank(), request.getNumberBank()))
            throw new AppException(ErrorCode.BANK_EXISTED);
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        Bank bank = bankMapper.toBank(request);
        bank.setEmployee(employee);

        return bankMapper.toBankRespone(bankRepository.save(bank));
    }

    // cập nhật
    public BankRespone updateB(int bankId, BankRequest request) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new AppException(ErrorCode.BANK_NOT_EXISTED));
        if (bankRepository.existsByNameAndAccount(request.getNameBank(), request.getNumberBank()))
            throw new AppException(ErrorCode.BANK_EXISTED);

        bankMapper.updateBank(bank, request);

        return bankMapper.toBankRespone(bankRepository.save(bank));
    }

    // lấy ra tất cả
    public List<BankRespone> getAllB(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return bankRepository.findAll(pageable)
                .stream().map(bankMapper::toBankRespone).toList();
    }

    // lấy theo id
    public BankRespone getById(int id) {
        return bankMapper.toBankRespone(bankRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BANK_NOT_EXISTED)));
    }

    // tìm kiếm
    public List<BankRespone> searchAllB
    (int pageNumber, int pageSize, String name, String nameDay, Integer status, Integer priority) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return bankRepository.findBankByNameAndNameBank(name, status, priority, nameDay, pageable)
                .stream().map(bankMapper::toBankRespone).toList();
    }

    public PageCustom getPagination(int pageNumber, String name, String nameBank, Integer status, Integer priority){
        Pageable pageable = PageRequest.of(pageNumber - 1, 30);
        Page<Bank> page = bankRepository.findBankByNameAndNameBank(name, status, priority, nameBank, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void deleteB(int id) {
        bankRepository.deleteById(id);
    }
}
