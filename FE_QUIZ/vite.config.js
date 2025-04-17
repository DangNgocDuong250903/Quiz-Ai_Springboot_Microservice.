import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());
  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: [{ find: "~", replacement: resolve(__dirname, "./src") }]
    },
    server: {
      port: 5173
    }
  }
})



