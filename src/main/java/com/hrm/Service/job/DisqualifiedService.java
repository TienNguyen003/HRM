package com.hrm.Service.job;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.job.Disqualified;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Service.CloudinaryService;
import com.hrm.repository.job.DisqualifiedRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DisqualifiedService {
    DisqualifiedRepository disqualifiedRepository;
    CloudinaryService cloudinaryService;

    // thêm
    public void createDisqualified(Disqualified request, MultipartFile file, String nameFile) throws IOException {
        if(disqualifiedRepository.existsByEmailAndPhone(request.getEmail(), request.getPhone()))
            throw new AppException(ErrorCode.CV_DISQUALIFIED);
        String url = cloudinaryService.uploadFile(file, nameFile);
        request.setLinkFile(url);
        request.setUpdateTime(LocalDateTime.now());

        disqualifiedRepository.save(request);
    }

    public boolean getDisqualified(String email, String phone){
        return disqualifiedRepository.existsByEmailAndPhone(email, phone);
    }

    public List<Disqualified> getAllDisqualified(){
        return disqualifiedRepository.findAll();
    }

    // lấy chi tiết 1 CV
    public Disqualified getDisqualifiedById(int id){
        return disqualifiedRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CV_NOT_EXISTED));
    }

    // cập nhật trạng thái duyệt / loại CV
    public Disqualified updateStatus(int id, int status){
        Disqualified disqualified = getDisqualifiedById(id);
        disqualified.setStatus(status);
        disqualified.setUpdateTime(LocalDateTime.now());
        return disqualifiedRepository.save(disqualified);
    }

    public List<Disqualified> getDisqualifiedByPage(int pageNumber, int pageSize, String email, String phone, Integer status) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return disqualifiedRepository.findByFilters(email, phone, status, pageable).getContent();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String email, String phone, Integer status) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Disqualified> page = disqualifiedRepository.findByFilters(email, phone, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }
}
