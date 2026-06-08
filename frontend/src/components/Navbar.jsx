import React from 'react';
import { useAuth } from '../context/AuthContext';

const initials = (name = '') =>
  name
    .trim()
    .split(/\s+/)
    .map((w) => w[0] ?? '')
    .join('')
    .toUpperCase()
    .slice(0, 2) || 'U';

const Navbar = ({ onMenuClick }) => {
  const { user, isAdmin } = useAuth();
  const displayName = user?.name || user?.username || 'User';

  return (
    <header className="sticky top-0 z-20 border-b border-gray-100 bg-white px-4 py-3 sm:px-6">
      <div className="flex items-center gap-3">

        {/* Mobile hamburger */}
        <button
          onClick={onMenuClick}
          aria-label="Open navigation"
          className="inline-flex items-center justify-center border border-gray-200 p-2 text-gray-500 transition hover:bg-gray-50 hover:text-gray-900 lg:hidden"
        >
          <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
          </svg>
        </button>

        {/* Search bar */}
        <div className="relative hidden max-w-xs flex-1 sm:block lg:max-w-sm">
          <div className="pointer-events-none absolute inset-y-0 left-3 flex items-center">
            <svg className="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
            </svg>
          </div>
          <input
            type="text"
            placeholder="Search products, orders..."
            readOnly
            className="w-full border border-gray-200 bg-gray-50 py-2 pl-9 pr-10 text-sm text-gray-700 placeholder-gray-400 focus:border-green-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-green-100 cursor-pointer"
          />
          <div className="pointer-events-none absolute inset-y-0 right-3 flex items-center">
            <kbd className="border border-gray-200 bg-white px-1.5 py-0.5 text-[10px] font-medium text-gray-400 shadow-sm">
              /
            </kbd>
          </div>
        </div>

        {/* Right side */}
        <div className="ml-auto flex items-center gap-2">

          {/* Notification bell */}
          <button className="relative border border-gray-200 p-2 text-gray-500 transition hover:bg-gray-50 hover:text-gray-900">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0" />
            </svg>
            <span className="absolute right-1.5 top-1.5 h-1.5 w-1.5 rounded-full bg-green-500 ring-2 ring-white" />
          </button>

          {/* Divider */}
          <div className="mx-1 hidden h-8 w-px bg-gray-100 sm:block" />

          {/* User info */}
          <div className="hidden items-center gap-2.5 sm:flex">
            <div className="text-right leading-tight">
              <p className="text-sm font-semibold text-gray-900">{displayName}</p>
              <p className="text-xs text-gray-400">{user?.email || 'Signed in'}</p>
            </div>
            <div className="relative">
              <div className="flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-green-600 to-green-800 text-sm font-bold text-white shadow-sm">
                {initials(displayName)}
              </div>
              {isAdmin && (
                <span className="absolute -bottom-0.5 -right-0.5 flex h-3.5 w-3.5 items-center justify-center rounded-full bg-amber-400 ring-2 ring-white">
                  <svg className="h-2 w-2 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M9.664 1.319a.75.75 0 01.672 0 41.059 41.059 0 018.198 5.424.75.75 0 01-.254 1.285 31.372 31.372 0 00-7.86 3.83.75.75 0 01-.84 0 31.508 31.508 0 00-2.08-1.287V9.394c0-.244.116-.463.302-.592a35.504 35.504 0 013.305-2.033.75.75 0 00-.714-1.319 37 37 0 00-3.446 2.12A2.216 2.216 0 006 9.393v.38a31.293 31.293 0 00-4.28-1.746.75.75 0 01-.254-1.285 41.059 41.059 0 018.198-5.424zM6 11.459a29.848 29.848 0 00-2.455-1.158 41.029 41.029 0 00-.39 3.114.75.75 0 00.419.74c.528.256 1.046.53 1.554.82-.21.324-.455.63-.739.914a.75.75 0 101.06 1.06c.37-.369.69-.77.96-1.193a26.61 26.61 0 013.095 2.348.75.75 0 00.992 0 26.547 26.547 0 015.93-3.95.75.75 0 00.42-.739 41.053 41.053 0 00-.39-3.114 29.925 29.925 0 00-5.199 2.801 2.25 2.25 0 01-2.514 0c-.41-.275-.826-.541-1.25-.797zM12 9a.75.75 0 100-1.5.75.75 0 000 1.5z" clipRule="evenodd" />
                  </svg>
                </span>
              )}
            </div>
          </div>

          {/* Mobile avatar only */}
          <div className="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-green-600 to-green-800 text-xs font-bold text-white sm:hidden">
            {initials(displayName)}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
