import type { Entity, Row } from "./api";

export type Field = {
  key: string;
  label: string;
  required?: boolean;
  type?: "text" | "date" | "boolean";
  readOnlyOnEdit?: boolean;
};
export type ResourceConfig = {
  entity: Entity;
  path: string;
  title: string;
  description: string;
  icon: string;
  idField: string;
  fields: Field[];
};

export const resources: ResourceConfig[] = [
  {
    entity: "users",
    path: "/system/users",
    title: "사용자 관리",
    description: "사용자 계정, 조직 배정과 시스템 사용 여부를 관리합니다.",
    icon: "US",
    idField: "user_id",
    fields: [
      {
        key: "user_id",
        label: "사용자 ID",
        required: true,
        readOnlyOnEdit: true,
      },
      {
        key: "employee_no",
        label: "사번",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "name", label: "이름", required: true, readOnlyOnEdit: true },
      { key: "organization_code", label: "조직 코드", readOnlyOnEdit: true },
      { key: "position_name", label: "보직명", readOnlyOnEdit: true },
      { key: "employment_status", label: "재직 상태", readOnlyOnEdit: true },
      { key: "system_enabled", label: "시스템 사용", type: "boolean" },
    ],
  },
  {
    entity: "organizations",
    path: "/system/organizations",
    title: "조직 관리",
    description: "조직 계층, 유형과 유효기간을 관리합니다.",
    icon: "OR",
    idField: "organization_code",
    fields: [
      {
        key: "organization_code",
        label: "조직 코드",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "organization_name", label: "조직명", required: true },
      { key: "organization_type", label: "조직 유형", required: true },
      { key: "parent_organization_code", label: "상위 조직" },
      {
        key: "effective_from",
        label: "유효 시작일",
        type: "date",
        required: true,
      },
      { key: "effective_to", label: "유효 종료일", type: "date" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "positions",
    path: "/system/positions",
    title: "보직 관리",
    description: "사용자별 보직과 조직, 유효기간을 관리합니다.",
    icon: "PO",
    idField: "id",
    fields: [
      { key: "user_id", label: "사용자 ID", required: true },
      { key: "position_name", label: "보직명", required: true },
      { key: "organization_code", label: "조직 코드" },
      { key: "role_code", label: "역할 코드" },
      { key: "valid_from", label: "시작일", type: "date", required: true },
      { key: "valid_to", label: "종료일", type: "date" },
      { key: "primary_position", label: "주 보직", type: "boolean" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "roles",
    path: "/system/roles",
    title: "역할 관리",
    description: "역할 코드와 기본 데이터 범위를 관리합니다.",
    icon: "RL",
    idField: "role_code",
    fields: [
      {
        key: "role_code",
        label: "역할 코드",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "role_name", label: "역할명", required: true },
      { key: "purpose", label: "설명", required: true },
      { key: "assignment_criteria", label: "할당 기준" },
      { key: "default_data_scope", label: "기본 범위", required: true },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "user-roles",
    path: "/system/user-roles",
    title: "사용자 역할",
    description: "사용자와 역할의 조직별 유효기간을 관리합니다.",
    icon: "UR",
    idField: "id",
    fields: [
      { key: "user_id", label: "사용자 ID", required: true },
      { key: "organization_code", label: "조직 코드" },
      { key: "role_code", label: "역할 코드", required: true },
      { key: "valid_from", label: "시작일", type: "date", required: true },
      { key: "valid_to", label: "종료일", type: "date" },
    ],
  },
  {
    entity: "menu-permissions",
    path: "/system/menu-permissions",
    title: "메뉴 권한",
    description: "역할별 메뉴 접근과 행위 권한을 관리합니다.",
    icon: "MP",
    idField: "id",
    fields: [
      { key: "role_code", label: "역할 코드", required: true },
      { key: "menu_id", label: "메뉴 ID", required: true },
      { key: "action_code", label: "행위", required: true },
      { key: "allowed", label: "허용", type: "boolean" },
    ],
  },
  {
    entity: "feature-permissions",
    path: "/system/feature-permissions",
    title: "기능 권한",
    description: "역할별 화면 기능의 CRUD 권한을 관리합니다.",
    icon: "FP",
    idField: "id",
    fields: [
      { key: "role_code", label: "역할 코드", required: true },
      { key: "menu_id", label: "메뉴 ID", required: true },
      { key: "action_code", label: "행위", required: true },
      { key: "allowed", label: "허용", type: "boolean" },
    ],
  },
  {
    entity: "data-scopes",
    path: "/system/data-scopes",
    title: "데이터 범위",
    description: "역할별 조직 데이터 접근 범위를 관리합니다.",
    icon: "DS",
    idField: "id",
    fields: [
      { key: "role_code", label: "역할 코드", required: true },
      { key: "organization_code", label: "조직 코드" },
      { key: "scope_type", label: "범위 유형", required: true },
      { key: "allowed", label: "허용", type: "boolean" },
    ],
  },
];

export const placeholders = [
  ["메뉴 관리", "/placeholder/menus"],
  ["코드그룹 관리", "/placeholder/code-groups"],
  ["상세코드 관리", "/placeholder/codes"],
  ["환경설정", "/placeholder/config"],
  ["기준연도", "/placeholder/years"],
  ["파일정책", "/placeholder/file-policy"],
  ["공지사항", "/placeholder/notices"],
  ["첨부파일", "/placeholder/attachments"],
  ["업로드 양식", "/placeholder/upload-forms"],
  ["엑셀 업로드", "/placeholder/uploads"],
  ["엑셀 다운로드", "/placeholder/downloads"],
  ["개인정보 관리", "/placeholder/pii"],
  ["접속현황", "/placeholder/access"],
  ["감사 로그", "/placeholder/audit"],
  ["배치 정의", "/placeholder/batches"],
  ["배치 실행", "/placeholder/batch-runs"],
  ["배치 결과", "/placeholder/batch-results"],
] as const;

export function displayValue(row: Row, field: Field) {
  const value = row[field.key];
  if (field.type === "boolean")
    return value === true || value === "true" ? "허용" : "미허용";
  return value === null || value === undefined || value === ""
    ? "-"
    : String(value);
}
