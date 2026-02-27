# 在你身边 BeSide 💕

异地恋情侣陪伴 App — 距离再远，心也在一起呢~

## 功能

- 🔗 **情侣配对** — 邀请码 / 用户ID / 二维码
- 📍 **位置共享** — 城市/区域/精确（用户自选）
- 🕐 **当地时间** — 实时显示对方时区
- 🔔 **通知读取** — 了解对方今天做了什么
- 📖 **ta的一天** — 活动时间线
- 🔒 **隐私可控** — 三档隐私等级，用户自己决定

## 技术栈

- **语言:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **后端:** Firebase (Auth + Firestore + Cloud Messaging)
- **位置:** Google Play Services Location
- **通知:** NotificationListenerService

## 项目结构

```
app/src/main/java/com/beside/app/
├── BeSideApp.kt              # Application
├── MainActivity.kt            # 入口
├── data/
│   ├── model/Models.kt       # 数据模型
│   ├── repository/            # 数据仓库
│   └── service/               # 通知监听 & 位置同步
└── ui/
    ├── Navigation.kt          # 导航
    ├── theme/Theme.kt         # 主题配色
    └── screens/
        ├── auth/              # 登录注册
        ├── home/              # 首页（对方状态）
        ├── timeline/          # ta的一天
        ├── pairing/           # 配对
        └── settings/          # 设置（隐私等级）
```

## 开始使用

### 1. 创建 Firebase 项目
1. 打开 [Firebase Console](https://console.firebase.google.com)
2. 创建新项目
3. 添加 Android 应用，包名填 `com.beside.app`
4. 下载 `google-services.json` 放到 `app/` 目录
5. 启用 **Authentication**（Email/Password）
6. 启用 **Cloud Firestore**
7. 部署安全规则（用 `firestore.rules`）

### 2. 编译运行
1. 用 Android Studio 打开项目根目录
2. 等 Gradle 同步完成
3. 连接 Android 手机或模拟器
4. 点击 Run ▶️

### 3. 测试通知读取
1. 打开 app → 设置 → 点击「去开启权限」
2. 在系统设置中找到「在你身边」并开启通知读取
3. 其他 app 收到通知时会自动上传

## 隐私等级说明

| 等级 | 通知 | 位置 |
|------|------|------|
| 🟢 低 | 仅 app 名称 | 城市级 |
| 🟡 中 | 简要摘要 | 区域级 |
| 🔴 高 | 完整内容 | 精确位置 |

用户自己选择，随时可改~

## TODO

- [ ] 二维码扫码配对
- [ ] 头像上传
- [ ] 推送通知（对方上线提醒）
- [ ] 深色模式优化
- [ ] 聊天功能（可选）
- [ ] App 图标设计
- [ ] Google Play 上架
