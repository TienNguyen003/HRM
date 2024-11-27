package com.hrm.Service.timekeeping;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.timekeeping.TimeKeeping;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.timekeeping.TimeKeepingMapper;
import com.hrm.dto.request.timekeeping.TimeKeepingRequest;
import com.hrm.dto.response.timekeeping.TimeKeepingRespone;
import com.hrm.repository.timekeeping.TimeRepository;
import com.hrm.repository.user.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeService {
	TimeRepository timeRepository;
	TimeKeepingMapper keepingMapper;
	EmployeeRepository employeeRepository;
	
	public TimeKeepingRespone createTime(TimeKeepingRequest request) {
		int isCheck = timeRepository.exitsByDayId(request.getEmployeeId(), request.getDate());
		if(isCheck > 1)
			throw new AppException(ErrorCode.TIME_EXISTED);
		Employee employee = employeeRepository.findById(request.getEmployeeId())
				.orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
		LocalTime time = LocalTime.parse(request.getTime());
		LocalTime defaultTime = LocalTime.parse("08:00:00");
		if (time.isAfter(defaultTime)) employee.setLateness(employee.getLateness() + 1);

		employee.setTimekeeping(employee.getTimekeeping() - 1);
		TimeKeeping keeping = keepingMapper.toKeeping(request);
		keeping.setEmployee(employee);
		if (isCheck == 1) keeping.setType(1);

		return keepingMapper.toKeepingRespone(timeRepository.save(keeping));
	}

	public List<TimeKeepingRespone> getAll(){
		return timeRepository.findAll()
				.stream().map(keepingMapper::toKeepingRespone).toList();
	}

	public List<TimeKeepingRespone> getTimes(String name, String date, String department, String office, Integer id, int pageNumber, int pageSize){
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		return timeRepository.findByName(name, date, department, office, id, pageable)
				.stream().map(keepingMapper::toKeepingRespone).toList();
	}

	public PageCustom getPagination(String name, String date, String department, String office, Integer id, int pageNumber, int pageSize){
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<TimeKeeping> page = timeRepository.findByName(name, date, department, office, id, pageable);
		return PageCustom.builder()
				.totalPages(String.valueOf(page.getTotalPages()))
				.totalItems(String.valueOf(page.getTotalElements()))
				.totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
				.currentPage(String.valueOf(pageNumber))
				.build();
	}

	@PostAuthorize("returnObject.employee.id == authentication.principal.getClaimAsString('idE') or !hasRole('NHÂN')")
	public TimeKeepingRespone getTime(int id) {
		return keepingMapper.toKeepingRespone(timeRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.TIME_NOT_EXISTED)));
	}
	
	public TimeKeepingRespone updateTime(int timeId, TimeKeepingRequest request) {
		TimeKeeping keeping = timeRepository.findById(timeId)
				.orElseThrow(() -> new AppException(ErrorCode.TIME_NOT_EXISTED));

		keepingMapper.updateKeeping(keeping, request);
		return keepingMapper.toKeepingRespone(timeRepository.save(keeping));
	}
	
	public void deleteTime(int id) {
		timeRepository.deleteById(id);
	}
}
