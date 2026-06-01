import React from 'react';
import Modal from './Modal';
import Button from './Button';

const ConfirmDialog = ({ open, title, message, onConfirm, onClose, loading = false }) => (
  <Modal open={open} title={title} onClose={onClose} maxWidth="max-w-lg">
    <p className="text-sm leading-6 text-slate-600">{message}</p>
    <div className="mt-6 flex justify-end gap-3">
      <Button variant="secondary" onClick={onClose} disabled={loading}>
        Cancel
      </Button>
      <Button variant="danger" onClick={onConfirm} disabled={loading}>
        {loading ? 'Processing...' : 'Delete'}
      </Button>
    </div>
  </Modal>
);

export default ConfirmDialog;
