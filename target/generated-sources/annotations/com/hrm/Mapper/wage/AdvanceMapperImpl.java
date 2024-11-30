package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Advance;
import com.hrm.dto.request.wage.advance.AdvanceRequest;
import com.hrm.dto.request.wage.advance.AdvanceUpdateSttRequest;
import com.hrm.dto.response.wage.AdvanceRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class AdvanceMapperImpl implements AdvanceMapper {

    @Override
    public Advance toAdvance(AdvanceRequest request) {
        if ( request == null ) {
            return null;
        }

        Advance.AdvanceBuilder advance = Advance.builder();

        advance.requestTime( request.getRequestTime() );
        advance.money( request.getMoney() );
        advance.status( request.getStatus() );
        advance.note( request.getNote() );

        return advance.build();
    }

    @Override
    public Advance toAdvanceStt(AdvanceUpdateSttRequest request) {
        if ( request == null ) {
            return null;
        }

        Advance.AdvanceBuilder advance = Advance.builder();

        advance.approvalTime( request.getApprovalTime() );
        advance.approvedBy( request.getApprovedBy() );
        advance.status( request.getStatus() );

        return advance.build();
    }

    @Override
    public AdvanceRespone toAdvanceRespone(Advance advance) {
        if ( advance == null ) {
            return null;
        }

        AdvanceRespone.AdvanceResponeBuilder advanceRespone = AdvanceRespone.builder();

        advanceRespone.id( advance.getId() );
        advanceRespone.requestTime( advance.getRequestTime() );
        advanceRespone.approvalTime( advance.getApprovalTime() );
        advanceRespone.money( advance.getMoney() );
        advanceRespone.approvedBy( advance.getApprovedBy() );
        advanceRespone.status( advance.getStatus() );
        advanceRespone.note( advance.getNote() );
        advanceRespone.employee( advance.getEmployee() );

        return advanceRespone.build();
    }

    @Override
    public void updateAdvance(Advance advance, AdvanceRequest request) {
        if ( request == null ) {
            return;
        }

        advance.setRequestTime( request.getRequestTime() );
        advance.setMoney( request.getMoney() );
        advance.setStatus( request.getStatus() );
        advance.setNote( request.getNote() );
    }

    @Override
    public void updateAdvanceStt(Advance advance, AdvanceUpdateSttRequest request) {
        if ( request == null ) {
            return;
        }

        advance.setApprovalTime( request.getApprovalTime() );
        advance.setApprovedBy( request.getApprovedBy() );
        advance.setStatus( request.getStatus() );
    }
}
