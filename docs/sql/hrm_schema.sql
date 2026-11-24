-- ============================================================
-- HRM module (/api/hrm) - schema migration (MySQL 8)
-- Chạy được nhiều lần: CREATE TABLE IF NOT EXISTS / CREATE INDEX không trùng.
-- Hibernate ddl-auto=update vẫn là cơ chế tạo schema chính; file này dùng để
-- audit/triển khai thủ công và đảm bảo index/FK có mặt.
-- ============================================================

CREATE TABLE IF NOT EXISTS hrm_reference_data (
  id varchar(200) NOT NULL,
  category varchar(80) NOT NULL,
  code varchar(120) NOT NULL,
  label varchar(255),
  sort_order int NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_hrm_ref_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_seed_log (
  seed_key varchar(100) NOT NULL,
  description varchar(255),
  executed_at datetime NOT NULL,
  row_count int NOT NULL DEFAULT 0,
  status varchar(40),
  PRIMARY KEY (seed_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_audit_log (
  id varchar(60) NOT NULL,
  action varchar(120) NOT NULL,
  entity varchar(80),
  entity_id varchar(80),
  actor varchar(120),
  date varchar(40) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_hrm_audit_date (date),
  KEY idx_hrm_audit_entity (entity, entity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_employee (
  id varchar(20) NOT NULL,
  first_name varchar(255),
  last_name varchar(255),
  name varchar(255),
  initials varchar(20),
  position varchar(255),
  department varchar(255),
  manager varchar(255),
  employment_type varchar(80),
  status varchar(80),
  joined_date varchar(80),
  joined_date_iso varchar(40),
  email varchar(255),
  phone varchar(80),
  date_of_birth varchar(80),
  address varchar(500),
  emergency_name varchar(255),
  emergency_relation varchar(80),
  emergency_phone varchar(80),
  location varchar(255),
  id_card varchar(80),
  bank_account varchar(120),
  probation_end_date varchar(80),
  avatar varchar(500),
  attendance_rate int NOT NULL DEFAULT 0,
  leave_balance int NOT NULL DEFAULT 0,
  goals int NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_hrm_emp_department (department),
  KEY idx_hrm_emp_status (status),
  KEY idx_hrm_emp_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_attendance (
  id varchar(80) NOT NULL,
  employee_id varchar(20) NOT NULL,
  employee_name varchar(255),
  department varchar(255),
  att_date varchar(40),
  date_label varchar(80),
  weekday varchar(10),
  check_in varchar(20),
  check_out varchar(20),
  hours varchar(40),
  late_minutes int NOT NULL DEFAULT 0,
  status varchar(80),
  PRIMARY KEY (id),
  KEY idx_hrm_att_date (att_date),
  KEY idx_hrm_att_employee (employee_id),
  KEY idx_hrm_att_status (status),
  CONSTRAINT fk_hrm_att_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_leave_request (
  id varchar(40) NOT NULL,
  employee_id varchar(20) NOT NULL,
  employee_name varchar(255),
  type varchar(120),
  start varchar(40),
  end varchar(40),
  days int NOT NULL DEFAULT 0,
  status varchar(80),
  approver varchar(255),
  submitted varchar(40),
  reason varchar(1000),
  PRIMARY KEY (id),
  KEY idx_hrm_leave_status (status),
  KEY idx_hrm_leave_employee (employee_id),
  CONSTRAINT fk_hrm_leave_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_leave_balance (
  id varchar(40) NOT NULL,
  employee_id varchar(20) NOT NULL,
  employee_name varchar(255),
  department varchar(255),
  annual_total int NOT NULL DEFAULT 0, annual_used int NOT NULL DEFAULT 0, annual_pending int NOT NULL DEFAULT 0,
  sick_total int NOT NULL DEFAULT 0, sick_used int NOT NULL DEFAULT 0, sick_pending int NOT NULL DEFAULT 0,
  unpaid_total int NOT NULL DEFAULT 0, unpaid_used int NOT NULL DEFAULT 0, unpaid_pending int NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_hrm_balance_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_document (
  id varchar(40) NOT NULL,
  employee_id varchar(20) NOT NULL,
  name varchar(255), type varchar(120), size varchar(80), updated varchar(80), expires varchar(80),
  url varchar(1000), file_name varchar(255), content_type varchar(255),
  PRIMARY KEY (id),
  KEY idx_hrm_doc_employee (employee_id),
  CONSTRAINT fk_hrm_doc_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_activity_log (
  id varchar(40) NOT NULL,
  employee_id varchar(40),
  type varchar(120), detail varchar(1000), date varchar(80), icon varchar(120), tone varchar(40),
  PRIMARY KEY (id),
  KEY idx_hrm_activity_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_candidate (
  id varchar(40) NOT NULL,
  name varchar(255), initials varchar(20), role varchar(255), stage varchar(80), applied varchar(80),
  location varchar(255), rating double, email varchar(255), phone varchar(80), experience varchar(120),
  source varchar(120), stage_history json, attributes json,
  PRIMARY KEY (id),
  KEY idx_hrm_candidate_stage (stage),
  KEY idx_hrm_candidate_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_interview (
  id varchar(40) NOT NULL,
  candidate_id varchar(40) NOT NULL,
  candidate_name varchar(255), role varchar(255), iv_date varchar(40), time varchar(40),
  duration int NOT NULL DEFAULT 0, mode varchar(120), interviewer varchar(255), status varchar(80),
  PRIMARY KEY (id),
  KEY idx_hrm_interview_candidate (candidate_id),
  KEY idx_hrm_interview_date (iv_date),
  CONSTRAINT fk_hrm_interview_candidate FOREIGN KEY (candidate_id) REFERENCES hrm_candidate (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_position (
  id varchar(40) NOT NULL,
  title varchar(255), department varchar(255), location varchar(255), type varchar(80), status varchar(80),
  candidates int NOT NULL DEFAULT 0, created varchar(40), posted_date varchar(40),
  openings int NOT NULL DEFAULT 0, description varchar(1000),
  PRIMARY KEY (id),
  KEY idx_hrm_position_status (status),
  KEY idx_hrm_position_dept (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_goal (
  id varchar(40) NOT NULL,
  title varchar(255), owner varchar(255), department varchar(255), progress int NOT NULL DEFAULT 0,
  due varchar(80), due_iso varchar(40), status varchar(80), priority varchar(40), metric varchar(120), target varchar(120),
  PRIMARY KEY (id),
  KEY idx_hrm_goal_status (status),
  KEY idx_hrm_goal_owner (owner)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_kpi (
  id varchar(40) NOT NULL,
  name varchar(255), owner varchar(255), department varchar(255), current double, target double,
  unit varchar(40), trend double, status varchar(80), attributes json,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_review (
  id varchar(40) NOT NULL,
  employee varchar(255), employee_id varchar(40), cycle varchar(255), reviewer varchar(255), score double,
  status varchar(80), date varchar(40), strengths varchar(1000), improvements varchar(1000), attributes json,
  PRIMARY KEY (id),
  KEY idx_hrm_review_status (status),
  KEY idx_hrm_review_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_feedback (
  id varchar(40) NOT NULL,
  tone varchar(40), text varchar(1000), author varchar(255), subject varchar(255), created varchar(40), attributes json,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_notification (
  id varchar(40) NOT NULL,
  title varchar(255), detail varchar(1000), date varchar(80),
  unread tinyint NOT NULL DEFAULT 0, icon varchar(255), tone varchar(40), link varchar(255), notif_group varchar(80),
  PRIMARY KEY (id),
  KEY idx_hrm_notif_unread (unread)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_growth (
  id varchar(40) NOT NULL,
  month varchar(40), total int NOT NULL DEFAULT 0, hires int NOT NULL DEFAULT 0, exits int NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_overtime_request (
  id varchar(40) NOT NULL,
  employee_id varchar(20), employee_name varchar(255), department varchar(255), date varchar(40),
  hours double, reason varchar(1000), status varchar(80), approver varchar(255), submitted varchar(40),
  PRIMARY KEY (id),
  KEY idx_hrm_ot_status (status),
  KEY idx_hrm_ot_employee (employee_id),
  CONSTRAINT fk_hrm_ot_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_payroll_run (
  id varchar(40) NOT NULL,
  pay_month varchar(40), department varchar(255), amount double, status varchar(80), created varchar(40),
  PRIMARY KEY (id),
  KEY idx_hrm_payroll_month (pay_month),
  KEY idx_hrm_payroll_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_payslip (
  id varchar(40) NOT NULL,
  employee_id varchar(20) NOT NULL,
  employee_name varchar(255), pay_month varchar(40), basic double, allowances double, deductions double,
  net double, status varchar(80), created varchar(40),
  PRIMARY KEY (id),
  KEY idx_hrm_payslip_employee (employee_id),
  KEY idx_hrm_payslip_month (pay_month),
  CONSTRAINT fk_hrm_payslip_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_onboarding_task (
  id varchar(40) NOT NULL,
  employee_id varchar(20), employee_name varchar(255), task varchar(255), owner varchar(255),
  due_date varchar(40), status varchar(80), created varchar(40),
  PRIMARY KEY (id),
  KEY idx_hrm_onb_employee (employee_id),
  KEY idx_hrm_onb_status (status),
  CONSTRAINT fk_hrm_onb_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_offboarding_task (
  id varchar(40) NOT NULL,
  employee_id varchar(20), employee_name varchar(255), task varchar(255), owner varchar(255),
  due_date varchar(40), status varchar(80), created varchar(40),
  PRIMARY KEY (id),
  KEY idx_hrm_offb_employee (employee_id),
  KEY idx_hrm_offb_status (status),
  CONSTRAINT fk_hrm_offb_employee FOREIGN KEY (employee_id) REFERENCES hrm_employee (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_leave_policy (
  id varchar(40) NOT NULL,
  name varchar(255), type varchar(120), days int, carry_over tinyint, description varchar(1000),
  status varchar(80), attributes json,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_shift_schedule (
  id varchar(40) NOT NULL,
  name varchar(255), start_time varchar(40), end_time varchar(40), break_minutes int, location varchar(255), attributes json,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_roster (
  id varchar(40) NOT NULL,
  employee_id varchar(40), employee_name varchar(255), roster_month varchar(40), date varchar(40),
  shift varchar(120), department varchar(255), attributes json,
  PRIMARY KEY (id),
  KEY idx_hrm_roster_month (roster_month),
  KEY idx_hrm_roster_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_benefit (
  id varchar(40) NOT NULL,
  name varchar(255), type varchar(120), description varchar(1000), status varchar(80), attributes json,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_benefit_enrollment (
  id varchar(40) NOT NULL,
  employee_id varchar(40), employee_name varchar(255), benefit_id varchar(40), benefit_name varchar(255),
  enrolled_date varchar(40), status varchar(80), attributes json,
  PRIMARY KEY (id),
  KEY idx_hrm_be_employee (employee_id),
  KEY idx_hrm_be_benefit (benefit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_hr_request (
  id varchar(40) NOT NULL,
  employee_id varchar(40), employee_name varchar(255), type varchar(120), subject varchar(255),
  description varchar(1000), status varchar(80), created varchar(40), attributes json,
  PRIMARY KEY (id),
  KEY idx_hrm_hrreq_status (status),
  KEY idx_hrm_hrreq_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_announcement (
  id varchar(40) NOT NULL,
  title varchar(255), content varchar(2000), author varchar(255), created varchar(40),
  pinned tinyint NOT NULL DEFAULT 0, attributes json,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hrm_company_event (
  id varchar(40) NOT NULL,
  title varchar(255), event_date varchar(40), location varchar(255), description varchar(1000), attributes json,
  PRIMARY KEY (id),
  KEY idx_hrm_event_date (event_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
