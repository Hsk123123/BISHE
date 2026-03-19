# 技能链 - 游戏化技能共享预约平台

## 项目简介

基于 Spring Boot + Vue 3 的个人工作室预约服务平台，采用双币经济模型、游戏化任务激励、即时预约排期算法等核心亮点。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis
- JWT
- MinIO (文件存储)

### 前端
- Vue 3.3.11
- TypeScript
- Vite 5.0.8
- Vant UI (移动端)
- Element Plus (后台管理)
- Pinia (状态管理)
- Vue Router 4.2.5
- Axios

## 项目结构

```
skill-chain/
├── skill-chain-backend/          # 后端项目
│   ├── src/main/java/com/skillchain/
│   │   ├── controller/           # 控制器层
│   │   ├── service/              # 服务层
│   │   ├── mapper/               # 数据访问层
│   │   ├── entity/               # 实体类
│   │   ├── dto/                  # 数据传输对象
│   │   ├── vo/                   # 视图对象
│   │   ├── config/               # 配置类
│   │   ├── utils/                # 工具类
│   │   ├── aspect/               # 切面
│   │   ├── enums/                # 枚举
│   │   ├── exception/            # 异常
│   │   └── common/               # 公共类
│   └── src/main/resources/
│       ├── application.yml       # 配置文件
│       └── schema.sql            # 数据库脚本
│
└── skill-chain-frontend/         # 前端项目
    ├── src/
    │   ├── api/                  # API接口
    │   ├── assets/               # 静态资源
    │   ├── components/           # 组件
    │   ├── router/               # 路由
    │   ├── store/                # 状态管理
    │   ├── types/                # 类型定义
    │   ├── utils/                # 工具类
    │   ├── views/                # 页面
    │   │   ├── admin/            # 后台管理页面
    │   │   ├── Home.vue          # 首页
    │   │   ├── Discover.vue      # 发现页
    │   │   ├── SkillDetail.vue   # 技能详情
    │   │   ├── Orders.vue        # 订单页
    │   │   ├── Profile.vue       # 个人中心
    │   │   ├── Wallet.vue        # 钱包
    │   │   ├── Login.vue         # 登录
    │   │   └── Register.vue      # 注册
    │   ├── App.vue               # 根组件
    │   └── main.ts               # 入口文件
    ├── package.json
    ├── vite.config.ts
    └── tsconfig.json
```

## 数据库设计

### 核心表结构

1. **user** - 用户表
2. **wallet** - 钱包表（双币系统：CNY挂钩币 + 积分）
3. **decoration** - 虚拟装饰表
4. **skill** - 技能表
5. **schedule** - 排期表（时间槽机制）
6. **order** - 订单表
7. **transaction_log** - 交易流水表
8. **task** - 任务字典表
9. **user_task** - 用户任务进度表
10. **review** - 评价表
11. **appeal** - 申诉表
12. **system_config** - 系统配置表

## 核心功能

### 1. 用户门户模块
- 用户注册/登录（JWT认证）
- 个人中心（资料管理、实名认证）
- 虚拟装饰系统（头像框、头衔）
- 钱包系统（双币：CNY挂钩币 + 积分）
- 每日签到（Redis BitMap）
- 技能搜索与浏览
- 预约下单
- 订单管理
- 评价系统

### 2. 个人工作者模块
- 入驻与认证
- 技能发布与管理
- 排期与日历管理（时间槽Time Slot）
- 订单处理（接单/拒单/核销）
- 收益与提现（含手续费）
- 数据看板
- 客户维护

### 3. 经济运营模块
- **双币引擎**：
  - CNY挂钩币：用于核心交易，严格事务控制
  - 积分：通胀模型，用于购买虚拟装饰
- **任务系统**（AOP实现）：
  - 每日登录
  - 浏览技能
  - 完成订单
  - 发布评价
- 积分商城

### 4. 后台管理模块
- 用户管理与审核
- 技能内容审核
- 订单监控与仲裁
- 财务报表与提现审批
- 系统配置管理

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 后端启动

1. 创建数据库并执行SQL脚本：
```bash
mysql -u root -p < skill-chain-backend/src/main/resources/schema.sql
```

2. 修改配置文件 `application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/skill_chain
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

3. 启动后端服务：
```bash
cd skill-chain-backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

### 前端启动

1. 安装依赖：
```bash
cd skill-chain-frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## 技术亮点

1. **双币经济模型**
   - CNY挂钩币：严格事务控制，用于核心交易
   - 积分：通胀模型，用于娱乐消费

2. **游戏化任务系统**
   - 使用Spring AOP实现任务触发
   - 自动发放积分奖励
   - Redis BitMap实现每日签到

3. **即时预约排期算法**
   - 时间槽（Time Slot）机制
   - 可视化日历组件
   - 并发安全控制

4. **状态机订单管理**
   - 使用状态模式管理订单状态流转
   - 支持退款、仲裁等复杂场景

5. **虚拟装饰系统**
   - 用户可购买和佩戴头像框、头衔
   - 前端CSS覆盖或Canvas合成

## 开发进度

- [x] 项目初始化与环境搭建
- [x] 数据库设计与创建
- [x] 用户认证与权限模块
- [ ] 个人中心模块
- [ ] 技能与排期模块
- [ ] 订单与支付模块
- [ ] 游戏化任务系统
- [ ] 后台管理模块
- [ ] 性能优化与测试

## API文档

### 认证接口
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- GET `/api/auth/info` - 获取用户信息
- PUT `/api/auth/profile` - 更新用户资料

### 技能接口
- GET `/api/skill/list` - 获取技能列表
- GET `/api/skill/{id}` - 获取技能详情
- POST `/api/skill/publish` - 发布技能
- PUT `/api/skill/{id}` - 更新技能

### 订单接口
- POST `/api/order/create` - 创建订单
- GET `/api/order/list` - 获取订单列表
- PUT `/api/order/{id}/accept` - 接单
- PUT `/api/order/{id}/complete` - 完成订单

## 论文素材

### 系统架构图
- 前后端分离架构
- 微服务架构设计

### ER图
- 用户-技能-订单关系图
- 钱包-交易流水关系图

### 核心代码片段
- AOP任务切面实现
- 支付事务控制
- 时间槽排期算法
- 状态机订单管理

## 许可证

MIT License

## 联系方式

如有问题，请联系：your-email@example.com