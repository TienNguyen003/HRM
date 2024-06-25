package com.hrm.Service.wage;

import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Advance;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.AdvanceMapper;
import com.hrm.dto.request.wage.AdvanceRequest;
import com.hrm.dto.request.wage.AdvanceUpdateSttRequest;
import com.hrm.dto.response.wage.AdvanceRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.user.UserRepository;
import com.hrm.repository.wage.AdvanceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdvanceService {
    AdvanceRepository advanceRepository;
    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    AdvanceMapper advanceMapper;

    // thêm danh sách
    public AdvanceRespone create(AdvanceRequest request){

        Advance advance = advanceMapper.toAdvance(request);

        advance.setEmployee(employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No employee not found")));

        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
    }

    // cập nhật
    public AdvanceRespone update(int advanceId, AdvanceRequest request){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new RuntimeException("This advance not found"));

        if(advance.getStatus() != 0)
            throw new AppException(ErrorCode.Advance_Not_Edit);

        advanceMapper.updateAdvance(advance, request);

        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
    }

    // cập nhật status
    @PreAuthorize("hasRole('ADMIN')")
    public AdvanceRespone updateStt(int advanceId, AdvanceUpdateSttRequest request){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new RuntimeException("This advance not found"));

        advanceMapper.updateAdvanceStt(advance, request);

        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
    }

    // lấy ra tất cả
    public List<AdvanceRespone> getAll(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return advanceRepository.findAll(pageable)
                .stream().map(advanceMapper::toAdvanceRespone).toList();
    }

    // lấy theo id
    @PreAuthorize("hasRole('ADMIN') || #username == authentication.name")
    public AdvanceRespone getAdvance(int advanceId){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new RuntimeException("This advance not found"));

        int employee_id = advance.getEmployee().getId();

        String username = userRepository.findByEmployeeId(employee_id).getUsername();

        return advanceMapper.toAdvanceRespone(advance);
    }

    // tìm kiếm
    public List<AdvanceRespone> search(int pageNumber, int pageSize, String name, String status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return advanceRepository.findByNameAndStatus(name, status, pageable)
                .stream().map(advanceMapper::toAdvanceRespone).toList();
    }

    // xóa
    public void delete(int advanceId){
        advanceRepository.deleteById(advanceId);
    }

}
