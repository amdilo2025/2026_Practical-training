const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080,
    client: {
      overlay: {
        runtimeErrors: (msg) => {
          if (msg.message?.includes("ResizeObserver")) return false;
          return true;
        },
      },
    },
    proxy: {
      "/diabetes": {
        target: "http://localhost:8090",
        changeOrigin: true,
      },
    },
  },
});
