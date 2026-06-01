import React from 'react';

const StatCard = ({ label, value, helper, tone = 'slate' }) => {
  const tones = {
    slate: 'from-slate-900 to-slate-700',
    emerald: 'from-emerald-600 to-emerald-500',
    amber: 'from-amber-500 to-orange-500',
    sky: 'from-sky-600 to-cyan-500',
  };

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className={`mb-4 h-2 w-14 rounded-full bg-gradient-to-r ${tones[tone] || tones.slate}`} />
      <p className="text-sm font-medium text-slate-500">{label}</p>
      <p className="mt-2 text-3xl font-semibold text-slate-900">{value}</p>
      {helper ? <p className="mt-2 text-sm text-slate-500">{helper}</p> : null}
    </div>
  );
};

export default StatCard;
