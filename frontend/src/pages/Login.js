import React, { useState } from "react";
import { Boxes, Lock, User as UserIcon, Loader2 } from "lucide-react";
import { authApi } from "../api/authApi";

export default function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const data = await authApi.login(username, password);
      localStorage.setItem("pm_token", data.token);
      localStorage.setItem("pm_user", JSON.stringify({ username: data.username, role: data.role }));
      onLoginSuccess(data);
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Usuário ou senha inválidos.");
      } else {
        setError("Não foi possível conectar ao servidor. Verifique se o backend está rodando.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-ink-950 px-4">
      <div className="w-full max-w-sm">
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-brand-500 flex items-center justify-center mb-4">
            <Boxes size={28} className="text-white" />
          </div>
          <h1 className="text-white font-bold text-xl">ProductManager</h1>
          <p className="text-ink-400 text-sm">DMAXTECH Systems</p>
        </div>

        <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-xl p-6 space-y-4">
          <div>
            <h2 className="font-semibold text-ink-900 mb-1">Entrar</h2>
            <p className="text-sm text-ink-500 mb-4">Acesse com seu usuário e senha</p>
          </div>

          <div>
            <label className="block text-xs font-medium text-ink-600 mb-1.5">Usuário</label>
            <div className="relative">
              <UserIcon size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-ink-400" />
              <input
                required
                autoFocus
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full pl-9 pr-3 py-2.5 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
                placeholder="admin"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-ink-600 mb-1.5">Senha</label>
            <div className="relative">
              <Lock size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-ink-400" />
              <input
                required
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full pl-9 pr-3 py-2.5 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
                placeholder="••••••••"
              />
            </div>
          </div>

          {error && (
            <div className="text-sm text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-lg bg-brand-600 hover:bg-brand-700 disabled:opacity-60 text-white text-sm font-medium shadow-card transition-colors"
          >
            {loading && <Loader2 size={16} className="animate-spin" />}
            {loading ? "Entrando..." : "Entrar"}
          </button>

          <p className="text-xs text-ink-400 text-center pt-1">
            Usuário padrão: <span className="font-medium">admin</span> / senha:{" "}
            <span className="font-medium">admin123</span>
          </p>
        </form>
      </div>
    </div>
  );
}
