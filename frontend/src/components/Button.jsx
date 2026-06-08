import React from 'react';

const styles = {
  primary:
    'bg-green-700 text-white hover:bg-green-800 focus:ring-green-500 shadow-sm',
  secondary:
    'bg-white text-gray-700 ring-1 ring-gray-200 hover:bg-gray-50 focus:ring-gray-300',
  danger:
    'bg-rose-600 text-white hover:bg-rose-500 focus:ring-rose-400 shadow-sm',
  ghost:
    'bg-transparent text-gray-600 hover:bg-gray-100 focus:ring-gray-300',
};

const Button = ({ variant = 'primary', className = '', type = 'button', ...props }) => (
  <button
    type={type}
    className={`inline-flex items-center justify-center px-4 py-2 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 ${styles[variant]} ${className}`}
    {...props}
  />
);

export default Button;
