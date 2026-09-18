import axios from "axios";

const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

const http = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" }
});

// Anexa o token JWT (se existir) em toda requisição
http.interceptors.request.use((config) => {
  const token = localStorage.getItem("pm_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Se o token expirar/for invalido, o backend responde 401 -> força novo login
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("pm_token");
      localStorage.removeItem("pm_user");
      window.location.reload();
    }
    return Promise.reject(error);
  }
);

export default http;
