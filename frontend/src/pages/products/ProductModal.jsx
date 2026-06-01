import React, { useEffect, useState } from 'react';
import Modal from '../../components/Modal';
import Button from '../../components/Button';
import { FieldLabel, SelectInput, TextArea, TextInput } from '../../components/Input';
import { LOW_STOCK_THRESHOLD } from '../../constants';

const initialForm = {
  name: '',
  categoryId: '',
  supplierId: '',
  sku: '',
  price: '',
  stockQty: '',
  threshold: LOW_STOCK_THRESHOLD,
  description: '',
};

const ProductModal = ({ open, product, categories, suppliers, onClose, onSubmit, loading }) => {
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    if (product) {
      setForm({
        name: product.name || '',
        categoryId: product.categoryId || '',
        supplierId: product.supplierId || '',
        sku: product.sku || '',
        price: product.price ?? '',
        stockQty: product.stockQty ?? '',
        threshold: product.threshold ?? LOW_STOCK_THRESHOLD,
        description: product.description || '',
      });
    } else {
      setForm(initialForm);
    }
  }, [product, open]);

  const handleChange = (key) => (event) => {
    setForm((current) => ({ ...current, [key]: event.target.value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({
      ...form,
      price: Number(form.price),
      stockQty: Number(form.stockQty),
      threshold: Number(form.threshold),
    });
  };

  return (
    <Modal open={open} title={product ? 'Edit Product' : 'Add Product'} onClose={onClose}>
      <form onSubmit={handleSubmit} className="grid gap-5 md:grid-cols-2">
        <div className="md:col-span-2">
          <FieldLabel htmlFor="product-name">Name</FieldLabel>
          <TextInput id="product-name" value={form.name} onChange={handleChange('name')} required />
        </div>
        <div>
          <FieldLabel htmlFor="product-category">Category</FieldLabel>
          <SelectInput
            id="product-category"
            value={form.categoryId}
            onChange={handleChange('categoryId')}
            required
          >
            <option value="">Select category</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </SelectInput>
        </div>
        <div>
          <FieldLabel htmlFor="product-supplier">Supplier</FieldLabel>
          <SelectInput
            id="product-supplier"
            value={form.supplierId}
            onChange={handleChange('supplierId')}
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
          <FieldLabel htmlFor="product-sku">SKU</FieldLabel>
          <TextInput id="product-sku" value={form.sku} onChange={handleChange('sku')} />
        </div>
        <div>
          <FieldLabel htmlFor="product-price">Price</FieldLabel>
          <TextInput
            id="product-price"
            type="number"
            min="0"
            step="0.01"
            value={form.price}
            onChange={handleChange('price')}
            required
          />
        </div>
        <div>
          <FieldLabel htmlFor="product-stock">Stock Qty</FieldLabel>
          <TextInput
            id="product-stock"
            type="number"
            min="0"
            value={form.stockQty}
            onChange={handleChange('stockQty')}
            required
          />
        </div>
        <div>
          <FieldLabel htmlFor="product-threshold">Low stock threshold</FieldLabel>
          <TextInput
            id="product-threshold"
            type="number"
            min="0"
            value={form.threshold}
            onChange={handleChange('threshold')}
            required
          />
        </div>
        <div className="md:col-span-2">
          <FieldLabel htmlFor="product-description">Description</FieldLabel>
          <TextArea
            id="product-description"
            rows="4"
            value={form.description}
            onChange={handleChange('description')}
          />
        </div>
        <div className="md:col-span-2 flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? 'Saving...' : 'Save product'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default ProductModal;
