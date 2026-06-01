/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      boxShadow: {
        soft: '0 20px 45px -15px rgba(15, 23, 42, 0.25)',
      },
      colors: {
        ink: {
          950: '#0b1020',
        },
      },
    },
  },
  plugins: [],
};
