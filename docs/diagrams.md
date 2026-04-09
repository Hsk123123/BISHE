# SkillChain 核心图表（Mermaid 版）

---

## 图 1：系统总体架构图

**结构草稿**
- 客户端：手机浏览器运行 Vue3 单页应用
- 前端层：Vue3 + Vant / Element Plus + Pinia + Vue Router，Axios 附加 JWT Token 发请求
- 网络层：HTTP/JSON，所有请求前缀 /api
- 后端层：Spring Boot → JWT Filter → Controller → Service → MyBatis-Plus Mapper
- 数据层：MySQL 8.0，数据库 skill_chain

```mermaid
graph TB
    subgraph 客户端
        Browser["手机浏览器"]
    end

    subgraph 前端["前端  Vue 3 + Vite"]
        UI["Vant UI / Element Plus"]
        Store["Pinia 状态管理"]
        Router["Vue Router"]
        AxiosClient["Axios\n请求拦截器（附加 JWT）\n响应拦截器（统一错误处理）"]
    end

    subgraph 后端["后端  Spring Boot 3"]
        JWTFilter["JwtAuthenticationFilter\n解析 Token，注入 userId"]
        AdminInterceptor["AdminInterceptor\n校验 role=2（管理员路由）"]
        Controllers["Controller 层\nSkillController / OrderController\nScheduleController / WalletController 等"]
        Services["Service 层\n业务逻辑 / 订单状态机 / 事务控制"]
        Mappers["Mapper 层\nMyBatis-Plus BaseMapper"]
    end

    DB[("MySQL 8.0\nskill_chain")]

    Browser --> UI
    UI --> Store
    UI --> Router
    UI --> AxiosClient
    AxiosClient -->|"HTTP JSON /api"| JWTFilter
    JWTFilter --> AdminInterceptor
    AdminInterceptor --> Controllers
    Controllers --> Services
    Services --> Mappers
    Mappers --> DB
```

---

## 图 2：功能模块图

**结构草稿**
- 三个端：买家端 / 服务者端 / 管理端
- 买家：浏览→收藏→浏览记录→预约下单→支付→订单→评价→签到
- 服务者：发布技能→排班管理→接单处理→收益提现
- 管理：用户/技能/订单/提现/商家审核/公告

```mermaid
graph TB
    Root["SkillChain 技能链平台"]

    Root --> Buyer["买家端"]
    Root --> Provider["服务者端"]
    Root --> Admin["管理端"]

    Buyer --> B1["技能浏览\n分类筛选 / 搜索 / 推荐"]
    Buyer --> B2["技能详情\n描述 / 评价 / 可用时间段"]
    Buyer --> B3["收藏管理\n添加 / 取消 / 列表"]
    Buyer --> B4["浏览记录\n自动记录 / 清空"]
    Buyer --> B5["预约下单\n选择日期和时间段"]
    Buyer --> B6["钱包\n余额 / 充值 / 支付 / 提现"]
    Buyer --> B7["订单管理\n多状态 Tab / 我的预约"]
    Buyer --> B8["评价系统\n提交评价 / 查看评价"]
    Buyer --> B9["每日签到\n积分奖励"]

    Provider --> P1["技能管理\n发布 / 编辑 / 上下架"]
    Provider --> P2["排班管理\n添加时间段 / 删除空闲时间段"]
    Provider --> P3["技能订单处理\n接单 → 开始服务 → 完成服务"]
    Provider --> P4["收益管理\n统计 / 提现申请"]

    Admin --> A1["用户管理\n查看 / 封禁 / 启用"]
    Admin --> A2["技能审核\n上架 / 下架"]
    Admin --> A3["订单监控\n全平台订单查看"]
    Admin --> A4["提现审批\n通过 / 拒绝"]
    Admin --> A5["商家入驻审核\n通过后 role 升级为 1"]
    Admin --> A6["公告管理\n发布 / 管理系统公告"]
```

---

## 图 3：订单业务流程图

**结构草稿**
- 主路径：浏览→详情→选时间→下单→支付→接单→开始→完成→评价
- 分支：余额不足走充值，无排班需求直接下单
- 状态机：0待支付→1待接单→2已接单→3服务中→4待评价→5已完成
- 时间段状态联动：空闲(0)→锁定(2)→已预约(1)

```mermaid
flowchart TD
    A([买家登录]) --> B[浏览技能列表\n分类 / 搜索 / 推荐]
    B --> C[查看技能详情\n自动写入浏览记录]
    C --> D{是否需要排班\nscheduleRequired}

    D -->|需要排班| E[选择预约日期\n拉取服务者当日时间段]
    E --> F[选择空闲时间段]
    F --> G

    D -->|不需要排班| G[提交订单]

    G --> H["创建订单（status=0 待支付）\n时间段锁定（status: 0→2）"]
    H --> I{钱包余额是否充足}

    I -->|余额不足| J[充值后重试]
    J --> I

    I -->|余额充足| K["确认支付\n余额扣减 写交易流水\n订单: 待支付→待接单\n时间段: 锁定→已预约"]

    K --> L["服务者接单\n订单: 待接单→已接单"]
    L --> M["服务者开始服务\n订单: 已接单→服务中"]
    M --> N["服务者完成服务\n订单: 服务中→待评价"]
    N --> O["买家提交评价\n评价写入 review 表"]
    O --> P(["订单: 待评价→已完成\n积分奖励触发"])

    K -.->|买家取消| Q(["订单: 已取消\n时间段释放"])
```

---

## 图 4：数据库 ER 图

**结构草稿**
- user 1对1 wallet（每用户一个钱包）
- user 1对多 skill（服务者发布多个技能）
- user 1对多 order（买家下多个订单）
- skill 1对多 order
- wallet 1对多 transaction_log
- order 1对1 review（一订单一评价）
- user 1对多 schedule（服务者管理时间段）
- user 1对多 user_favorite / browse_history
- skill 1对多 user_favorite / browse_history
- user 1对1 worker_application
- user 1对多 withdrawal_request

```mermaid
erDiagram
    user {
        bigint userId PK
        varchar username
        varchar password
        int role "0用户 1商家 2管理员"
        varchar nickname
        varchar avatar
        int status "0正常 1封禁"
        int realNameStatus
        datetime createdAt
    }

    wallet {
        bigint walletId PK
        bigint userId FK
        decimal cnyCoinBalance "平台币余额"
        int pointBalance "积分余额"
    }

    skill {
        bigint skillId PK
        bigint providerId FK
        int categoryId
        varchar title
        text description
        decimal pricePerUnit
        int unitType "1小时 2次 3单 4月"
        int scheduleRequired "0否 1是"
        varchar mediaUrls
        int status "0待审核 1上架 2下架"
        int orderCount
        decimal avgRating
    }

    schedule {
        bigint scheduleId PK
        bigint providerId FK
        bigint skillId "可为NULL（通用时段）"
        date date
        varchar timeSlot
        int status "0空闲 1已预约 2锁定"
        int deleted
    }

    order {
        bigint orderId PK
        bigint buyerId FK
        bigint skillId FK
        bigint providerId
        int status "0-8状态机"
        decimal amount
        date scheduleDate
        varchar timeSlot
        varchar remark
        datetime createdAt
    }

    review {
        bigint reviewId PK
        bigint orderId FK
        bigint userId FK
        bigint skillId FK
        int rating "1-5星"
        text content
        datetime createTime
    }

    transaction_log {
        bigint logId PK
        bigint walletId FK
        bigint userId
        int type "1充值 2支付 3退款 4提现"
        decimal amount
        varchar description
        datetime createTime
    }

    user_favorite {
        bigint id PK
        bigint userId FK
        bigint skillId FK
        datetime createdAt
    }

    browse_history {
        bigint id PK
        bigint userId FK
        bigint skillId FK
        datetime viewedAt
    }

    worker_application {
        bigint applicationId PK
        bigint userId FK
        int status "0待审核 1通过 2拒绝"
        varchar reason
        datetime createdAt
    }

    withdrawal_request {
        bigint withdrawalId PK
        bigint userId FK
        decimal amount
        varchar bankName
        varchar bankCard
        int status "0审核中 1通过 2拒绝"
        datetime createdAt
    }

    user ||--o| wallet : "拥有钱包"
    user ||--o{ skill : "发布(provider)"
    user ||--o{ order : "下单(buyer)"
    skill ||--o{ order : "被预约"
    user ||--o{ schedule : "管理排班"
    wallet ||--o{ transaction_log : "产生流水"
    order ||--o| review : "完成后评价"
    user ||--o{ user_favorite : "收藏"
    skill ||--o{ user_favorite : "被收藏"
    user ||--o{ browse_history : "产生浏览"
    skill ||--o{ browse_history : "被浏览"
    user ||--o| worker_application : "申请成为商家"
    user ||--o{ withdrawal_request : "申请提现"
```
