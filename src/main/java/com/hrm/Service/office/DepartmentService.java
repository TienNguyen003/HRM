package com.hrm.Service.office;

import com.hrm.Entity.PageCustom;
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
import org.springframework.data.domain.Page;
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
        if(departmentRepository.existsByNameAndId(request.getName(), request.getOfficeId()) )
            throw new AppException(ErrorCode.DEPARTMENT_EXISTED);
        OfficeI officeI = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED));

        Department department = departmentMapper.toDepartment(request);
        department.setOfficeI(officeI);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    // cập nhật
    public DepartmentResponse update(int id, DepartmentRequest request){
        if(departmentRepository.existsByNameAndId(request.getName(), request.getOfficeId()) )
            throw new AppException(ErrorCode.DEPARTMENT_EXISTED);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_EXISTED));
        OfficeI officeI = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED));

        department.setOfficeI(officeI);
        departmentMapper.updateDepartment(department, request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    public String updateStt(int id, int status){
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_EXISTED));

        department.setStatus(status);
        departmentMapper.toDepartmentResponse(departmentRepository.save(department));

        return "Update success";
    }

    // lấy ra tất cả
    public List<DepartmentResponse> getAll(){
        return departmentRepository.getAllDepartment()
                .stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    // lấy theo id
    public DepartmentResponse getById(int id){
        return departmentMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_EXISTED)));
    }

    // tìm kiếm
    public List<DepartmentResponse> searchAll(int pageNumber, int pageSize, String name, String shortName, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return departmentRepository.findByNameAndStatus(name, shortName, status, pageable)
                .stream().map(departmentMapper::toDepartmentResponse).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, String shortName, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Department> page = departmentRepository.findByNameAndStatus(name, shortName, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void delete(int id){
        departmentRepository.deleteById(id);
    }
}
