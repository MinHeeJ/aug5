ALTER TABLE IF EXISTS menu_permissions
  ADD COLUMN IF NOT EXISTS subject_type varchar(20);
ALTER TABLE IF EXISTS menu_permissions
  ADD COLUMN IF NOT EXISTS subject_id varchar(40);
ALTER TABLE IF EXISTS menu_permissions
  ADD COLUMN IF NOT EXISTS screen_id varchar(40);
ALTER TABLE IF EXISTS menu_permissions
  ADD COLUMN IF NOT EXISTS scope_type varchar(30);
ALTER TABLE IF EXISTS menu_permissions
  ALTER COLUMN action_code DROP NOT NULL;

ALTER TABLE IF EXISTS feature_permissions
  ADD COLUMN IF NOT EXISTS subject_type varchar(20);
ALTER TABLE IF EXISTS feature_permissions
  ADD COLUMN IF NOT EXISTS subject_id varchar(40);
ALTER TABLE IF EXISTS feature_permissions
  ADD COLUMN IF NOT EXISTS screen_id varchar(40);
ALTER TABLE IF EXISTS feature_permissions
  ADD COLUMN IF NOT EXISTS scope_type varchar(30);
ALTER TABLE IF EXISTS feature_permissions
  ALTER COLUMN action_code DROP NOT NULL;

ALTER TABLE IF EXISTS data_scope_permissions
  ADD COLUMN IF NOT EXISTS subject_type varchar(20);
ALTER TABLE IF EXISTS data_scope_permissions
  ADD COLUMN IF NOT EXISTS subject_id varchar(40);
ALTER TABLE IF EXISTS data_scope_permissions
  ADD COLUMN IF NOT EXISTS menu_id varchar(40);
ALTER TABLE IF EXISTS data_scope_permissions
  ADD COLUMN IF NOT EXISTS screen_id varchar(40);
ALTER TABLE IF EXISTS data_scope_permissions
  ADD COLUMN IF NOT EXISTS action_code varchar(30);
ALTER TABLE IF EXISTS data_scope_permissions
  ALTER COLUMN scope_type DROP NOT NULL;

CREATE TABLE IF NOT EXISTS organization_relation_history (
  id bigserial PRIMARY KEY,
  organization_code varchar(40) NOT NULL
    REFERENCES organizations(organization_code),
  parent_organization_code varchar(40)
    REFERENCES organizations(organization_code),
  relation_type varchar(30) NOT NULL DEFAULT 'PARENT',
  effective_from date NOT NULL DEFAULT current_date,
  effective_to date,
  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp,
  active boolean NOT NULL DEFAULT true,
  CONSTRAINT organization_relation_history_dates
    CHECK (effective_to IS NULL OR effective_to >= effective_from),
  CONSTRAINT organization_relation_history_not_self
    CHECK (organization_code <> parent_organization_code)
);

COMMENT ON TABLE organization_relation_history IS
  '조직의 상위 관계 변경 이력을 보존하여 계층 변경의 추적성과 복원을 지원한다.';
CREATE INDEX IF NOT EXISTS idx_org_relation_history_org
  ON organization_relation_history(organization_code, effective_from DESC);
CREATE INDEX IF NOT EXISTS idx_org_relation_history_parent
  ON organization_relation_history(parent_organization_code);
