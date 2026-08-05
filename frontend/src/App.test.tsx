import { describe, expect, it, vi } from "vitest";
import { list, save } from "./api";
describe("relative API client", () => {
  it("builds relative admin list paths", () => {
    const url = new URL("/api/admin/users?page=1&size=20", "http://localhost");
    expect(url.pathname).toBe("/api/admin/users");
    expect(url.protocol).toBe("http:");
  });
  it("surfaces server field errors without changing the relative API path", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify({
            message: "입력값을 확인해 주세요.",
            fieldErrors: { role_code: "역할 코드는 필수입니다." },
          }),
          { status: 400, headers: { "Content-Type": "application/json" } },
        ),
      ),
    );

    const selectedRoleId = "role-from-selected-row";
    await expect(
      save("roles", { __id: selectedRoleId, role_code: "" }, selectedRoleId),
    ).rejects.toEqual(
      expect.objectContaining({
        status: 400,
        fieldErrors: { role_code: "역할 코드는 필수입니다." },
      }),
    );
    expect(fetch).toHaveBeenCalledWith(
      `/api/admin/roles/${encodeURIComponent(selectedRoleId)}`,
      expect.objectContaining({ method: "PUT" }),
    );
  });
});
