# Termux千秋辅助

Termux QianQiu Assistant 是一个面向 Android 平台的超级 AI Agent 应用，定位为以 Termux 为执行核心的移动端智能开发平台。

当前仓库已经从“纯展示原型”推进到“第二阶段最小可用版雏形”。

## 当前交付

- Android 客户端源码
- 第二阶段 MVP 界面与状态流
- 可配置 OpenAI 兼容对话接口
- Termux / Termux API / Shizuku 环境检测
- Shell 命令执行与错误输出展示
- 应用工作区文件创建 / 编辑 / 删除
- SQLite 本地持久化消息、任务、日志、配置
- 架构设计文档与阶段说明文档

## 第二阶段已落地能力

- `AI 聊天`
  - 支持配置 OpenAI 兼容 `Base URL`、`API Key`、`Model`
  - 当未配置接口或接口不可用时，自动使用离线回退回复
- `任务中心`
  - 所有聊天、命令、文件操作都会生成真实任务记录
  - 支持状态、进度、步骤、重试次数、日志展示
- `Termux 检测`
  - 检测 `com.termux`
  - 检测 `com.termux.api`
  - 检测 `moe.shizuku.privileged.api`
- `命令执行`
  - 通过 `sh -c` 执行基础 Shell 命令
  - 展示 `stdout`、`stderr`、退出码、耗时
  - 失败时自动进行基础重试
- `文件操作`
  - 在应用私有工作区内创建、覆盖、删除文件
  - 支持工作区文件列表回填编辑
- `本地存储`
  - 本地 SQLite 保存消息、任务、日志和 AI 配置

## 页面结构

- `首页`
  - 实时总览、阶段能力、执行环境状态
- `AI聊天`
  - 接口配置、消息流、发送入口
- `工作台`
  - 环境检测、命令执行、文件工作区
- `任务中心`
  - 任务列表、最近日志
- `我的`
  - 当前持久化能力和下一阶段目标

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

## 后续阶段

- 第三阶段：Agent 执行版
- 第四阶段：Termux 深度集成版
- 第五阶段：智能增强版
- 第六阶段：生态版

详细架构见 `docs/architecture.md`，第二阶段路线见 `docs/phase2-roadmap.md`。
