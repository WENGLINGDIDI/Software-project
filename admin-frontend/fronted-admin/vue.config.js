const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    host: 'localhost',
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://192.168.144.147:8088',
        changeOrigin: true,
        logLevel: 'debug'
      }
    }
  }
})
