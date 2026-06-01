import React, { useEffect, useState } from 'react';
import Modal from '../../components/Modal';
import Button from '../../components/Button';
import { FieldLabel, TextArea, TextInput } from '../../components/Input';

const initialForm = {
  name: '',
  company: '',
  email: '',
  phone: '',
  address: '',
};

const SupplierModal = ({ open, supplier, onClose, onSubmit, loading }) => {
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    setForm(
      supplier
        ? {
            name: supplier.name || '',
            company: supplier.company || '',
            email: supplier.email || '',
            phone: supplier.phone || '',
            address: supplier.address || '',
          }
        : initialForm,
    );
  }, [supplier, open]);

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(form);
  };

  return (
    <Modal open={open} title={supplier ? 'Edit Supplier' : 'Add Supplier'} onClose={onClose}>
      <form onSubmit={handleSubmit} className="grid gap-5 md:grid-cols-2">
        <div className="md:col-span-2">
          <FieldLabel htmlFor="supplier-name">Name</FieldLabel>
          <TextInput
            id="supplier-name"
            value={form.name}
            onChange={(event) => setForm({ ...form, name: event.target.value })}
            required
          />
        </div>
        <div>
          <FieldLabel htmlFor="supplier-company">Company</FieldLabel>
          <TextInput
            id="supplier-company"
            value={form.company}
            onChange={(event) => setForm({ ...form, company: event.target.value })}
          />
        </div>
        <div>
          <FieldLabel htmlFor="supplier-email">Email</FieldLabel>
          <TextInput
            id="supplier-email"
            type="email"
            value={form.email}
            onChange={(event) => setForm({ ...form, email: event.target.value })}
          />
        </div>
        <div>
          <FieldLabel htmlFor="supplier-phone">Phone</FieldLabel>
          <TextInput
            id="supplier-phone"
            value={form.phone}
            onChange={(event) => setForm({ ...form, phone: event.target.value })}
          />
        </div>
        <div className="md:col-span-2">
          <FieldLabel htmlFor="supplier-address">Address</FieldLabel>
          <TextArea
            id="supplier-address"
            rows="4"
            value={form.address}
            onChange={(event) => setForm({ ...form, address: event.target.value })}
          />
        </div>
        <div className="md:col-span-2 flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? 'Saving...' : 'Save supplier'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default SupplierModal;
