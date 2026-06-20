# Termux千秋辅助

Termux QianQiu Assistant 是一个面向 Android 平台的超级 AI Agent 应用原型，定位为以 Termux 为执行核心的移动端智能开发平台。

## 当前交付

- Android 原型客户端源码
- 可安装 Debug APK
- 首版产品原型界面
- 架构设计文档

## APK 产物

- `app/build/outputs/apk/debug/app-debug.apk`

## 本地构建

在已经准备好 Android SDK 的环境中执行：

```bash
export JAVA_HOME=/path/to/jdk17
export ANDROID_SDK_ROOT=/path/to/android-sdk
export ANDROID_HOME=/path/to/android-sdk
gradle --no-daemon assembleDebug
```

如果网络环境要求代理，可额外设置：

```bash
export JAVA_TOOL_OPTIONS='-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=18080 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=18080'
```

## 当前原型范围

当前版本优先完成了：

- 深色风格移动端主框架
- 首页 / AI聊天 / 工作台 / 任务中心 / 个人中心
- 产品愿景与核心模块展示
- 为后续 Agent、记忆、插件、知识库扩展预留清晰架构入口

更完整的工业级设计说明见 `docs/architecture.md`。
