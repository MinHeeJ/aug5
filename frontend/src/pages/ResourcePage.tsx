import { FormEvent, useEffect, useMemo, useState } from "react";
import { ApiError, list, Row, save } from "../api";
import { displayValue, Field, ResourceConfig } from "../config";

function StateBanner({
  kind,
  children,
}: {
  kind: "error" | "success";
  children: React.ReactNode;
}) {
  return (
    <div className={`state-banner ${kind}`} role="status">
      {kind === "success" ? "✓" : "!"}
      <span>{children}</span>
    </div>
  );
}
function SkeletonRows({ columns }: { columns: number }) {
  return (
    <div className="skeleton-list">
      {[1, 2, 3, 4, 5].map((row) => (
        <div className="skeleton-row" key={row}>
          {Array.from({ length: columns }).map((_, index) => (
            <span key={index} />
          ))}
        </div>
      ))}
    </div>
  );
}
function FieldControl({
  field,
  value,
  disabled,
  required,
  invalid,
  onChange,
}: {
  field: Field;
  value: unknown;
  disabled: boolean;
  required: boolean;
  invalid: boolean;
  onChange: (value: unknown) => void;
}) {
  if (field.type === "boolean")
    return (
      <label className="switch-field">
        <input
          type="checkbox"
          checked={value === true || value === "true"}
          disabled={disabled}
          onChange={(event) => onChange(event.target.checked)}
        />
        <span className="switch" />
        <span>{value === true || value === "true" ? "사용" : "미사용"}</span>
      </label>
    );
  return (
    <input
      className="input"
      type={field.type === "date" ? "date" : "text"}
      value={String(value ?? "")}
      disabled={disabled}
      required={required}
      aria-invalid={invalid}
      onChange={(event) => onChange(event.target.value)}
    />
  );
}
function EditDialog({
  config,
  row,
  onClose,
  onSaved,
}: {
  config: ResourceConfig;
  row: Row;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [draft, setDraft] = useState<Row>(row);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const editing = Boolean(row.__id);
  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    setFieldErrors({});
    const validationErrors: Record<string, string> = {};
    for (const field of config.fields) {
      if (field.required && !String(draft[field.key] ?? "").trim()) {
        validationErrors[field.key] = `${field.label}은(는) 필수입니다.`;
      }
    }
    const from = draft.effective_from ?? draft.valid_from;
    const to = draft.effective_to ?? draft.valid_to;
    if (from && to && String(to) < String(from)) {
      const endKey =
        draft.effective_to !== undefined ? "effective_to" : "valid_to";
      validationErrors[endKey] = "종료일은 시작일보다 빠를 수 없습니다.";
    }
    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors);
      setSaving(false);
      return;
    }
    try {
      await save(config.entity, draft, editing ? String(row.__id) : undefined);
      onSaved();
    } catch (err) {
      setError((err as Error).message);
      setFieldErrors(err instanceof ApiError ? err.fieldErrors : {});
    } finally {
      setSaving(false);
    }
  };
  return (
    <div className="dialog-backdrop" role="presentation">
      <form
        className="dialog"
        onSubmit={submit}
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
      >
        <div className="dialog-header">
          <div>
            <span className="eyebrow">
              {editing ? "EDIT RECORD" : "NEW RECORD"}
            </span>
            <h2 id="dialog-title">
              {editing ? `${config.title} 수정` : `${config.title} 등록`}
            </h2>
          </div>
          <button
            type="button"
            className="icon-button"
            onClick={onClose}
            aria-label="닫기"
          >
            ×
          </button>
        </div>
        {error && <StateBanner kind="error">{error}</StateBanner>}
        <div className="form-grid">
          {config.fields.map((field) => (
            <label
              className={`field ${field.key === "purpose" || field.key === "assignment_criteria" ? "field-wide" : ""}`}
              key={field.key}
            >
              <span>
                {field.label}
                {field.required && <b> *</b>}
              </span>
              <FieldControl
                field={field}
                value={draft[field.key]}
                disabled={Boolean(editing && field.readOnlyOnEdit)}
                required={Boolean(field.required)}
                invalid={Boolean(fieldErrors[field.key])}
                onChange={(value) => setDraft({ ...draft, [field.key]: value })}
              />
              {fieldErrors[field.key] && (
                <small className="field-error" role="alert">
                  {fieldErrors[field.key]}
                </small>
              )}
              {editing && field.readOnlyOnEdit && (
                <small className="helper">
                  기준 데이터는 수정할 수 없습니다.
                </small>
              )}
            </label>
          ))}
        </div>
        <div className="dialog-footer">
          <button type="button" className="button secondary" onClick={onClose}>
            취소
          </button>
          <button className="button" disabled={saving}>
            {saving ? "저장 중…" : "저장"}
          </button>
        </div>
      </form>
    </div>
  );
}

export function ResourcePage({ config }: { config: ResourceConfig }) {
  const [rows, setRows] = useState<Row[]>([]);
  const [query, setQuery] = useState("");
  const [submittedQuery, setSubmittedQuery] = useState("");
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [dialogRow, setDialogRow] = useState<Row | null>(null);
  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const result = await list(config.entity, submittedQuery, page, size);
      setRows(result.items);
      setTotal(result.total);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    void load();
  }, [config.entity, submittedQuery, page, size]);
  const totalPages = Math.max(1, Math.ceil(total / size));
  const visibleFields = useMemo(
    () => config.fields.slice(0, 6),
    [config.fields],
  );
  const search = (event: FormEvent) => {
    event.preventDefault();
    setPage(1);
    setSubmittedQuery(query.trim());
  };
  const saved = () => {
    setDialogRow(null);
    setSuccess("변경사항을 저장했습니다.");
    void load();
    window.setTimeout(() => setSuccess(""), 3500);
  };
  const permissionDenied = error === "접근 권한이 없습니다.";
  return (
    <div className="page-stack">
      <section className="page-hero">
        <div className="hero-decoration" />
        <div>
          <span className="eyebrow">SYSTEM MANAGEMENT / BATCH 1</span>
          <h1>{config.title}</h1>
          <p>{config.description}</p>
        </div>
        <button className="button hero-action" onClick={() => setDialogRow({})}>
          ＋ 신규 등록
        </button>
      </section>
      <section className="insight-grid">
        <div className="insight-card">
          <span className="insight-icon">{config.icon}</span>
          <div>
            <span>전체 레코드</span>
            <strong>{loading ? "—" : total.toLocaleString()}</strong>
          </div>
        </div>
        <div className="insight-card">
          <span className="insight-icon soft">⌁</span>
          <div>
            <span>현재 페이지</span>
            <strong>
              {page} / {totalPages}
            </strong>
          </div>
        </div>
        <div className="insight-card">
          <span className="insight-icon warm">↻</span>
          <div>
            <span>동기화 상태</span>
            <strong className="status-text">API 연결됨</strong>
          </div>
        </div>
      </section>
      <section className="table-card">
        <div className="toolbar">
          <form className="search-form" onSubmit={search}>
            <span className="search-icon">⌕</span>
            <input
              className="search-input"
              placeholder="코드, 이름으로 검색"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
            />
            <button className="button secondary" type="submit">
              검색
            </button>
          </form>
          <div className="toolbar-right">
            <span className="muted">페이지당</span>
            <select
              className="select compact"
              value={size}
              onChange={(event) => {
                setPage(1);
                setSize(Number(event.target.value));
              }}
            >
              <option value={20}>20개</option>
              <option value={50}>50개</option>
              <option value={100}>100개</option>
            </select>
            <button className="button outline" onClick={() => void load()}>
              ↻ 새로고침
            </button>
          </div>
        </div>
        {success && <StateBanner kind="success">{success}</StateBanner>}
        {error && (
          <div className="error-block">
            <StateBanner kind="error">{error}</StateBanner>
            {!permissionDenied && (
              <button className="button secondary" onClick={() => void load()}>
                다시 시도
              </button>
            )}
          </div>
        )}
        {loading ? (
          <SkeletonRows columns={visibleFields.length + 1} />
        ) : rows.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">{config.icon}</div>
            <h3>
              {submittedQuery
                ? "검색 결과가 없습니다"
                : "등록된 데이터가 없습니다"}
            </h3>
            <p>
              {submittedQuery
                ? "검색어를 바꾸거나 필터를 초기화해 보세요."
                : "첫 번째 레코드를 등록하면 이곳에 표시됩니다."}
            </p>
            {submittedQuery ? (
              <button
                className="button secondary"
                onClick={() => {
                  setQuery("");
                  setSubmittedQuery("");
                }}
              >
                검색 초기화
              </button>
            ) : (
              <button className="button" onClick={() => setDialogRow({})}>
                ＋ 신규 등록
              </button>
            )}
          </div>
        ) : (
          <>
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    {visibleFields.map((field) => (
                      <th key={field.key}>{field.label}</th>
                    ))}
                    <th className="action-column">관리</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row, index) => (
                    <tr key={String(row[config.idField] ?? index)}>
                      {visibleFields.map((field) => (
                        <td key={field.key}>
                          {field.type === "boolean" ? (
                            <span
                              className={`status-badge ${displayValue(row, field) === "허용" ? "positive" : "neutral"}`}
                            >
                              {displayValue(row, field)}
                            </span>
                          ) : (
                            <span
                              className={
                                field.key === config.idField ? "mono" : ""
                              }
                            >
                              {displayValue(row, field)}
                            </span>
                          )}
                        </td>
                      ))}
                      <td className="action-column">
                        <button
                          className="button small secondary"
                          onClick={() =>
                            setDialogRow({ ...row, __id: row[config.idField] })
                          }
                        >
                          상세 · 수정
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="pagination">
              <span className="muted">
                총 {total.toLocaleString()}건 · {page}페이지
              </span>
              <div>
                <button
                  className="page-button"
                  disabled={page <= 1}
                  onClick={() => setPage(page - 1)}
                >
                  ‹
                </button>
                <button
                  className="page-button"
                  disabled={page >= totalPages}
                  onClick={() => setPage(page + 1)}
                >
                  ›
                </button>
              </div>
            </div>
          </>
        )}
      </section>
      {dialogRow && (
        <EditDialog
          config={config}
          row={dialogRow}
          onClose={() => setDialogRow(null)}
          onSaved={saved}
        />
      )}
    </div>
  );
}
