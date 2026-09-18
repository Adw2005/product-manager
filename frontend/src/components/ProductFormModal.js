import React, { useEffect, useState } from "react";
import { X } from "lucide-react";

const emptyForm = { name: "", description: "", category: "", price: "", stock: "" };

export default function ProductFormModal({ open, onClose, onSubmit, initialData, errors }) {
  const [form, setForm] = useState(emptyForm);

  useEffect(() => {
    if (initialData) {
      setForm({
        name: initialData.name || "",
        description: initialData.description || "",
        category: initialData.category || "",
        price: initialData.price ?? "",
        stock: initialData.stock ?? ""
      });
    } else {
      setForm(emptyForm);
    }
  }, [initialData, open]);

  if (!open) return null;

  const handleChange = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({
      ...form,
      price: parseFloat(form.price),
      stock: parseInt(form.stock, 10)
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink-950/50 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
        <div className="flex items-center justify-between px-6 py-4 border-b border-ink-100">
          <h2 className="font-semibold text-ink-900">
            {initialData ? "Editar produto" : "Novo produto"}
          </h2>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-ink-50 text-ink-400">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="px-6 py-5 space-y-4">
          <div>
            <label className="block text-xs font-medium text-ink-600 mb-1.5">Nome</label>
            <input
              required
              value={form.name}
              onChange={handleChange("name")}
              className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
              placeholder="Ex: Notebook Ultra 15"
            />
            {errors?.name && <p className="text-xs text-red-600 mt-1">{errors.name}</p>}
          </div>

          <div>
            <label className="block text-xs font-medium text-ink-600 mb-1.5">Descrição</label>
            <textarea
              value={form.description}
              onChange={handleChange("description")}
              rows={2}
              className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500 resize-none"
              placeholder="Detalhes do produto (opcional)"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-ink-600 mb-1.5">Categoria</label>
              <input
                required
                value={form.category}
                onChange={handleChange("category")}
                className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
                placeholder="Eletrônicos"
              />
              {errors?.category && <p className="text-xs text-red-600 mt-1">{errors.category}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-ink-600 mb-1.5">Estoque</label>
              <input
                required
                type="number"
                min="0"
                value={form.stock}
                onChange={handleChange("stock")}
                className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
                placeholder="0"
              />
              {errors?.stock && <p className="text-xs text-red-600 mt-1">{errors.stock}</p>}
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-ink-600 mb-1.5">Preço (R$)</label>
            <input
              required
              type="number"
              min="0"
              step="0.01"
              value={form.price}
              onChange={handleChange("price")}
              className="w-full px-3 py-2 text-sm rounded-lg border border-ink-200 focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500"
              placeholder="0.00"
            />
            {errors?.price && <p className="text-xs text-red-600 mt-1">{errors.price}</p>}
          </div>

          <div className="flex items-center gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-lg border border-ink-200 text-ink-600 text-sm font-medium hover:bg-ink-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="flex-1 py-2.5 rounded-lg bg-brand-600 hover:bg-brand-700 text-white text-sm font-medium shadow-card transition-colors"
            >
              {initialData ? "Salvar alterações" : "Cadastrar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
