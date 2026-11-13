package com.hrm.Service.job;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.job.JobPosting;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.job.JobPostingMapper;
import com.hrm.dto.request.job.JobPostingRequest;
import com.hrm.dto.response.job.JobPostingResponse;
import com.hrm.repository.job.JobPostingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobPostingService {
    JobPostingRepository jobPostingRepository;
    JobPostingMapper jobPostingMapper;

    // tạo tin tuyển dụng
    public JobPostingResponse create(JobPostingRequest request) {
        if (jobPostingRepository.existsByTitle(request.getTitle()))
            throw new AppException(ErrorCode.JOB_EXISTED);

        JobPosting jobPosting = jobPostingMapper.toJobPosting(request);
        jobPosting.setCreateTime(String.valueOf(LocalDateTime.now()));

        return jobPostingMapper.toJobPostingResponse(jobPostingRepository.save(jobPosting));
    }

    // lấy theo id
    public JobPostingResponse getById(int id) {
        return jobPostingMapper.toJobPostingResponse(getJobPosting(id));
    }

    // cập nhật tin tuyển dụng
    public JobPostingResponse update(int id, JobPostingRequest request) {
        JobPosting jobPosting = getJobPosting(id);
        jobPostingMapper.updateJobPosting(jobPosting, request);

        return jobPostingMapper.toJobPostingResponse(jobPostingRepository.save(jobPosting));
    }

    // tìm kiếm theo chức danh + trạng thái
    public List<JobPostingResponse> search(int pageNumber, int pageSize, String title, Integer status) {
        return jobPostingRepository.findByFilters(title, status, pageRequest(pageNumber, pageSize))
                .stream().map(jobPostingMapper::toJobPostingResponse).toList();
    }

    // thông tin phân trang
    public PageCustom getPagination(int pageNumber, int pageSize, String title, Integer status) {
        Page<JobPosting> page = jobPostingRepository.findByFilters(title, status, pageRequest(pageNumber, pageSize));
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa tin tuyển dụng
    public void delete(int id) {
        jobPostingRepository.delete(getJobPosting(id));
    }

    private JobPosting getJobPosting(int id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));
    }

    private Pageable pageRequest(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber - 1, pageSize);
    }
}
