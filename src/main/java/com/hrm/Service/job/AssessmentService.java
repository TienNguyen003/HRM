package com.hrm.Service.job;

import com.hrm.Entity.job.Assessment;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.job.AssessmentMapper;
import com.hrm.dto.request.job.AssessmentRequest;
import com.hrm.dto.response.job.AssessmentResponse;
import com.hrm.repository.job.AssessmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssessmentService {
    AssessmentRepository assessmentRepository;
    AssessmentMapper assessmentMapper;

    // thêm
    public AssessmentResponse createAssess(AssessmentRequest request){
        if(assessmentRepository.existsByTitle(request.getTitle()))
            throw new AppException(ErrorCode.REQUIRE_EXISTED);

        Assessment assessment = assessmentMapper.toAssessment(request);

        return assessmentMapper.toAssessmentResponse(assessmentRepository.save(assessment));
    }

    // cập nhật
    public AssessmentResponse updateAssess(int assessId, AssessmentRequest request){
        Assessment assessment = assessmentRepository.findById(assessId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUIRE_NOT_EXISTED));

        assessmentMapper.updateAssessment(assessment, request);

        return assessmentMapper.toAssessmentResponse(assessmentRepository.save(assessment));
    }

    public List<AssessmentResponse> getAllAssess(){
        return assessmentRepository.findAll()
                .stream().map(assessmentMapper::toAssessmentResponse).toList();
    }

    // lấy theo id
    public AssessmentResponse getAssess(int datOffId){
        return assessmentMapper.toAssessmentResponse(assessmentRepository.findById(datOffId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUIRE_NOT_EXISTED)));
    }

    // xóa
    public void deleteAssess(int assessId){
        assessmentRepository.deleteById(assessId);
    }
}
