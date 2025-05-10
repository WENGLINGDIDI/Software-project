"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  __name: "login",
  setup(__props) {
    const username = common_vendor.ref("1");
    const password = common_vendor.ref("1");
    const message = common_vendor.ref("");
    const messageClass = common_vendor.ref("error");
    const handleLogin = async () => {
      if (!username.value || !password.value) {
        message.value = "请输入用户名和密码";
        messageClass.value = "error";
        return;
      }
      try {
        common_vendor.index.__f__("log", "at pages/login/login.vue:30", "click!");
        const response = await common_vendor.axios.post("https://192.168.126.147:8088/user/login", {
          email: username.value,
          password: password.value,
          role: "user"
        }, {
          headers: { "Content-Type": "application/json" }
        });
        common_vendor.index.__f__("log", "at pages/login/login.vue:38", response);
        if (response.data.statusCode === 200) {
          message.value = "登录成功！";
          messageClass.value = "success";
          localStorage.setItem("user", JSON.stringify(response.data.data));
        } else {
          message.value = "用户名或密码错误";
          messageClass.value = "error";
        }
      } catch (error) {
        message.value = "登录请求失败，请检查网络";
        messageClass.value = "error";
      }
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: username.value,
        b: common_vendor.o(($event) => username.value = $event.detail.value),
        c: password.value,
        d: common_vendor.o(($event) => password.value = $event.detail.value),
        e: common_vendor.o(handleLogin),
        f: common_vendor.o((...args) => _ctx.handleSign && _ctx.handleSign(...args)),
        g: message.value
      }, message.value ? {
        h: common_vendor.t(message.value),
        i: common_vendor.n(messageClass.value)
      } : {});
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
