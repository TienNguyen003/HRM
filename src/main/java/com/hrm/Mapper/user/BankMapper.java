package com.hrm.Mapper.user;

import com.hrm.Entity.user.Bank;
import com.hrm.dto.request.user.bank.BankRequest;
import com.hrm.dto.request.user.bank.BankUpdateRequest;
import com.hrm.dto.response.user.BankRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BankMapper {
    Bank toBank(BankRequest request);

    BankRespone toBankRespone(Bank bank);

    void updateBank(@MappingTarget Bank bank, BankRequest request);
    void updateBankUp(@MappingTarget Bank bank, BankUpdateRequest request);
}
