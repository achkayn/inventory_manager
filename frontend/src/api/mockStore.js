import { LOW_STOCK_THRESHOLD } from '../constants';

const deepClone = (value) => JSON.parse(JSON.stringify(value));

const initialState = {
  users: [
    {
      id: 'user-1',
      name: 'Admin User',
      email: 'admin@demo.com',
      password: 'Admin123!',
    },
  ],
  categories: [
    { id: 'cat-1', name: 'Beverages', description: 'Drinks and liquids' },
    { id: 'cat-2', name: 'Snacks', description: 'Packaged snack items' },
    { id: 'cat-3', name: 'Fresh Produce', description: 'Fresh stock items' },
    { id: 'cat-4', name: 'Stationery', description: 'Office supplies' },
  ],
  suppliers: [
    {
      id: 'sup-1',
      name: 'Fresh Farm Supplies',
      email: 'orders@freshfarm.example',
      phone: '+212 600 000 001',
      company: 'Fresh Farm Supplies',
      address: 'Casablanca, Morocco',
    },
    {
      id: 'sup-2',
      name: 'Metro Wholesale',
      email: 'sales@metrowholesale.example',
      phone: '+212 600 000 002',
      company: 'Metro Wholesale',
      address: 'Rabat, Morocco',
    },
  ],
  products: [
    {
      id: 'prod-1',
      name: 'Mineral Water',
      categoryId: 'cat-1',
      categoryName: 'Beverages',
      price: 1.25,
      stockQty: 120,
      threshold: 25,
      sku: 'BEV-001',
      supplierId: 'sup-2',
      description: '500ml bottle',
    },
    {
      id: 'prod-2',
      name: 'Orange Juice',
      categoryId: 'cat-1',
      categoryName: 'Beverages',
      price: 2.5,
      stockQty: 18,
      threshold: 25,
      sku: 'BEV-002',
      supplierId: 'sup-1',
      description: 'Fresh orange juice',
    },
    {
      id: 'prod-3',
      name: 'Potato Chips',
      categoryId: 'cat-2',
      categoryName: 'Snacks',
      price: 1.75,
      stockQty: 42,
      threshold: 25,
      sku: 'SNK-001',
      supplierId: 'sup-2',
      description: 'Salted chips',
    },
    {
      id: 'prod-4',
      name: 'Notebook A5',
      categoryId: 'cat-4',
      categoryName: 'Stationery',
      price: 0.99,
      stockQty: 8,
      threshold: 25,
      sku: 'STA-004',
      supplierId: 'sup-2',
      description: '80-sheet notebook',
    },
  ],
  orders: [
    {
      id: 'ord-1001',
      supplierId: 'sup-1',
      supplierName: 'Fresh Farm Supplies',
      productId: 'prod-2',
      productName: 'Orange Juice',
      qty: 60,
      status: 'Pending',
      createdAt: '2026-05-29T08:30:00.000Z',
    },
    {
      id: 'ord-1002',
      supplierId: 'sup-2',
      supplierName: 'Metro Wholesale',
      productId: 'prod-4',
      productName: 'Notebook A5',
      qty: 200,
      status: 'Confirmed',
      createdAt: '2026-05-30T11:00:00.000Z',
    },
  ],
};

let state = deepClone(initialState);

const delay = (value, shouldReject = false, ms = 350) =>
  new Promise((resolve, reject) => {
    setTimeout(() => {
      if (shouldReject) {
        reject(value);
      } else {
        resolve(deepClone(value));
      }
    }, ms);
  });

const createError = (status, message) => {
  const error = new Error(message);
  error.response = { status, data: { message } };
  return error;
};

const ensureAuth = (token) => {
  if (!token) {
    throw createError(401, 'Unauthorized');
  }
};

const nextId = (prefix, items) => {
  const max = items.reduce((acc, item) => {
    const numeric = Number(String(item.id).split('-').pop());
    return Number.isFinite(numeric) && numeric > acc ? numeric : acc;
  }, 0);
  return `${prefix}-${max + 1}`;
};

const findCategoryName = (categoryId) =>
  state.categories.find((category) => category.id === categoryId)?.name || '';

const findSupplierName = (supplierId) =>
  state.suppliers.find((supplier) => supplier.id === supplierId)?.name || '';

const findProductName = (productId) =>
  state.products.find((product) => product.id === productId)?.name || '';

export const mockStore = {
  auth: {
    login: async ({ email, password }) => {
      const user = state.users.find(
        (entry) =>
          entry.email.toLowerCase() === String(email || '').toLowerCase() &&
          entry.password === password,
      );

      if (!user) {
        throw createError(401, 'Invalid email or password');
      }

      return delay({
        token: `mock-token-${user.id}`,
        user: { id: user.id, name: user.name, email: user.email },
      });
    },
    register: async ({ name, email, password }) => {
      const existing = state.users.find(
        (entry) => entry.email.toLowerCase() === String(email || '').toLowerCase(),
      );
      if (existing) {
        throw createError(409, 'A user with that email already exists');
      }

      const user = {
        id: nextId('user', state.users),
        name,
        email,
        password,
      };
      state.users.push(user);

      return delay({
        token: `mock-token-${user.id}`,
        user: { id: user.id, name: user.name, email: user.email },
      });
    },
  },
  products: {
    list: async (token) => {
      ensureAuth(token);
      return delay(
        state.products.map((product) => ({
          ...product,
          categoryName: findCategoryName(product.categoryId),
        })),
      );
    },
    create: async (token, payload) => {
      ensureAuth(token);
      const category = state.categories.find((item) => item.id === payload.categoryId);
      if (!category) {
        throw createError(400, 'Category does not exist');
      }

      const supplier = state.suppliers.find((item) => item.id === payload.supplierId);
      if (!supplier) {
        throw createError(400, 'Supplier does not exist');
      }

      const product = {
        id: nextId('prod', state.products),
        name: payload.name,
        categoryId: payload.categoryId,
        categoryName: category.name,
        price: Number(payload.price),
        stockQty: Number(payload.stockQty),
        threshold: Number(payload.threshold || LOW_STOCK_THRESHOLD),
        sku: payload.sku || '',
        supplierId: payload.supplierId,
        description: payload.description || '',
      };
      state.products.unshift(product);
      return delay(product);
    },
    update: async (token, id, payload) => {
      ensureAuth(token);
      const index = state.products.findIndex((item) => item.id === id);
      if (index === -1) {
        throw createError(404, 'Product not found');
      }
      const category = state.categories.find((item) => item.id === payload.categoryId);
      if (!category) {
        throw createError(400, 'Category does not exist');
      }
      const supplier = state.suppliers.find((item) => item.id === payload.supplierId);
      if (!supplier) {
        throw createError(400, 'Supplier does not exist');
      }
      state.products[index] = {
        ...state.products[index],
        ...payload,
        price: Number(payload.price),
        stockQty: Number(payload.stockQty),
        threshold: Number(payload.threshold || LOW_STOCK_THRESHOLD),
        categoryName: category.name,
      };
      return delay(state.products[index]);
    },
    remove: async (token, id) => {
      ensureAuth(token);
      state.products = state.products.filter((item) => item.id !== id);
      state.orders = state.orders.filter((order) => order.productId !== id);
      return delay({ success: true });
    },
  },
  categories: {
    list: async (token) => {
      ensureAuth(token);
      return delay(
        state.categories.map((category) => ({
          ...category,
          productCount: state.products.filter(
            (product) => product.categoryId === category.id,
          ).length,
        })),
      );
    },
    create: async (token, payload) => {
      ensureAuth(token);
      const exists = state.categories.find(
        (category) => category.name.toLowerCase() === String(payload.name || '').toLowerCase(),
      );
      if (exists) {
        throw createError(409, 'Category already exists');
      }
      const category = {
        id: nextId('cat', state.categories),
        name: payload.name,
        description: payload.description || '',
      };
      state.categories.unshift(category);
      return delay(category);
    },
    update: async (token, id, payload) => {
      ensureAuth(token);
      const index = state.categories.findIndex((category) => category.id === id);
      if (index === -1) {
        throw createError(404, 'Category not found');
      }
      state.categories[index] = {
        ...state.categories[index],
        ...payload,
      };
      state.products = state.products.map((product) =>
        product.categoryId === id
          ? { ...product, categoryName: state.categories[index].name }
          : product,
      );
      return delay(state.categories[index]);
    },
    remove: async (token, id) => {
      ensureAuth(token);
      const inUse = state.products.some((product) => product.categoryId === id);
      if (inUse) {
        throw createError(400, 'Category cannot be deleted while products reference it');
      }
      state.categories = state.categories.filter((category) => category.id !== id);
      return delay({ success: true });
    },
  },
  suppliers: {
    list: async (token) => {
      ensureAuth(token);
      return delay(
        state.suppliers.map((supplier) => ({
          ...supplier,
          productCount: state.products.filter(
            (product) => product.supplierId === supplier.id,
          ).length,
        })),
      );
    },
    create: async (token, payload) => {
      ensureAuth(token);
      const supplier = {
        id: nextId('sup', state.suppliers),
        name: payload.name,
        email: payload.email || '',
        phone: payload.phone || '',
        company: payload.company || payload.name,
        address: payload.address || '',
      };
      state.suppliers.unshift(supplier);
      return delay(supplier);
    },
    update: async (token, id, payload) => {
      ensureAuth(token);
      const index = state.suppliers.findIndex((supplier) => supplier.id === id);
      if (index === -1) {
        throw createError(404, 'Supplier not found');
      }
      state.suppliers[index] = {
        ...state.suppliers[index],
        ...payload,
      };
      return delay(state.suppliers[index]);
    },
    remove: async (token, id) => {
      ensureAuth(token);
      const inUse = state.orders.some((order) => order.supplierId === id);
      if (inUse) {
        throw createError(400, 'Supplier cannot be deleted while orders reference it');
      }
      state.suppliers = state.suppliers.filter((supplier) => supplier.id !== id);
      return delay({ success: true });
    },
  },
  orders: {
    list: async (token) => {
      ensureAuth(token);
      return delay(state.orders.map((order) => ({ ...order })));
    },
    create: async (token, payload) => {
      ensureAuth(token);
      const supplierName = findSupplierName(payload.supplierId);
      const productName = findProductName(payload.productId);
      if (!supplierName) {
        throw createError(400, 'Supplier does not exist');
      }
      if (!productName) {
        throw createError(400, 'Product does not exist');
      }
      const order = {
        id: nextId('ord', state.orders),
        supplierId: payload.supplierId,
        supplierName,
        productId: payload.productId,
        productName,
        qty: Number(payload.qty),
        status: payload.status || 'Pending',
        createdAt: new Date().toISOString(),
      };
      state.orders.unshift(order);
      return delay(order);
    },
    updateStatus: async (token, id, status) => {
      ensureAuth(token);
      const index = state.orders.findIndex((order) => order.id === id);
      if (index === -1) {
        throw createError(404, 'Order not found');
      }
      state.orders[index] = {
        ...state.orders[index],
        status,
      };
      return delay(state.orders[index]);
    },
    remove: async (token, id) => {
      ensureAuth(token);
      state.orders = state.orders.filter((order) => order.id !== id);
      return delay({ success: true });
    },
  },
  dashboard: {
    summary: async (token) => {
      ensureAuth(token);
      return delay({
        totalProducts: state.products.length,
        lowStockItems: state.products.filter(
          (product) => Number(product.stockQty) < Number(product.threshold || LOW_STOCK_THRESHOLD),
        ).length,
        pendingOrders: state.orders.filter((order) => order.status === 'Pending').length,
        totalSuppliers: state.suppliers.length,
      });
    },
    categoryStock: async (token) => {
      ensureAuth(token);
      return delay(
        state.categories.map((category) => ({
          name: category.name,
          stockQty: state.products
            .filter((product) => product.categoryId === category.id)
            .reduce((sum, product) => sum + Number(product.stockQty || 0), 0),
        })),
      );
    },
  },
};

export const getMockToken = () => localStorage.getItem('inventory_auth_token');
export const mockDelay = delay;
