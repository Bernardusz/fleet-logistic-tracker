/// <reference types="vitest" />

import { defineConfig } from 'vite';
import analog from '@analogjs/platform';
import tailwindcss from '@tailwindcss/vite';
import basicSsl from '@vitejs/plugin-basic-ssl'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  build: {
    target: ['es2020'],
  },
  resolve: {
    mainFields: ['module'],
    tsconfigPaths: true
  },
  plugins: [
    analog({
      ssr: false,
    }),
    tailwindcss(),
    basicSsl()
  ],
  server: {
    https: true,
  }
}));
