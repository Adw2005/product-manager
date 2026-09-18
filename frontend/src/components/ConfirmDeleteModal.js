import React from "react";
import { AlertTriangle } from "lucide-react";

export default function ConfirmDeleteModal({ open, product, onCancel, onConfirm }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink-950/50 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 text-center">
        <div className="w-12 h-12 rounded-full bg-red-50 flex items-center justify-center mx-auto mb-4">
          <AlertTriangle size={22} className="text-red-600" />
        </div>
        <h2 className="font-semibold text-ink-900 mb-1">Excluir produto</h2>
        <p className="text-sm text-ink-500 mb-6">
          Tem certeza que deseja excluir <span className="font-medium text-ink-700">{product?.name}</span>?
          Essa ação não pode ser desfeita.
        </p>
        <div className="flex items-center gap-3">
          <button
            onClick={onCancel}
            className="flex-1 py-2.5 rounded-lg border border-ink-200 text-ink-600 text-sm font-medium hover:bg-ink-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 py-2.5 rounded-lg bg-red-600 hover:bg-red-700 text-white text-sm font-medium transition-colors"
          >
            Excluir
          </button>
        </div>
      </div>
    </div>
  );
}
