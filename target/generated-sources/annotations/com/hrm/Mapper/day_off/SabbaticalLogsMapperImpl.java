package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class SabbaticalLogsMapperImpl implements SabbaticalLogsMapper {

    @Override
    public SabbaticalLeaveLogs toSabLogs(SabbaticalLogsRequest request) {
        if ( request == null ) {
            return null;
        }

        SabbaticalLeaveLogs.SabbaticalLeaveLogsBuilder sabbaticalLeaveLogs = SabbaticalLeaveLogs.builder();

        sabbaticalLeaveLogs.updateTime( request.getUpdateTime() );
        sabbaticalLeaveLogs.fluctuatesTime( request.getFluctuatesTime() );
        sabbaticalLeaveLogs.content( request.getContent() );
        sabbaticalLeaveLogs.remaining( request.getRemaining() );

        return sabbaticalLeaveLogs.build();
    }

    @Override
    public SabbaticalLogsResponse toSabLogsResponse(SabbaticalLeaveLogs dayOffCategories) {
        if ( dayOffCategories == null ) {
            return null;
        }

        SabbaticalLogsResponse.SabbaticalLogsResponseBuilder sabbaticalLogsResponse = SabbaticalLogsResponse.builder();

        sabbaticalLogsResponse.id( dayOffCategories.getId() );
        sabbaticalLogsResponse.content( dayOffCategories.getContent() );
        sabbaticalLogsResponse.fluctuatesTime( dayOffCategories.getFluctuatesTime() );
        sabbaticalLogsResponse.remaining( dayOffCategories.getRemaining() );
        sabbaticalLogsResponse.updateTime( dayOffCategories.getUpdateTime() );
        sabbaticalLogsResponse.employee( dayOffCategories.getEmployee() );

        return sabbaticalLogsResponse.build();
    }

    @Override
    public void updateSabLogs(SabbaticalLeaveLogs sabbaticalLeaveLogs, SabbaticalLogsRequest request) {
        if ( request == null ) {
            return;
        }

        sabbaticalLeaveLogs.setUpdateTime( request.getUpdateTime() );
        sabbaticalLeaveLogs.setFluctuatesTime( request.getFluctuatesTime() );
        sabbaticalLeaveLogs.setContent( request.getContent() );
        sabbaticalLeaveLogs.setRemaining( request.getRemaining() );
    }
}
