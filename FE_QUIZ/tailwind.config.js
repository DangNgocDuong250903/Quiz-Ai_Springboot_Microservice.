/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        bgColor: "rgb(var(--color-bg) / <alpha-value>)",
        bgSearch: "rgb(var(--color-bgSearch) / <alpha-value>)",
        primary: "rgb(var(--color-primary) / <alpha-value>)",
        bluePrimary: "rgb(var(--color-bluePrimary) / <alpha-value>)",
        bgStandard: "rgb(var(--color-ascent1) / <alpha-value>)",
        textStandard: "rgb(var(--color-standard) / <alpha-value>)",
        secondary: "rgb(var(--color-secondary) / <alpha-value>)",
        blue: "rgb(var(--color-blue) / <alpha-value>)",
        ascent: {
          1: "rgb(var(--color-ascent1) / <alpha-value>)",
          2: "rgb(var(--color-ascent2) / <alpha-value>)",
        },
        hoverItem: "rgb(var(--color-hoverItem) / <alpha-value>)",
      },
      borderColor: {
        'borderNewFeed': "rgb(var(--color-border) / <alpha-value>)",
      },
      boxShadow: {
        'newFeed': '0px 0px 12px 0px rgba(0, 0, 0, 0.04) '
      },
      flexGrow: {
        3: '3',
        1: '1'
      },
      borderWidth: {
        '1': "0.8px"
      }
    },
    screens: {
      sm: "640px",

      md: "768px",

      lg: "1024px",

      xl: "1280px",

      "2xl": "1536px",
    },
  },
  plugins: [],
}

