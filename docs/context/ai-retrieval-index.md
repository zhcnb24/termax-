# AI 检索索引

## 1. 目标

本文件是给 AI 或后续开发者使用的统一检索入口。

在开始阅读代码、修改架构、发布版本之前，应优先查看本索引，减少重复决策和偏离产品方向的风险。

## 2. 优先阅读顺序

### 第一级：产品与总规则

1. `docs/product/product-charter.md`
2. `docs/rules/project-core-rules.md`

作用：

- 判断项目到底是什么
- 判断哪些方向不能偏

### 第二级：执行与安全规则

1. `docs/rules/ai-execution-rules.md`
2. `docs/rules/security-rules.md`
3. `docs/rules/update-rules.md`

作用：

- 判断 AI 应该怎么执行
- 判断哪些操作需要确认
- 判断版本记录应该怎么补

### 第三级：架构与阶段信息

1. `docs/architecture.md`
2. `docs/phase2-roadmap.md`

作用：

- 理解当前实现到哪一步
- 理解长期结构与短期范围

### 第四级：版本记录

1. `docs/releases/`

作用：

- 理解最近做了什么
- 理解改动范围和风险

### 第五级：架构决策

1. `docs/adr/`

作用：

- 理解关键方案为什么这样定

## 3. 按问题检索

### 如果想知道产品方向

查：

- `docs/product/product-charter.md`
- `docs/rules/project-core-rules.md`

### 如果想知道 AI 该怎么做事

查：

- `docs/rules/ai-execution-rules.md`
- `docs/rules/security-rules.md`

### 如果想知道每次更新必须补什么

查：

- `docs/rules/update-rules.md`
- `docs/releases/`

### 如果想知道当前版本实现到了哪里

查：

- `README.md`
- `docs/phase2-roadmap.md`
- `docs/releases/`

### 如果想知道整体架构怎么演进

查：

- `docs/architecture.md`
- `docs/adr/`

## 4. AI 工作建议

每次开始任务时，建议最少完成以下动作：

1. 先看本索引
2. 再看产品总纲
3. 再看相关规则
4. 再看对应版本记录
5. 最后才开始修改代码

## 5. 维护要求

新增重要文档后，应同步更新本索引，否则会导致后续检索链断裂。
