import React from 'react';

const styles = {
  gray: 'bg-slate-100 text-slate-700 ring-slate-200',
  green: 'bg-emerald-100 text-emerald-700 ring-emerald-200',
  yellow: 'bg-amber-100 text-amber-800 ring-amber-200',
  red: 'bg-rose-100 text-rose-700 ring-rose-200',
  blue: 'bg-sky-100 text-sky-700 ring-sky-200',
  indigo: 'bg-indigo-100 text-indigo-700 ring-indigo-200',
};

const Badge = ({ tone = 'gray', children, className = '' }) => (
  <span
    className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ring-1 ring-inset ${styles[tone] || styles.gray} ${className}`}
  >
    {children}
  </span>
);

export default Badge;
