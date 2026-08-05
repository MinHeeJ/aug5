import { FormEvent, useEffect, useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { api } from "./api";
import { placeholders, resources } from "./config";
import { AppShell } from "./components/AppShell";
import { ResourcePage } from "./pages/ResourcePage";

export function LoginPage({ onLogin }: { onLogin: () => void }) {
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ userId, password }),
      });
      onLogin();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };
  return (
    <div className="login-page">
      <div className="login-brand">
        <span className="brand-mark">K</span>
        <span>KNUE 평가관리</span>
      </div>
      <div className="login-grid">
        <section className="login-intro">
          <span className="eyebrow">CIVIC BLUE · ADMIN PLATFORM</span>
          <h1>
            평가 운영을 위한
            <br />
            <em>신뢰할 수 있는 기준</em>
          </h1>
          <p>
            교수업적평가 공통기능을 한 곳에서 관리하고, 모든 변경 이력을
            안전하게 보존합니다.
          </p>
          <div className="login-trust">
            <span>✓ 권한 기반 접근 제어</span>
            <span>✓ 감사 로그 자동 기록</span>
          </div>
        </section>
        <form className="login-card" onSubmit={submit}>
          <span className="eyebrow">WELCOME BACK</span>
          <h2>관리자 로그인</h2>
          <p className="muted">관리자 계정으로 콘솔에 접속하세요.</p>
          {error && (
            <div className="state-banner error" role="alert">
              !<span>{error}</span>
            </div>
          )}
          <label className="field">
            <span>아이디</span>
            <input
              className="input"
              autoComplete="username"
              value={userId}
              onChange={(event) => setUserId(event.target.value)}
              required
            />
          </label>
          <label className="field">
            <span>비밀번호</span>
            <input
              className="input"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </label>
          <button className="button login-submit" disabled={loading}>
            {loading ? "확인 중…" : "로그인 →"}
          </button>
          <small className="login-note">
            접근 권한이 없는 계정은 시스템 관리 메뉴를 이용할 수 없습니다.
          </small>
        </form>
      </div>
    </div>
  );
}

function PlaceholderPage({ label }: { label: string }) {
  return (
    <div className="placeholder-card">
      <div className="empty-icon">···</div>
      <span className="eyebrow">PREPARING FOR NEXT BATCH</span>
      <h1>{label}</h1>
      <p>
        이 메뉴는 다음 배치에서 업무 API와 함께 제공됩니다. 현재 Batch 1 범위의
        데이터는 시스템 관리 메뉴에서 관리할 수 있습니다.
      </p>
    </div>
  );
}

export function AppRouter() {
  const [logged, setLogged] = useState<boolean | null>(null);
  const navigate = useNavigate();
  useEffect(() => {
    api("/api/auth/me")
      .then(() => setLogged(true))
      .catch(() => setLogged(false));
  }, []);
  if (logged === null)
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <span>관리 콘솔을 준비하고 있습니다…</span>
      </div>
    );
  return (
    <Routes>
      <Route
        path="/login"
        element={
          logged ? (
            <Navigate to="/system/users" replace />
          ) : (
            <LoginPage
              onLogin={() => {
                setLogged(true);
                navigate("/system/users");
              }}
            />
          )
        }
      />
      {logged ? (
        <Route element={<AppShell />}>
          <Route index element={<Navigate to="/system/users" replace />} />
          {resources.map((resource) => (
            <Route
              key={resource.entity}
              path={resource.path}
              element={<ResourcePage config={resource} />}
            />
          ))}
          {placeholders.map(([label, path]) => (
            <Route
              key={path}
              path={path}
              element={<PlaceholderPage label={label} />}
            />
          ))}
          <Route path="*" element={<Navigate to="/system/users" replace />} />
        </Route>
      ) : (
        <Route path="*" element={<Navigate to="/login" replace />} />
      )}
    </Routes>
  );
}
