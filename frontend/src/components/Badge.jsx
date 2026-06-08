import React from 'react';

const styles = {
  gray: 'bg-gray-100 text-gray-700 ring-gray-200',
  green: 'bg-green-100 text-green-700 ring-green-200',
  yellow: 'bg-amber-100 text-amber-800 ring-amber-200',
  red: 'bg-rose-100 text-rose-700 ring-rose-200',
  blue: 'bg-sky-100 text-sky-700 ring-sky-200',
  indigo: 'bg-indigo-100 text-indigo-700 ring-indigo-200',
};

const Badge = ({ tone = 'gray', children, className = '' }) => (
  <span
    className={`inline-flex items-center px-2.5 py-1 text-xs font-semibold ring-1 ring-inset ${styles[tone] || styles.gray} ${className}`}
  >
    {children}
  </span>
);

export default Badge;
