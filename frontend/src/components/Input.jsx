import React from 'react';

export const FieldLabel = ({ children, htmlFor }) => (
  <label htmlFor={htmlFor} className="mb-1 block text-sm font-medium text-slate-700">
    {children}
  </label>
);

export const TextInput = React.forwardRef(({ className = '', ...props }, ref) => (
  <input
    ref={ref}
    className={`w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-slate-400 focus:ring-2 focus:ring-slate-200 ${className}`}
    {...props}
  />
));

TextInput.displayName = 'TextInput';

export const TextArea = React.forwardRef(({ className = '', ...props }, ref) => (
  <textarea
    ref={ref}
    className={`w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-slate-400 focus:ring-2 focus:ring-slate-200 ${className}`}
    {...props}
  />
));

TextArea.displayName = 'TextArea';

export const SelectInput = React.forwardRef(({ className = '', ...props }, ref) => (
  <select
    ref={ref}
    className={`w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200 ${className}`}
    {...props}
  />
));

SelectInput.displayName = 'SelectInput';
