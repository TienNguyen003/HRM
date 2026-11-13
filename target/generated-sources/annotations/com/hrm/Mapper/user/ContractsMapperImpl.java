package com.hrm.Mapper.user;

import com.hrm.Entity.user.Contracts;
import com.hrm.dto.request.user.contract.ContractsRequest;
import com.hrm.dto.request.user.contract.ContractsUpdateRequest;
import com.hrm.dto.response.user.ContractsRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ContractsMapperImpl implements ContractsMapper {

    @Override
    public Contracts toContracts(ContractsRequest request) {
        if ( request == null ) {
            return null;
        }

        Contracts.ContractsBuilder contracts = Contracts.builder();

        contracts.dismissal_date( request.getDismissal_date() );
        contracts.hire_date( request.getHire_date() );
        contracts.linkFile( request.getLinkFile() );
        contracts.status( request.getStatus() );
        contracts.urlFile( request.getUrlFile() );

        return contracts.build();
    }

    @Override
    public ContractsRespone toContractsRespone(Contracts contracts) {
        if ( contracts == null ) {
            return null;
        }

        ContractsRespone.ContractsResponeBuilder contractsRespone = ContractsRespone.builder();

        contractsRespone.dismissal_date( contracts.getDismissal_date() );
        contractsRespone.employee( contracts.getEmployee() );
        contractsRespone.hire_date( contracts.getHire_date() );
        contractsRespone.id( String.valueOf( contracts.getId() ) );
        contractsRespone.linkFile( contracts.getLinkFile() );
        contractsRespone.status( contracts.getStatus() );
        contractsRespone.urlFile( contracts.getUrlFile() );

        return contractsRespone.build();
    }

    @Override
    public void updateContracts(Contracts contracts, ContractsRequest request) {
        if ( request == null ) {
            return;
        }

        contracts.setDismissal_date( request.getDismissal_date() );
        contracts.setHire_date( request.getHire_date() );
        contracts.setLinkFile( request.getLinkFile() );
        contracts.setStatus( request.getStatus() );
        contracts.setUrlFile( request.getUrlFile() );
    }

    @Override
    public void updateContractsUp(Contracts contracts, ContractsUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        contracts.setDismissal_date( request.getDismissal_date() );
        contracts.setHire_date( request.getHire_date() );
        contracts.setLinkFile( request.getLinkFile() );
        contracts.setUrlFile( request.getUrlFile() );
    }
}
