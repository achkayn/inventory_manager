import React, { useEffect, useState } from 'react';
import { createCategory, deleteCategory, listCategories, updateCategory } from '../../api/categories';
import { listProducts } from '../../api/products';
import Badge from '../../components/Badge';
import Button from '../../components/Button';
import ConfirmDialog from '../../components/ConfirmDialog';
import ErrorState from '../../components/ErrorState';
import LoadingState from '../../components/LoadingState';
import PageHeader from '../../components/PageHeader';
import Table from '../../components/Table';
import CategoryModal from './CategoryModal';

const CategoriesPage = () => {
  const [categories, setCategories] = useState([]);
  const [productCountLookup, setProductCountLookup] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [categoriesData, productsData] = await Promise.all([listCategories(), listProducts()]);
      setCategories(categoriesData);
      setProductCountLookup(
        productsData.reduce((acc, product) => {
          acc[product.categoryId] = (acc[product.categoryId] || 0) + 1;
          return acc;
        }, {}),
      );
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load categories');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSubmit = async (payload) => {
    setSaving(true);
    try {
      if (editingCategory) {
        await updateCategory(editingCategory.id, payload);
      } else {
        await createCategory(payload);
      }
      setModalOpen(false);
      setEditingCategory(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to save category');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await deleteCategory(deleteTarget.id);
      setDeleteTarget(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to delete category');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    { key: 'name', label: 'Name' },
    { key: 'description', label: 'Description' },
    { key: 'products', label: 'Products' },
    { key: 'actions', label: 'Actions', className: 'text-right' },
  ];

  if (loading) {
    return <LoadingState label="Loading categories..." />;
  }

  if (error && categories.length === 0) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  return (
    <div>
      <PageHeader
        title="Categories"
        description="Maintain category metadata used in the product catalog and dashboard chart."
        actions={[
          <Button
            key="add-category"
            onClick={() => {
              setEditingCategory(null);
              setModalOpen(true);
            }}
          >
            Add Category
          </Button>,
        ]}
      />

      {error ? <div className="mb-4"><ErrorState message={error} onRetry={loadData} /></div> : null}

      <Table
        columns={columns}
        data={categories}
        renderRow={(category) => (
          <tr key={category.id}>
            <td className="px-4 py-4 text-sm font-medium text-slate-900">{category.name}</td>
            <td className="px-4 py-4 text-sm text-slate-600">{category.description || '-'}</td>
            <td className="px-4 py-4">
              <Badge tone="blue">{productCountLookup[category.id] || 0} Products</Badge>
            </td>
            <td className="px-4 py-4 text-right">
              <div className="flex justify-end gap-2">
                <Button
                  variant="secondary"
                  onClick={() => {
                    setEditingCategory(category);
                    setModalOpen(true);
                  }}
                >
                  Edit
                </Button>
                <Button variant="danger" onClick={() => setDeleteTarget(category)}>
                  Delete
                </Button>
              </div>
            </td>
          </tr>
        )}
      />

      <CategoryModal
        open={modalOpen}
        category={editingCategory}
        onClose={() => {
          setModalOpen(false);
          setEditingCategory(null);
        }}
        onSubmit={handleSubmit}
        loading={saving}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title="Delete category"
        message={`Delete ${deleteTarget?.name || 'this category'}? Categories in use by products cannot be removed.`}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        loading={deleting}
      />
    </div>
  );
};

export default CategoriesPage;
