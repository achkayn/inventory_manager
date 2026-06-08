import React from 'react';

export const FieldLabel = ({ children, htmlFor }) => (
  <label htmlFor={htmlFor} className="mb-1 block text-sm font-medium text-gray-700">
    {children}
  </label>
);

export const TextInput = React.forwardRef(({ className = '', ...props }, ref) => (
  <input
    ref={ref}
    className={`w-full border border-gray-200 bg-white px-3 py-2 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-green-500 focus:ring-2 focus:ring-green-100 ${className}`}
    {...props}
  />
));

TextInput.displayName = 'TextInput';

export const TextArea = React.forwardRef(({ className = '', ...props }, ref) => (
  <textarea
    ref={ref}
    className={`w-full border border-gray-200 bg-white px-3 py-2 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-green-500 focus:ring-2 focus:ring-green-100 ${className}`}
    {...props}
  />
));

TextArea.displayName = 'TextArea';

export const SelectInput = React.forwardRef(({ className = '', ...props }, ref) => (
  <select
    ref={ref}
    className={`w-full border border-gray-200 bg-white px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-green-500 focus:ring-2 focus:ring-green-100 ${className}`}
    {...props}
  />
));

SelectInput.displayName = 'SelectInput';
