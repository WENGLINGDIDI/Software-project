# 电动车租赁平台

基于Vue3 + uni-app开发的电动车租赁平台移动端应用。

## 功能特点

- 响应式设计，适配各种移动设备
- 支持深色/浅色主题切换
- 新人专区、骑手专区等多个功能区块
- 租赁列表展示与筛选
- 促销活动展示
- 订单管理系统

## 技术栈

- Vue 3
- uni-app
- Sass
- uni-ui组件库

## 安装

1. 克隆项目
```bash
git clone [项目地址]
cd electric-scooter-rental
```

2. 安装依赖
```bash
npm install
```

3. 运行开发环境
```bash
npm run dev
```

4. 构建生产环境
```bash
npm run build
```

## 项目结构

```
├── components/          # 组件目录
│   └── ThemeSwitch.vue # 主题切换组件
├── pages/              # 页面目录
│   └── index/         # 首页
├── static/            # 静态资源
│   ├── icons/        # 图标
│   └── images/       # 图片
├── styles/           # 样式文件
│   └── theme.scss   # 主题配置
├── package.json     # 项目配置
└── README.md       # 项目说明
```

## 主题切换

项目支持深色/浅色主题切换，可以：
1. 跟随系统设置自动切换
2. 通过界面按钮手动切换
3. 记住用户的主题偏好设置

## 开发指南

### 添加新页面

1. 在`pages`目录下创建新的页面目录
2. 在`pages.json`中注册页面路由
3. 实现页面组件

### 自定义主题

1. 在`styles/theme.scss`中添加新的主题变量
2. 在组件中使用CSS变量引用主题颜色

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 许可证

ISC 