# Termux千秋辅助架构设计

## 1. 产品定位

Termux千秋辅助不是普通聊天 AI，也不是普通 Android Termux 工具箱，而是一个面向 Android 的超级 AI Agent 平台。

平台核心目标：

- 以聊天为入口
- 以 Agent 为调度中枢
- 以 Termux 为执行核心
- 以沙盒、安全、记忆、知识库为长期能力底座

---

## 2. 客户端架构

推荐演进为分层模块化架构：

### 2.1 模块划分

- `app`
  - Android 入口模块
- `core-ui`
  - 主题、设计系统、动效、组件库
- `core-model`
  - 通用数据模型
- `core-data`
  - Repository、数据源、缓存策略
- `feature-chat`
  - AI 对话与消息卡片
- `feature-agent`
  - Agent 状态、任务编排、执行监控
- `feature-workbench`
  - 代码卡片、编辑器、执行结果展示
- `feature-knowledge`
  - 知识库导入、解析、索引、检索
- `feature-plugin`
  - 插件市场、权限审查、安装流程
- `feature-settings`
  - 安全策略、模型设置、人格设置、开发者选项
- `service-runtime`
  - 前台服务、后台恢复、通知系统

### 2.2 UI 设计原则

- 深色模式优先
- Telegram 风格信息流
- Cursor 风格任务与代码面板
- iOS 风格转场和手势交互
- 玻璃质感卡片与大圆角
- 所有核心信息都能单手操作

---

## 3. 后端架构设计

推荐采用微服务化但可单体部署的演进式架构：

### 3.1 服务边界

- `gateway-service`
  - 统一鉴权、限流、设备接入
- `user-service`
  - 账号、角色、人设、云同步
- `agent-service`
  - 任务编排、状态机、执行策略
- `memory-service`
  - 长期记忆、时间轴、重要度评分
- `knowledge-service`
  - 文档导入、切片、嵌入、召回
- `plugin-service`
  - 插件审核、签名、版本分发、风险评级
- `model-router-service`
  - 本地模型、云模型、混合模式路由
- `task-log-service`
  - AI 行动日志、执行原因、错误记录
- `sync-service`
  - 用户资料、项目、记忆、知识库云同步

### 3.2 通讯方式

- 外部入口：HTTPS REST
- 实时状态：WebSocket
- 内部异步任务：消息队列
- 大文件导入：对象存储直传

### 3.3 推荐技术栈

- API：Kotlin Ktor / Spring Boot
- 队列：RabbitMQ / Redis Stream
- 检索：PostgreSQL + pgvector 或 Elasticsearch + 向量库
- 对象存储：MinIO / S3

---

## 4. 数据库设计

推荐主库使用 PostgreSQL。

### 4.1 核心表

- `users`
  - 用户基础资料
- `user_profiles`
  - 人设、偏好、头像、声音、角色卡信息
- `devices`
  - Android 设备、ABI、系统版本、Termux 状态
- `sessions`
  - 登录会话、刷新令牌
- `agent_tasks`
  - Agent 任务主表
- `agent_task_steps`
  - 任务拆分步骤与执行状态
- `agent_logs`
  - AI 行动日志、原因、结果、报错
- `memory_items`
  - 长期记忆条目
- `memory_summaries`
  - 周期总结、自动归档结果
- `knowledge_documents`
  - 导入文档元信息
- `knowledge_chunks`
  - 文档切片
- `knowledge_embeddings`
  - 向量索引引用
- `plugins`
  - 插件定义与权限等级
- `plugin_versions`
  - 插件版本、签名、审计状态
- `security_rules`
  - 用户安全规则
- `runtime_snapshots`
  - 执行前快照与回滚点

### 4.2 关键字段建议

- `agent_tasks`
  - `id`
  - `user_id`
  - `goal`
  - `mode`
  - `status`
  - `risk_level`
  - `current_step_id`
  - `created_at`
  - `updated_at`

- `memory_items`
  - `id`
  - `user_id`
  - `tier`
  - `importance_score`
  - `source_type`
  - `content`
  - `summary`
  - `created_at`

- `plugins`
  - `id`
  - `name`
  - `language`
  - `permission_level`
  - `risk_score`
  - `audit_status`

---

## 5. AI 模型系统设计

### 5.1 运行模式

- 本地模式
  - 优先使用 GGUF 模型在端侧推理
- 云端模式
  - 高复杂任务交给云端模型
- 混合模式
  - 本地负责常驻助手，云端负责高强度编码与规划

### 5.2 模型路由策略

- 对话短请求
  - 本地轻量模型
- 编码规划
  - 云端大模型
- 文件分析
  - 本地摘要 + 云端增强
- 隐私敏感内容
  - 优先本地

### 5.3 模型兼容目标

- Qwen
- DeepSeek
- Llama
- GGUF 自动识别与加载

---

## 6. Agent 模块设计

Agent 采用状态机 + 工具编排架构。

### 6.1 主流程

1. 接收用户目标
2. 可行性评估
3. 风险评估
4. 联网检索
5. 任务拆分
6. 生成执行计划
7. 沙盒执行
8. 读取结果
9. 自动修复
10. 成果确认与归档

### 6.2 核心子模块

- `Planner`
  - 任务分析与拆分
- `Tool Router`
  - 选择 Termux / Shizuku / Intent / 云端工具
- `Executor`
  - 执行命令、读写文件、收集输出
- `Verifier`
  - 检查结果是否达标
- `Repairer`
  - 自动修复错误并重试
- `Guard`
  - 危险操作检测与用户确认
- `Recorder`
  - 写入日志、记忆、知识库

### 6.3 模式控制

- 普通模式
  - 危险操作前确认
- 专家模式
  - 自动执行高频标准流程
- 开发者模式
  - 开放高级控制、日志、工具选择

---

## 7. Termux 集成设计

执行优先级：

1. Shizuku
2. Termux API
3. Intent

### 7.1 集成策略

- 启动时检测 Termux 是否安装
- 检测 Termux API 可用性
- 检测 Shizuku 授权状态
- 根据权限自动选路

### 7.2 能力边界

- Shell 命令执行
- 文件编辑与项目创建
- ZIP 解压
- 环境安装
- 日志回读

---

## 8. 插件系统设计

### 8.1 插件类型

- Python 插件
- Shell 插件
- NodeJS 插件
- Java 插件

### 8.2 权限等级

- 一级：读取文件
- 二级：修改文件
- 三级：执行命令
- 四级：Shizuku 权限
- 五级：Root 权限

### 8.3 安装流程

1. AI 审核
2. 风险分析
3. 用户确认
4. 沙盒安装
5. 权限授予
6. 正式启用

---

## 9. 安全与沙盒设计

### 9.1 默认禁止

- 删除系统文件
- 修改系统关键配置
- 删除用户重要数据

### 9.2 高风险策略

- 执行前快照
- 强制确认
- 沙盒先跑
- 支持任务回滚

### 9.3 审计要求

- 记录 AI 做了什么
- 记录为什么这样做
- 记录输入、输出、错误
- 记录是否触发过安全规则

---

## 10. 长期记忆系统

三级架构：

- 第一梯队
  - 用户重要资料
- 第二梯队
  - 技术知识与项目经验
- 第三梯队
  - 日常聊天记录

每条记忆追加：

- 时间戳
- 来源
- 重要度评分
- 摘要
- 关联任务

---

## 11. 页面原型设计

### 11.1 首页

- 项目定位
- 核心能力卡片
- 视觉风格标签

### 11.2 AI聊天

- 消息流
- 角色人格入口
- 模式切换

### 11.3 代码工作台

- 代码卡片
- 执行按钮
- 错误分析
- 自动修复入口

### 11.4 任务中心

- 当前任务
- 当前步骤
- 进度百分比
- 错误状态

### 11.5 个人中心

- 登录体系
- 同步状态
- 知识库入口
- 插件权限说明

---

## 12. 建议目录结构

```text
termux-qianqiu-assistant/
├── app/
│   ├── src/main/
│   └── build.gradle.kts
├── docs/
│   └── architecture.md
├── core-ui/
├── core-model/
├── core-data/
├── feature-chat/
├── feature-agent/
├── feature-workbench/
├── feature-knowledge/
├── feature-plugin/
├── feature-settings/
├── service-runtime/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 13. 当前原型说明

当前交付版本优先保证：

- 工程可编译
- APK 可生成
- UI 风格方向明确
- 核心信息架构已搭建

下一阶段建议继续实现：

- 真正的聊天会话引擎
- Termux / Shizuku 真机接入
- 本地模型管理器
- 后台前台服务与持续通知
- SQLite / Room 本地存储
- 云端同步 API
