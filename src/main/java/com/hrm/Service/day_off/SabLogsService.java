package com.hrm.Service.day_off;

import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.day_off.SabbaticalLogsMapper;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import com.hrm.repository.day_off.SabLogsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SabLogsService {
    SabLogsRepository sabLogsRepository;
    SabbaticalLogsMapper sabbaticalLogsMapper;

    // thêm
    public SabbaticalLogsResponse create(SabbaticalLogsRequest request){

        SabbaticalLeaveLogs sabbaticalLeaveLogs = sabbaticalLogsMapper.toSabLogs(request);

        return sabbaticalLogsMapper.toSabLogsResponse(sabLogsRepository.save(sabbaticalLeaveLogs));
    }

    // lấy ra tất cả
    public List<SabbaticalLogsResponse> getAllSab(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return sabLogsRepository.findAll(pageable)
                .stream().map(sabbaticalLogsMapper::toSabLogsResponse).toList();
    }

    // lấy theo id
    public List<SabbaticalLogsResponse> getByEmpId(String employeeId, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return sabLogsRepository.findByEmpId(employeeId, pageable)
                .stream().map(sabbaticalLogsMapper::toSabLogsResponse).toList();
    }

    // tìm kiếm
    public List<SabbaticalLogsResponse> searchAllSab(int pageNumber, int pageSize, String name){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return sabLogsRepository.findByNameContaining(name, pageable)
                .stream().map(sabbaticalLogsMapper::toSabLogsResponse).toList();
    }
}
