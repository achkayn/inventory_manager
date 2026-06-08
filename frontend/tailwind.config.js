/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      boxShadow: {
        soft: '0 20px 45px -15px rgba(15, 23, 42, 0.25)',
        card: '0 1px 3px 0 rgba(0,0,0,0.05), 0 1px 2px -1px rgba(0,0,0,0.04)',
        'green-glow': '0 8px 32px -4px rgba(21, 128, 61, 0.38)',
      },
    },
  },
  plugins: [],
};
