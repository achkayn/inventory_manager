export const formatCurrency = (value) =>
  new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(Number(value || 0));

export const formatNumber = (value) =>
  new Intl.NumberFormat('en-US').format(Number(value || 0));

export const toTitleCase = (value) =>
  String(value || '')
    .split(' ')
    .filter(Boolean)
    .map((word) => word[0].toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
