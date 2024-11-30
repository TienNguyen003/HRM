package com.hrm.Mapper.user;

import com.hrm.Entity.user.Contracts;
import com.hrm.dto.request.user.contract.ContractsRequest;
import com.hrm.dto.request.user.contract.ContractsUpdateRequest;
import com.hrm.dto.response.user.ContractsRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class ContractsMapperImpl implements ContractsMapper {

    @Override
    public Contracts toContracts(ContractsRequest request) {
        if ( request == null ) {
            return null;
        }

        Contracts.ContractsBuilder contracts = Contracts.builder();

        contracts.urlFile( request.getUrlFile() );
        contracts.linkFile( request.getLinkFile() );
        contracts.hire_date( request.getHire_date() );
        contracts.dismissal_date( request.getDismissal_date() );
        contracts.status( request.getStatus() );

        return contracts.build();
    }

    @Override
    public ContractsRespone toContractsRespone(Contracts contracts) {
        if ( contracts == null ) {
            return null;
        }

        ContractsRespone.ContractsResponeBuilder contractsRespone = ContractsRespone.builder();

        contractsRespone.id( String.valueOf( contracts.getId() ) );
        contractsRespone.urlFile( contracts.getUrlFile() );
        contractsRespone.linkFile( contracts.getLinkFile() );
        contractsRespone.hire_date( contracts.getHire_date() );
        contractsRespone.dismissal_date( contracts.getDismissal_date() );
        contractsRespone.employee( contracts.getEmployee() );
        contractsRespone.status( contracts.getStatus() );

        return contractsRespone.build();
    }

    @Override
    public void updateContracts(Contracts contracts, ContractsRequest request) {
        if ( request == null ) {
            return;
        }

        contracts.setUrlFile( request.getUrlFile() );
        contracts.setLinkFile( request.getLinkFile() );
        contracts.setHire_date( request.getHire_date() );
        contracts.setDismissal_date( request.getDismissal_date() );
        contracts.setStatus( request.getStatus() );
    }

    @Override
    public void updateContractsUp(Contracts contracts, ContractsUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        contracts.setUrlFile( request.getUrlFile() );
        contracts.setLinkFile( request.getLinkFile() );
        contracts.setHire_date( request.getHire_date() );
        contracts.setDismissal_date( request.getDismissal_date() );
    }
}
