package com.hrm.Mapper.user;

import com.hrm.Entity.user.Contracts;
import com.hrm.dto.request.user.contract.ContractsRequest;
import com.hrm.dto.request.user.contract.ContractsUpdateRequest;
import com.hrm.dto.response.user.ContractsRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractsMapper {
    Contracts toContracts(ContractsRequest request);

    ContractsRespone toContractsRespone(Contracts contracts);

    void updateContracts(@MappingTarget Contracts contracts, ContractsRequest request);
    void updateContractsUp(@MappingTarget Contracts contracts, ContractsUpdateRequest request);
}
