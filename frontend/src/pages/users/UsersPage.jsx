import React, { useEffect, useState } from 'react';
import { listUsers, updateUserRole } from '../../api/users';
import Badge from '../../components/Badge';
import Button from '../../components/Button';
import ConfirmDialog from '../../components/ConfirmDialog';
import ErrorState from '../../components/ErrorState';
import LoadingState from '../../components/LoadingState';
import PageHeader from '../../components/PageHeader';
import Table from '../../components/Table';

const UsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [roleTarget, setRoleTarget] = useState(null);
  const [updating, setUpdating] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await listUsers();
      setUsers(data);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load users');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleRoleChange = async () => {
    if (!roleTarget) return;
    setUpdating(true);
    const newRole = roleTarget.role === 'ROLE_ADMIN' ? 'ROLE_USER' : 'ROLE_ADMIN';
    try {
      await updateUserRole(roleTarget.id, newRole);
      setRoleTarget(null);
      await loadData();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to update role');
    } finally {
      setUpdating(false);
    }
  };

  const columns = [
    { key: 'username', label: 'Username' },
    { key: 'email', label: 'Email' },
    { key: 'role', label: 'Role' },
    { key: 'actions', label: 'Actions', className: 'text-right' },
  ];

  if (loading) {
    return <LoadingState label="Loading users..." />;
  }

  if (error && users.length === 0) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  return (
    <div>
      <PageHeader
        title="Users"
        description="Manage accounts and roles."
      />

      {error ? <div className="mb-4"><ErrorState message={error} onRetry={loadData} /></div> : null}

      <Table
        columns={columns}
        data={users}
        renderRow={(user) => (
          <tr key={user.id} className="bg-white">
            <td className="px-4 py-4 text-sm font-medium text-slate-900">{user.username}</td>
            <td className="px-4 py-4 text-sm text-slate-600">{user.email}</td>
            <td className="px-4 py-4">
              {user.role === 'ROLE_ADMIN' ? (
                <Badge tone="indigo">Admin</Badge>
              ) : (
                <Badge tone="gray">Employee</Badge>
              )}
            </td>
            <td className="px-4 py-4 text-right">
              <Button variant="secondary" onClick={() => setRoleTarget(user)}>
                {user.role === 'ROLE_ADMIN' ? 'Make Employee' : 'Make Admin'}
              </Button>
            </td>
          </tr>
        )}
      />

      <ConfirmDialog
        open={Boolean(roleTarget)}
        title="Change user role"
        message={`Change this user's role? ${roleTarget?.username} will become ${roleTarget?.role === 'ROLE_ADMIN' ? 'Employee' : 'Admin'}.`}
        onClose={() => setRoleTarget(null)}
        onConfirm={handleRoleChange}
        loading={updating}
      />
    </div>
  );
};

export default UsersPage;
