CREATE TABLE IF NOT EXISTS organizations (
  organization_code varchar(40) PRIMARY KEY,
  organization_name varchar(200) NOT NULL,
  organization_type varchar(30) NOT NULL,
  parent_organization_code varchar(40) REFERENCES organizations(organization_code),
  effective_from date NOT NULL DEFAULT current_date,
  effective_to date,
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  CONSTRAINT organizations_dates CHECK (
    effective_to IS NULL OR effective_to >= effective_from
  )
);

COMMENT ON TABLE organizations IS
  '조직 계층과 유효기간을 관리하는 기준 정보.';

CREATE TABLE IF NOT EXISTS organization_relation_history (
  id bigserial PRIMARY KEY,
  organization_code varchar(40) NOT NULL REFERENCES organizations(organization_code),
  parent_organization_code varchar(40) REFERENCES organizations(organization_code),
  relation_type varchar(30) NOT NULL DEFAULT 'PARENT',
  effective_from date NOT NULL DEFAULT current_date,
  effective_to date,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  active boolean NOT NULL DEFAULT true,
  CONSTRAINT organization_relation_history_dates CHECK (
    effective_to IS NULL OR effective_to >= effective_from
  ),
  CONSTRAINT organization_relation_history_not_self CHECK (
    organization_code <> parent_organization_code
  )
);

COMMENT ON TABLE organization_relation_history IS
  '조직의 상위 관계 변경 이력을 보존하여 계층 변경의 추적성과 복원을 지원한다.';

CREATE TABLE IF NOT EXISTS app_users (
  user_id varchar(40) PRIMARY KEY,
  password varchar(200) NOT NULL DEFAULT 'disabled',
  employee_no varchar(40) NOT NULL UNIQUE,
  name varchar(100) NOT NULL,
  organization_code varchar(40) REFERENCES organizations(organization_code),
  position_name varchar(100),
  employment_status varchar(30) NOT NULL DEFAULT 'ACTIVE',
  system_enabled boolean NOT NULL DEFAULT true,
  last_synced_at timestamp,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

COMMENT ON TABLE app_users IS
  '시스템 사용자와 개발용 인증 계정을 관리한다.';

CREATE TABLE IF NOT EXISTS korus_personnel_snapshots (
  id bigserial PRIMARY KEY,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  active boolean NOT NULL DEFAULT true
);

COMMENT ON TABLE korus_personnel_snapshots IS
  '외부 KORUS 연동을 위한 로컬 Mock 스냅샷.';

CREATE TABLE IF NOT EXISTS positions (
  id bigserial PRIMARY KEY,
  user_id varchar(40) NOT NULL REFERENCES app_users(user_id),
  organization_code varchar(40) REFERENCES organizations(organization_code),
  role_code varchar(10),
  position_name varchar(100) NOT NULL,
  primary_position boolean NOT NULL DEFAULT false,
  valid_from date NOT NULL,
  valid_to date,
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  CONSTRAINT positions_dates CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

COMMENT ON TABLE positions IS
  '사용자의 조직별 보직과 유효기간을 관리한다.';

CREATE TABLE IF NOT EXISTS user_positions (
  id bigserial PRIMARY KEY,
  user_id varchar(40) NOT NULL REFERENCES app_users(user_id),
  position_id bigint NOT NULL REFERENCES positions(id),
  primary_position boolean NOT NULL DEFAULT false,
  valid_from date NOT NULL,
  valid_to date
);

COMMENT ON TABLE user_positions IS
  '사용자와 보직의 이력 관계를 관리한다.';

CREATE TABLE IF NOT EXISTS roles (
  role_code varchar(10) PRIMARY KEY,
  role_name varchar(100) NOT NULL,
  purpose varchar(500) NOT NULL,
  assignment_criteria varchar(500),
  default_data_scope varchar(30) NOT NULL DEFAULT 'ORGANIZATION',
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

COMMENT ON TABLE roles IS
  '시스템 역할과 기본 데이터 범위를 관리한다.';

CREATE TABLE IF NOT EXISTS user_roles (
  id bigserial PRIMARY KEY,
  user_id varchar(40) NOT NULL REFERENCES app_users(user_id),
  organization_code varchar(40) REFERENCES organizations(organization_code),
  role_code varchar(10) REFERENCES roles(role_code),
  valid_from date NOT NULL,
  valid_to date,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  CONSTRAINT user_roles_dates CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

COMMENT ON TABLE user_roles IS
  '사용자 역할의 유효기간 이력을 관리한다.';

CREATE TABLE IF NOT EXISTS menus (
  menu_id varchar(40) PRIMARY KEY,
  parent_menu_id varchar(40) REFERENCES menus(menu_id),
  menu_name varchar(100) NOT NULL,
  screen_id varchar(40),
  route_path varchar(200),
  icon varchar(80),
  sort_order integer NOT NULL,
  active boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

COMMENT ON TABLE menus IS
  '25개 공통기능 메뉴 트리와 후속 배치 화면을 관리한다.';

CREATE TABLE IF NOT EXISTS menu_permissions (
  id bigserial PRIMARY KEY,
  role_code varchar(10) NOT NULL REFERENCES roles(role_code),
  subject_type varchar(20),
  subject_id varchar(40),
  menu_id varchar(40) REFERENCES menus(menu_id),
  screen_id varchar(40),
  action_code varchar(30),
  scope_type varchar(30),
  allowed boolean NOT NULL DEFAULT false,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  UNIQUE (role_code, menu_id, action_code)
);

COMMENT ON TABLE menu_permissions IS
  '역할별 메뉴 접근 권한을 관리한다.';

CREATE TABLE IF NOT EXISTS feature_permissions (
  id bigserial PRIMARY KEY,
  role_code varchar(10) NOT NULL REFERENCES roles(role_code),
  subject_type varchar(20),
  subject_id varchar(40),
  menu_id varchar(40) REFERENCES menus(menu_id),
  screen_id varchar(40),
  action_code varchar(30),
  scope_type varchar(30),
  allowed boolean NOT NULL DEFAULT false,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  UNIQUE (role_code, menu_id, action_code)
);

COMMENT ON TABLE feature_permissions IS
  '역할별 기능 행위 권한을 관리한다.';

CREATE TABLE IF NOT EXISTS data_scope_permissions (
  id bigserial PRIMARY KEY,
  role_code varchar(10) NOT NULL REFERENCES roles(role_code),
  subject_type varchar(20),
  subject_id varchar(40),
  menu_id varchar(40) REFERENCES menus(menu_id),
  screen_id varchar(40),
  action_code varchar(30),
  organization_code varchar(40) REFERENCES organizations(organization_code),
  scope_type varchar(30),
  allowed boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  UNIQUE (role_code, organization_code, scope_type)
);

COMMENT ON TABLE data_scope_permissions IS
  '역할별 조직 데이터 범위를 관리한다.';

CREATE TABLE IF NOT EXISTS audit_logs (
  audit_id bigserial PRIMARY KEY,
  actor_user_id varchar(40) NOT NULL,
  action_type varchar(40) NOT NULL,
  target_type varchar(80) NOT NULL,
  target_key varchar(120) NOT NULL,
  before_value jsonb,
  after_value jsonb,
  reason varchar(500),
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);

COMMENT ON TABLE audit_logs IS
  '변경 전후 값과 처리자를 보존하는 append-only 감사 로그.';

CREATE INDEX IF NOT EXISTS idx_users_org
  ON app_users (organization_code);
CREATE INDEX IF NOT EXISTS idx_org_relation_history_org
  ON organization_relation_history (organization_code, effective_from DESC);
CREATE INDEX IF NOT EXISTS idx_org_relation_history_parent
  ON organization_relation_history (parent_organization_code);
CREATE INDEX IF NOT EXISTS idx_positions_org
  ON positions (organization_code);
CREATE INDEX IF NOT EXISTS idx_user_roles_user
  ON user_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_user_positions_user
  ON user_positions (user_id, valid_from DESC);
CREATE INDEX IF NOT EXISTS idx_menu_permissions_role
  ON menu_permissions (role_code, menu_id);
CREATE INDEX IF NOT EXISTS idx_feature_permissions_role
  ON feature_permissions (role_code, menu_id, action_code);
CREATE INDEX IF NOT EXISTS idx_data_scope_permissions_role
  ON data_scope_permissions (role_code, organization_code);
CREATE INDEX IF NOT EXISTS idx_audit_target
  ON audit_logs (target_type, target_key);
