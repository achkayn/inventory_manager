import React, { useEffect, useState } from 'react';
import Modal from '../../components/Modal';
import Button from '../../components/Button';
import { FieldLabel, TextArea, TextInput } from '../../components/Input';

const initialForm = {
  name: '',
  description: '',
};

const CategoryModal = ({ open, category, onClose, onSubmit, loading }) => {
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    setForm(
      category
        ? {
            name: category.name || '',
            description: category.description || '',
          }
        : initialForm,
    );
  }, [category, open]);

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(form);
  };

  return (
    <Modal open={open} title={category ? 'Edit Category' : 'Add Category'} onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <FieldLabel htmlFor="category-name">Name</FieldLabel>
          <TextInput
            id="category-name"
            value={form.name}
            onChange={(event) => setForm({ ...form, name: event.target.value })}
            required
          />
        </div>
        <div>
          <FieldLabel htmlFor="category-description">Description</FieldLabel>
          <TextArea
            id="category-description"
            rows="4"
            value={form.description}
            onChange={(event) => setForm({ ...form, description: event.target.value })}
          />
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? 'Saving...' : 'Save category'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default CategoryModal;
