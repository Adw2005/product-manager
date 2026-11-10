/**
 * ProductManager — Lógica de Interface em JavaScript Puro (Vanilla JS sem Frameworks)
 * Manipulação de DOM nativa, gerenciamento de estado e eventos.
 */

document.addEventListener('DOMContentLoaded', () => {
  // Elementos do DOM - Login
  const loginView = document.getElementById('loginView');
  const appView = document.getElementById('appView');
  const loginForm = document.getElementById('loginForm');
  const loginUsernameInput = document.getElementById('loginUsername');
  const loginPasswordInput = document.getElementById('loginPassword');
  const loginError = document.getElementById('loginError');
  const loginBtnSubmit = document.getElementById('btnLoginSubmit');
  const loginBtnText = document.getElementById('loginBtnText');

  // Elementos do DOM - Sidebar e Header
  const currentUsername = document.getElementById('currentUsername');
  const btnLogout = document.getElementById('btnLogout');
  const navDashboard = document.getElementById('navDashboard');
  const navSettings = document.getElementById('navSettings');
  const sectionDashboard = document.getElementById('sectionDashboard');
  const sectionSettings = document.getElementById('sectionSettings');
  const pageHeaderTitle = document.getElementById('pageHeaderTitle');
  const pageHeaderSubtitle = document.getElementById('pageHeaderSubtitle');
  const headerActions = document.getElementById('headerActions');
  const searchInput = document.getElementById('searchInput');
  const btnNewProduct = document.getElementById('btnNewProduct');

  // Elementos do DOM - Stats Cards
  const statTotalProducts = document.getElementById('statTotalProducts');
  const statTotalCategories = document.getElementById('statTotalCategories');
  const statLowStock = document.getElementById('statLowStock');
  const statTotalValue = document.getElementById('statTotalValue');

  // Elementos do DOM - Tabela
  const productTableBody = document.getElementById('productTableBody');
  const tableEmpty = document.getElementById('tableEmpty');
  const tableLoading = document.getElementById('tableLoading');

  // Elementos do DOM - Modal de Produto
  const productModal = document.getElementById('productModal');
  const modalProductTitle = document.getElementById('modalProductTitle');
  const productForm = document.getElementById('productForm');
  const btnModalClose = document.getElementById('btnModalClose');
  const btnCancelProductModal = document.getElementById('btnCancelProductModal');
  const btnSaveProductModal = document.getElementById('btnSaveProductModal');
  const formProductName = document.getElementById('formProductName');
  const formProductDescription = document.getElementById('formProductDescription');
  const formProductCategory = document.getElementById('formProductCategory');
  const formProductStock = document.getElementById('formProductStock');
  const formProductPrice = document.getElementById('formProductPrice');
  const errName = document.getElementById('errName');
  const errCategory = document.getElementById('errCategory');
  const errStock = document.getElementById('errStock');
  const errPrice = document.getElementById('errPrice');

  // Elementos do DOM - Modal de Exclusão
  const deleteModal = document.getElementById('deleteModal');
  const deleteProductName = document.getElementById('deleteProductName');
  const btnCancelDeleteModal = document.getElementById('btnCancelDeleteModal');
  const btnConfirmDeleteModal = document.getElementById('btnConfirmDeleteModal');

  // Elementos do DOM - Configurações
  const settingsForm = document.getElementById('settingsForm');
  const settingCompanyName = document.getElementById('settingCompanyName');
  const settingCurrency = document.getElementById('settingCurrency');
  const settingNotifyLowStock = document.getElementById('settingNotifyLowStock');
  const settingLowStockThreshold = document.getElementById('settingLowStockThreshold');

  // Elementos do DOM - Toasts
  const toastContainer = document.getElementById('toastContainer');

  // Estado da Aplicação
  let products = [];
  let editingProductId = null;
  let deletingProductId = null;
  let searchDebounceTimeout = null;
  let appSettings = {
    companyName: 'DMAXTECH Systems',
    currency: 'BRL',
    notifyLowStock: true,
    lowStockThreshold: 5,
  };

  // Carrega configurações salvas no localStorage
  const savedSettings = localStorage.getItem('pm_settings');
  if (savedSettings) {
    try {
      appSettings = { ...appSettings, ...JSON.parse(savedSettings) };
    } catch (e) {}
  }

  // ================= TOAST NOTIFICATIONS =================

  function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(12px)';
      setTimeout(() => toast.remove(), 300);
    }, 3000);
  }

  // ================= CONTROLE DE SESSÃO =================

  API.setOnUnauthorized(() => {
    showLogin();
    showToast('Sessão expirada. Por favor faça login novamente.', 'error');
  });

  function showLogin() {
    loginView.style.display = 'flex';
    appView.style.display = 'none';
    loginError.style.display = 'none';
    loginUsernameInput.value = '';
    loginPasswordInput.value = '';
  }

  function showApp(user) {
    loginView.style.display = 'none';
    appView.style.display = 'flex';
    currentUsername.textContent = user?.username || 'admin';
    applySettingsToUI();
    navigateTo('dashboard');
    loadProducts();
  }

  // Inicialização: verifica se o usuário já possui token salvo
  const existingToken = API.getToken();
  const existingUser = API.getUser();
  if (existingToken && existingUser) {
    showApp(existingUser);
  } else {
    showLogin();
  }

  // Submit de Login
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    loginError.style.display = 'none';
    loginBtnSubmit.disabled = true;
    loginBtnText.textContent = 'Entrando...';

    const username = loginUsernameInput.value.trim();
    const password = loginPasswordInput.value;

    try {
      const data = await API.auth.login(username, password);
      API.setToken(data.token);
      API.setUser({ username: data.username, role: data.role });
      showApp({ username: data.username, role: data.role });
      showToast(`Bem-vindo, ${data.username}!`);
    } catch (err) {
      loginError.textContent = err.message || 'Usuário ou senha incorretos.';
      loginError.style.display = 'block';
    } finally {
      loginBtnSubmit.disabled = false;
      loginBtnText.textContent = 'Entrar';
    }
  });

  // Logout
  btnLogout.addEventListener('click', () => {
    API.auth.logout();
    showLogin();
    showToast('Você saiu do sistema.');
  });

  // ================= NAVEGAÇÃO =================

  function navigateTo(page) {
    if (page === 'dashboard') {
      navDashboard.classList.add('active');
      navSettings.classList.remove('active');
      sectionDashboard.style.display = 'block';
      sectionSettings.style.display = 'none';
      headerActions.style.display = 'flex';
      pageHeaderTitle.textContent = 'Gestão de Produtos';
      pageHeaderSubtitle.textContent = 'Visualize, cadastre e atualize o catálogo de produtos';
    } else if (page === 'settings') {
      navSettings.classList.add('active');
      navDashboard.classList.remove('active');
      sectionSettings.style.display = 'block';
      sectionDashboard.style.display = 'none';
      headerActions.style.display = 'none';
      pageHeaderTitle.textContent = 'Configurações';
      pageHeaderSubtitle.textContent = 'Preferências gerais do sistema';
    }
  }

  navDashboard.addEventListener('click', () => navigateTo('dashboard'));
  navSettings.addEventListener('click', () => navigateTo('settings'));

  // ================= CONFIGURAÇÕES =================

  function applySettingsToUI() {
    settingCompanyName.value = appSettings.companyName;
    settingCurrency.value = appSettings.currency;
    settingNotifyLowStock.checked = appSettings.notifyLowStock;
    settingLowStockThreshold.value = appSettings.lowStockThreshold;

    document.querySelectorAll('.sidebar-subtitle, .brand-subtitle').forEach((el) => {
      el.textContent = appSettings.companyName;
    });
  }

  settingsForm.addEventListener('submit', (e) => {
    e.preventDefault();
    appSettings.companyName = settingCompanyName.value.trim() || 'DMAXTECH Systems';
    appSettings.currency = settingCurrency.value;
    appSettings.notifyLowStock = settingNotifyLowStock.checked;
    appSettings.lowStockThreshold = parseInt(settingLowStockThreshold.value, 10) || 5;

    localStorage.setItem('pm_settings', JSON.stringify(appSettings));
    applySettingsToUI();
    renderStats();
    renderTable();
    showToast('Configurações salvas com sucesso!');
  });

  // ================= CARREGAMENTO E RENDERIZAÇÃO DE PRODUTOS =================

  function formatCurrency(value) {
    const currency = appSettings.currency || 'BRL';
    const locale = currency === 'BRL' ? 'pt-BR' : currency === 'USD' ? 'en-US' : 'de-DE';
    return Number(value).toLocaleString(locale, { style: 'currency', currency });
  }

  async function loadProducts(search = '') {
    tableLoading.style.display = 'block';
    tableEmpty.style.display = 'none';
    productTableBody.innerHTML = '';

    try {
      const data = await API.products.list(search);
      products = Array.isArray(data) ? data : [];
      renderStats();
      renderTable();
    } catch (err) {
      showToast('Erro ao carregar produtos. Verifique se o MySQL e o backend estão ativos.', 'error');
    } finally {
      tableLoading.style.display = 'none';
    }
  }

  function renderStats() {
    const total = products.length;
    const categories = new Set(products.map((p) => p.category)).size;
    const lowStockCount = products.filter((p) => Number(p.stock) <= appSettings.lowStockThreshold).length;
    const totalValue = products.reduce((acc, p) => acc + (Number(p.price) * Number(p.stock)), 0);

    statTotalProducts.textContent = total;
    statTotalCategories.textContent = categories;
    statLowStock.textContent = lowStockCount;
    statTotalValue.textContent = formatCurrency(totalValue);
  }

  function renderTable() {
    productTableBody.innerHTML = '';

    if (products.length === 0) {
      tableEmpty.style.display = 'block';
      return;
    }
    tableEmpty.style.display = 'none';

    products.forEach((product) => {
      const tr = document.createElement('tr');

      const isLowStock = Number(product.stock) <= appSettings.lowStockThreshold;
      const stockBadgeClass = isLowStock ? 'badge-warning' : 'badge-success';

      tr.innerHTML = `
        <td>
          <p class="product-name">${escapeHtml(product.name)}</p>
          ${product.description ? `<p class="product-desc">${escapeHtml(product.description)}</p>` : ''}
        </td>
        <td>
          <span class="badge badge-brand">${escapeHtml(product.category)}</span>
        </td>
        <td style="font-weight: 600;">
          ${formatCurrency(product.price)}
        </td>
        <td>
          <span class="badge ${stockBadgeClass}">
            ${product.stock} un.
          </span>
        </td>
        <td style="text-align: right; white-space: nowrap;">
          <button class="btn-icon edit" data-id="${product.id}" title="Editar produto">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
            </svg>
          </button>
          <button class="btn-icon danger" data-id="${product.id}" title="Excluir produto">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="3 6 5 6 21 6"></polyline>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
              <line x1="10" y1="11" x2="10" y2="17"></line>
              <line x1="14" y1="11" x2="14" y2="17"></line>
            </svg>
          </button>
        </td>
      `;

      // Event listeners dos botões de ação
      const btnEdit = tr.querySelector('.btn-icon.edit');
      const btnDelete = tr.querySelector('.btn-icon.danger');

      btnEdit.addEventListener('click', () => openEditProductModal(product));
      btnDelete.addEventListener('click', () => openDeleteProductModal(product));

      productTableBody.appendChild(tr);
    });
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Busca debounced
  searchInput.addEventListener('input', (e) => {
    clearTimeout(searchDebounceTimeout);
    const searchTerm = e.target.value.trim();
    searchDebounceTimeout = setTimeout(() => {
      loadProducts(searchTerm);
    }, 300);
  });

  // ================= MODAL CADASTRAR / EDITAR PRODUTO =================

  function clearFormErrors() {
    errName.textContent = '';
    errCategory.textContent = '';
    errStock.textContent = '';
    errPrice.textContent = '';
  }

  function openNewProductModal() {
    editingProductId = null;
    modalProductTitle.textContent = 'Novo produto';
    btnSaveProductModal.textContent = 'Cadastrar';
    productForm.reset();
    clearFormErrors();
    productModal.style.display = 'flex';
    formProductName.focus();
  }

  function openEditProductModal(product) {
    editingProductId = product.id;
    modalProductTitle.textContent = 'Editar produto';
    btnSaveProductModal.textContent = 'Salvar alterações';
    clearFormErrors();

    formProductName.value = product.name || '';
    formProductDescription.value = product.description || '';
    formProductCategory.value = product.category || '';
    formProductStock.value = product.stock ?? '';
    formProductPrice.value = product.price ?? '';

    productModal.style.display = 'flex';
    formProductName.focus();
  }

  function closeProductModal() {
    productModal.style.display = 'none';
    editingProductId = null;
    clearFormErrors();
  }

  btnNewProduct.addEventListener('click', openNewProductModal);
  btnModalClose.addEventListener('click', closeProductModal);
  btnCancelProductModal.addEventListener('click', closeProductModal);

  // Fecha modal ao clicar fora do card
  productModal.addEventListener('click', (e) => {
    if (e.target === productModal) {
      closeProductModal();
    }
  });

  // Submit do formulário de produto
  productForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearFormErrors();

    const name = formProductName.value.trim();
    const description = formProductDescription.value.trim();
    const category = formProductCategory.value.trim();
    const stock = parseInt(formProductStock.value, 10);
    const price = parseFloat(formProductPrice.value);

    // Validação básica do lado do cliente
    let hasClientError = false;
    if (!name) {
      errName.textContent = 'O nome do produto é obrigatório.';
      hasClientError = true;
    }
    if (!category) {
      errCategory.textContent = 'A categoria é obrigatória.';
      hasClientError = true;
    }
    if (isNaN(stock) || stock < 0) {
      errStock.textContent = 'O estoque não pode ser negativo.';
      hasClientError = true;
    }
    if (isNaN(price) || price < 0) {
      errPrice.textContent = 'O preço não pode ser negativo.';
      hasClientError = true;
    }

    if (hasClientError) return;

    const payload = {
      name,
      description,
      category,
      stock,
      price,
    };

    btnSaveProductModal.disabled = true;

    try {
      if (editingProductId) {
        await API.products.update(editingProductId, payload);
        showToast('Produto atualizado com sucesso!');
      } else {
        await API.products.create(payload);
        showToast('Produto cadastrado com sucesso!');
      }

      closeProductModal();
      loadProducts(searchInput.value.trim());
    } catch (err) {
      if (err.data?.errors) {
        // Mapeia erros de validação retornados pelo Spring Boot
        const errors = err.data.errors;
        if (errors.name) errName.textContent = errors.name;
        if (errors.category) errCategory.textContent = errors.category;
        if (errors.stock) errStock.textContent = errors.stock;
        if (errors.price) errPrice.textContent = errors.price;
      } else {
        showToast(err.message || 'Erro ao salvar produto.', 'error');
      }
    } finally {
      btnSaveProductModal.disabled = false;
    }
  });

  // ================= MODAL DE EXCLUSÃO =================

  function openDeleteProductModal(product) {
    deletingProductId = product.id;
    deleteProductName.textContent = product.name;
    deleteModal.style.display = 'flex';
  }

  function closeDeleteModal() {
    deleteModal.style.display = 'none';
    deletingProductId = null;
  }

  btnCancelDeleteModal.addEventListener('click', closeDeleteModal);
  deleteModal.addEventListener('click', (e) => {
    if (e.target === deleteModal) closeDeleteModal();
  });

  btnConfirmDeleteModal.addEventListener('click', async () => {
    if (!deletingProductId) return;

    btnConfirmDeleteModal.disabled = true;
    try {
      await API.products.remove(deletingProductId);
      showToast('Produto excluído com sucesso.');
      closeDeleteModal();
      loadProducts(searchInput.value.trim());
    } catch (err) {
      showToast(err.message || 'Erro ao excluir produto.', 'error');
    } finally {
      btnConfirmDeleteModal.disabled = false;
    }
  });
});

