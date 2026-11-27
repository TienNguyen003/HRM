package com.hrm.Service.hrm;

import com.hrm.Entity.hrm.HrmLeaveRequest;
import com.hrm.Entity.hrm.HrmOvertimeRequest;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Formula;
import com.hrm.Entity.wage.Wage;
import com.hrm.Service.extra.ExtraRequestService;
import com.hrm.repository.hrm.HrmLeaveRequestRepository;
import com.hrm.repository.hrm.HrmOvertimeRequestRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.AdvanceRepository;
import com.hrm.repository.wage.WageMonthlyRepository;
import com.hrm.repository.wage.WageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tính bảng lương tháng cho từng nhân viên từ dữ liệu lương có sẵn.
 *
 * <p><b>Lương thực lãnh được tính theo công thức gán cho nhân viên</b>
 * ({@link Employee#getFormula()} → {@link Formula#getSalaryFormula()}). Công thức là biểu thức
 * toán học với các biến: {@code basic, allowances, overtime, advance, unpaidLeave, deductions,
 * overtimeHours, leaveDays, standardHours, overtimeRate, workingDays}; hỗ trợ {@code + - * / ()}
 * và các hàm {@code min, max, abs, round}.</p>
 *
 * <p>Các khoản đầu vào vẫn được tính theo quy tắc chung (thống nhất với AdvanceService /
 * ExtraRequestService):</p>
 * <ul>
 *   <li>Lương cơ bản: Wage theo danh mục có id = 4.</li>
 *   <li>Phụ cấp: WageMonthly của tháng (trừ danh mục cơ bản); nếu tháng chưa có thì lấy Wage tĩnh.</li>
 *   <li>Tăng ca: giờ duyệt × (lương cơ bản / 174) × 1.5.</li>
 *   <li>Ứng lương: tổng các phiếu ứng lương đã duyệt trong tháng.</li>
 *   <li>Nghỉ không lương: số ngày × (lương cơ bản / 22).</li>
 * </ul>
 *
 * <p>Nếu nhân viên chưa được gán công thức (hoặc công thức lỗi), hệ thống dùng công thức mặc định
 * {@code basic + allowances + overtime - advance - unpaidLeave} để không làm hỏng bảng lương.
 * Trường hợp nhân viên chưa có lương cơ bản, dùng {@code fallbackBasic} (amount của đợt lương).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayrollCalculationService {

    static final int BASIC_SALARY_CATEGORY_ID = 4;
    static final double STANDARD_HOURS_PER_MONTH = 174;
    static final double OVERTIME_RATE = 1.5;
    static final double STANDARD_WORKING_DAYS = 22;
    static final String UNPAID_LEAVE = "Unpaid";
    static final String APPROVED = "Approved";

    WageRepository wageRepository;
    WageMonthlyRepository wageMonthlyRepository;
    AdvanceRepository advanceRepository;
    HrmOvertimeRequestRepository overtimeRequestRepository;
    HrmLeaveRequestRepository leaveRequestRepository;
    EmployeeRepository employeeRepository;
    ExtraRequestService extraRequestService;
    SalaryFormulaEvaluator formulaEvaluator;

    public Map<String, Object> calculate(int employeeId, String month, double fallbackBasic) {
        String yearMonth = normalizeMonth(month);
        String legacyMonth = toLegacyMonth(yearMonth);

        double basic = basicSalary(employeeId);
        boolean usingFallback = basic <= 0 && fallbackBasic > 0;
        if (usingFallback) basic = fallbackBasic;

        List<Map<String, Object>> components = new ArrayList<>();
        double allowances = allowances(employeeId, legacyMonth, components);

        double hourlyRate = basic / STANDARD_HOURS_PER_MONTH;
        OvertimePart overtimePart = overtime(employeeId, yearMonth, hourlyRate, components);
        double overtime = overtimePart.amount();

        double advance = advance(employeeId, yearMonth);
        if (advance > 0) components.add(component("Ứng lương", "DEDUCTION", -advance));

        LeavePart leavePart = unpaidLeave(employeeId, yearMonth, basic, components);
        double unpaidLeave = leavePart.amount();
        double deductions = advance + unpaidLeave;

        // Công thức mặc định, dùng khi nhân viên chưa được gán công thức hoặc công thức lỗi.
        double defaultNet = basic + allowances + overtime - deductions;
        FormulaApplication application = applyFormula(employeeId, basic, allowances, overtime,
                advance, unpaidLeave, deductions, overtimePart.hours(), leavePart.days(), defaultNet);
        double net = application.applied() ? application.net() : defaultNet;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("employeeId", employeeId);
        result.put("month", yearMonth);
        result.put("basic", round(basic));
        result.put("allowances", round(allowances));
        result.put("overtime", round(overtime));
        result.put("advance", round(advance));
        result.put("unpaidLeave", round(unpaidLeave));
        result.put("deductions", round(deductions));
        result.put("overtimeHours", round(overtimePart.hours()));
        result.put("leaveDays", round(leavePart.days()));
        result.put("net", round(net));
        result.put("components", components);
        result.put("formulaId", application.formulaId());
        result.put("formulaName", application.formulaName());
        result.put("formulaApplied", application.applied());
        result.put("note", note(application, usingFallback));
        return result;
    }

    /** Áp dụng công thức được gán cho nhân viên. Trả về applied=false nếu không có hoặc lỗi. */
    private FormulaApplication applyFormula(int employeeId, double basic, double allowances,
                                            double overtime, double advance, double unpaidLeave,
                                            double deductions, double overtimeHours, double leaveDays,
                                            double defaultNet) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        Formula formula = employee == null ? null : employee.getFormula();
        if (formula == null || formula.getSalaryFormula() == null || formula.getSalaryFormula().isBlank()) {
            return new FormulaApplication(false, defaultNet, null, null, null);
        }

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("basic", basic);
        variables.put("allowances", allowances);
        variables.put("overtime", overtime);
        variables.put("advance", advance);
        variables.put("unpaidLeave", unpaidLeave);
        variables.put("deductions", deductions);
        variables.put("overtimeHours", overtimeHours);
        variables.put("leaveDays", leaveDays);
        variables.put("standardHours", STANDARD_HOURS_PER_MONTH);
        variables.put("overtimeRate", OVERTIME_RATE);
        variables.put("workingDays", STANDARD_WORKING_DAYS);

        try {
            double value = formulaEvaluator.evaluate(formula.getSalaryFormula(), variables);
            log.debug("Áp dụng công thức '{}' cho nhân viên {}: {}", formula.getName(), employeeId, value);
            return new FormulaApplication(true, value, formula.getId(), formula.getName(), null);
        } catch (RuntimeException e) {
            log.warn("Công thức lương '{}' của nhân viên {} không hợp lệ, dùng công thức mặc định: {}",
                    formula.getName(), employeeId, e.getMessage());
            return new FormulaApplication(false, defaultNet, formula.getId(), formula.getName(), e.getMessage());
        }
    }

    private String note(FormulaApplication application, boolean usingFallback) {
        if (application.applied())
            return "Tính theo công thức '" + application.formulaName() + "' gán cho nhân viên";
        if (application.error() != null)
            return "Công thức lương không hợp lệ (" + application.error() + "), dùng công thức mặc định";
        if (usingFallback) return "Chưa có lương cơ bản, dùng amount của đợt lương";
        return "Tính từ lương cơ bản, phụ cấp, tăng ca, ứng lương và nghỉ không lương";
    }

    // ------------------------------------------------------------------ parts

    private double basicSalary(int employeeId) {
        try {
            Wage wage = wageRepository.existsByEmployeeCate(employeeId, BASIC_SALARY_CATEGORY_ID);
            return wage == null ? 0 : amount(wage.getSalary());
        } catch (NonUniqueResultException e) {
            return 0;
        }
    }

    private double allowances(int employeeId, String legacyMonth, List<Map<String, Object>> components) {
        List<com.hrm.Entity.wage.WageMonthly> monthly =
                wageMonthlyRepository.findByEmployeeId(employeeId, legacyMonth);
        double total = 0;
        for (com.hrm.Entity.wage.WageMonthly wage : monthly) {
            if (isBasic(wage.getWageCategories() == null ? -1 : wage.getWageCategories().getId())) continue;
            double value = amount(wage.getSalary());
            total += value;
            components.add(component(categoryName(wage.getWageCategories()),
                    categoryType(wage.getWageCategories()), value));
        }
        if (!monthly.isEmpty()) return total;

        // Tháng chưa có lương động -> dùng các danh mục lương tĩnh (trừ lương cơ bản).
        try {
            for (Wage wage : wageRepository.findByEmployee(employeeId)) {
                if (isBasic(wage.getWageCategories() == null ? -1 : wage.getWageCategories().getId())) continue;
                double value = amount(wage.getSalary());
                total += value;
                components.add(component(categoryName(wage.getWageCategories()),
                        categoryType(wage.getWageCategories()), value));
            }
        } catch (Exception ignored) {
            // Dữ liệu lương tĩnh có thể trùng danh mục; bỏ qua thay vì làm hỏng cả bảng lương.
        }
        return total;
    }

    private OvertimePart overtime(int employeeId, String yearMonth, double hourlyRate,
                                  List<Map<String, Object>> components) {
        double hours = 0;
        for (HrmOvertimeRequest request : overtimeRequestRepository.findAllByEmployee_Id(employeeId)) {
            if (!APPROVED.equalsIgnoreCase(request.getStatus())) continue;
            if (!inMonth(request.getDate(), yearMonth)) continue;
            hours += request.getHours() == null ? 0 : request.getHours();
        }
        if (hours <= 0) {
            // Fallback sang đơn tăng ca legacy (đã duyệt) dùng cùng công thức 174h/1.5.
            try {
                Map<String, Object> info = extraRequestService.overtimeInfo(employeeId, yearMonth);
                Object value = info.get("hours");
                hours = value instanceof Number n ? n.doubleValue() : 0;
            } catch (Exception ignored) {
                hours = 0;
            }
        }
        double value = Math.round(hours * hourlyRate * OVERTIME_RATE);
        if (value > 0) components.add(component("Tăng ca (" + hours + " giờ)", "ALLOWANCE", value));
        return new OvertimePart(hours, value);
    }

    private double advance(int employeeId, String yearMonth) {
        // SUM(money) không chạy trên H2 vì cột money là String -> cộng trong Java.
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
        double total = 0;
        try {
            for (com.hrm.Entity.wage.Advance item : advanceRepository
                    .findByNameAndStatus(null, 1, employeeId, org.springframework.data.domain.Pageable.unpaged())) {
                if (item.getRequestTime() == null) continue;
                if (item.getRequestTime().isBefore(start) || item.getRequestTime().isAfter(end)) continue;
                total += amount(item.getMoney());
            }
        } catch (Exception ignored) {
            return 0;
        }
        return total;
    }

    private LeavePart unpaidLeave(int employeeId, String yearMonth, double basic,
                                  List<Map<String, Object>> components) {
        double days = 0;
        for (HrmLeaveRequest request : leaveRequestRepository.findAllByEmployee_Id(employeeId)) {
            if (!APPROVED.equalsIgnoreCase(request.getStatus())) continue;
            if (!UNPAID_LEAVE.equalsIgnoreCase(request.getType())) continue;
            if (!overlapsMonth(request.getStart(), request.getEnd(), yearMonth)) continue;
            days += request.getDays();
        }
        double value = Math.round(days * (basic / STANDARD_WORKING_DAYS));
        if (value > 0) components.add(component("Nghỉ không lương (" + days + " ngày)", "DEDUCTION", -value));
        return new LeavePart(days, value);
    }

    // ------------------------------------------------------------------ helpers

    private Map<String, Object> component(String name, String type, double amount) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("type", type);
        item.put("amount", round(amount));
        return item;
    }

    private boolean isBasic(int categoryId) {
        return categoryId == BASIC_SALARY_CATEGORY_ID;
    }

    private String categoryName(com.hrm.Entity.wage.WageCategories category) {
        return category == null ? "Khác" : category.getName();
    }

    private String categoryType(com.hrm.Entity.wage.WageCategories category) {
        return category == null ? "OTHER" : category.getSalaryType();
    }

    private double amount(String value) {
        if (value == null || value.isBlank()) return 0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean inMonth(String date, String yearMonth) {
        return date != null && date.length() >= 7 && date.substring(0, 7).equals(yearMonth);
    }

    private boolean overlapsMonth(String start, String end, String yearMonth) {
        if (start == null || start.isBlank()) return false;
        String first = yearMonth + "-01";
        String last = YearMonth.parse(yearMonth).atEndOfMonth().toString();
        String from = start.length() >= 10 ? start.substring(0, 10) : start;
        String to = end == null || end.isBlank() ? first : (end.length() >= 10 ? end.substring(0, 10) : end);
        return from.compareTo(last) <= 0 && to.compareTo(first) >= 0;
    }

    /** Chuyển mọi định dạng tháng về YYYY-MM. */
    public String normalizeMonth(String month) {
        if (month == null || month.isBlank()) return "";
        if (month.contains("-")) {
            String[] parts = month.split("-");
            if (parts.length < 2) return "";
            return parts[0] + "-" + String.format("%02d", Integer.parseInt(parts[1]));
        }
        String[] parts = month.split("/");
        if (parts.length < 2) return "";
        return parts[1] + "-" + String.format("%02d", Integer.parseInt(parts[0]));
    }

    private String toLegacyMonth(String yearMonth) {
        if (yearMonth == null || yearMonth.length() < 7) return "";
        return yearMonth.substring(5) + "/" + yearMonth.substring(0, 4);
    }

    // ------------------------------------------------------------------ records

    private record OvertimePart(double hours, double amount) {}

    private record LeavePart(double days, double amount) {}

    private record FormulaApplication(boolean applied, double net, Integer formulaId,
                                      String formulaName, String error) {}
}
