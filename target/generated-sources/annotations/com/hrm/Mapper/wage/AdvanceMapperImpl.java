package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Advance;
import com.hrm.dto.request.wage.advance.AdvanceRequest;
import com.hrm.dto.request.wage.advance.AdvanceUpdateSttRequest;
import com.hrm.dto.response.wage.AdvanceRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class AdvanceMapperImpl implements AdvanceMapper {

    @Override
    public Advance toAdvance(AdvanceRequest request) {
        if ( request == null ) {
            return null;
        }

        Advance.AdvanceBuilder advance = Advance.builder();

        advance.money( request.getMoney() );
        advance.note( request.getNote() );
        advance.requestTime( request.getRequestTime() );
        advance.status( request.getStatus() );

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

        advanceRespone.approvalTime( advance.getApprovalTime() );
        advanceRespone.approvedBy( advance.getApprovedBy() );
        advanceRespone.employee( advance.getEmployee() );
        advanceRespone.id( advance.getId() );
        advanceRespone.money( advance.getMoney() );
        advanceRespone.note( advance.getNote() );
        advanceRespone.requestTime( advance.getRequestTime() );
        advanceRespone.status( advance.getStatus() );

        return advanceRespone.build();
    }

    @Override
    public void updateAdvance(Advance advance, AdvanceRequest request) {
        if ( request == null ) {
            return;
        }

        advance.setMoney( request.getMoney() );
        advance.setNote( request.getNote() );
        advance.setRequestTime( request.getRequestTime() );
        advance.setStatus( request.getStatus() );
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
