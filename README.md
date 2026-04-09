# SkillChain 技能链

一个基于技能共享的预约服务平台，支持技能发布、预约下单、钱包支付、评价、排班管理等完整业务闭环。

---

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.1.5 | 主框架 |
| MyBatis Plus | 3.5.5 | ORM / 数据访问 |
| MySQL | 8.0 | 主数据库 |
| JWT (jjwt) | 0.12.3 | 无状态鉴权 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.3.11 | 主框架 |
| TypeScript | 5.3.3 | 类型安全 |
| Vite | 5.0.8 | 构建工具 |
| Vant | 4.8.1 | 移动端 UI |
| Element Plus | 2.4.4 | 管理后台 UI |
| Pinia | 2.1.7 | 状态管理 |
| Axios | 1.6.2 | HTTP 请求 |

---

## 三种角色

| 角色 | role 值 | 说明 |
|------|---------|------|
| 普通用户 | 0 | 浏览、预约、支付、评价 |
| 服务者（商家） | 1 | 发布技能、接单、排班、提现 |
| 管理员 | 2 | 全平台审核与数据管理 |

---

## 核心功能

### 用户端

- 注册 / 登录（JWT 无状态鉴权）
- 技能浏览（分类筛选 / 关键字搜索 / 推荐排序）
- 技能详情查看（含评价、可用时间段）
- 收藏技能（真实数据，支持添加 / 取消 / 列表查看）
- 浏览记录（自动记录，支持清空）
- 下单预约（基于服务者排班时间段选择）
- 钱包支付（真实余额扣款，写交易流水）
- 钱包充值 / 提现申请
- 订单管理（待支付 / 待接单 / 服务中 / 待评价 / 已完成 / 退款）
- 我的预约（筛选带时间段的订单）
- 评价系统（提交评价写入真实数据，订单状态联动）
- 每日签到积分
- 实名认证申请

### 服务者端

- 发布 / 编辑技能
- 接单 → 开始服务 → 完成服务（状态机驱动）
- 收益统计与提现申请
- 排班管理：
  - 添加可预约时间段（日期 + 时间段，4 种时段选项）
  - 删除时间段（仅空闲状态可删，已预约/锁定受保护）
  - 重复时间段检测（同 provider + 日期 + 时段唯一）

### 管理后台

- 用户管理（封禁 / 启用）
- 技能审核（上架 / 下架）
- 订单监控
- 评价管理
- 提现审批（通过 / 拒绝，审批后写交易流水）
- 公告管理
- 商家入驻审核
- 财务报表

---

## 核心业务流程

### 用户预约流程

```
浏览技能列表
  → 查看技能详情（含评价 + 可用时间段）
  → 选择预约日期（基于服务者排班）
  → 选择时间段（空闲态才可选）
  → 确认下单（写订单，锁定时间段）
  → 钱包支付（扣余额，写流水，时间段状态 locked→booked）
  → 订单进入「待接单」
```

### 服务者接单流程

```
收到新订单（待接单）
  → 接单（状态：待接单 → 已接单）
  → 开始服务（状态：已接单 → 服务中）
  → 完成服务（状态：服务中 → 待评价）
```

### 评价完成流程

```
用户提交评价
  → 评价写入 review 表
  → 订单状态：待评价 → 已完成
  → 触发积分奖励（AOP TaskAspect）
```

---

## 系统设计亮点

### 1. 订单状态机（State Pattern）

订单共 9 个状态，通过 `OrderState` 接口 + 多个 State 实现类管理流转，杜绝非法状态跳转：

```
待支付(0) →[pay]→ 待接单(1) →[accept]→ 已接单(2)
          →[startService]→ 服务中(3) →[complete]→ 待评价(4)
          →[createReview]→ 已完成(5)
任意阶段  →[cancel/refund]→ 已取消(8) / 退款中(6) / 已退款(7)
```

### 2. 支付闭环（事务原子性）

`OrderService.payOrder()` 在同一事务中完成：

1. 余额扣减（`WalletService.deductCoin`）
2. 写交易流水（`TransactionLogService`）
3. 订单状态更新（PENDING_PAYMENT → PENDING_ACCEPT）
4. 排班时间段状态更新（locked → booked）

任意步骤异常，全部回滚。

### 3. 排班驱动预约

- 服务者在「日程管理」页添加可用时间段（存入 `schedule` 表，skillId 为 null 表示通用时段）
- 买家在技能详情选择日期后，实时拉取该服务者当日所有空闲时间段
- 下单时锁定时间段（status: 0→2），支付后确认（status: 2→1），全程防止重复预约

### 4. 用户行为数据记录

- `user_favorite`：收藏关系表，支持查询收藏数与收藏列表
- `browse_history`：浏览行为表，查看技能详情自动写入，upsert 更新 viewedAt，防止重复记录

### 5. AOP 任务积分系统

通过自定义注解 `@TaskTrigger` + `TaskAspect` 切面，在关键动作后自动触发积分奖励，业务逻辑与激励系统完全解耦。

### 6. 双权限拦截

- `JwtAuthenticationFilter`：解析 Token，将 `userId` 注入 request attribute
- `AdminInterceptor`：拦截 `/admin/**`，校验 role = 2

### 7. 双货币体系

- **CNY 挂钩币**：用于核心交易（支付、提现），严格事务控制
- **积分**：用于激励（签到、完成订单）

---

## 数据库主要表

| 表名 | 说明 |
|------|------|
| user | 用户账号（含角色字段） |
| wallet | 双币钱包（CNY币 + 积分） |
| skill | 技能服务信息 |
| schedule | 排班时间段（providerId + date + timeSlot，支持通用时段） |
| order | 订单（含状态机字段、scheduleDate、timeSlot） |
| review | 评价记录 |
| transaction_log | 交易流水 |
| withdrawal_request | 提现申请 |
| user_favorite | 用户收藏关系表 |
| browse_history | 浏览行为记录表 |
| worker_application | 商家入驻申请 |
| task / user_task | 任务积分系统 |
| notice | 系统公告 |
| category | 技能分类 |

---

## 项目结构

```
bishe1/
├── skill-chain-backend/                # Spring Boot 后端
│   └── src/main/java/com/skillchain/
│       ├── controller/                 # REST 控制器
│       ├── service/                    # 业务逻辑层
│       │   └── state/                  # 订单状态机实现
│       ├── mapper/                     # MyBatis Plus Mapper
│       ├── entity/                     # 数据库实体
│       ├── aspect/                     # AOP 切面（任务系统）
│       ├── config/                     # Spring 配置类
│       ├── filter/                     # JWT 过滤器
│       ├── exception/                  # 全局异常处理（400/403/404）
│       └── common/                     # 统一响应 Result<T>
│
└── skill-chain-frontend/               # Vue 3 前端
    └── src/
        ├── api/                        # Axios 接口封装
        ├── views/                      # 页面组件
        │   └── admin/                  # 管理后台页面
        ├── store/                      # Pinia Store
        ├── router/                     # Vue Router（含角色守卫）
        └── utils/request.ts            # Axios 拦截器（统一错误处理）
```

---

## 启动方式

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+（字符集 utf8mb4）

### 后端启动

**1. 初始化数据库**

```bash
mysql -u root -p
CREATE DATABASE skill_chain CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE skill_chain;
source skill-chain-backend/src/main/resources/schema.sql
```

**2. 修改配置文件** `skill-chain-backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/skill_chain?characterEncoding=utf8mb4&characterSetResults=utf8mb4
    username: root
    password: 你的密码
```

**3. 启动**

```bash
cd skill-chain-backend
mvn spring-boot:run
```

后端运行在：`http://localhost:8081/api`

### 前端启动

```bash
cd skill-chain-frontend
npm install
npm run dev
```

前端运行在：`http://localhost:3000`

> API 代理已在 `vite.config.ts` 配置，自动转发到 `localhost:8081`，无需手动修改。

---

## 项目亮点（答辩要点）

- **完整业务闭环**：从浏览、收藏、预约、支付到服务完成、评价，全链路真实数据驱动
- **排班驱动预约**：服务者主动管理可用时间段，买家基于真实排班选择预约，防止重复预约
- **真实支付逻辑**：钱包余额扣款 + 交易流水记录，支付与订单状态在同一事务中更新
- **用户行为数据**：收藏、浏览记录均写入独立表，支持统计与个性化展示
- **订单状态机**：9 个状态 + State Pattern，状态流转严格受控，无法非法跳转
- **前后端完全联调**：所有功能均对接真实接口，无 mock 数据

---

## License

MIT
