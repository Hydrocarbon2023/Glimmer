# 🌊 Glimmer (微光漂流)

**v1.0.1 2026.1**

> 在数字的海洋里，寻找来自远方的微光。
>
> In the digital ocean, finding a glimmer from afar.

**Glimmer** 是一款基于 **Android (Jetpack Compose)** 和 **Firebase** 构建的匿名漂流瓶社交应用。用户可以将自己的心事写进瓶子扔进大海，也可以在沙滩上捡拾陌生人的漂流瓶，进行点赞、回复和互动。

------

## ✨ 主要功能 (Features)

- **📱 沉浸式海洋界面**
    - 采用全新的“热带海洋”配色方案 (Tropical Theme)，营造轻松治愈的视觉体验。
    - 动态漂浮动画：瓶子会在屏幕上随波逐流，带来真实的漂流感。
- **☁️ 云端同步 (Firebase)**
    - **身份认证**：支持邮箱/账号注册与登录，数据安全上云。
    - **实时数据库**：漂流瓶数据存储于 Firestore，实现全球互通。
    - **开国大典**：若数据库为空，系统会自动注入经典漂流瓶（如“红烧肉”、“概率论求过”等）作为初始内容。
- **💌 社交互动**
    - **扔瓶子**：写下心事，随机漂向远方。
    - **捡瓶子**：每日限制捡拾次数（防沉迷），探索未知。
    - **点赞**：实心红心反馈，云端原子计数，防止无限刷赞。
    - **回复与聊天**：支持对感兴趣的瓶子进行回复，进入独立的聊天界面。
- **🔔 通知系统**
    - 主页铃铛入口，实时查看收到的喜欢和回复（目前基于本地模拟）。

## 🛠️ 技术栈 (Tech Stack)

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose (Material Design 3)
- **架构模式**: MVVM (Model-View-ViewModel)
- **后端服务 (BaaS)**: Google Firebase
    - **Firebase Authentication**: 用户注册与登录管理
    - **Cloud Firestore**: NoSQL 云数据库，存储漂流瓶数据
- **异步处理**: Kotlin Coroutines & Flow
- **导航**: Jetpack Navigation Compose

## 🚀 快速开始 (Getting Started)

### 1. 克隆项目

Bash

```
git clone https://github.com/your-username/glimmer.git
```

### 2. 配置 Firebase 环境 (关键步骤)

本项目依赖 Firebase 服务，你需要配置自己的 Firebase 项目：

1. 访问 [Firebase Console](https://console.firebase.google.com/) 创建一个新项目。
2. 添加 Android 应用，包名需与 `app/build.gradle.kts` 中的 `applicationId` 一致（例如 `com.cocos.glimmer`）。
3. 下载 `google-services.json` 文件，并将其放置在项目的 `app/` 根目录下。
4. 在 Firebase 控制台中：
    - **Authentication**: 启用 **Email/Password** 登录方式。
    - **Firestore Database**: 创建数据库并设置为 **Test Mode**（或配置相应的读写规则）。

### 3. 构建与运行

使用 Android Studio (推荐最新版) 打开项目，等待 Gradle Sync 完成后，点击 **Run** 即可在模拟器或真机上运行。

> **注意**: 请确保模拟器或真机已连接互联网，且安装了 Google Play Services（模拟器需选择带 Play Store 图标的镜像）。

## 📂 项目结构 (Project Structure)

Plaintext

```
com.cocos.glimmer
├── data
│   └── model
│       ├── Bottle.kt         # 漂流瓶数据模型
│       └── SimulationDB.kt   # 本地模拟数据库 (处理聊天/通知)
├── ui
│   ├── theme                 # 主题配置 (Color, Type, Theme)
│   └── components            # 通用 UI 组件
├── AuthManager.kt            # Firebase 认证管理 (登录/注册)
├── OceanViewModel.kt         # 核心 ViewModel (处理 Firestore 数据流)
├── MainActivity.kt           # 主入口 & 海洋主界面 (OceanScreen)
├── AuthScreens.kt            # 登录与注册界面
└── ChatScreen.kt             # 聊天与通知界面
```

## 📝 待办事项 (To-Do List)

- [x] 完成 UI 从深海风向热带风的改版。
- [x] 接入 Firebase Auth 和 Firestore 实现漂流瓶云同步。
- [x] 修复点赞逻辑（防刷赞 + 实心图标）。
- [ ] **迁移聊天记录**：将 `SimulationDB` 中的聊天数据迁移至 Firestore 子集合。
- [ ] **迁移通知系统**：实现云端推送通知。
- [ ] 增加“我的瓶子”列表页。