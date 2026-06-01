import React, { useEffect, useState } from 'react';
import { createSupplier, deleteSupplier, listSuppliers, updateSupplier } from '../../api/suppliers';
import Button from '../../components/Button';
import ConfirmDialog from '../../components/ConfirmDialog';
import ErrorState from '../../components/ErrorState';
import LoadingState from '../../components/LoadingState';
import PageHeader from '../../components/PageHeader';
import Table from '../../components/Table';
import SupplierModal from './SupplierModal';

const SuppliersPage = () => {
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState(null);
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const suppliersData = await listSuppliers();
      setSuppliers(suppliersData);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load suppliers');
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
      if (editingSupplier) {
        await updateSupplier(editingSupplier.id, payload);
      } else {
        await createSupplier(payload);
      }
      setModalOpen(false);
      setEditingSupplier(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to save supplier');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await deleteSupplier(deleteTarget.id);
      setDeleteTarget(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to delete supplier');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Phone' },
    { key: 'address', label: 'Address' },
    { key: 'actions', label: 'Actions', className: 'text-right' },
  ];

  if (loading) {
    return <LoadingState label="Loading suppliers..." />;
  }

  if (error && suppliers.length === 0) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  return (
    <div>
      <PageHeader
        title="Suppliers"
        description="Maintain supplier records used to create restocking orders."
        actions={[
          <Button
            key="add-supplier"
            onClick={() => {
              setEditingSupplier(null);
              setModalOpen(true);
            }}
          >
            Add Supplier
          </Button>,
        ]}
      />

      {error ? <div className="mb-4"><ErrorState message={error} onRetry={loadData} /></div> : null}

      <Table
        columns={columns}
        data={suppliers}
        renderRow={(supplier) => (
          <tr key={supplier.id}>
            <td className="px-4 py-4 text-sm font-medium text-slate-900">{supplier.name}</td>
            <td className="px-4 py-4 text-sm text-slate-600">{supplier.email || '-'}</td>
            <td className="px-4 py-4 text-sm text-slate-600">{supplier.phone || '-'}</td>
            <td className="px-4 py-4 text-sm text-slate-600">{supplier.address || '-'}</td>
            <td className="px-4 py-4 text-right">
              <div className="flex justify-end gap-2">
                <Button
                  variant="secondary"
                  onClick={() => {
                    setEditingSupplier(supplier);
                    setModalOpen(true);
                  }}
                >
                  Edit
                </Button>
                <Button variant="danger" onClick={() => setDeleteTarget(supplier)}>
                  Delete
                </Button>
              </div>
            </td>
          </tr>
        )}
      />

      <SupplierModal
        open={modalOpen}
        supplier={editingSupplier}
        onClose={() => {
          setModalOpen(false);
          setEditingSupplier(null);
        }}
        onSubmit={handleSubmit}
        loading={saving}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title="Delete supplier"
        message={`Delete ${deleteTarget?.name || 'this supplier'}? Orders linked to a supplier block deletion.`}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        loading={deleting}
      />
    </div>
  );
};

export default SuppliersPage;
