# 🚀 技能链项目 - 完整启动指南

## ✅ 已完成配置

- ✅ MySQL密码已配置为：123456
- ✅ Redis已禁用（使用内存替代）
- ✅ 创建了启动脚本（start-backend.bat 和 start-frontend.bat）
- ✅ 创建了数据库初始化指南（DATABASE_SETUP.md）

---

## 📋 快速启动（3步）

### 第1步：初始化数据库

**方式A：使用MySQL Workbench（推荐）**

1. 打开MySQL Workbench
2. 连接到本地MySQL（用户名：root，密码：123456）
3. 执行SQL创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. 打开文件：`e:\study\bishe1\skill-chain-backend\src\main\resources\schema-english.sql`
5. 复制全部内容，在MySQL Workbench中执行

**方式B：使用命令行**

```powershell
# 进入MySQL
mysql -u root -p123456

# 在MySQL命令行中执行：
source e:/study/bishe1/skill-chain-backend/src/main/resources/schema-english.sql
```

---

### 第2步：启动后端

**方式A：双击启动脚本**

直接双击文件：`e:\study\bishe1\start-backend.bat`

**方式B：使用命令行**

```powershell
cd e:\study\bishe1\skill-chain-backend
mvn spring-boot:run
```

**等待看到以下日志表示成功：**
```
Started SkillChainApplication in X.XXX seconds
```

后端地址：`http://localhost:8080/api`

---

### 第3步：启动前端

**方式A：双击启动脚本**

直接双击文件：`e:\study\bishe1\start-frontend.bat`

**方式B：使用命令行**

```powershell
cd e:\study\bishe1\skill-chain-frontend

# 首次运行需要安装依赖
npm install

# 启动前端
npm run dev
```

**等待看到以下日志表示成功：**
```
VITE v5.0.8  ready in XXX ms

➜  Local:   http://localhost:3000/
```

前端地址：`http://localhost:3000`

---

## 🎯 访问系统

### 移动端（用户门户）
打开浏览器访问：`http://localhost:3000`

功能：
- 首页 - 浏览技能、分类
- 发现 - 搜索技能
- 订单 - 查看订单
- 我的 - 个人中心、钱包

### 后台管理
打开浏览器访问：`http://localhost:3000/admin`

功能：
- 数据看板 - 统计数据
- 用户管理 - 用户审核
- 技能审核 - 技能管理
- 订单管理 - 订单仲裁

---

## 📚 文档说明

| 文件 | 说明 |
|------|------|
| [FINAL_START.md](file:///e:/study/bishe1/FINAL_START.md) | 完整启动指南（本文档）|
| [DATABASE_SETUP.md](file:///e:/study/bishe1/DATABASE_SETUP.md) | 数据库初始化详细指南 |
| [QUICK_START.md](file:///e:/study/bishe1/QUICK_START.md) | 快速启动指南 |
| [RUN_GUIDE.md](file:///e:/study/bishe1/RUN_GUIDE.md) | 完整运行指南 |
| [PROJECT_SUMMARY.md](file:///e:/study/bishe1/PROJECT_SUMMARY.md) | 项目实现总结（用于论文）|
| [README.md](file:///e:/study/bishe1/README.md) | 项目说明文档 |

---

## 🔧 配置说明

### MySQL配置
文件：`skill-chain-backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/skill_chain
    username: root
    password: 123456  # 已配置
```

### 端口配置
```yaml
server:
  port: 8080  # 后端端口
```

前端端口：3000

### Redis配置
已禁用，使用内存替代（无需安装Redis）

---

## ❓ 常见问题

### Q1: 数据库连接失败
**错误**：`Access denied for user 'root'@'localhost'`

**解决**：
1. 确认MySQL密码是123456
2. 检查MySQL服务是否启动

### Q2: 端口被占用
**错误**：`Port 8080 was already in use`

**解决**：
1. 关闭占用8080端口的程序
2. 或修改application.yml中的端口为8081

### Q3: Maven依赖下载慢
**解决**：配置阿里云镜像（见RUN_GUIDE.md）

### Q4: 前端无法启动
**错误**：`node: command not found`

**解决**：
1. 下载安装Node.js：https://nodejs.org/
2. 重启命令行

### Q5: 数据库表创建失败
**错误**：中文注释乱码

**解决**：使用schema-english.sql（无中文注释）

---

## 📝 测试流程

### 1. 注册用户
访问：`http://localhost:3000/register`

填写：
- 用户名：testuser
- 密码：123456
- 手机号：13800138000

### 2. 登录系统
访问：`http://localhost:3000/login`

使用注册的账号登录

### 3. 浏览技能
访问：`http://localhost:3000`

查看首页和发现页的技能列表

### 4. 创建订单
点击技能详情，选择预约时间，创建订单

### 5. 查看订单
访问：`http://localhost:3000/orders`

查看订单状态变化

---

## 🎓 毕业设计提示

### 1. 功能测试
- ✅ 用户注册/登录
- ✅ 技能浏览/搜索
- ✅ 订单创建/支付
- ✅ 个人中心/钱包
- ✅ 后台管理

### 2. 截图准备
- 首页
- 发现页
- 技能详情
- 订单列表
- 个人中心
- 后台管理

### 3. 论文撰写
参考：`PROJECT_SUMMARY.md`

包含：
- 系统架构图
- ER图
- 核心代码片段
- 技术栈说明

### 4. 答辩准备
- 准备演示PPT
- 录制演示视频
- 准备常见问题回答

---

## 🎉 启动成功标志

### 后端启动成功
看到以下日志：
```
Started SkillChainApplication in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8080 (http)
```

### 前端启动成功
看到以下日志：
```
VITE v5.0.8  ready in XXX ms

➜  Local:   http://localhost:3000/
➜  Network: use --host to expose
```

---

## 📞 技术支持

如遇到问题：

1. 查看控制台日志
2. 检查数据库连接
3. 确认端口未被占用
4. 参考详细文档

---

## 🚀 一键启动（推荐）

如果所有环境都已配置好，可以直接双击：

1. `start-backend.bat` - 启动后端
2. `start-frontend.bat` - 启动前端

祝您毕业设计顺利！🎓