import React from 'react';
import Button from './Button';
import { useAuth } from '../context/AuthContext';

const Navbar = ({ onMenuClick }) => {
  const { user, logout } = useAuth();

  return (
    <header className="border-b border-slate-200 bg-white px-4 py-4 sm:px-6">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <Button
            variant="secondary"
            className="lg:hidden"
            onClick={onMenuClick}
            aria-label="Open navigation"
          >
            Menu
          </Button>
          <div>
            <p className="text-sm font-medium text-slate-500">Mini Stock Manager</p>
            <h1 className="text-xl font-semibold text-slate-900">Inventory Dashboard</h1>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="hidden text-right sm:block">
            <p className="text-sm font-semibold text-slate-900">{user?.name || 'User'}</p>
            <p className="text-xs text-slate-500">{user?.email || 'Signed in'}</p>
          </div>
          <Button variant="secondary" onClick={logout}>
            Logout
          </Button>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
