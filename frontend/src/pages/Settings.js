import React, { useState } from "react";
import Sidebar from "../components/Sidebar";
import { Save, Building2, Palette, Bell } from "lucide-react";

export default function Settings({ onNavigate, onLogout, user }) {
  const [companyName, setCompanyName] = useState("DMAXTECH Systems");
  const [lowStockThreshold, setLowStockThreshold] = useState(5);
  const [notifyLowStock, setNotifyLowStock] = useState(true);
  const [currency, setCurrency] = useState("BRL");
  const [saved, setSaved] = useState(false);

  const handleSave = (e) => {
    e.preventDefault();
    // Neste projeto de estudo as preferências ficam apenas em memória (estado do React).
    // Numa evolução do sistema, esses dados poderiam ser persistidos via API/Service próprios.
    setSaved(true);
    setTimeout(() => setSaved(false), 2500);
  };

  return (
    <div className="flex min-h-screen bg-ink-50">
      <Sidebar active="settings" onNavigate={onNavigate} onLogout={onLogout} user={user} />

      <div className="flex-1 flex flex-col">
        <header className="sticky top-0 z-10 bg-white border-b border-ink-100">
          <div className="px-6 py-4">
            <h1 className="text-xl font-bold text-ink-900">Configurações</h1>
            <p className="text-sm text-ink-500">Preferências gerais do sistema</p>
          </div>
        </header>

        <main className="flex-1 px-6 py-6">
          <form onSubmit={handleSave} className="max-w-2xl space-y-6">
            <section className="bg-white rounded-xl border border-ink-100 shadow-card p-6">
              <div className="flex items-center gap-2 mb-4">
                <Building2 size={18} className="text-brand-600" />
                <h2 className="font-semibold text-ink-900">Empresa</h2>
              </div>
              <label className="block text-xs font-medium text-ink-600 mb-1.5">Nome exibido no sistema</label>
              <input
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
                className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
              />
            </section>

            <section className="bg-white rounded-xl border border-ink-100 shadow-card p-6">
              <div className="flex items-center gap-2 mb-4">
                <Palette size={18} className="text-brand-600" />
                <h2 className="font-semibold text-ink-900">Exibição</h2>
              </div>
              <label className="block text-xs font-medium text-ink-600 mb-1.5">Moeda</label>
              <select
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
                className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
              >
                <option value="BRL">Real (R$)</option>
                <option value="USD">Dólar (US$)</option>
                <option value="EUR">Euro (€)</option>
              </select>
            </section>

            <section className="bg-white rounded-xl border border-ink-100 shadow-card p-6">
              <div className="flex items-center gap-2 mb-4">
                <Bell size={18} className="text-brand-600" />
                <h2 className="font-semibold text-ink-900">Alertas de estoque</h2>
              </div>

              <div className="flex items-center justify-between mb-4">
                <div>
                  <p className="text-sm font-medium text-ink-800">Notificar estoque baixo</p>
                  <p className="text-xs text-ink-500">Destaca produtos abaixo do limite definido</p>
                </div>
                <button
                  type="button"
                  onClick={() => setNotifyLowStock((v) => !v)}
                  className={`relative w-11 h-6 rounded-full transition-colors ${
                    notifyLowStock ? "bg-brand-600" : "bg-ink-200"
                  }`}
                >
                  <span
                    className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white transition-transform ${
                      notifyLowStock ? "translate-x-5" : ""
                    }`}
                  />
                </button>
              </div>

              <label className="block text-xs font-medium text-ink-600 mb-1.5">
                Limite de estoque baixo (unidades)
              </label>
              <input
                type="number"
                min="0"
                value={lowStockThreshold}
                onChange={(e) => setLowStockThreshold(e.target.value)}
                className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
              />
            </section>

            <div className="flex items-center gap-3">
              <button
                type="submit"
                className="flex items-center gap-2 px-5 py-2.5 rounded-lg bg-brand-600 hover:bg-brand-700 text-white text-sm font-medium shadow-card transition-colors"
              >
                <Save size={16} />
                Salvar alterações
              </button>
              {saved && <span className="text-sm text-emerald-600 font-medium">Preferências salvas!</span>}
            </div>
          </form>
        </main>
      </div>
    </div>
  );
}
