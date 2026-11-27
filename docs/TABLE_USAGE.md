# Tình trạng sử dụng bảng trong HRM Backend

Rà soát từ code (`src/main/java`) ngày 25/11/2026. "Đang dùng" = có Entity + Repository được
Service/Controller/Seeder gọi. Các bảng cũ vẫn có thể tồn tại trong MySQL vì `ddl-auto: update`
không tự DROP.

## 1. Bảng đang dùng cho nghiệp vụ

### Hệ thống cũ (`/api/*`)

| Bảng | Entity | Repository dùng ở |
|---|---|---|
| `employee` | `Entity.user.Employee` | 21 service (Employee, User, Payroll, Contract, Task, Leave, Dashboard, Report, HRM...) |
| `user` | `Entity.user.User` | UserService, AuthenticationService, ApplicationInitConfig, HrmService |
| `role`, `permission` | `Entity.role.*` | RoleService, PermissionService, ApplicationInitConfig |
| `department` | `Entity.office.Department` | DepartmentService, EmployeeService, OfficeService, DashboardService, HrmService |
| `officei` | `Entity.office.OfficeI` | OfficeService, DepartmentService, HrmService, HrmSeeder |
| `shift` | `Entity.office.Shift` | ShiftService, HrmService |
| `day_off_categories` | `Entity.day_off.DayOffCategories` | DayOffService, LeaveService |
| `holiday` | `Entity.day_off.Holiday` | HolidayService |
| `sabbatical_leave_logs` | `Entity.day_off.SabbaticalLeaveLogs` | SabLogsService, LeaveService |
| `document` | `Entity.document.Document` | DocumentService |
| `extra_request` | `Entity.extra.ExtraRequest` | ExtraRequestService |
| `assessment`, `requirements` | `Entity.job.*` | AssessmentService, RequireService, CheckCVService, CvAssessmentService |
| `job_posting`, `disqualified` | `Entity.job.*` | JobPostingService, DisqualifiedService, DashboardService, ReportService |
| `cv_assessment_score` | `Entity.job.CvAssessmentScore` | CvAssessmentService |
| `notification` | `Entity.notification.Notification` | NotificationService |
| `invalidate_token` | `Entity.token.InvalidateToken` | AuthenticationService |
| `task` | `Entity.task.Task` | TaskService |
| `time_keeping` | `Entity.timekeeping.TimeKeeping` | TimeService, DashboardService, ReportService |
| `bank`, `contracts` | `Entity.user.*` | BankService, ContractsService, NotificationService |
| `advance`, `formula`, `payroll`, `wage`, `wage_categories`, `wage_monthly` | `Entity.wage.*` | dịch vụ lương tương ứng + Dashboard/Report |

### Module HRM (`/api/hrm/*`)

Tất cả bảng `hrm_*` dưới đây đều có Entity + Repository và được `HrmService` dùng:

`hrm_attendance`, `hrm_leave_request`, `hrm_leave_balance`, `hrm_leave_policy`,
`hrm_document`, `hrm_activity_log`, `hrm_candidate`, `hrm_interview`, `hrm_position`,
`hrm_goal`, `hrm_kpi`, `hrm_review`, `hrm_feedback`, `hrm_notification`, `hrm_growth`,
`hrm_overtime_request`, `hrm_payroll_run`, `hrm_payslip`, `hrm_onboarding_task`,
`hrm_offboarding_task`, `hrm_roster`, `hrm_benefit`, `hrm_benefit_enrollment`,
`hrm_hr_request`, `hrm_announcement`, `hrm_company_event`, `hrm_audit_log`.

## 2. Bảng chỉ dùng cho seed/hạ tầng

| Bảng | Dùng bởi | Mục đích |
|---|---|---|
| `hrm_reference_data` | HrmSeeder, HrmService, OfficeService | danh mục + marker (reference/config seed) |
| `hrm_seed_log` | HrmSeeder | đảm bảo seed idempotent |

Không phải nghiệp vụ, nhưng **bắt buộc** để khởi tạo và chạy lại seed an toàn.

## 3. Bảng KHÔNG còn được dùng (có thể tồn tại trong DB)

| Bảng | Lý do |
|---|---|
| `hrm_employee` | Entity/repo đã xóa; dữ liệu đã import sang `employee` (`employee_code`) bởi `HrmEmployeeIdMigration` |
| `application_leave` | Entity/repo/mapper đã xóa; dữ liệu đã import sang `hrm_leave_request` bởi `HrmLeaveMigration` |
| `hrm_shift_schedule` | Entity/repo đã xóa; dữ liệu đã gộp sang `shift` bởi `HrmOrganizationMigration` |
| `attendance` | Entity `com.hrm.Entity.Attendance` đã bị **xóa** (không có repository, không ai dùng); bảng vẫn còn trong DB |

→ Có thể DROP 4 bảng này sau khi đã backup và xác nhận migration chạy thành công.
Hibernate `ddl-auto: update` sẽ không tự tạo lại nếu entity đã bị xóa.

Code chết đã xóa kèm: `Entity/Attendance.java`, `dto/request/job/CandidateCreate.java`,
`dto/request/job/CandidateUpdate.java`.

## 4. Ghi chú thêm

- Vẫn có **2 bảng thông báo song song**: `notification` (module cũ) và `hrm_notification` (module HRM).
  Cả hai đều đang được dùng, chưa gom — khác nghiệp vụ với các bảng ở mục 3.
- Mọi repository còn lại đều có ít nhất 1 nơi injected (kiểm tra bằng tìm kiếm toàn `src/main/java`).
- Controller/config (`@RestController`, `@Configuration`, `@ControllerAdvice`, `ApplicationRunner`)
  dù không bị class nào import vẫn **được Spring quét qua annotation** — không phải code chết.
