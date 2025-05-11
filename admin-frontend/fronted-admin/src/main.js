import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/en'
import 'element-ui/lib/theme-chalk/index.css'
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import '@fortawesome/fontawesome-free/css/all.css'
import '@fortawesome/fontawesome-free/css/regular.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import request from './utiles/request'

Vue.config.productionTip = false
Vue.use(ElementUI, { locale })

Vue.prototype.$request = request

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
