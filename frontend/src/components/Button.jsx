import React from 'react';

const styles = {
  primary:
    'bg-slate-900 text-white hover:bg-slate-800 focus:ring-slate-500 shadow-soft',
  secondary:
    'bg-white text-slate-700 ring-1 ring-slate-200 hover:bg-slate-50 focus:ring-slate-300',
  danger:
    'bg-rose-600 text-white hover:bg-rose-500 focus:ring-rose-400 shadow-soft',
  ghost:
    'bg-transparent text-slate-600 hover:bg-slate-100 focus:ring-slate-300',
};

const Button = ({ variant = 'primary', className = '', type = 'button', ...props }) => (
  <button
    type={type}
    className={`inline-flex items-center justify-center rounded-xl px-4 py-2 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 ${styles[variant]} ${className}`}
    {...props}
  />
);

export default Button;
