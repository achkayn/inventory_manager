import React, { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { createOrder, deleteOrder, listOrders, updateOrderStatus } from '../../api/orders';
import { listProducts } from '../../api/products';
import { listSuppliers } from '../../api/suppliers';
import Badge from '../../components/Badge';
import Button from '../../components/Button';
import ConfirmDialog from '../../components/ConfirmDialog';
import ErrorState from '../../components/ErrorState';
import LoadingState from '../../components/LoadingState';
import PageHeader from '../../components/PageHeader';
import Table from '../../components/Table';
import OrderModal from './OrderModal';

const nextStatus = {
  PENDING: 'CONFIRMED',
  CONFIRMED: 'DELIVERED',
  DELIVERED: 'DELIVERED',
};

const statusTone = {
  PENDING: 'yellow',
  CONFIRMED: 'blue',
  DELIVERED: 'green',
};

const statusFilters = ['ALL', 'PENDING', 'CONFIRMED', 'DELIVERED'];

const OrdersPage = () => {
  const { isAdmin } = useAuth();
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [statusSavingId, setStatusSavingId] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [ordersData, productsData, suppliersData] = await Promise.all([
        listOrders(statusFilter === 'ALL' ? undefined : statusFilter),
        listProducts(),
        listSuppliers(),
      ]);
      setOrders(ordersData);
      setProducts(productsData);
      setSuppliers(suppliersData);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [statusFilter]);

  const productLookup = useMemo(
    () => Object.fromEntries(products.map((product) => [product.id, product.name])),
    [products],
  );

  const handleCreate = async (payload) => {
    setSaving(true);
    try {
      await createOrder(payload);
      setModalOpen(false);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to create order');
    } finally {
      setSaving(false);
    }
  };

  const handleAdvanceStatus = async (order) => {
    const status = nextStatus[order.status] || 'DELIVERED';
    setStatusSavingId(order.id);
    try {
      await updateOrderStatus(order.id, status);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to update order');
    } finally {
      setStatusSavingId(null);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await deleteOrder(deleteTarget.id);
      setDeleteTarget(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to delete order');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    { key: 'id', label: 'Order ID' },
    { key: 'supplier', label: 'Supplier' },
    { key: 'product', label: 'Product' },
    { key: 'quantity', label: 'Qty' },
    { key: 'status', label: 'Status' },
    { key: 'actions', label: 'Actions', className: 'text-right' },
  ];

  if (loading) {
    return <LoadingState label="Loading orders..." />;
  }

  if (error && orders.length === 0) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  return (
    <div>
      <PageHeader
        title="Orders"
        description="Track restocking requests from pending approval through delivery."
        actions={[
          <Button key="create-order" onClick={() => setModalOpen(true)}>
            Create Order
          </Button>,
        ]}
      />

      <div className="mb-4 flex flex-wrap gap-2">
        {statusFilters.map((filter) => (
          <Button
            key={filter}
            variant={statusFilter === filter ? 'primary' : 'secondary'}
            onClick={() => setStatusFilter(filter)}
          >
            {filter === 'ALL' ? 'All' : filter}
          </Button>
        ))}
      </div>

      {error ? <div className="mb-4"><ErrorState message={error} onRetry={loadData} /></div> : null}

      <Table
        columns={columns}
        data={orders}
        renderRow={(order) => (
          <tr key={order.id}>
            <td className="px-4 py-4 text-sm font-medium text-slate-900">{order.id}</td>
            <td className="px-4 py-4 text-sm text-slate-600">{order.supplierName}</td>
            <td className="px-4 py-4 text-sm text-slate-600">
              {order.productName || productLookup[order.productId] || '-'}
            </td>
            <td className="px-4 py-4 text-sm text-slate-600">{order.quantity}</td>
            <td className="px-4 py-4">
              <Badge tone={statusTone[order.status] || 'gray'}>{order.status}</Badge>
            </td>
            <td className="px-4 py-4 text-right">
              <div className="flex justify-end gap-2">
                {isAdmin && order.status !== 'DELIVERED' ? (
                  <Button
                    variant="secondary"
                    onClick={() => handleAdvanceStatus(order)}
                    disabled={statusSavingId === order.id}
                  >
                    {statusSavingId === order.id ? 'Updating...' : `Mark ${nextStatus[order.status]}`}
                  </Button>
                ) : null}
                {isAdmin && (
                  <Button variant="danger" onClick={() => setDeleteTarget(order)}>
                    Delete
                  </Button>
                )}
              </div>
            </td>
          </tr>
        )}
      />

      <OrderModal
        open={modalOpen}
        suppliers={suppliers}
        products={products}
        onClose={() => setModalOpen(false)}
        onSubmit={handleCreate}
        loading={saving}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title="Delete order"
        message={`Delete ${deleteTarget?.id || 'this order'}? This cannot be undone.`}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        loading={deleting}
      />
    </div>
  );
};

export default OrdersPage;
