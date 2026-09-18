import http from "./http";

export const productApi = {
  list: (search = "") =>
    http.get("/products", { params: search ? { search } : {} }).then((r) => r.data),
  getById: (id) => http.get(`/products/${id}`).then((r) => r.data),
  create: (product) => http.post("/products", product).then((r) => r.data),
  update: (id, product) => http.put(`/products/${id}`, product).then((r) => r.data),
  remove: (id) => http.delete(`/products/${id}`)
};

export default http;
