package com.hrm.Service.job;

import com.hrm.Entity.job.Disqualified;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Service.CloudinaryService;
import com.hrm.repository.job.DisqualifiedRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

        disqualifiedRepository.save(request);
    }

    public boolean getDisqualified(String email, String phone){
        return disqualifiedRepository.existsByEmailAndPhone(email, phone);
    }

    public List<Disqualified> getAllDisqualified(){
        return disqualifiedRepository.findAll();
    }
}
