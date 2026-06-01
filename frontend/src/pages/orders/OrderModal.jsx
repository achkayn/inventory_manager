import React, { useEffect, useState } from 'react';
import Modal from '../../components/Modal';
import Button from '../../components/Button';
import { FieldLabel, SelectInput, TextInput } from '../../components/Input';

const initialForm = {
  supplierId: '',
  productId: '',
  quantity: '',
};

const OrderModal = ({ open, suppliers, products, onClose, onSubmit, loading }) => {
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    setForm(initialForm);
  }, [open]);

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({
      supplierId: Number(form.supplierId),
      productId: Number(form.productId),
      quantity: Number(form.quantity),
    });
  };

  return (
    <Modal open={open} title="Create Order" onClose={onClose}>
      <form onSubmit={handleSubmit} className="grid gap-5 md:grid-cols-2">
        <div>
          <FieldLabel htmlFor="order-supplier">Supplier</FieldLabel>
          <SelectInput
            id="order-supplier"
            value={form.supplierId}
            onChange={(event) => setForm({ ...form, supplierId: event.target.value })}
            required
          >
            <option value="">Select supplier</option>
            {suppliers.map((supplier) => (
              <option key={supplier.id} value={supplier.id}>
                {supplier.name}
              </option>
            ))}
          </SelectInput>
        </div>
        <div>
          <FieldLabel htmlFor="order-product">Product</FieldLabel>
          <SelectInput
            id="order-product"
            value={form.productId}
            onChange={(event) => setForm({ ...form, productId: event.target.value })}
            required
          >
            <option value="">Select product</option>
            {products.map((product) => (
              <option key={product.id} value={product.id}>
                {product.name}
              </option>
            ))}
          </SelectInput>
        </div>
        <div className="md:col-span-2">
          <FieldLabel htmlFor="order-qty">Quantity</FieldLabel>
          <TextInput
            id="order-qty"
            type="number"
            min="1"
            value={form.quantity}
            onChange={(event) => setForm({ ...form, quantity: event.target.value })}
            required
          />
        </div>
        <div className="md:col-span-2 flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Create order'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default OrderModal;
