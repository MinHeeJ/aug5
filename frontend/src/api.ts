export type Entity =
  | "users"
  | "organizations"
  | "positions"
  | "roles"
  | "user-roles"
  | "menu-permissions"
  | "feature-permissions"
  | "data-scopes";

export type Row = Record<string, unknown>;
export type ApiFieldErrors = Record<string, string>;
export class ApiError extends Error {
  status: number;
  fieldErrors: ApiFieldErrors;

  constructor(
    message: string,
    status: number,
    fieldErrors: ApiFieldErrors = {},
  ) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}
export type PageResult = {
  items: Row[];
  page: number;
  size: number;
  total: number;
};

export async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    credentials: "include",
    headers: { "Content-Type": "application/json", ...(init?.headers || {}) },
    ...init,
  });
  if (!response.ok) {
    let body: { message?: string; fieldErrors?: ApiFieldErrors } = {};
    try {
      body = await response.json();
    } catch {
      // Some infrastructure errors do not return JSON.
    }
    throw new ApiError(
      response.status === 401
        ? "인증이 필요합니다."
        : response.status === 403
          ? "접근 권한이 없습니다."
          : body.message || "요청 처리에 실패했습니다.",
      response.status,
      body.fieldErrors,
    );
  }
  return response.status === 204 ? (undefined as T) : response.json();
}

export const list = (
  entity: Entity,
  query: string,
  page: number,
  size: number,
) =>
  api<PageResult>(
    `/api/admin/${entity}?page=${page}&size=${size}${query ? `&query=${encodeURIComponent(query)}` : ""}`,
  );

export const save = (entity: Entity, data: Row, id?: string) => {
  const payload = Object.fromEntries(
    Object.entries(data).filter(([key]) => key !== "__id"),
  );
  return api(`/api/admin/${entity}${id ? `/${encodeURIComponent(id)}` : ""}`, {
    method: id ? "PUT" : "POST",
    body: JSON.stringify(payload),
  });
};
