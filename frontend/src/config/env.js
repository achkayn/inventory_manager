export const API_URL =
  (typeof process !== 'undefined' &&
    process.env &&
    process.env.REACT_APP_API_URL) ||
  'http://localhost:8080/api';
