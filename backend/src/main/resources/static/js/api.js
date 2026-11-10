/**
 * ProductManager — Camada de Comunicação com a API REST (Fetch API nativo puro)
 * Sem dependências de bibliotecas externas como Axios ou jQuery.
 */

const API = (function () {
  // Determina automaticamente a URL base da API
  const getBaseUrl = () => {
    const origin = window.location.origin;
    // Se estiver rodando em porta diferente do backend (ex: porta 3000 ou 5500 do live server)
    if (origin.includes(':3000') || origin.includes(':5500') || origin.includes(':5173') || window.location.protocol === 'file:') {
      return 'http://localhost:8080/api';
    }
    // Se estiver sendo servido pelo próprio Spring Boot (ex: porta 8080)
    return '/api';
  };

  const BASE_URL = getBaseUrl();

  const getToken = () => localStorage.getItem('pm_token');
  const setToken = (token) => localStorage.setItem('pm_token', token);
  const removeToken = () => localStorage.removeItem('pm_token');

  const getUser = () => {
    const userStr = localStorage.getItem('pm_user');
    return userStr ? JSON.parse(userStr) : null;
  };

  const setUser = (user) => localStorage.setItem('pm_user', JSON.stringify(user));
  const removeUser = () => localStorage.removeItem('pm_user');

  let onUnauthorizedCallback = null;
  const setOnUnauthorized = (cb) => {
    onUnauthorizedCallback = cb;
  };

  /**
   * Função genérica para efetuar chamadas HTTP à API REST.
   */
  async function request(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...options.headers,
    };

    const token = getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers,
    };

    let response;
    try {
      response = await fetch(url, config);
    } catch (networkErr) {
      throw {
        status: 0,
        message: 'Não foi possível conectar ao servidor. Verifique se o backend está rodando no MySQL.',
        networkError: true,
      };
    }

    // Se o token estiver expirado ou inválido (401)
    if (response.status === 401) {
      removeToken();
      removeUser();
      if (onUnauthorizedCallback) {
        onUnauthorizedCallback();
      }
      const data = await response.json().catch(() => ({}));
      throw {
        status: 401,
        message: data.message || 'Sessão expirada ou credenciais inválidas.',
        data,
      };
    }

    // Status 204 No Content (DELETE)
    if (response.status === 204) {
      return null;
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      throw {
        status: response.status,
        message: data?.message || `Erro HTTP ${response.status}`,
        data,
      };
    }

    return data;
  }

  // Métodos de Autenticação
  const auth = {
    async login(username, password) {
      return request('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      });
    },
    logout() {
      removeToken();
      removeUser();
      if (onUnauthorizedCallback) {
        onUnauthorizedCallback();
      }
    },
  };

  // Métodos CRUD de Produtos
  const products = {
    async list(search = '') {
      const query = search ? `?search=${encodeURIComponent(search)}` : '';
      return request(`/products${query}`, { method: 'GET' });
    },
    async getById(id) {
      return request(`/products/${id}`, { method: 'GET' });
    },
    async create(product) {
      return request('/products', {
        method: 'POST',
        body: JSON.stringify(product),
      });
    },
    async update(id, product) {
      return request(`/products/${id}`, {
        method: 'PUT',
        body: JSON.stringify(product),
      });
    },
    async remove(id) {
      return request(`/products/${id}`, { method: 'DELETE' });
    },
  };

  return {
    BASE_URL,
    getToken,
    setToken,
    getUser,
    setUser,
    removeToken,
    removeUser,
    setOnUnauthorized,
    auth,
    products,
  };
})();
