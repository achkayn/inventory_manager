import React, { useEffect, useMemo, useState } from 'react';
import { createProduct, deleteProduct, listProducts, updateProduct } from '../../api/products';
import { listCategories } from '../../api/categories';
import Badge from '../../components/Badge';
import Button from '../../components/Button';
import ConfirmDialog from '../../components/ConfirmDialog';
import ErrorState from '../../components/ErrorState';
import LoadingState from '../../components/LoadingState';
import PageHeader from '../../components/PageHeader';
import Table from '../../components/Table';
import { formatCurrency } from '../../utils/format';
import ProductModal from './ProductModal';

const ProductsPage = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [productsData, categoriesData] = await Promise.all([
        listProducts(),
        listCategories(),
      ]);
      setProducts(productsData);
      setCategories(categoriesData);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const categoryLookup = useMemo(
    () => Object.fromEntries(categories.map((category) => [category.id, category.name])),
    [categories],
  );

  const handleSubmit = async (payload) => {
    setSaving(true);
    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, payload);
      } else {
        await createProduct(payload);
      }
      setModalOpen(false);
      setEditingProduct(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to save product');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await deleteProduct(deleteTarget.id);
      setDeleteTarget(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to delete product');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    { key: 'name', label: 'Name' },
    { key: 'category', label: 'Category' },
    { key: 'price', label: 'Price' },
    { key: 'stockQty', label: 'Stock Qty' },
    { key: 'status', label: 'Status' },
    { key: 'actions', label: 'Actions', className: 'text-right' },
  ];

  if (loading) {
    return <LoadingState label="Loading products..." />;
  }

  if (error && products.length === 0) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  return (
    <div>
      <PageHeader
        title="Products"
        description="Manage inventory items, keep stock quantities in sync, and surface low-stock alerts."
        actions={[
          <Button
            key="add"
            onClick={() => {
              setEditingProduct(null);
              setModalOpen(true);
            }}
          >
            Add Product
          </Button>,
        ]}
      />

      {error ? <div className="mb-4"><ErrorState message={error} onRetry={loadData} /></div> : null}

      <Table
        columns={columns}
        data={products}
        renderRow={(product) => {
          return (
            <tr
              key={product.id}
              className={product.isLowStock ? 'bg-rose-50/60' : 'bg-white'}
            >
              <td className="px-4 py-4 text-sm font-medium text-slate-900">{product.name}</td>
              <td className="px-4 py-4 text-sm text-slate-600">
                {product.categoryName || categoryLookup[product.categoryId] || '-'}
              </td>
              <td className="px-4 py-4 text-sm text-slate-600">{formatCurrency(product.price)}</td>
              <td className="px-4 py-4 text-sm text-slate-600">
                <span className={product.isLowStock ? 'font-semibold text-rose-700' : ''}>
                  {product.stockQty}
                </span>
              </td>
              <td className="px-4 py-4">
                {product.isLowStock ? <Badge tone="red">Low Stock</Badge> : <Badge tone="green">OK</Badge>}
              </td>
              <td className="px-4 py-4 text-right">
                <div className="flex justify-end gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => {
                      setEditingProduct(product);
                      setModalOpen(true);
                    }}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="danger"
                    onClick={() => setDeleteTarget(product)}
                  >
                    Delete
                  </Button>
                </div>
              </td>
            </tr>
          );
        }}
      />

      <ProductModal
        open={modalOpen}
        product={editingProduct}
        categories={categories}
        onClose={() => {
          setModalOpen(false);
          setEditingProduct(null);
        }}
        onSubmit={handleSubmit}
        loading={saving}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title="Delete product"
        message={`Delete ${deleteTarget?.name || 'this product'}? This action cannot be undone.`}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        loading={deleting}
      />
    </div>
  );
};

export default ProductsPage;
