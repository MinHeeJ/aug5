ALTER TABLE IF EXISTS menus
  ADD COLUMN IF NOT EXISTS description varchar(500);
ALTER TABLE IF EXISTS menus
  ADD COLUMN IF NOT EXISTS display_order integer;

CREATE TABLE IF NOT EXISTS code_groups (
  group_code varchar(40) PRIMARY KEY,
  group_name varchar(120) NOT NULL,
  description varchar(500),
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS detail_codes (
  id bigserial PRIMARY KEY,
  group_code varchar(40) NOT NULL REFERENCES code_groups(group_code),
  code varchar(40) NOT NULL,
  code_name varchar(120) NOT NULL,
  sort_order integer NOT NULL DEFAULT 0,
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  UNIQUE (group_code, code)
);

CREATE TABLE IF NOT EXISTS system_configs (
  config_key varchar(80) PRIMARY KEY,
  config_value varchar(1000) NOT NULL,
  config_type varchar(40) NOT NULL DEFAULT 'STRING',
  description varchar(500),
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS base_years (
  base_year varchar(4) PRIMARY KEY,
  status varchar(30) NOT NULL DEFAULT 'OPEN',
  starts_on date,
  ends_on date,
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS file_policies (
  policy_code varchar(40) PRIMARY KEY,
  policy_name varchar(120) NOT NULL,
  max_size_mb integer NOT NULL DEFAULT 20,
  allowed_extensions varchar(500) NOT NULL DEFAULT 'pdf,xlsx,docx,png,jpg',
  retention_days integer NOT NULL DEFAULT 365,
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS notices (
  notice_id bigserial PRIMARY KEY,
  notice_title varchar(200) NOT NULL,
  notice_body text NOT NULL,
  pinned boolean NOT NULL DEFAULT false,
  published boolean NOT NULL DEFAULT true,
  created_by varchar(40) NOT NULL DEFAULT 'admin',
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS attachments (
  file_id bigserial PRIMARY KEY,
  file_name varchar(255) NOT NULL,
  storage_path varchar(500) NOT NULL,
  content_type varchar(120),
  file_size bigint NOT NULL DEFAULT 0,
  virus_scan_status varchar(40) NOT NULL DEFAULT 'PENDING',
  created_by varchar(40) NOT NULL DEFAULT 'admin',
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS upload_forms (
  form_code varchar(40) PRIMARY KEY,
  form_name varchar(120) NOT NULL,
  template_path varchar(500),
  target_table varchar(80),
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS excel_uploads (
  upload_id bigserial PRIMARY KEY,
  upload_name varchar(160) NOT NULL,
  form_code varchar(40) REFERENCES upload_forms(form_code),
  file_id bigint REFERENCES attachments(file_id),
  upload_status varchar(40) NOT NULL DEFAULT 'READY',
  total_rows integer NOT NULL DEFAULT 0,
  success_rows integer NOT NULL DEFAULT 0,
  error_rows integer NOT NULL DEFAULT 0,
  created_by varchar(40) NOT NULL DEFAULT 'admin',
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS excel_downloads (
  download_id bigserial PRIMARY KEY,
  download_name varchar(160) NOT NULL,
  target_menu_id varchar(40),
  download_status varchar(40) NOT NULL DEFAULT 'READY',
  file_name varchar(255),
  created_by varchar(40) NOT NULL DEFAULT 'admin',
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS privacy_requests (
  request_id bigserial PRIMARY KEY,
  subject_user_id varchar(40) NOT NULL,
  request_type varchar(40) NOT NULL,
  process_status varchar(40) NOT NULL DEFAULT 'REQUESTED',
  masked_field varchar(120),
  reason varchar(500),
  created_by varchar(40) NOT NULL DEFAULT 'admin',
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS access_events (
  access_id bigserial PRIMARY KEY,
  user_id varchar(40) NOT NULL,
  access_type varchar(40) NOT NULL,
  ip_address varchar(80),
  user_agent varchar(500),
  success boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS batch_definitions (
  batch_code varchar(40) PRIMARY KEY,
  batch_name varchar(160) NOT NULL,
  cron_expression varchar(80),
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS batch_runs (
  run_id bigserial PRIMARY KEY,
  batch_code varchar(40) NOT NULL REFERENCES batch_definitions(batch_code),
  run_status varchar(40) NOT NULL DEFAULT 'READY',
  started_at timestamp,
  finished_at timestamp,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS batch_results (
  result_id bigserial PRIMARY KEY,
  run_id bigint NOT NULL REFERENCES batch_runs(run_id),
  result_status varchar(40) NOT NULL DEFAULT 'SUCCESS',
  message varchar(1000),
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

INSERT INTO code_groups (group_code, group_name, description)
VALUES
  ('COMMON_STATUS', '공통 상태', '시스템 공통 상태 코드'),
  ('FILE_STATUS', '파일 상태', '파일/엑셀 처리 상태')
ON CONFLICT DO NOTHING;

INSERT INTO detail_codes (group_code, code, code_name, sort_order)
VALUES
  ('COMMON_STATUS', 'ACTIVE', '활성', 1),
  ('COMMON_STATUS', 'INACTIVE', '비활성', 2),
  ('FILE_STATUS', 'READY', '준비', 1),
  ('FILE_STATUS', 'DONE', '완료', 2)
ON CONFLICT DO NOTHING;

INSERT INTO system_configs (config_key, config_value, config_type, description)
VALUES
  ('SYSTEM_NAME', '한국교원대학교 교수업적평가시스템', 'STRING', '시스템 표시명'),
  ('DEFAULT_BASE_YEAR', '2026', 'STRING', '기본 기준연도')
ON CONFLICT DO NOTHING;

INSERT INTO base_years (base_year, status, starts_on, ends_on)
VALUES ('2026', 'OPEN', '2026-01-01', '2026-12-31')
ON CONFLICT DO NOTHING;

INSERT INTO file_policies (policy_code, policy_name, max_size_mb, allowed_extensions)
VALUES ('DEFAULT', '기본 첨부파일 정책', 20, 'pdf,xlsx,docx,png,jpg')
ON CONFLICT DO NOTHING;

INSERT INTO notices (notice_title, notice_body, pinned, published)
VALUES ('공통기능 관리 콘솔 오픈', '시스템 관리 공통기능을 사용할 수 있습니다.', true, true)
ON CONFLICT DO NOTHING;

INSERT INTO attachments (file_name, storage_path, content_type, file_size, virus_scan_status)
VALUES ('sample.xlsx', '/data/uploads/sample.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 0, 'PASSED')
ON CONFLICT DO NOTHING;

INSERT INTO upload_forms (form_code, form_name, template_path, target_table)
VALUES ('USER_IMPORT', '사용자 업로드 양식', '/templates/user-import.xlsx', 'app_users')
ON CONFLICT DO NOTHING;

INSERT INTO excel_uploads (upload_name, form_code, upload_status, total_rows, success_rows, error_rows)
VALUES ('사용자 샘플 업로드', 'USER_IMPORT', 'READY', 0, 0, 0)
ON CONFLICT DO NOTHING;

INSERT INTO excel_downloads (download_name, target_menu_id, download_status, file_name)
VALUES ('사용자 목록 다운로드', 'M001', 'READY', 'users.xlsx')
ON CONFLICT DO NOTHING;

INSERT INTO privacy_requests (subject_user_id, request_type, process_status, masked_field, reason)
VALUES ('admin', 'MASK', 'REQUESTED', 'employee_no', '개인정보 보호 기본 점검')
ON CONFLICT DO NOTHING;

INSERT INTO access_events (user_id, access_type, ip_address, user_agent, success)
VALUES ('admin', 'LOGIN', '127.0.0.1', 'seed', true)
ON CONFLICT DO NOTHING;

INSERT INTO batch_definitions (batch_code, batch_name, cron_expression)
VALUES ('EVAL_SYNC', '평가 기준정보 동기화', '0 2 * * *')
ON CONFLICT DO NOTHING;

INSERT INTO batch_runs (batch_code, run_status)
VALUES ('EVAL_SYNC', 'READY')
ON CONFLICT DO NOTHING;

INSERT INTO batch_results (run_id, result_status, message)
SELECT run_id, 'SUCCESS', '초기 배치 결과 샘플'
FROM batch_runs
WHERE batch_code = 'EVAL_SYNC'
ON CONFLICT DO NOTHING;
