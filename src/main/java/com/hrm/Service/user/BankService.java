package com.hrm.Service.user;

import com.hrm.Entity.user.Bank;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.BankMapper;
import com.hrm.dto.request.user.BankRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.BankRespone;
import com.hrm.repository.user.BankRepository;
import com.hrm.repository.user.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
        if (bankRepository.existsByNumberBank(request.getNumberBank()) &&
                bankRepository.existsByNameBank(request.getNameBank()))
            throw new AppException(ErrorCode.BANK_EXISTED);
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No employee not found"));
        Bank bank = bankMapper.toBank(request);
        bank.setEmployee(employee);

        return bankMapper.toBankRespone(bankRepository.save(bank));
    }

    // cập nhật
    public BankRespone updateB(int bankId, BankRequest request) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("This bank not found"));

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
                .orElseThrow(() -> new RuntimeException("This bank not found")));
    }

    // tìm kiếm
    public List<BankRespone> searchAllB
    (int pageNumber, int pageSize, String name, String nameDay, Integer status, Integer priority) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return bankRepository.findBankByNameAndNameBank(name, status, priority, nameDay, pageable)
                .stream().map(bankMapper::toBankRespone).toList();
    }

    // xóa
    public void deleteB(int id) {
        bankRepository.deleteById(id);
    }

}
