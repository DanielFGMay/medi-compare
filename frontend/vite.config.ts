import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Configuración de Vite para el frontend de Farmacompara
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3002,
    // Proxy para redirigir API al backend local
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  // Base URL para despliegue en Cloudflare Pages
  base: '/',
  build: {
    outDir: 'dist',
    sourcemap: true,
  },
});