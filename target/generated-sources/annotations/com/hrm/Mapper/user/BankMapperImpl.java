package com.hrm.Mapper.user;

import com.hrm.Entity.user.Bank;
import com.hrm.dto.request.user.bank.BankRequest;
import com.hrm.dto.request.user.bank.BankUpdateRequest;
import com.hrm.dto.response.user.BankRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class BankMapperImpl implements BankMapper {

    @Override
    public Bank toBank(BankRequest request) {
        if ( request == null ) {
            return null;
        }

        Bank.BankBuilder bank = Bank.builder();

        bank.nameBank( request.getNameBank() );
        bank.numberBank( request.getNumberBank() );
        bank.owner( request.getOwner() );
        bank.priority( request.getPriority() );
        bank.status( request.getStatus() );

        return bank.build();
    }

    @Override
    public BankRespone toBankRespone(Bank bank) {
        if ( bank == null ) {
            return null;
        }

        BankRespone.BankResponeBuilder bankRespone = BankRespone.builder();

        bankRespone.employee( bank.getEmployee() );
        bankRespone.id( bank.getId() );
        bankRespone.nameBank( bank.getNameBank() );
        bankRespone.numberBank( bank.getNumberBank() );
        bankRespone.owner( bank.getOwner() );
        bankRespone.priority( bank.getPriority() );
        bankRespone.status( bank.getStatus() );

        return bankRespone.build();
    }

    @Override
    public void updateBank(Bank bank, BankRequest request) {
        if ( request == null ) {
            return;
        }

        bank.setNameBank( request.getNameBank() );
        bank.setNumberBank( request.getNumberBank() );
        bank.setOwner( request.getOwner() );
        bank.setPriority( request.getPriority() );
        bank.setStatus( request.getStatus() );
    }

    @Override
    public void updateBankUp(Bank bank, BankUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        bank.setNameBank( request.getNameBank() );
        bank.setNumberBank( request.getNumberBank() );
        bank.setOwner( request.getOwner() );
        bank.setPriority( request.getPriority() );
    }
}
