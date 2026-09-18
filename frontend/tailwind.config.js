/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}", "./public/index.html"],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#eef4ff",
          100: "#dbe7fe",
          200: "#bfd4fe",
          300: "#93b6fd",
          400: "#608efa",
          500: "#3b6ff2",
          600: "#2650e6",
          700: "#1f3fd1",
          800: "#1f37a8",
          900: "#1f3384",
          950: "#161f4d"
        },
        ink: {
          50: "#f6f7f9",
          100: "#eceef2",
          200: "#d5d9e2",
          300: "#b1b9c9",
          400: "#8691a8",
          500: "#67728c",
          600: "#525b73",
          700: "#434a5e",
          800: "#3a3f4f",
          900: "#232630",
          950: "#16181f"
        }
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"]
      },
      boxShadow: {
        card: "0 1px 2px 0 rgba(16, 24, 40, 0.05), 0 1px 3px 0 rgba(16, 24, 40, 0.06)"
      }
    }
  },
  plugins: []
};
