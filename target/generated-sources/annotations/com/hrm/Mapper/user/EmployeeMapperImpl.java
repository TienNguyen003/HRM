package com.hrm.Mapper.user;

import com.hrm.Entity.user.Employee;
import com.hrm.dto.request.user.employee.EmployeeRequest;
import com.hrm.dto.response.user.EmployeeRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toEmployee(EmployeeRequest request) {
        if ( request == null ) {
            return null;
        }

        Employee.EmployeeBuilder employee = Employee.builder();

        employee.name( request.getName() );
        employee.email( request.getEmail() );
        employee.phone_number( request.getPhone_number() );
        employee.gender( request.getGender() );
        employee.image( request.getImage() );
        employee.birth_date( request.getBirth_date() );
        employee.joined_date( request.getJoined_date() );
        employee.shift_id( request.getShift_id() );
        employee.vacationTime( request.getVacationTime() );
        employee.hourOff( request.getHourOff() );
        employee.vacationHours( request.getVacationHours() );

        return employee.build();
    }

    @Override
    public EmployeeRespone toEmployeeRespone(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeRespone.EmployeeResponeBuilder employeeRespone = EmployeeRespone.builder();

        employeeRespone.id( employee.getId() );
        employeeRespone.name( employee.getName() );
        employeeRespone.email( employee.getEmail() );
        employeeRespone.phone_number( employee.getPhone_number() );
        employeeRespone.gender( employee.getGender() );
        employeeRespone.image( employee.getImage() );
        employeeRespone.birth_date( employee.getBirth_date() );
        employeeRespone.joined_date( employee.getJoined_date() );
        employeeRespone.shift_id( employee.getShift_id() );
        employeeRespone.vacationTime( employee.getVacationTime() );
        employeeRespone.hourOff( employee.getHourOff() );
        employeeRespone.vacationHours( employee.getVacationHours() );
        employeeRespone.timekeeping( employee.getTimekeeping() );
        employeeRespone.department( employee.getDepartment() );
        employeeRespone.formula( employee.getFormula() );

        return employeeRespone.build();
    }

    @Override
    public void updateEmployee(Employee employee, EmployeeRequest request) {
        if ( request == null ) {
            return;
        }

        employee.setName( request.getName() );
        employee.setEmail( request.getEmail() );
        employee.setPhone_number( request.getPhone_number() );
        employee.setGender( request.getGender() );
        employee.setImage( request.getImage() );
        employee.setBirth_date( request.getBirth_date() );
        employee.setJoined_date( request.getJoined_date() );
        employee.setShift_id( request.getShift_id() );
        employee.setVacationTime( request.getVacationTime() );
        employee.setHourOff( request.getHourOff() );
        employee.setVacationHours( request.getVacationHours() );
    }
}
