# Chuyển đổi Backend Seed → Nghiệp vụ thực tế (module `/api/hrm`)

## 1. Hiện trạng trước khi chuyển đổi

- `HrmService` lưu **toàn bộ** dữ liệu trong các `List<Map<String,Object>>` in-memory, nạp từ
  `src/main/resources/hrm-seed.json` lúc `@PostConstruct`.
- Dữ liệu bị mất khi restart, không có transaction, không có FK/index, không có audit persistent.
- `HrmController` đã có sẵn API và phân quyền `@PreAuthorize`, nhưng chỉ thao tác trên bộ nhớ.

## 2. Kiến trúc sau chuyển đổi

```
HrmController (/api/hrm/*)   ← giữ NGUYÊN path + shape response (FE không đổi)
        │
        ▼
HrmService  ── validation + workflow + @Transactional + audit
        │
        ├── 30 JPA Entity (com.hrm.Entity.hrm, bảng hrm_*)
        ├── 30 Repository  (com.hrm.repository.hrm)
        ├── HrmAuditService → hrm_audit_log
        └── HrmSeeder → hrm_reference_data / hrm_seed_log + các bảng nghiệp vụ
```

Không tạo backend mới; dùng lại Spring Boot 3.3 / JPA / MySQL đang có. Các Entity/API cũ
(`com.hrm.Entity.user.Employee`, `/api/employees`, ...) **không bị sửa đổi**, nên không ảnh hưởng
chức năng hiện hữu.

## 3. Phân loại Seed

| Nhóm | Dữ liệu | Hành vi |
|---|---|---|
| System/Configuration | trạng thái workflow (`overtimeStatuses`, `payrollStatuses`, `hrRequestStatuses`, `taskStatuses`) | luôn seed, chỉ insert nếu chưa có |
| Reference | `departments`, `locations`, `employmentTypes`, `employeeStatuses`, `attendanceStatuses`, `leaveTypes`, `leaveStatuses`, `recruitmentStages`, `goalPriorities` | luôn seed, chỉ insert nếu chưa có |
| Business Sample | 8 nhân viên, 112 chấm công, 6 đơn nghỉ, 7 ứng viên, KPI, review, ... | **mặc định TẮT**, chỉ import 1 lần theo `sample-key` |

### Cấu hình (`application.yml`)

```yaml
app:
  hrm:
    seed:
      reference-enabled: true        # danh mục bắt buộc
      sample-enabled: false          # bật=true ở dev/demo
      sample-key: HRM-SAMPLE-2026-09  # đổi key nếu muốn import bộ mẫu mới
      sample-only-when-empty: true   # không ghi đè DB đã có dữ liệu người dùng
```

Idempotent được đảm bảo bởi:
- `hrm_reference_data.id = REF#<CATEGORY>#<CODE>` + `existsById` trước khi insert.
- `hrm_seed_log.seed_key` là khóa chính; sample seed ghi log `IMPORTED`/`SKIPPED`.
- Mọi bản ghi sample đều kiểm tra `existsById` trước khi lưu.

## 4. Nghiệp vụ đã chuyển thành chức năng thực tế

| Nhóm | Entity / bảng | Quản lý qua API |
|---|---|---|
| Nhân viên | `HrmEmployee` / `hrm_employee` | CRUD, import/export, check-id, hồ sơ + quan hệ |
| Chấm công | `HrmAttendance` / `hrm_attendance` | danh sách, summary, calendar, check-in/out |
| Nghỉ phép | `HrmLeaveRequest`, `HrmLeaveBalance`, `HrmLeavePolicy` | CRUD đơn, phê duyệt cập nhật số dư, cộng dồn idempotent |
| Tuyển dụng | `HrmCandidate`, `HrmInterview`, `HrmPosition` | CRUD ứng viên, chuyển stage, phỏng vấn, vị trí |
| Hiệu suất | `HrmGoal`, `HrmKpi`, `HrmReview`, `HrmFeedback` | CRUD, cập nhật tiến độ, hoàn thành review |
| Tài liệu | `HrmDocument`, `HrmActivityLog` | thêm/xóa, ghi hoạt động |
| Thông báo | `HrmNotification` | đọc, đánh dấu đã đọc |
| Tăng ca | `HrmOvertimeRequest` | tạo, phê duyệt |
| Lương | `HrmPayrollRun`, `HrmPayslip` | tạo đợt, phê duyệt → **sinh phiếu lương** (idempotent) |
| On/Offboarding | `HrmOnboardingTask`, `HrmOffboardingTask` | tạo, đổi trạng thái |
| Ca kíp | `HrmShiftSchedule`, `HrmRoster` | quản lý ca, phân ca (kiểm tra tháng/ngày) |
| Phúc lợi | `HrmBenefit`, `HrmBenefitEnrollment` | quản lý gói, đăng ký (chống trùng) |
| Yêu cầu HR | `HrmHrRequest` | tạo, đổi trạng thái theo workflow |
| Thông báo/Sự kiện | `HrmAnnouncement`, `HrmCompanyEvent` | CRUD, lọc theo tháng |
| Audit | `HrmAuditLog` | ghi lại mọi thao tác quan trọng, actor từ JWT |

## 5. Business logic / ràng buộc đã bổ sung

- Validation: ngày ISO `YYYY-MM-DD`, `start <= end`, `days > 0`, giờ tăng ca > 0,
  tháng `YYYY-MM`, tên bắt buộc, trạng thái phải thuộc Reference Seed.
- Workflow: `Pending → Approved/Rejected/Cancelled` cập nhật `pending/used` của số dư nghỉ phép;
  duyệt bảng lương sinh phiếu lương; hoàn thành review đặt trạng thái `Completed`.
- Transaction: mọi thao tác liên quan nhiều bảng (tạo nhân viên → chấm công + số dư + activity;
  xóa nhân viên → xóa dữ liệu liên quan; duyệt lương → sinh payslip) nằm trong `@Transactional`.
- Idempotency: chạy accrual 2 lần trong tháng chỉ cộng 1 lần; duyệt lương 2 lần không sinh trùng payslip;
  seed chạy lại không trùng bản ghi.
- Audit log persistent, có index theo ngày/thực thể.

## 6. Database & Migration

- Hibernate `ddl-auto: update` tiếp tục tạo/cập nhật schema (không xóa dữ liệu cũ).
- Các bảng mới đều có `@Id`, FK (`@ManyToOne` cho quan hệ nhân viên/ứng viên),
  unique tự nhiên (`REF#...`, seed key, payslip id) và `@Index` cho các cột lọc thường dùng.
- Script DDL tham khảo/chạy thủ công: `docs/sql/hrm_schema.sql` (dùng `CREATE TABLE IF NOT EXISTS`, an toàn chạy lại).

## 7. Kiểm thử

`./mvnw test` → **15 tests, 0 failures, 0 errors** (H2 profile `test`):

- `HrmApplicationTests`: context Spring tải thành công.
- `HrmSeederIntegrationTest`: seed lần đầu (8 nhân viên/112 chấm công), chạy lại 2 lần không trùng.
- `HrmServiceBusinessTest`: CRUD, cascade delete, workflow nghỉ phép, sinh payslip idempotent,
  validation/exception, audit, check-in/out.
- `HrmApiContractTest`: path + shape response `/api/hrm/*`, CRUD qua HTTP, phân quyền (401 khi chưa login).

## 8. Cách chạy

```bash
# production/dev: chỉ reference seed (không có data mẫu)
./mvnw spring-boot:run

# dev/demo: import thêm bộ mẫu 1 lần
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.hrm.seed.sample-enabled=true
```
