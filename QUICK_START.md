# 🚀 快速启动指南

## ✅ 环境检查结果

| 软件 | 版本 | 状态 |
|------|--------|------|
| Java | 17.0.4 | ✅ 已安装 |
| Maven | 3.9.6 | ✅ 已安装 |
| MySQL | 8.0.44 | ✅ 已安装 |
| Node.js | 未检测到 | ⚠️ 需要安装 |
| Redis | 未安装 | ⚠️ 可选 |

---

## 📋 快速启动步骤（3步）

### 第1步：初始化数据库

**方式A：使用MySQL Workbench（推荐）**

1. 打开MySQL Workbench
2. 连接到本地MySQL
3. 执行以下SQL：

```sql
CREATE DATABASE IF NOT EXISTS skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. 打开文件：`e:\study\bishe1\skill-chain-backend\src\main\resources\schema.sql`
5. 复制全部内容，在MySQL Workbench中执行

**方式B：使用命令行**

```powershell
# 进入MySQL（输入密码）
mysql -u root -p

# 在MySQL中执行：
source e:/study/bishe1/skill-chain-backend/src/main/resources/schema.sql
```

---

### 第2步：启动后端

```powershell
cd e:\study\bishe1\skill-chain-backend

# 启动后端服务
mvn spring-boot:run
```

**等待看到以下日志表示成功：**
```
Started SkillChainApplication in X.XXX seconds
```

后端地址：`http://localhost:8080/api`

---

### 第3步：启动前端

**如果Node.js未安装，请先安装：**

1. 下载Node.js：https://nodejs.org/
2. 安装后重启命令行
3. 验证安装：`node --version`

**启动前端：**

```powershell
cd e:\study\bishe1\skill-chain-frontend

# 安装依赖（首次运行）
npm install

# 启动前端
npm run dev
```

**等待看到以下日志表示成功：**
```
➜  Local:   http://localhost:3000/
```

前端地址：`http://localhost:3000`

---

## 🎯 访问系统

### 移动端（用户门户）
打开浏览器：`http://localhost:3000`

- 首页、发现、订单、我的

### 后台管理
打开浏览器：`http://localhost:3000/admin`

---

## 🔧 配置说明

### 修改MySQL密码

如果MySQL密码不是`root`，修改文件：

`skill-chain-backend/src/main/resources/application.yml`

第14行改为：
```yaml
password: your_password
```

### 修改端口

如果8080端口被占用，修改文件：

`skill-chain-backend/src/main/resources/application.yml`

第2行改为：
```yaml
port: 8081
```

---

## ❓ 常见问题

### Q1: MySQL连接失败
**错误**：`Access denied for user 'root'@'localhost'`

**解决**：检查`application.yml`中的密码是否正确

### Q2: 端口被占用
**错误**：`Port 8080 was already in use`

**解决**：修改`application.yml`中的端口号

### Q3: Maven下载慢
**解决**：配置阿里云镜像（见RUN_GUIDE.md）

### Q4: 前端无法启动
**解决**：确保Node.js已正确安装

---

## 📝 测试流程

1. 访问 `http://localhost:3000/register`
2. 注册一个新账号
3. 登录系统
4. 浏览技能列表
5. 创建订单
6. 查看订单状态

---

## 📚 更多帮助

详细文档请查看：`RUN_GUIDE.md`

项目总结请查看：`PROJECT_SUMMARY.md`

---

## 🎓 毕业设计提示

1. **功能测试**：确保所有功能正常运行
2. **截图准备**：截取系统运行界面
3. **论文撰写**：参考PROJECT_SUMMARY.md中的素材
4. **答辩准备**：准备演示PPT

祝您毕业顺利！🎓