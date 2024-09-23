package com.hrm.Mapper.user;

import com.hrm.Entity.user.Employee;
import com.hrm.dto.request.user.employee.EmployeeRequest;
import com.hrm.dto.response.user.EmployeeRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee toEmployee(EmployeeRequest request);

    EmployeeRespone toEmployeeRespone(Employee employee);

    void updateEmployee(@MappingTarget Employee employee, EmployeeRequest request);
}
