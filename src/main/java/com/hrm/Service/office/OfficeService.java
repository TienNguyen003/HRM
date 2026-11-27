package com.hrm.Service.office;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.office.OfficeI;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.office.OfficeMapper;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.office.OfficeRespone;
import com.hrm.repository.hrm.HrmReferenceDataRepository;
import com.hrm.repository.office.DepartmentRepository;
import com.hrm.repository.office.OfficeRepository;
import com.hrm.repository.user.EmployeeRepository;
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
    HrmReferenceDataRepository referenceDataRepository;
    DepartmentRepository departmentRepository;
    EmployeeRepository employeeRepository;

    // thêm
    public OfficeRespone create(OfficeRequest request){
        if(officeRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.OFFICE_EXISTED);

        OfficeI office = officeMapper.toOfficeI(request);
        office.setStatus(1);
        OfficeI saved = officeRepository.save(office);
        syncReference(saved.getName(), null);
        return officeMapper.toOfficeRespone(saved);
    }

    // cập nhật
    public OfficeRespone update(int id, OfficeRequest request){
        OfficeI office = officeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED));

        String oldName = office.getName();
        officeMapper.updateOffice(office, request);
        OfficeI saved = officeRepository.save(office);

        if (oldName != null && !oldName.equals(saved.getName())) {
            syncReference(saved.getName(), oldName);
            var affected = employeeRepository.findByOffice_Name(oldName);
            affected.forEach(e -> {
                e.setOffice(saved);
                e.setLocation(saved.getName());
            });
            employeeRepository.saveAll(affected);
        }
        return officeMapper.toOfficeRespone(saved);
    }

    public String updateStt(int id, int status){
        OfficeI department = officeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED));

        department.setStatus(status);
        officeMapper.toOfficeRespone(officeRepository.save(department));

        return "Update success";
    }

    // lấy ra tất cả
    public List<OfficeRespone> getAll(){
        return officeRepository.getOffices()
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
    private void syncReference(String newName, String oldName) {
        if (oldName != null) {
            referenceDataRepository.findById("REF#LOCATIONS#" + oldName.toUpperCase())
                    .ifPresent(referenceDataRepository::delete);
        }
        String refId = "REF#LOCATIONS#" + newName.toUpperCase();
        if (newName != null && !referenceDataRepository.existsById(refId)) {
            referenceDataRepository.save(new com.hrm.Entity.hrm.HrmReferenceData(
                    refId, "locations", newName, newName, 0));
        }
    }

    public void delete(int id){
        OfficeI office = officeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OFFICE_NOT_EXISTED));
        if (departmentRepository.findAll().stream()
                .anyMatch(d -> d.getOfficeI() != null && office.getId() == d.getOfficeI().getId())) {
            throw new AppException(ErrorCode.OFFICE_IN_USE);
        }
        var affected = employeeRepository.findByOffice_Name(office.getName());
        affected.forEach(e -> {
            e.setOffice(null);
            e.setLocation("Unassigned");
        });
        employeeRepository.saveAll(affected);
        referenceDataRepository.findById("REF#LOCATIONS#" + office.getName().toUpperCase())
                .ifPresent(referenceDataRepository::delete);
        officeRepository.delete(office);
    }
}

