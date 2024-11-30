package com.hrm.Mapper.user;

import com.hrm.Entity.user.Bank;
import com.hrm.dto.request.user.bank.BankRequest;
import com.hrm.dto.request.user.bank.BankUpdateRequest;
import com.hrm.dto.response.user.BankRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
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
        bank.owner( request.getOwner() );
        bank.numberBank( request.getNumberBank() );
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

        bankRespone.id( bank.getId() );
        bankRespone.nameBank( bank.getNameBank() );
        bankRespone.owner( bank.getOwner() );
        bankRespone.numberBank( bank.getNumberBank() );
        bankRespone.priority( bank.getPriority() );
        bankRespone.status( bank.getStatus() );
        bankRespone.employee( bank.getEmployee() );

        return bankRespone.build();
    }

    @Override
    public void updateBank(Bank bank, BankRequest request) {
        if ( request == null ) {
            return;
        }

        bank.setNameBank( request.getNameBank() );
        bank.setOwner( request.getOwner() );
        bank.setNumberBank( request.getNumberBank() );
        bank.setPriority( request.getPriority() );
        bank.setStatus( request.getStatus() );
    }

    @Override
    public void updateBankUp(Bank bank, BankUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        bank.setNameBank( request.getNameBank() );
        bank.setOwner( request.getOwner() );
        bank.setNumberBank( request.getNumberBank() );
        bank.setPriority( request.getPriority() );
    }
}
