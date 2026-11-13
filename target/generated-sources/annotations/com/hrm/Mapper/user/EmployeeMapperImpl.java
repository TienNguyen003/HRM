package com.hrm.Mapper.user;

import com.hrm.Entity.user.Employee;
import com.hrm.dto.request.user.employee.EmployeeRequest;
import com.hrm.dto.response.user.EmployeeRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toEmployee(EmployeeRequest request) {
        if ( request == null ) {
            return null;
        }

        Employee.EmployeeBuilder employee = Employee.builder();

        employee.birth_date( request.getBirth_date() );
        employee.email( request.getEmail() );
        employee.gender( request.getGender() );
        employee.hourOff( request.getHourOff() );
        employee.image( request.getImage() );
        employee.joined_date( request.getJoined_date() );
        employee.name( request.getName() );
        employee.phone_number( request.getPhone_number() );
        employee.shift_id( request.getShift_id() );
        employee.vacationHours( request.getVacationHours() );
        employee.vacationTime( request.getVacationTime() );

        return employee.build();
    }

    @Override
    public EmployeeRespone toEmployeeRespone(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeRespone.EmployeeResponeBuilder employeeRespone = EmployeeRespone.builder();

        employeeRespone.birth_date( employee.getBirth_date() );
        employeeRespone.department( employee.getDepartment() );
        employeeRespone.email( employee.getEmail() );
        employeeRespone.formula( employee.getFormula() );
        employeeRespone.gender( employee.getGender() );
        employeeRespone.hourOff( employee.getHourOff() );
        employeeRespone.id( employee.getId() );
        employeeRespone.image( employee.getImage() );
        employeeRespone.joined_date( employee.getJoined_date() );
        employeeRespone.name( employee.getName() );
        employeeRespone.phone_number( employee.getPhone_number() );
        employeeRespone.shift_id( employee.getShift_id() );
        employeeRespone.timekeeping( employee.getTimekeeping() );
        employeeRespone.vacationHours( employee.getVacationHours() );
        employeeRespone.vacationTime( employee.getVacationTime() );

        return employeeRespone.build();
    }

    @Override
    public void updateEmployee(Employee employee, EmployeeRequest request) {
        if ( request == null ) {
            return;
        }

        employee.setBirth_date( request.getBirth_date() );
        employee.setEmail( request.getEmail() );
        employee.setGender( request.getGender() );
        employee.setHourOff( request.getHourOff() );
        employee.setImage( request.getImage() );
        employee.setJoined_date( request.getJoined_date() );
        employee.setName( request.getName() );
        employee.setPhone_number( request.getPhone_number() );
        employee.setShift_id( request.getShift_id() );
        employee.setVacationHours( request.getVacationHours() );
        employee.setVacationTime( request.getVacationTime() );
    }
}
