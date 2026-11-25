# FE Update Prompt — HRM Backend sau khi gom trùng lặp

> Copy nội dung này cho team FE / FE agent. Backend vẫn chạy tại `http://localhost:2003`,
> prefix `api/`, auth Bearer JWT. **Hầu hết endpoint giữ nguyên path và response shape** —
> FE chỉ cần đổi ở các mục được đánh dấu 🔴 / 🟡 bên dưới.

---

## 0. Những điều KHÔNG đổi (không cần sửa FE)

- Đường dẫn API cũ `/api/employee`, `/api/structures`, `/api/offices`, `/api/shift`,
  `/api/day_off_letter`, `/api/holidays`, `/api/day_off_categories`, `/api/cv/*`,
  `/api/require`, `/api/assessment`, `/api/job_posting`, `/api/disqualified`.
- Permission strings: `EMP_*`, `LEAVE_*`, `REQ_*`, `CV_*`, `ORG_VIEW`, `SHIFT_*`, `ALL`, `ROLE_ADMIN`.
  ⚠️ API nghỉ phép cũ dùng `REQ_ADD / REQ_EDIT / REQ_APPROVALS / REQ_VIEW / REQ_DELETE`,
  **không phải** `LEAVE_*` — giữ nguyên check quyền hiện tại.
- Shape response chuẩn `{ code, result, page? }`; phân trang `{ items, page, pageSize, total, totalPages, from, to }`.
- ID nhân viên ở module `/api/hrm` vẫn là `EMP-00123` (không đổi thành số).

---

## 1. 🟢 Nhân viên — chỉ thêm field, không đổi logic

| Endpoint | Thay đổi |
|---|---|
| `GET/POST/PUT/DELETE /api/hrm/employees...` | Không đổi |
| Object employee trả về | **Thêm** `employeeDbId` (số, id thật trong DB). Dùng khi cần gọi API cũ `/api/employee?id=...` |

```json
{
  "id": "EMP-00124",
  "employeeId": "EMP-00124",
  "employeeDbId": 124,
  "name": "Nguyen Van An",
  "department": "Engineering",
  "status": "Active"
}
```

Backend giờ **chỉ có 1 bảng nhân viên** (`employee`). `/api/employee` và `/api/hrm/employees`
cùng trỏ vào bảng này.

---

## 2. 🔴 Nghỉ phép — 2 API giờ dùng CHUNG 1 nguồn dữ liệu

Trước đây có 2 hệ thống nghỉ phép riêng biệt → dashboard cũ và dashboard HRM ra 2 số khác nhau.
Giờ thống nhất về `hrm_leave_request`.

### 2.1 FE có 2 lựa chọn (đều đúng, đừng tạo đơn ở cả 2 cho cùng 1 lần)

| Luồng | Endpoint | Ghi chú |
|---|---|---|
| Cũ (giữ nguyên) | `POST/PUT/DELETE /api/day_off_letter`, `GET /api/day_off_letter/leave?leaveId=...` | `leaveId` là **số** |
| Mới (HRM) | `POST /api/hrm/leave/requests`, `GET /api/hrm/leave/requests/{id}`, `PUT .../{id}/status` | `id` dạng `LV-001` |

**Hai chiều đã thông nhau**: đơn tạo từ API cũ có `id` số để FE cũ dùng; đơn **tạo mới** từ `/api/hrm`
cũng được gán số tương ứng → `GET /api/day_off_letter/leave?leaveId=<số>` vẫn trả về.
⚠️ Đơn **dữ liệu mẫu** import từ `hrm-seed.json` chỉ tồn tại ở API HRM (không có `legacyId`),
nên không tra được bằng `leaveId` số ở API cũ — đây là trường hợp duy nhất.

### 2.2 Mapping field FE cũ ↔ FE HRM (quan trọng)

| `/api/day_off_letter` (`LeaveRespone`) | `/api/hrm/leave/requests` | Quy đổi |
|---|---|---|
| `id` (int) | `id` (`LV-...`) | BE tự map qua `legacyId` |
| `status` `0/1/2/3` | `status` `Pending/Approved/Rejected/Cancelled` | `0→Pending, 1→Approved, 2→Rejected, 3→Cancelled` |
| `startTime` `"YYYY-MM-DD 08:00"` | `start` `"YYYY-MM-DD"` | BE cố định giờ 08:00 |
| `endTime` `"YYYY-MM-DD 17:00"` | `end` `"YYYY-MM-DD"` | BE cố định giờ 17:00 |
| `totalTime` `"16 hours"` | `days` `2` | `days * 8 = hours` |
| `approved` | `approver` | tên người duyệt |
| `creationTime` | `submitted` | ngày tạo |
| `dayOffCategories` | `type` | BE tìm category theo `nameDay === type` |

⚠️ **Lưu ý cho FE/Admin**: tên danh mục nghỉ ở `/api/day_off_categories` (`nameDay`) phải khớp
với `leaveTypes` trong `GET /api/hrm/filter-options` (`Annual Leave`, `Sick Leave`, `Unpaid Leave`,
`Maternity Leave`, `Marriage Leave`). Nếu lệch, `dayOffCategories` trong response cũ sẽ là `null`.

### 2.3 Payload tạo đơn cũ (không đổi)

```json
POST /api/day_off_letter
{
  "dayOff": 1,
  "startTime": "2026-12-01 08:00",
  "endTime": "2026-12-02 17:00",
  "totalTime": "16",
  "employeeId": 124,
  "reason": "Nghỉ việc riêng"
}
```

Payload duyệt cũ (không đổi):
```json
PUT /api/day_off_letter/status?leaveId=12
{ "status": 1, "nameApproval": "Tran Thi Mai", "time": 16, "employeeId": 124 }
```

✅ Sau khi duyệt qua API cũ, số dư ở `GET /api/hrm/leave/balances?employeeId=EMP-00124` **được cập nhật**
(`annual.used`/`annual.pending`). FE không cần gọi thêm API cập nhật số dư.

### 2.4 Dashboard / Report / Reminder

`/api/dashboard`, `/api/reports`, `/api/notifications/reminders` giờ cùng đếm số đơn pending từ
1 bảng. FE **không cần cộng/trừ thủ công** ở client nữa.

---

## 3. 🟡 Tổ chức — `Department` là nguồn gốc, response không đổi

| Endpoint | Thay đổi |
|---|---|
| `GET /api/hrm/organization/overview`, `.../departments/{name}`, `.../chart` | Response giữ nguyên, nhưng dữ liệu lấy từ bảng `department` (chung với `/api/structures`) |
| `POST/PUT/DELETE /api/structures` | Khi **đổi tên** phòng ban, BE tự cập nhật `department` của mọi nhân viên thuộc phòng đó |

Hệ quả FE cần biết:
- Sau khi rename ở `/api/structures`,刷新 lại `GET /api/hrm/organization/*` sẽ thấy tên mới ngay.
- `GET /api/hrm/organization/departments/{name}` trả **404** nếu `name` không tồn tại trong bảng
  `department` (ngay cả khi có nhân viên mang chuỗi department đó từ trước) — FE nên handle 404.

---

## 4. 🟡 Ca làm việc — gộp về bảng `shift`, response vẫn dạng `SH-xxx`

| Endpoint | Thay đổi |
|---|---|
| `GET/POST/PUT /api/hrm/shift-schedules` | Vẫn trả `id: "SH-001"`, `name`, `startTime`, `endTime`, `breakMinutes`, `location` |
| `/api/shift` (cũ, id số) | Cùng bảng `shift` với API HRM |

Mapping: `SH-001` (HRM) là `shift_code`, còn API cũ dùng `id` số tự tăng — **đừng giả định số trong
mã bằng id số**, hãy tra cứu qua API nếu cần đối chiếu. FE không cần đổi payload, nhưng lưu ý:
- Sửa ca qua `/api/shift?id=1` → `/api/hrm/shift-schedules` thấy ngay theo `SH-001`.
- `startTime/endTime` trả về dạng `HH:mm` (VD `08:00`), `breakMinutes` là số.

---

## 5. 🔴 Lọc CV — phải truyền `assessmentId`, bỏ tự dựng tiêu chí ở client

### 5.1 `POST /api/cv/filter` (multipart)

Trước: FE tự gửi `data` = danh sách tiêu chí do client dựng.
Nay: **nên gửi `assessmentId`** để BE đọc tiêu chí từ DB.

```
POST /api/cv/filter?assessmentId=3
Content-Type: multipart/form-data

file: <pdf, có thể nhiều file>
data: (không bắt buộc khi có assessmentId — giữ lại chỉ để tương thích client cũ)
```

- Khi có `assessmentId`: BE lấy `Assessment` + `Requirements` từ DB, **bỏ qua `data`**.
- Khi không có `assessmentId`: vẫn chạy như cũ (FE cũ không vỡ), nhưng đây là đường deprecated.
- BE tự gắn `jobId`/`stage` vào ứng viên (`jobId` lấy từ `Assessment.jobId`).

### 5.2 API tiêu chí mở rộng (request/response thêm field)

```json
POST /api/assessment
{ "title": "Backend 2026", "requirement": 2, "jobId": 7 }

POST /api/require
{
  "title": "Java experience",
  "assessmentId": 3,
  "keyword": "Java",
  "weight": 3,
  "mandatory": true
}
```

Response tương ứng có thêm `jobId`, `keyword`, `weight`, `mandatory`.

### 5.3 API CHẤM ĐIỂM ứng viên (mới)

```http
POST /api/cv/assess/{candidateId}
```
```json
{
  "assessmentId": 3,
  "scores": [
    { "requirementId": 11, "score": 2.5, "comment": "đạt" },
    { "requirementId": 12, "score": 2, "comment": "ok" }
  ]
}
```

Response:
```json
{
  "code": 303,
  "result": {
    "candidateId": 12,
    "assessmentId": 3,
    "scores": [
      { "requirementId": 11, "title": "Java experience", "score": 2.5, "max": 3, "comment": "đạt" },
      { "requirementId": 12, "title": "Spring Boot", "score": 2, "max": 2, "comment": "ok" }
    ],
    "totalScore": 4.5,
    "maxScore": 5.0,
    "percentage": 90
  }
}
```

```http
GET /api/cv/assess/{candidateId}
```
Trả lại các điểm đã lưu (upsert khi chấm lại — không tạo dòng trùng).

Validation FE cần hiểu:
- `score` phải nằm trong `[0, weight]` của từng tiêu chí, nếu không BE trả lỗi `CV_SCORE_INVALID`.
- `requirementId` phải thuộc `assessmentId` đang chọn, nếu không cũng lỗi.
- `assessmentId` không tồn tại → `CV_ASSESSMENT_NOT_FOUND`.
- Quyền: POST dùng `CV_EDIT`, GET dùng `CV_VIEW`.

### 5.4 Sửa bug ảnh hưởng kết quả lọc

BE đã sửa lỗi tiêu chí bắt buộc (`require === "1"`) không bao giờ được tính do dùng post-increment.
Kết quả lọc CV có thể **khác với trước đây** — FE không cần đổi code, chỉ cần biết để giải thích cho user.

---

## 6. Checklist FE cần làm (theo ưu tiên)

1. 🔴 **Luồng lọc CV**: đổi `POST /api/cv/filter` để gửi `assessmentId`, ngừng dựng `data` ở client.
2. 🔴 **Màn hình chấm điểm ứng viên**: gọi `POST/GET /api/cv/assess/{candidateId}`, hiển thị
   `totalScore / maxScore / percentage`, nhập điểm theo từng `requirementId` với max = `weight`.
3. 🟡 **Form tạo/sửa tiêu chí**: thêm các field `keyword`, `weight`, `mandatory` (Assessment thêm `jobId`).
4. 🟡 **Nghỉ phép**: nếu FE đang tự tính số dư/pending ở client → bỏ, dùng response BE.
   Đảm bảo tên `dayOffCategories.nameDay` khớp `leaveTypes`.
5. 🟢 **Employee**: có thể đọc thêm `employeeDbId` nếu cần gọi API cũ; không bắt buộc.
6. 🟢 **Tổ chức / ca làm việc**: không đổi payload; chỉ handle 404 ở department detail và nhớ mapping
   `SH-xxx ↔ id số` khi debug.

---

## 7. Lưu ý khi deploy BE lần đầu

- BE sẽ tự chạy 3 migration idempotent lúc startup:
  - gom nhân viên về bảng `employee` (backfill `employee_code`, convert FK `hrm_*` sang int);
  - import đơn nghỉ cũ `application_leave` → `hrm_leave_request` (**66 bản ghi** trong DB hiện tại);
  - tạo/gắn `Department` cho nhân viên (**8 bản ghi**) và gộp ca làm việc vào bảng `shift`.
- Chạy lại BE nhiều lần **không import trùng**.
- Dữ liệu mẫu vẫn tắt mặc định (`app.hrm.seed.sample-enabled: false`); FE không cần làm gì với cấu hình này.

---

## 8. Gợi ý smoke test sau khi FE chỉnh

```bash
# 1. Đăng nhập lấy token (admin / 123321)
curl -X POST http://localhost:2003/api/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123321"}'

# 2. Nhân viên: vẫn có EMP-00124
curl -H 'Authorization: Bearer <token>' \
  'http://localhost:2003/api/hrm/employees?page=1&pageSize=5'

# 3. Tiêu chí từ DB
curl -H 'Authorization: Bearer <token>' 'http://localhost:2003/api/require/assess?assessId=3'

# 4. Lọc CV có assessmentId
curl -X POST -H 'Authorization: Bearer <token>' \
  -F 'file=@cv.pdf' -F 'assessmentId=3' \
  'http://localhost:2003/api/cv/filter?assessmentId=3'

# 5. Chấm điểm
curl -X POST -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"assessmentId":3,"scores":[{"requirementId":11,"score":2.5}]}' \
  'http://localhost:2003/api/cv/assess/12'

# 6. Nghỉ phép 2 luồng cùng số liệu
curl -H 'Authorization: Bearer <token>' 'http://localhost:2003/api/hrm/leave/summary'
curl -H 'Authorization: Bearer <token>' 'http://localhost:2003/api/day_off_letter/leave?leaveId=1'
```
