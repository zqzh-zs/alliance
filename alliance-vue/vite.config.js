import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 导入对应包

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

import ElementPlus from 'unplugin-element-plus/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [
        ElementPlusResolver({ importStyle: 'sass' })
      ],
    }),
    Components({
      resolvers: [
        ElementPlusResolver({ importStyle: 'sass' })
      ],
    }),
    // 按需定制主题配置
    ElementPlus({
      useSource: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 自动导入定制化样式文件进行样式覆盖
        additionalData: `
          @use "@/assets/css/index.scss" as *;
        `,
      }
    }
  },
  server: {
    proxy: {
      '/files': {
        target: 'http://localhost:8080', // 你的 Spring Boot 后端地址
        changeOrigin: true,
        rewrite: path => path.replace(/^\/files/, '/files')
      },
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/news': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
	  '/uploads': {
	      target: 'http://localhost:8080',
	      changeOrigin: true,
	    }
    }
  }
})
