/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: '#1e3a8a',
          50: '#eef2ff',
          100: '#e0e7ff',
          500: '#3b82f6',
          600: '#1e3a8a',
          700: '#1e2a78',
        },
      },
    },
  },
  plugins: [],
};
