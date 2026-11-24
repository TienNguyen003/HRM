# FE API Integration Prompt – HRM Backend

Tài liệu này dành cho Frontend, hướng dẫn nối API Backend HRM hiện tại (Spring Boot, port `2003`, prefix `api/`).

---

## 1. Thông tin chung

- **Base URL**: `http://localhost:2003/api`
- **Auth**: Bearer token JWT.
  - Login: `POST /auth/token` body `{ username, password }` → `{ code, result: { token, refreshToken, ... } }`
  - Introspect: `POST /auth/introspect` body `{ token }`
  - Refresh: `POST /auth/refresh` body `{ refreshToken }`
  - My info: `GET /users/myInfo` (Bearer)
- **Header**: `Authorization: Bearer <token>`
- **Content-Type**: `application/json` (trừ upload file dùng `multipart/form-data`)
- **Response shape chuẩn**:
  ```json
  { "code": 303, "result": <data>, "page": <pagination?> }
  ```
  - Với list có phân trang, `result` chứa `{ items, page, pageSize, total, totalPages, from, to, allItems, ... }`.
  - Một số endpoint trả `result` là object/array trực tiếp (xem từng endpoint).
- **Ngày/giờ**: ISO `YYYY-MM-DD` / `YYYY-MM-DDTHH:mm:ss`, server date (không hardcode).
- **Soft delete**: dữ liệu quan trọng hỗ trợ xóa mềm khi endpoint DELETE (một số module).
- **Permission**: FE đọc permission set từ user/role; experience theo permission, không theo tên role.

---

## 2. Quyền (Permission) & Role experience

Permission strings chính:

| Nhóm | Permission |
|---|---|
| Self | `SELF_VIEW`, `SELF_UPDATE` |
| Attendance | `ATT_VIEW`, `ATT_CHECKIN` |
| Leave | `LEAVE_VIEW`, `LEAVE_CREATE`, `LEAVE_APPROVE` |
| Employee | `EMP_VIEW`, `EMP_ADD`, `EMP_EDIT`, `EMP_DELETE` |
| Recruitment | `REC_VIEW`, `REC_ADD`, `REC_EDIT`, `REC_DELETE` |
| Performance | `PERF_VIEW`, `PERF_ADD`, `PERF_EDIT` |
| Organization | `ORG_VIEW` |
| Overtime | `OT_VIEW`, `OT_CREATE`, `OT_APPROVE` |
| Payroll | `PAYROLL_VIEW`, `PAYROLL_RUN`, `PAYSLIP_VIEW` |
| Onboarding | `ONBOARDING_VIEW`, `ONBOARDING_MANAGE` |
| Leave policy | `LEAVE_POLICY_VIEW`, `LEAVE_POLICY_MANAGE` |
| Shift | `SHIFT_VIEW`, `SHIFT_MANAGE` |
| Benefit | `BENEFIT_VIEW`, `BENEFIT_MANAGE` |
| HR Request | `HR_REQUEST_VIEW`, `HR_REQUEST_CREATE`, `HR_REQUEST_MANAGE` |
| System | `USER_*`, `PERM_*`, `COMP_VIEW`, `OFF_VIEW` |

Role experience gợi ý:
- **Employee**: `SELF_VIEW`, `SELF_UPDATE`, `ATT_CHECKIN`, `LEAVE_CREATE`
- **Manager**: + `TEAM_VIEW`, `LEAVE_APPROVE`, `PERF_VIEW`, `PERF_ADD`
- **Sub-admin**: + `EMP_*`, `ATT_VIEW`, `LEAVE_VIEW`, `REC_*`, `ORG_VIEW`, ...
- **Admin**: tất cả (role `ADMIN` bypass mọi permission)

---

## 3. HRM API Contract (prefix `/hrm`)

### 3.1 Metadata
- `GET /hrm/filter-options` → `{ departments, locations, employmentTypes, employeeStatuses, attendanceStatuses, leaveTypes, leaveStatuses, recruitmentStages, goalPriorities }`

### 3.2 Employee
- `GET /hrm/employees?search=&department=&status=&position=&employmentType=&location=&sort=&sortDirection=&page=&pageSize=`
  - result: `{ items, page, pageSize, total, totalPages, from, to, allItems, departments, positions, locations, employmentTypes }`
- `GET /hrm/employees/summary` → `{ total, active, onLeave, inactive, departments }`
- `GET /hrm/employees/check-id?id=` → boolean
- `GET /hrm/employees/:id` → employee + `{ attendance, leave, balance, documents, activity, directReports }`
- `POST /hrm/employees` body employee payload → created employee
- `PUT /hrm/employees/:id` body partial → updated employee
- `DELETE /hrm/employees/:id` → deleted employee
- `GET /hrm/employees/export` → `List<Employee>`
- `POST /hrm/employees/import` body `List<Employee>` → `{ imported }`

Self-update cho phép: `phone`, `address`, `emergencyContact`, `avatar`. Không cho: `department`, `position`, `salary`, `status`, `manager`, `role/permissions`.

### 3.3 Organization
- `GET /hrm/organization/overview` → `{ departments, total, locations }`
- `GET /hrm/organization/departments/:name` → department detail
- `GET /hrm/organization/chart` → `{ root, total }`

### 3.4 Attendance
- `GET /hrm/attendance?date=&department=&employeeId=&status=&sort=&sortDirection=&page=&pageSize=`
- `GET /hrm/attendance/summary` → today metrics + breakdown
- `GET /hrm/attendance/calendar?month=&department=` → `{ month, cells, weekdays }`
- `POST /hrm/attendance/check-in` body `{ employeeId }` → record
- `POST /hrm/attendance/check-out` body `{ employeeId }` → record

### 3.5 Leave
- `GET /hrm/leave/requests?search=&status=&type=&employeeId=&sort=&sortDirection=&page=&pageSize=`
- `GET /hrm/leave/summary` → `{ pending, approved, rejected, cancelled, onLeaveToday, byType }`
- `GET /hrm/leave/balances?employeeId=` → `List<LeaveBalance>`
- `GET /hrm/leave/requests/:id` → leave + `{ timeline }`
- `POST /hrm/leave/requests` body `{ employeeId, type, start, end, days, reason }`
- `PUT /hrm/leave/requests/:id/status` body `{ status, approver }`

### 3.6 Recruitment
- `GET /hrm/recruitment/overview` → `{ candidates, positions }`
- `GET /hrm/recruitment/candidates/:id` → candidate + `{ interviews }`
- `PUT /hrm/recruitment/candidates/:id/stage` body `{ stage }`
- `POST /hrm/recruitment/candidates` body candidate
- `PUT /hrm/recruitment/candidates/:id` body partial
- `DELETE /hrm/recruitment/candidates/:id`
- `POST /hrm/recruitment/interviews` body `{ candidateId, date, time, duration, mode, interviewer }`
- `PUT /hrm/recruitment/interviews/:id` body partial
- `PUT /hrm/recruitment/interviews/:id/cancel`
- `POST /hrm/recruitment/positions` body `{ title, department, location, type, openings, description }`
- `PUT /hrm/recruitment/positions/:id` body partial
- `PUT /hrm/recruitment/positions/:id/close`
- `GET /hrm/recruitment/summary` → `{ openPositions, totalCandidates, interviewing, offers, hired, pipeline, interviews }`

### 3.7 Performance
- `GET /hrm/performance/overview` → `{ goals, reviews, feedback, kpis, employees }`
- `POST /hrm/performance/goals` body `{ title, owner, department, due, priority, metric, target }`
- `PUT /hrm/performance/goals/:id/progress` body `{ progress }`
- `GET /hrm/performance/reviews/:id`
- `PUT /hrm/performance/reviews/:id` body partial
- `PUT /hrm/performance/reviews/:id/complete` body `{ score, strengths, improvements, ... }`
- `GET /hrm/performance/kpis`
- `POST /hrm/performance/kpis` body kpi
- `PUT /hrm/performance/kpis/:id` body partial
- `DELETE /hrm/performance/kpis/:id`
- `POST /hrm/performance/feedback` body feedback
- `PUT /hrm/performance/feedback/:id` body partial

### 3.8 Documents
- `POST /hrm/employees/:id/documents` (JSON) body `{ name, type, size, expires }`
- `POST /hrm/employees/:id/documents` (multipart) fields `file`, `name?`, `type?`, `expires?`
- `DELETE /hrm/employees/:employeeId/documents/:documentId`

### 3.9 Notifications
- `GET /hrm/notifications` → `List<Notification>`
- `GET /hrm/notifications/unread-count` → number
- `PUT /hrm/notifications/:id/read` → notification
- `PUT /hrm/notifications/read-all` → `List<Notification>`

### 3.10 Overtime
- `GET /hrm/overtime?employeeId=&department=&status=&page=&pageSize=`
- `GET /hrm/overtime/summary` → `{ total, pending, approved, rejected, totalHours }`
- `POST /hrm/overtime` body `{ employeeId, date, hours, reason, approver }`
- `PUT /hrm/overtime/:id/status` body `{ status }`

### 3.11 Payroll
- `GET /hrm/payroll-runs?month=&department=&status=&page=&pageSize=`
- `POST /hrm/payroll-runs` body `{ month, department, amount }`
- `GET /hrm/payroll-runs/:id`
- `PUT /hrm/payroll-runs/:id/status` body `{ status }`
- `GET /hrm/payslips?employeeId=&month=&page=&pageSize=`
- `GET /hrm/payslips/:id`

### 3.12 Onboarding / Offboarding
- `GET /hrm/onboarding?employeeId=&status=&page=&pageSize=`
- `POST /hrm/onboarding` body `{ employeeId, task, owner, dueDate }`
- `PUT /hrm/onboarding/:id/status` body `{ status }`
- `GET /hrm/offboarding?employeeId=&status=&page=&pageSize=`
- `POST /hrm/offboarding` body `{ employeeId, task, owner, dueDate }`

### 3.13 Leave Policies
- `GET /hrm/leave-policies` → `List`
- `POST /hrm/leave-policies` body policy
- `PUT /hrm/leave-policies/:id` body partial
- `POST /hrm/leave/accrual/run` → `{ processed, date, status }`

### 3.14 Shift Scheduling
- `GET /hrm/shift-schedules` → `List`
- `POST /hrm/shift-schedules` body shift
- `PUT /hrm/shift-schedules/:id` body partial
- `GET /hrm/rosters?month=&department=` → `{ items, month, department }`
- `POST /hrm/rosters/assign` body roster

### 3.15 Benefits
- `GET /hrm/benefits` → `List`
- `POST /hrm/benefits` body benefit
- `PUT /hrm/benefits/:id` body partial
- `GET /hrm/benefit-enrollments?employeeId=` → `{ items }`
- `POST /hrm/benefit-enrollments` body enrollment

### 3.16 HR Requests
- `GET /hrm/hr-requests?status=&employeeId=&page=&pageSize=`
- `GET /hrm/hr-requests/:id`
- `POST /hrm/hr-requests` body `{ employeeId, ... }`
- `PUT /hrm/hr-requests/:id/status` body `{ status }`

### 3.17 Announcements / Events / Birthdays
- `GET /hrm/announcements` → `List`
- `POST /hrm/announcements` body announcement
- `GET /hrm/events?month=` → `List`
- `GET /hrm/birthdays?month=` → `List<{ employeeId, name, dateOfBirth }>`

### 3.18 Audit Logs
- `GET /hrm/audit-logs` → `List`

### 3.19 Dashboard / Reports
- `GET /hrm/dashboard/overview` → `{ metrics, growth, departmentDistribution, attendanceBreakdown, leaveStats, hiringPipeline, recentActivity, upcomingLeave }`
- `GET /hrm/reports/overview` → `{ metrics, growth, departmentDistribution, locationDistribution, employmentTypeDistribution, statusDistribution, attendanceBreakdown, leaveStats, hiringPipeline, goalStatusDistribution, reviewStatusDistribution, kpiStatusDistribution, lateByDepartment }`

---

## 4. FE Adapter notes

- Bật `USE_HRM_API=true`.
- Giữ mock service cho demo/test.
- Empty/loading/error/unauthorized states đã có ở FE; backend trả empty array/object khi không có dữ liệu.
- Phân trang: dùng `result.items`, `result.page`, `result.pageSize`, `result.total`, `result.totalPages`, `result.from`, `result.to`.
- Permission check FE: ẩn/hiện menu theo permission set.
- Date: parse ISO; hiển thị theo locale.
- Upload file: dùng `FormData` với field `file`.

---

## 5. Migration plan

1. Bật `USE_HRM_API=true`.
2. Test từng role: employee, manager, sub-admin, admin.
3. Test CRUD, permission, responsive, empty/error states.
4. Sau khi ổn định, thêm realtime notifications, file upload, audit log, export.

---

## 6. Ghi chú

- Không thay đổi API đang hoạt động.
- Role experience dựa trên permission, không hardcode role name.
- Server date/time, không hardcode `2026-09-25`.
- Soft delete / restore cho dữ liệu quan trọng.
- File upload multipart cho documents.
