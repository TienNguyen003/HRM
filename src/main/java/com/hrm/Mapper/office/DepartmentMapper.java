package com.hrm.Mapper.office;

import com.hrm.Entity.office.Department;
import com.hrm.dto.request.office.DepartmentRequest;
import com.hrm.dto.response.office.DepartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toDepartment(DepartmentRequest request);

    DepartmentResponse toDepartmentResponse(Department request);

    void updateDepartment(@MappingTarget Department shift, DepartmentRequest request);
}
