import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { api } from "../api";
import { placeholders, resources } from "../config";

function NavItem({
  to,
  label,
  icon,
}: {
  to: string;
  label: string;
  icon?: string;
}) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => `nav-item${isActive ? " active" : ""}`}
    >
      <span className="nav-icon">{icon ?? "·"}</span>
      <span>{label}</span>
    </NavLink>
  );
}

export function AppShell() {
  const navigate = useNavigate();
  const logout = async () => {
    await api<void>("/api/auth/logout", { method: "POST" }).catch(
      () => undefined,
    );
    navigate("/login");
  };
  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">K</span>
          <div>
            <strong>KNUE 평가관리</strong>
            <small>공통기능 관리 콘솔</small>
          </div>
        </div>
        <div className="nav-group-title">시스템 관리</div>
        <nav>
          {resources.map((resource) => (
            <NavItem
              key={resource.entity}
              to={resource.path}
              label={resource.title}
              icon={resource.icon}
            />
          ))}
        </nav>
        <div className="nav-group-title">후속 배치 준비</div>
        <nav>
          {placeholders.map(([label, path]) => (
            <NavItem key={path} to={path} label={label} icon="·" />
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="avatar">A</span>
          <div>
            <strong>admin</strong>
            <small>R09 시스템관리자</small>
          </div>
          <button className="icon-button" title="로그아웃" onClick={logout}>
            ↪
          </button>
        </div>
      </aside>
      <div className="main">
        <header className="topbar">
          <div>
            <span className="topbar-kicker">ADMIN CONSOLE</span>
            <strong>공통기능 관리</strong>
          </div>
          <div className="topbar-actions">
            <span className="role-pill">R09 · 시스템관리자</span>
            <button className="icon-button" title="도움말">
              ?
            </button>
          </div>
        </header>
        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export function AuthRoute() {
  return <AppShell />;
}
