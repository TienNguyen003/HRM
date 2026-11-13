package com.hrm.Mapper.office;

import com.hrm.Entity.office.Department;
import com.hrm.dto.request.office.DepartmentRequest;
import com.hrm.dto.response.office.DepartmentResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class DepartmentMapperImpl implements DepartmentMapper {

    @Override
    public Department toDepartment(DepartmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Department.DepartmentBuilder department = Department.builder();

        department.name( request.getName() );
        department.shortName( request.getShortName() );

        return department.build();
    }

    @Override
    public DepartmentResponse toDepartmentResponse(Department request) {
        if ( request == null ) {
            return null;
        }

        DepartmentResponse.DepartmentResponseBuilder departmentResponse = DepartmentResponse.builder();

        departmentResponse.id( String.valueOf( request.getId() ) );
        departmentResponse.name( request.getName() );
        departmentResponse.shortName( request.getShortName() );
        departmentResponse.status( request.getStatus() );
        departmentResponse.officeI( request.getOfficeI() );

        return departmentResponse.build();
    }

    @Override
    public void updateDepartment(Department shift, DepartmentRequest request) {
        if ( request == null ) {
            return;
        }

        shift.setName( request.getName() );
        shift.setShortName( request.getShortName() );
    }
}
