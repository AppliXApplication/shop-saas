import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// В разработке фронт (5173) и бэкенд (8080) — разные порты.
// Прокси перенаправляет /api/* на Spring Boot, чтобы браузер не блокировал
// запросы как cross-origin и не пришлось настраивать CORS на бэкенде.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
