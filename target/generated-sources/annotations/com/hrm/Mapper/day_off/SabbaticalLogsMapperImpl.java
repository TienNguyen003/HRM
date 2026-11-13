package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class SabbaticalLogsMapperImpl implements SabbaticalLogsMapper {

    @Override
    public SabbaticalLeaveLogs toSabLogs(SabbaticalLogsRequest request) {
        if ( request == null ) {
            return null;
        }

        SabbaticalLeaveLogs.SabbaticalLeaveLogsBuilder sabbaticalLeaveLogs = SabbaticalLeaveLogs.builder();

        sabbaticalLeaveLogs.content( request.getContent() );
        sabbaticalLeaveLogs.fluctuatesTime( request.getFluctuatesTime() );
        sabbaticalLeaveLogs.remaining( request.getRemaining() );
        sabbaticalLeaveLogs.updateTime( request.getUpdateTime() );

        return sabbaticalLeaveLogs.build();
    }

    @Override
    public SabbaticalLogsResponse toSabLogsResponse(SabbaticalLeaveLogs dayOffCategories) {
        if ( dayOffCategories == null ) {
            return null;
        }

        SabbaticalLogsResponse.SabbaticalLogsResponseBuilder sabbaticalLogsResponse = SabbaticalLogsResponse.builder();

        sabbaticalLogsResponse.content( dayOffCategories.getContent() );
        sabbaticalLogsResponse.employee( dayOffCategories.getEmployee() );
        sabbaticalLogsResponse.fluctuatesTime( dayOffCategories.getFluctuatesTime() );
        sabbaticalLogsResponse.id( dayOffCategories.getId() );
        sabbaticalLogsResponse.remaining( dayOffCategories.getRemaining() );
        sabbaticalLogsResponse.updateTime( dayOffCategories.getUpdateTime() );

        return sabbaticalLogsResponse.build();
    }

    @Override
    public void updateSabLogs(SabbaticalLeaveLogs sabbaticalLeaveLogs, SabbaticalLogsRequest request) {
        if ( request == null ) {
            return;
        }

        sabbaticalLeaveLogs.setContent( request.getContent() );
        sabbaticalLeaveLogs.setFluctuatesTime( request.getFluctuatesTime() );
        sabbaticalLeaveLogs.setRemaining( request.getRemaining() );
        sabbaticalLeaveLogs.setUpdateTime( request.getUpdateTime() );
    }
}
