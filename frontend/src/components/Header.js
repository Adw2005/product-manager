import React from "react";
import { Search, Plus, Bell } from "lucide-react";

export default function Header({ search, onSearchChange, onNewProduct }) {
  return (
    <header className="sticky top-0 z-10 bg-white border-b border-ink-100">
      <div className="flex items-center justify-between gap-4 px-6 py-4">
        <div>
          <h1 className="text-xl font-bold text-ink-900">Gestão de Produtos</h1>
          <p className="text-sm text-ink-500">Visualize, cadastre e atualize o catálogo de produtos</p>
        </div>

        <div className="flex items-center gap-3">
          <div className="relative hidden sm:block">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-ink-400" />
            <input
              value={search}
              onChange={(e) => onSearchChange(e.target.value)}
              placeholder="Buscar produto..."
              className="pl-9 pr-3 py-2 text-sm rounded-lg border border-ink-200 bg-ink-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500 w-56 transition"
            />
          </div>

          <button className="relative p-2 rounded-lg hover:bg-ink-50 text-ink-500 transition-colors">
            <Bell size={18} />
            <span className="absolute top-1.5 right-1.5 w-1.5 h-1.5 rounded-full bg-brand-500" />
          </button>

          <button
            onClick={onNewProduct}
            className="flex items-center gap-2 px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-700 text-white text-sm font-medium shadow-card transition-colors"
          >
            <Plus size={16} />
            Novo Produto
          </button>
        </div>
      </div>
    </header>
  );
}
