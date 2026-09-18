import React from "react";
import { Boxes, LayoutDashboard, Settings as SettingsIcon, LogOut } from "lucide-react";

export default function Sidebar({ active = "dashboard", onNavigate = () => {}, onLogout = () => {}, user }) {
  const linkClass = (key) =>
    `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
      active === key
        ? "bg-white/5 text-white"
        : "text-ink-400 hover:bg-white/5 hover:text-white"
    }`;

  return (
    <aside className="hidden md:flex md:flex-col w-64 bg-ink-950 text-ink-200 min-h-screen">
      <div className="flex items-center gap-2 px-6 py-6 border-b border-white/5">
        <div className="w-9 h-9 rounded-lg bg-brand-500 flex items-center justify-center">
          <Boxes size={20} className="text-white" />
        </div>
        <div>
          <p className="text-white font-semibold leading-tight">ProductManager</p>
          <p className="text-xs text-ink-400">DMAXTECH Systems</p>
        </div>
      </div>

      <nav className="flex-1 px-3 py-6 space-y-1">
        <button onClick={() => onNavigate("dashboard")} className={linkClass("dashboard") + " w-full text-left"}>
          <LayoutDashboard size={18} />
          Dashboard
        </button>
        <button onClick={() => onNavigate("settings")} className={linkClass("settings") + " w-full text-left"}>
          <SettingsIcon size={18} />
          Configurações
        </button>
      </nav>

      <div className="px-3 py-4 border-t border-white/5">
        {user && (
          <div className="px-3 pb-3 mb-1">
            <p className="text-xs text-ink-500">Logado como</p>
            <p className="text-sm text-white font-medium truncate">{user.username}</p>
          </div>
        )}
        <button
          onClick={onLogout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-ink-400 hover:bg-white/5 hover:text-white text-sm font-medium transition-colors w-full text-left"
        >
          <LogOut size={18} />
          Sair
        </button>
      </div>
    </aside>
  );
}
