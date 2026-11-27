package com.hrm.hrm;

import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Formula;
import com.hrm.Entity.wage.Wage;
import com.hrm.Service.extra.ExtraRequestService;
import com.hrm.Service.hrm.PayrollCalculationService;
import com.hrm.Service.hrm.SalaryFormulaEvaluator;
import com.hrm.repository.hrm.HrmLeaveRequestRepository;
import com.hrm.repository.hrm.HrmOvertimeRequestRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.AdvanceRepository;
import com.hrm.repository.wage.WageMonthlyRepository;
import com.hrm.repository.wage.WageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/** Kiểm thử: lương thực lãnh được tính theo công thức gán cho nhân viên. */
@ExtendWith(MockitoExtension.class)
class PayrollCalculationServiceTest {

    @Mock WageRepository wageRepository;
    @Mock WageMonthlyRepository wageMonthlyRepository;
    @Mock AdvanceRepository advanceRepository;
    @Mock HrmOvertimeRequestRepository overtimeRequestRepository;
    @Mock HrmLeaveRequestRepository leaveRequestRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock ExtraRequestService extraRequestService;

    PayrollCalculationService service;

    @BeforeEach
    void setUp() {
        service = new PayrollCalculationService(wageRepository, wageMonthlyRepository, advanceRepository,
                overtimeRequestRepository, leaveRequestRepository, employeeRepository, extraRequestService,
                new SalaryFormulaEvaluator());

        when(wageRepository.existsByEmployeeCate(1, 4))
                .thenReturn(Wage.builder().salary("10000").build());
        when(wageMonthlyRepository.findByEmployeeId(1, "01/2026")).thenReturn(List.of());
        when(wageRepository.findByEmployee(1)).thenReturn(List.of());
        when(overtimeRequestRepository.findAllByEmployee_Id(1)).thenReturn(List.of());
        lenient().when(extraRequestService.overtimeInfo(1, "2026-01")).thenReturn(Map.of("hours", 0));
        when(advanceRepository.findByNameAndStatus(any(), anyInt(), eq(1), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(leaveRequestRepository.findAllByEmployee_Id(1)).thenReturn(List.of());
    }

    private void givenFormula(String expression) {
        Formula formula = Formula.builder()
                .id(7)
                .name("Công thức thử nghiệm")
                .salaryFormula(expression)
                .status(1)
                .build();
        when(employeeRepository.findById(1)).thenReturn(Optional.of(
                Employee.builder().id(1).name("NV1").formula(formula).build()));
    }

    @Test
    void usesEmployeeFormulaWhenPresent() {
        givenFormula("basic * 2 + allowances - deductions");

        Map<String, Object> result = service.calculate(1, "2026-01", 0);

        assertThat(result.get("net")).isEqualTo(20000d);
        assertThat(result.get("formulaApplied")).isEqualTo(true);
        assertThat(result.get("formulaId")).isEqualTo(7);
        assertThat(result.get("formulaName")).isEqualTo("Công thức thử nghiệm");
        assertThat(result.get("note").toString()).contains("Công thức thử nghiệm");
    }

    @Test
    void fallsBackToDefaultFormulaWhenEmployeeHasNoFormula() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(
                Employee.builder().id(1).name("NV1").build()));

        Map<String, Object> result = service.calculate(1, "2026-01", 0);

        assertThat(result.get("net")).isEqualTo(10000d);
        assertThat(result.get("formulaApplied")).isEqualTo(false);
        assertThat(result.get("formulaId")).isNull();
    }

    @Test
    void fallsBackToDefaultFormulaWhenFormulaIsInvalid() {
        givenFormula("basic + unknownVariable");

        Map<String, Object> result = service.calculate(1, "2026-01", 0);

        assertThat(result.get("net")).isEqualTo(10000d);
        assertThat(result.get("formulaApplied")).isEqualTo(false);
        assertThat(result.get("note").toString()).contains("không hợp lệ");
    }
}
