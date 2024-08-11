package com.hrm.Service.office;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.day_off.ApplicationLeave;
import com.hrm.Entity.office.OfficeI;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.office.OfficeMapper;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.office.OfficeRespone;
import com.hrm.repository.office.OfficeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OfficeService {
    OfficeRepository officeRepository;
    OfficeMapper officeMapper;

    // thêm
    public OfficeRespone create(OfficeRequest request){
        if(officeRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.OFFICE_EXISTED);

        OfficeI dayOffCategories = officeMapper.toOfficeI(request);

        return officeMapper.toOfficeRespone(officeRepository.save(dayOffCategories));
    }

    // cập nhật
    public OfficeRespone update(int id, OfficeRequest request){

        OfficeI department = officeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED));

        officeMapper.updateOffice(department, request);

        return officeMapper.toOfficeRespone(officeRepository.save(department));
    }

    // lấy ra tất cả
    public List<OfficeRespone> getAll(){
        return officeRepository.findAll()
                .stream().map(officeMapper::toOfficeRespone).toList();
    }

    // lấy theo id
    public OfficeRespone getById(int id){
        return officeMapper.toOfficeRespone(officeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED)));
    }

    // tìm kiếm
    public List<OfficeRespone> searchAll(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return officeRepository.findByNameAndStatus(name, status, pageable)
                .stream().map(officeMapper::toOfficeRespone).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<OfficeI> page = officeRepository.findByNameAndStatus(name, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void delete(int id){
        officeRepository.deleteById(id);
    }
}
