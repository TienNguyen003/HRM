package com.hrm.Service.job;

import com.hrm.Entity.job.Requirements;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.job.RequirementsMapper;
import com.hrm.dto.request.job.RequirementsRequest;
import com.hrm.dto.response.job.RequirementsResponse;
import com.hrm.repository.job.RequireRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequireService {
    RequireRepository requireRepository;
    RequirementsMapper requireMapper;

    // thêm
    public RequirementsResponse createRequire(RequirementsRequest request){
        if(requireRepository.existsByTitle(request.getTitle()))
            throw new AppException(ErrorCode.REQUIRE_EXISTED);

        Requirements require = requireMapper.toRequirements(request);

        return requireMapper.toRequirementsResponse(requireRepository.save(require));
    }

    // cập nhật
    public RequirementsResponse updateRequire(int requireId, RequirementsRequest request){
        Requirements require = requireRepository.findById(requireId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUIRE_NOT_EXISTED));

        requireMapper.updateRequire(require, request);

        return requireMapper.toRequirementsResponse(requireRepository.save(require));
    }

    public List<RequirementsResponse> getAllRequire(){
        return requireRepository.findAll()
                .stream().map(requireMapper::toRequirementsResponse).toList();
    }

    // lấy theo id
    public RequirementsResponse getRequire(int requireId){
        return requireMapper.toRequirementsResponse(requireRepository.findById(requireId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUIRE_NOT_EXISTED)));
    }

    // lấy theo id assessId
    public List<RequirementsResponse> getByAssessId(int requireId){
        return requireRepository.getByAssessmentId(requireId)
                .stream().map(requireMapper::toRequirementsResponse).toList();
    }

    // xóa
    public void deleteRequire(int requireId){
        requireRepository.deleteById(requireId);
    }
}
