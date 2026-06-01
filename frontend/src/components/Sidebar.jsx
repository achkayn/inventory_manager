import React from 'react';
import { NavLink } from 'react-router-dom';

const links = [
  { to: '/', label: 'Dashboard' },
  { to: '/products', label: 'Products' },
  { to: '/categories', label: 'Categories' },
  { to: '/suppliers', label: 'Suppliers' },
  { to: '/orders', label: 'Orders' },
];

const Sidebar = ({ open = false, onClose }) => (
  <>
    <div
      className={`fixed inset-0 z-30 bg-slate-950/40 transition-opacity lg:hidden ${
        open ? 'opacity-100' : 'pointer-events-none opacity-0'
      }`}
      onClick={onClose}
    />
    <aside
      className={`fixed inset-y-0 left-0 z-40 w-72 transform bg-slate-950 text-white transition-transform duration-300 lg:static lg:translate-x-0 ${
        open ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
      }`}
    >
      <div className="flex h-full flex-col">
        <div className="flex items-center justify-between border-b border-white/10 px-6 py-5">
          <div>
            <p className="text-xs uppercase tracking-[0.28em] text-slate-400">Inventory</p>
            <h2 className="mt-1 text-lg font-semibold">Mini Stock Manager</h2>
          </div>
          <button className="rounded-lg p-2 text-slate-400 hover:bg-white/10 lg:hidden" onClick={onClose}>
            X
          </button>
        </div>

        <nav className="flex-1 space-y-1 px-4 py-5">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.to === '/'}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center rounded-xl px-4 py-3 text-sm font-medium transition ${
                  isActive
                    ? 'bg-white text-slate-950'
                    : 'text-slate-300 hover:bg-white/10 hover:text-white'
                }`
              }
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-white/10 px-6 py-5 text-xs text-slate-400">
          Connected to a Jakarta EE REST backend with mock fallback.
        </div>
      </div>
    </aside>
  </>
);

export default Sidebar;
