import React from "react";
import { Pencil, Trash2, PackageOpen } from "lucide-react";

export default function ProductTable({ products, loading, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-ink-100 shadow-card p-12 text-center text-ink-400">
        Carregando produtos...
      </div>
    );
  }

  if (products.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-ink-100 shadow-card p-12 text-center">
        <PackageOpen size={36} className="mx-auto text-ink-300 mb-3" />
        <p className="text-ink-500 font-medium">Nenhum produto encontrado</p>
        <p className="text-ink-400 text-sm">Cadastre um novo produto para começar</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-ink-100 shadow-card overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-ink-50 text-left text-xs font-semibold text-ink-500 uppercase tracking-wide">
              <th className="px-6 py-3">Produto</th>
              <th className="px-6 py-3">Categoria</th>
              <th className="px-6 py-3">Preço</th>
              <th className="px-6 py-3">Estoque</th>
              <th className="px-6 py-3 text-right">Ações</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-ink-100">
            {products.map((product) => (
              <tr key={product.id} className="hover:bg-ink-50/60 transition-colors">
                <td className="px-6 py-4">
                  <p className="font-medium text-ink-900">{product.name}</p>
                  {product.description && (
                    <p className="text-ink-400 text-xs line-clamp-1">{product.description}</p>
                  )}
                </td>
                <td className="px-6 py-4">
                  <span className="inline-flex px-2.5 py-1 rounded-full text-xs font-medium bg-brand-50 text-brand-700">
                    {product.category}
                  </span>
                </td>
                <td className="px-6 py-4 font-medium text-ink-900">
                  {Number(product.price).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
                </td>
                <td className="px-6 py-4">
                  <span
                    className={`inline-flex px-2.5 py-1 rounded-full text-xs font-medium ${
                      product.stock <= 5
                        ? "bg-amber-50 text-amber-700"
                        : "bg-emerald-50 text-emerald-700"
                    }`}
                  >
                    {product.stock} un.
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex items-center justify-end gap-2">
                    <button
                      onClick={() => onEdit(product)}
                      className="p-2 rounded-lg text-ink-500 hover:bg-brand-50 hover:text-brand-700 transition-colors"
                      title="Editar"
                    >
                      <Pencil size={16} />
                    </button>
                    <button
                      onClick={() => onDelete(product)}
                      className="p-2 rounded-lg text-ink-500 hover:bg-red-50 hover:text-red-600 transition-colors"
                      title="Excluir"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
