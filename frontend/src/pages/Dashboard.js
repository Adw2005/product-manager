import React, { useCallback, useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import StatsCards from "../components/StatsCards";
import ProductTable from "../components/ProductTable";
import ProductFormModal from "../components/ProductFormModal";
import ConfirmDeleteModal from "../components/ConfirmDeleteModal";
import { productApi } from "../api/productApi";

export default function Dashboard({ onNavigate, onLogout, user }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [formOpen, setFormOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [deletingProduct, setDeletingProduct] = useState(null);
  const [formErrors, setFormErrors] = useState(null);
  const [toast, setToast] = useState(null);

  const loadProducts = useCallback(async (searchTerm = "") => {
    setLoading(true);
    try {
      const data = await productApi.list(searchTerm);
      setProducts(data);
    } catch (err) {
      showToast("Erro ao carregar produtos. Verifique se o backend está rodando.", "error");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  useEffect(() => {
    const timeout = setTimeout(() => loadProducts(search), 350);
    return () => clearTimeout(timeout);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  const showToast = (message, type = "success") => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000);
  };

  const handleNewProduct = () => {
    setEditingProduct(null);
    setFormErrors(null);
    setFormOpen(true);
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setFormErrors(null);
    setFormOpen(true);
  };

  const handleSubmit = async (formData) => {
    try {
      if (editingProduct) {
        await productApi.update(editingProduct.id, formData);
        showToast("Produto atualizado com sucesso!");
      } else {
        await productApi.create(formData);
        showToast("Produto cadastrado com sucesso!");
      }
      setFormOpen(false);
      loadProducts(search);
    } catch (err) {
      if (err.response?.data?.errors) {
        setFormErrors(err.response.data.errors);
      } else {
        showToast("Erro ao salvar produto.", "error");
      }
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      await productApi.remove(deletingProduct.id);
      showToast("Produto excluído.");
      setDeletingProduct(null);
      loadProducts(search);
    } catch (err) {
      showToast("Erro ao excluir produto.", "error");
    }
  };

  return (
    <div className="flex min-h-screen bg-ink-50">
      <Sidebar active="dashboard" onNavigate={onNavigate} onLogout={onLogout} user={user} />

      <div className="flex-1 flex flex-col">
        <Header search={search} onSearchChange={setSearch} onNewProduct={handleNewProduct} />

        <main className="flex-1 px-6 py-6 space-y-6">
          <StatsCards products={products} />
          <ProductTable
            products={products}
            loading={loading}
            onEdit={handleEdit}
            onDelete={setDeletingProduct}
          />
        </main>
      </div>

      <ProductFormModal
        open={formOpen}
        onClose={() => setFormOpen(false)}
        onSubmit={handleSubmit}
        initialData={editingProduct}
        errors={formErrors}
      />

      <ConfirmDeleteModal
        open={!!deletingProduct}
        product={deletingProduct}
        onCancel={() => setDeletingProduct(null)}
        onConfirm={handleDeleteConfirm}
      />

      {toast && (
        <div
          className={`fixed bottom-6 right-6 px-4 py-3 rounded-lg shadow-lg text-sm font-medium text-white ${
            toast.type === "error" ? "bg-red-600" : "bg-emerald-600"
          }`}
        >
          {toast.message}
        </div>
      )}
    </div>
  );
}
