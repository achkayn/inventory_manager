import React from 'react';

const PageHeader = ({ title, description, actions }) => (
  <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
    <div>
      <p className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
        Inventory Console
      </p>
      <h2 className="mt-2 text-2xl font-semibold text-slate-900">{title}</h2>
      {description ? <p className="mt-2 max-w-3xl text-sm text-slate-600">{description}</p> : null}
    </div>
    {actions ? <div className="flex flex-wrap gap-3">{actions}</div> : null}
  </div>
);

export default PageHeader;
