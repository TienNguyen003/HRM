package com.hrm.Service.office;

import com.hrm.Entity.office.Shift;
import com.hrm.Mapper.office.ShiftMapper;
import com.hrm.dto.request.office.ShiftRequest;
import com.hrm.dto.response.office.ShiftResponse;
import com.hrm.repository.office.ShiftRepository;
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
public class ShiftService {
    ShiftRepository shiftRepository;
    ShiftMapper shiftMapper;

    // thêm
    public ShiftResponse create(ShiftRequest request){

        Shift shift = shiftMapper.toShift(request);

        return shiftMapper.toShiftResponse(shiftRepository.save(shift));
    }

    // cập nhật
    public ShiftResponse update(int dayOffId, ShiftRequest request){

        Shift shift = shiftRepository.findById(dayOffId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        shiftMapper.updateShift(shift, request);

        return shiftMapper.toShiftResponse(shiftRepository.save(shift));
    }

    // lấy ra tất cả
    public List<ShiftResponse> getAll(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return shiftRepository.findAll(pageable)
                .stream().map(shiftMapper::toShiftResponse).toList();
    }

    // xóa
    public void delete(int id){
        shiftRepository.deleteById(id);
    }
}
