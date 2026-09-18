import React from "react";
import { Package, Layers, AlertTriangle, DollarSign } from "lucide-react";

function StatCard({ icon: Icon, label, value, accent }) {
  return (
    <div className="bg-white rounded-xl border border-ink-100 shadow-card p-5 flex items-center gap-4">
      <div className={`w-11 h-11 rounded-lg flex items-center justify-center ${accent}`}>
        <Icon size={20} className="text-white" />
      </div>
      <div>
        <p className="text-xs font-medium text-ink-500">{label}</p>
        <p className="text-xl font-bold text-ink-900">{value}</p>
      </div>
    </div>
  );
}

export default function StatsCards({ products }) {
  const total = products.length;
  const categories = new Set(products.map((p) => p.category)).size;
  const lowStock = products.filter((p) => p.stock <= 5).length;
  const totalValue = products.reduce((sum, p) => sum + Number(p.price) * Number(p.stock), 0);

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <StatCard icon={Package} label="Total de produtos" value={total} accent="bg-brand-600" />
      <StatCard icon={Layers} label="Categorias" value={categories} accent="bg-ink-700" />
      <StatCard icon={AlertTriangle} label="Estoque baixo" value={lowStock} accent="bg-amber-500" />
      <StatCard
        icon={DollarSign}
        label="Valor em estoque"
        value={totalValue.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
        accent="bg-emerald-600"
      />
    </div>
  );
}
