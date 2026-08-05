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

  {
    entity: "menus",
    path: "/system/menus",
    title: "메뉴 관리",
    description: "메뉴 구조, 화면 ID, 경로와 정렬 순서를 관리합니다.",
    icon: "MN",
    idField: "menu_id",
    fields: [
      {
        key: "menu_id",
        label: "메뉴 ID",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "parent_menu_id", label: "상위 메뉴" },
      { key: "menu_name", label: "메뉴명", required: true },
      { key: "screen_id", label: "화면 ID" },
      { key: "route_path", label: "경로" },
      { key: "sort_order", label: "정렬", required: true },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "code-groups",
    path: "/system/code-groups",
    title: "코드그룹 관리",
    description: "공통 코드그룹과 설명을 관리합니다.",
    icon: "CG",
    idField: "group_code",
    fields: [
      {
        key: "group_code",
        label: "그룹 코드",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "group_name", label: "그룹명", required: true },
      { key: "description", label: "설명" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "codes",
    path: "/system/codes",
    title: "상세코드 관리",
    description: "코드그룹별 상세코드와 표시 순서를 관리합니다.",
    icon: "CD",
    idField: "id",
    fields: [
      { key: "group_code", label: "그룹 코드", required: true },
      { key: "code", label: "코드", required: true },
      { key: "code_name", label: "코드명", required: true },
      { key: "sort_order", label: "정렬" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "config",
    path: "/system/config",
    title: "공통 환경설정",
    description: "시스템 설정 키와 값을 관리합니다.",
    icon: "CF",
    idField: "config_key",
    fields: [
      {
        key: "config_key",
        label: "설정 키",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "config_value", label: "설정 값", required: true },
      { key: "config_type", label: "유형" },
      { key: "description", label: "설명" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "years",
    path: "/system/years",
    title: "기준연도 관리",
    description: "평가 기준연도와 운영 상태를 관리합니다.",
    icon: "YR",
    idField: "base_year",
    fields: [
      {
        key: "base_year",
        label: "기준연도",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "status", label: "상태", required: true },
      { key: "starts_on", label: "시작일", type: "date" },
      { key: "ends_on", label: "종료일", type: "date" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "file-policies",
    path: "/system/file-policies",
    title: "파일정책 관리",
    description: "첨부파일 크기, 확장자, 보관 정책을 관리합니다.",
    icon: "FP",
    idField: "policy_code",
    fields: [
      {
        key: "policy_code",
        label: "정책 코드",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "policy_name", label: "정책명", required: true },
      { key: "max_size_mb", label: "최대 MB" },
      { key: "allowed_extensions", label: "허용 확장자" },
      { key: "retention_days", label: "보관일" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "notices",
    path: "/system/notices",
    title: "공지사항 관리",
    description: "관리자 공지와 게시 상태를 관리합니다.",
    icon: "NT",
    idField: "notice_id",
    fields: [
      { key: "notice_title", label: "제목", required: true },
      { key: "notice_body", label: "내용", required: true },
      { key: "pinned", label: "상단 고정", type: "boolean" },
      { key: "published", label: "게시", type: "boolean" },
    ],
  },
  {
    entity: "attachments",
    path: "/file/attachments",
    title: "첨부파일 관리",
    description: "첨부파일 메타데이터와 바이러스 검사 상태를 관리합니다.",
    icon: "AT",
    idField: "file_id",
    fields: [
      { key: "file_name", label: "파일명", required: true },
      { key: "storage_path", label: "저장 경로", required: true },
      { key: "content_type", label: "콘텐츠 유형" },
      { key: "file_size", label: "크기" },
      { key: "virus_scan_status", label: "검사 상태" },
    ],
  },
  {
    entity: "upload-forms",
    path: "/file/upload-forms",
    title: "업로드 양식 관리",
    description: "엑셀 업로드 양식과 대상 테이블을 관리합니다.",
    icon: "UF",
    idField: "form_code",
    fields: [
      {
        key: "form_code",
        label: "양식 코드",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "form_name", label: "양식명", required: true },
      { key: "template_path", label: "템플릿 경로" },
      { key: "target_table", label: "대상 테이블" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "uploads",
    path: "/file/uploads",
    title: "엑셀 업로드",
    description: "엑셀 업로드 작업과 처리 건수를 관리합니다.",
    icon: "UP",
    idField: "upload_id",
    fields: [
      { key: "upload_name", label: "업로드명", required: true },
      { key: "form_code", label: "양식 코드" },
      { key: "file_id", label: "파일 ID" },
      { key: "upload_status", label: "상태", required: true },
      { key: "total_rows", label: "전체 행" },
      { key: "success_rows", label: "성공 행" },
      { key: "error_rows", label: "오류 행" },
    ],
  },
  {
    entity: "downloads",
    path: "/file/downloads",
    title: "엑셀 다운로드",
    description: "엑셀 다운로드 요청과 생성 파일을 관리합니다.",
    icon: "DL",
    idField: "download_id",
    fields: [
      { key: "download_name", label: "다운로드명", required: true },
      { key: "target_menu_id", label: "대상 메뉴" },
      { key: "download_status", label: "상태", required: true },
      { key: "file_name", label: "파일명" },
    ],
  },
  {
    entity: "pii",
    path: "/security/pii",
    title: "개인정보 관리",
    description: "개인정보 마스킹/처리 요청과 상태를 관리합니다.",
    icon: "PI",
    idField: "request_id",
    fields: [
      { key: "subject_user_id", label: "대상 사용자", required: true },
      { key: "request_type", label: "요청 유형", required: true },
      { key: "process_status", label: "처리 상태", required: true },
      { key: "masked_field", label: "마스킹 필드" },
      { key: "reason", label: "사유" },
    ],
  },
  {
    entity: "access",
    path: "/security/access",
    title: "접속현황 관리",
    description: "사용자 접속 이력과 성공 여부를 조회합니다.",
    icon: "AC",
    idField: "access_id",
    fields: [
      { key: "user_id", label: "사용자 ID", required: true },
      { key: "access_type", label: "접속 유형", required: true },
      { key: "ip_address", label: "IP" },
      { key: "user_agent", label: "User Agent" },
      { key: "success", label: "성공", type: "boolean" },
    ],
  },
  {
    entity: "audit",
    path: "/security/audit",
    title: "감사 로그 관리",
    description: "변경 전후 값과 처리자 감사 로그를 조회/기록합니다.",
    icon: "AU",
    idField: "audit_id",
    fields: [
      { key: "actor_user_id", label: "처리자", required: true },
      { key: "action_type", label: "행위", required: true },
      { key: "target_type", label: "대상 유형", required: true },
      { key: "target_key", label: "대상 키", required: true },
      { key: "reason", label: "사유" },
    ],
  },
  {
    entity: "batches",
    path: "/ops/batches",
    title: "배치 정의 관리",
    description: "배치 코드, 실행 주기와 사용 여부를 관리합니다.",
    icon: "BD",
    idField: "batch_code",
    fields: [
      {
        key: "batch_code",
        label: "배치 코드",
        required: true,
        readOnlyOnEdit: true,
      },
      { key: "batch_name", label: "배치명", required: true },
      { key: "cron_expression", label: "Cron" },
      { key: "active", label: "활성", type: "boolean" },
    ],
  },
  {
    entity: "batch-runs",
    path: "/ops/batch-runs",
    title: "배치 실행 관리",
    description: "배치 실행 상태와 시작/종료 시각을 관리합니다.",
    icon: "BR",
    idField: "run_id",
    fields: [
      { key: "batch_code", label: "배치 코드", required: true },
      { key: "run_status", label: "상태", required: true },
    ],
  },
  {
    entity: "batch-results",
    path: "/ops/batch-results",
    title: "배치 결과 조회",
    description: "배치 실행 결과와 메시지를 조회합니다.",
    icon: "RS",
    idField: "result_id",
    fields: [
      { key: "run_id", label: "실행 ID", required: true },
      { key: "result_status", label: "결과", required: true },
      { key: "message", label: "메시지" },
    ],
  },
];

export const placeholders: ReadonlyArray<readonly [string, string]> = [];

export function displayValue(row: Row, field: Field) {
  const value = row[field.key];
  if (field.type === "boolean")
    return value === true || value === "true" ? "허용" : "미허용";
  return value === null || value === undefined || value === ""
    ? "-"
    : String(value);
}
