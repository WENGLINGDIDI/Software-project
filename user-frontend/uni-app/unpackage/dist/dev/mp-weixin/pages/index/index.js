"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  name: "App",
  methods: {
    scanCode() {
    },
    openWallet() {
    },
    openProfile() {
      common_vendor.index.__f__("log", "at pages/index/index.vue:40", "个人中心按钮被点击");
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
        // 确保 login.vue 的路径正确
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_assets._imports_0,
    b: common_vendor.o((...args) => $options.scanCode && $options.scanCode(...args)),
    c: common_vendor.o((...args) => $options.openWallet && $options.openWallet(...args)),
    d: common_vendor.o((...args) => $options.openProfile && $options.openProfile(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
