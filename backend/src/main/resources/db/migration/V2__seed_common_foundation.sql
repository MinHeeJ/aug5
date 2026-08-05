INSERT INTO organizations (
  organization_code,
  organization_name,
  organization_type,
  effective_from
)
VALUES
  ('ROOT', '한국교원대학교', 'UNIVERSITY', current_date),
  ('ADMIN', '시스템관리본부', 'DEPARTMENT', current_date)
ON CONFLICT DO NOTHING;

INSERT INTO roles (
  role_code,
  role_name,
  purpose,
  default_data_scope
)
VALUES
  ('R01', '교수', '교수 사용자', 'ORGANIZATION'),
  ('R02', '직원', '직원 사용자', 'ORGANIZATION'),
  ('R03', '학과장', '학과 관리', 'DESCENDANTS'),
  ('R04', '단과대학장', '단과대학 관리', 'DESCENDANTS'),
  ('R05', '평가담당자', '평가 운영', 'ORGANIZATION'),
  ('R06', '인사담당자', '인사 운영', 'ORGANIZATION'),
  ('R07', '감사담당자', '감사 조회', 'ALL'),
  ('R08', '시스템운영자', '운영 관리', 'ALL'),
  ('R09', '시스템관리자', '전체 시스템 관리', 'ALL')
ON CONFLICT DO NOTHING;

INSERT INTO app_users (
  user_id,
  password,
  employee_no,
  name,
  organization_code,
  position_name,
  employment_status,
  system_enabled
)
VALUES (
  'admin',
  'admin',
  'ADMIN-001',
  '시스템 관리자',
  'ADMIN',
  '시스템관리자',
  'ACTIVE',
  true
)
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (
  user_id,
  role_code,
  organization_code,
  valid_from
)
VALUES ('admin', 'R09', 'ADMIN', current_date)
ON CONFLICT DO NOTHING;

INSERT INTO menus (
  menu_id,
  menu_name,
  sort_order
)
VALUES
  ('SYS', '시스템 관리', 0),
  ('FILE', '파일·데이터 관리', 50),
  ('SEC', '보안·감사 관리', 70),
  ('OPS', '시스템 운영 관리', 78)
ON CONFLICT DO NOTHING;

INSERT INTO menus (
  menu_id,
  parent_menu_id,
  menu_name,
  screen_id,
  route_path,
  sort_order
)
VALUES
  ('M001', 'SYS', '사용자 관리', 'SCR-USER', '/system/users', 1),
  ('M002', 'SYS', '조직 관리', 'SCR-ORG', '/system/organizations', 2),
  ('M003', 'SYS', '보직 관리', 'SCR-POSITION', '/system/positions', 3),
  ('M005', 'SYS', '역할 관리', 'SCR-ROLE', '/system/roles', 4),
  ('M006', 'SYS', '사용자 역할', 'SCR-USER-ROLE', '/system/user-roles', 5),
  ('M007', 'SYS', '메뉴 권한', 'SCR-MENU-PERM', '/system/menu-permissions', 6),
  ('M008', 'SYS', '기능 권한', 'SCR-FEATURE-PERM', '/system/feature-permissions', 7),
  ('M009', 'SYS', '데이터 범위', 'SCR-DATA-SCOPE', '/system/data-scopes', 8),
  ('M013', 'SYS', '메뉴 관리', 'SCR-MENU', '/placeholder/menus', 13),
  ('M016', 'SYS', '코드그룹 관리', 'SCR-CODE-GROUP', '/placeholder/code-groups', 16),
  ('M017', 'SYS', '상세코드 관리', 'SCR-CODE', '/placeholder/codes', 17),
  ('M019', 'SYS', '환경설정', 'SCR-CONFIG', '/placeholder/config', 19),
  ('M020', 'SYS', '기준연도', 'SCR-YEAR', '/placeholder/years', 20),
  ('M021', 'SYS', '파일정책', 'SCR-FILE-POLICY', '/placeholder/file-policy', 21),
  ('M023', 'SYS', '공지사항', 'SCR-NOTICE', '/placeholder/notices', 23),
  ('M052', 'FILE', '첨부파일', 'SCR-ATTACH', '/placeholder/attachments', 52),
  ('M055', 'FILE', '업로드 양식', 'SCR-UPLOAD-FORM', '/placeholder/upload-forms', 55),
  ('M056', 'FILE', '엑셀 업로드', 'SCR-UPLOAD', '/placeholder/uploads', 56),
  ('M059', 'FILE', '엑셀 다운로드', 'SCR-DOWNLOAD', '/placeholder/downloads', 59),
  ('M071', 'SEC', '개인정보 관리', 'SCR-PII', '/placeholder/pii', 71),
  ('M074', 'SEC', '접속현황', 'SCR-ACCESS', '/placeholder/access', 74),
  ('M076', 'SEC', '감사 로그', 'SCR-AUDIT', '/placeholder/audit', 76),
  ('M079', 'OPS', '배치 정의', 'SCR-BATCH', '/placeholder/batches', 79),
  ('M080', 'OPS', '배치 실행', 'SCR-BATCH-RUN', '/placeholder/batch-runs', 80),
  ('M081', 'OPS', '배치 결과', 'SCR-BATCH-RESULT', '/placeholder/batch-results', 81)
ON CONFLICT DO NOTHING;

INSERT INTO menu_permissions (
  role_code,
  menu_id,
  action_code,
  allowed
)
SELECT
  'R09',
  menu_id,
  'READ',
  true
FROM menus
ON CONFLICT DO NOTHING;

INSERT INTO feature_permissions (
  role_code,
  menu_id,
  action_code,
  allowed
)
SELECT
  'R09',
  menu_id,
  action_value,
  true
FROM menus
CROSS JOIN (
  VALUES
    ('READ'),
    ('CREATE'),
    ('UPDATE'),
    ('DELETE')
) AS action_source (action_value)
ON CONFLICT DO NOTHING;

INSERT INTO data_scope_permissions (
  role_code,
  organization_code,
  scope_type
)
VALUES ('R09', 'ROOT', 'ALL')
ON CONFLICT DO NOTHING;
