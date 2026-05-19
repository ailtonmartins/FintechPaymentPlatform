import { CreditCard, Gauge, Home, Landmark, LogOut, Repeat2, User } from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../auth/AuthProvider";

const navItems = [
  { to: "/dashboard", label: "Dashboard", icon: Home },
  { to: "/me", label: "Usuario", icon: User },
  { to: "/accounts", label: "Contas", icon: Landmark },
  { to: "/transactions", label: "Transferencias", icon: Repeat2 },
  { to: "/operations", label: "Operacional", icon: Gauge }
];

export function AppLayout() {
  const { logout } = useAuth();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <CreditCard size={24} />
          <div>
            <strong>Fintech</strong>
            <span>Payment Platform</span>
          </div>
        </div>

        <nav>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink key={item.to} to={item.to}>
                <Icon size={18} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        <button className="button ghost sidebar-action" type="button" onClick={logout}>
          <LogOut size={18} />
          <span>Sair</span>
        </button>
      </aside>

      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
