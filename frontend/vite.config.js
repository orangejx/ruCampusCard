import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  // 构建配置
  build: {
    // 输出目录设置为Spring Boot的静态资源目录
    outDir: '../src/main/resources/static',
    // 生成静态资源的存放目录
    assetsDir: 'assets',
    // 小于此阈值的导入或引用资源将内联为base64编码
    assetsInlineLimit: 4096,
    // 启用/禁用CSS代码拆分
    cssCodeSplit: true,
    // 构建后是否生成source map文件
    sourcemap: false,
    // 自定义底层的Rollup打包配置
    rollupOptions: {
      output: {
        // 入口文件名
        entryFileNames: 'assets/[name].[hash].js',
        // 块文件名
        chunkFileNames: 'assets/[name].[hash].js',
        // 资源文件名
        assetFileNames: 'assets/[name].[hash].[ext]'
      }
    }
  },
  // 开发服务器配置
  server: {
    port: 5173,
    proxy: {
      // 开发时API请求代理
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
