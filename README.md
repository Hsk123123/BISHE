# SkillChain 技能链 — 技能服务交易平台

基于 Spring Boot + Vue 3 构建的技能服务预约交易平台，支持用户浏览技能、预约下单、在线支付，商家发布服务、接单执行，管理员审核管理的完整业务闭环。

---

## 技术栈

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.1.5 | 主框架 |
| Spring Security | 6.x | 安全认证 |
| MyBatis Plus | 3.5.5 | ORM / 数据访问 |
| MySQL | 8.0 | 主数据库 |
| JWT (jjwt) | 0.12.3 | 无状态鉴权 |
| MinIO | 8.6.0 | 文件存储 |
| Redis | 6.0+ | 缓存（可选） |

### 前端
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.3.11 | 主框架 |
| TypeScript | 5.3.3 | 类型安全 |
| Vite | 5.0.8 | 构建工具 |
| Pinia | 2.1.7 | 状态管理 |
| Vue Router | 4.2.5 | 前端路由 |
| Vant | 4.8.1 | 移动端 UI |
| Element Plus | 2.4.4 | 管理后台 UI |
| Axios | 1.6.2 | HTTP 请求 |

### 设计模式 / 工程亮点
- **状态机模式**（State Pattern）：订单全生命周期管理
- **AOP 切面**：任务积分触发系统
- **事务控制**：支付/退款保证原子性
- **双权限拦截**：JWT Filter + Admin Interceptor

---

## 三种角色

| 角色 | 权限 | 说明 |
|------|------|------|
| 普通用户（role=0） | 浏览、预约、支付、评价 | 注册后默认角色 |
| 商家/服务者（role=1） | 发布技能、接单、提现 | 申请审核通过后升级 |
| 管理员（role=2） | 全平台管理 | 审核技能、订单监控、财务管理 |

---

## 核心功能

### 用户端
- 注册 / 登录（JWT，无状态）
- 技能浏览、搜索、分类筛选
- 技能详情 + 时间段预约
- 订单管理（待支付 → 待接单 → 服务中 → 待评价 → 已完成）
- 在线支付（钱包扣款 + 交易流水）
- 评价系统
- 钱包余额查看、提现申请
- 每日签到积分
- 实名认证申请

### 商家端
- 技能发布与管理
- 接单 / 开始服务 / 完成服务
- 收益统计与提现
- 排期管理（时间槽机制）

### 管理后台
- 用户管理（封禁 / 启用）
- 技能审核（上架 / 下架）
- 订单监控
- 评价管理
- 提现审批（通过 / 拒绝）
- 公告管理
- 商家入驻审核
- 财务报表

---

## 系统亮点

### 1. 订单状态机（State Pattern）

订单共 9 个状态，通过 `OrderState` 接口 + 6 个 State 实现类管理流转，杜绝非法状态跳转：

```
待支付(0) →[pay]→ 待接单(1) →[accept]→ 已接单(2)
         →[startService]→ 服务中(3) →[complete]→ 待评价(4)
         →[createReview]→ 已完成(5)
任意阶段  →[cancel/refund]→ 已取消(8) / 退款中(6) / 已退款(7)
```

核心类：`OrderStateFactory`、`PendingPaymentState`、`AcceptedState`、`InServiceState` 等。

### 2. 支付闭环（事务保证一致性）

`OrderService.payOrder()` 使用 `@Transactional`，在同一事务中完成：
1. 钱包余额扣减（`WalletService.deductCoin`）
2. 写交易流水（`TransactionLogService.createTransactionLog`）
3. 订单状态更新（`PENDING_PAYMENT → PENDING_ACCEPT`）

任意步骤异常，全部回滚。

### 3. AOP 任务积分系统

通过自定义注解 `@TaskTrigger` + `TaskAspect` 切面，在完成订单等关键动作后自动触发积分奖励，业务代码与激励逻辑完全解耦。

### 4. 双权限拦截

- `JwtAuthenticationFilter`：解析 Token，将 `userId` 注入 request attribute
- `AdminInterceptor`：拦截 `/admin/**`，校验 role = 2
- `JwtInterceptor`：拦截需登录接口，校验 Token 有效性

### 5. 双货币体系

- **CNY 挂钩币**：用于核心交易（支付、提现），严格事务控制
- **积分**：用于激励（签到、完成订单），可用于积分商城

---

## 项目结构

```
bishe1/
├── skill-chain-backend/                # Spring Boot 后端
│   └── src/main/java/com/skillchain/
│       ├── controller/                 # REST 控制器（22个）
│       ├── service/                    # 业务逻辑层（20个）
│       │   ├── state/                  # 订单状态机（6个State类）
│       │   └── impl/                   # UserDetailsService 实现
│       ├── mapper/                     # MyBatis Plus Mapper（15个）
│       ├── entity/                     # 数据库实体（15张表）
│       ├── dto/                        # 请求数据传输对象
│       ├── vo/                         # 响应视图对象
│       ├── aspect/                     # AOP 切面（任务系统）
│       ├── config/                     # Spring 配置类
│       ├── filter/                     # JWT 过滤器
│       ├── enums/                      # 枚举（OrderStatus、UserRole）
│       ├── exception/                  # 全局异常处理
│       └── common/                     # 统一响应 Result<T>
│
└── skill-chain-frontend/               # Vue 3 前端
    └── src/
        ├── api/                        # Axios 接口封装（8个模块）
        ├── views/                      # 页面（50个）
        │   └── admin/                  # 管理后台页面（11个）
        ├── store/                      # Pinia Store（user）
        ├── router/                     # Vue Router（25+路由）
        ├── utils/request.ts            # Axios 拦截器
        ├── i18n/                       # 中英文国际化
        └── components/                 # 公共组件
```

---

## 数据库主要表

| 表名 | 说明 |
|------|------|
| user | 用户账号（含角色字段） |
| wallet | 双币钱包（CNY币 + 积分） |
| skill | 技能服务信息 |
| schedule | 时间段排期 |
| order | 订单（含状态机字段） |
| review | 评价记录 |
| transaction_log | 交易流水 |
| withdrawal_request | 提现申请 |
| worker_application | 商家入驻申请 |
| task / user_task | 任务系统 |
| notice | 系统公告 |
| category | 技能分类 |

---

## 启动方式

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+（可选，不启动 Redis 可使用 `application-no-redis.yml`）

### 后端启动

**1. 初始化数据库**

```bash
mysql -u root -p
CREATE DATABASE skill_chain CHARACTER SET utf8mb4;
USE skill_chain;
source skill-chain-backend/src/main/resources/schema.sql
```

**2. 修改配置文件** `skill-chain-backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/skill_chain
    username: root
    password: 你的密码
```

**3. 启动**

```bash
cd skill-chain-backend
mvn spring-boot:run
```

后端运行在：`http://localhost:8081/api`

---

### 前端启动

```bash
cd skill-chain-frontend
npm install
npm run dev
```

前端运行在：`http://localhost:3000`

> API 代理已在 `vite.config.ts` 配置，自动转发到 `localhost:8081`，无需手动修改。

---

## 当前已知限制

- **支付为模拟支付**：钱包余额为平台内部虚拟货币，不对接真实第三方支付
- **文件上传依赖 MinIO**：未配置 MinIO 时，头像上传等功能不可用
- **签到状态存内存**：服务重启后当日签到记录清空（生产环境应改为 Redis 持久化）
- **退款为人工审核**：管理员在后台手动处理提现/退款，无自动化流程

---

## License

MIT
