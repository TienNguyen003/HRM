package com.hrm.Service.office;

import com.hrm.Entity.office.Department;
import com.hrm.Entity.office.OfficeI;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.office.DepartmentMapper;
import com.hrm.dto.request.office.DepartmentRequest;
import com.hrm.dto.response.office.DepartmentResponse;
import com.hrm.repository.office.DepartmentRepository;
import com.hrm.repository.office.OfficeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentService {
    DepartmentRepository departmentRepository;
    DepartmentMapper departmentMapper;
    OfficeRepository officeRepository;

    // thêm
    public DepartmentResponse create(DepartmentRequest request){
        if(departmentRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.DEPARTMENT_EXISTED);
        OfficeI officeI = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new RuntimeException("Office not found"));

        Department department = departmentMapper.toDepartment(request);
        department.setOfficeI(officeI);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    // cập nhật
    public DepartmentResponse update(int id, DepartmentRequest request){

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        departmentMapper.updateDepartment(department, request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    // lấy ra tất cả
    public List<DepartmentResponse> getAll(){
        return departmentRepository.findAll()
                .stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    // lấy theo id
    public DepartmentResponse getById(int id){
        return departmentMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found")));
    }

    // tìm kiếm
    public List<DepartmentResponse> searchAll(int pageNumber, int pageSize, String name, String shortName, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return departmentRepository.findByNameContainingAndStatus(name, shortName, status, pageable)
                .stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    // xóa
    public void delete(int id){
        departmentRepository.deleteById(id);
    }
}
