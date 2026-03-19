# 数据库初始化指南

## 方式1：使用MySQL Workbench（推荐）

### 步骤1：创建数据库
打开MySQL Workbench，连接到本地MySQL，执行以下SQL：

```sql
CREATE DATABASE IF NOT EXISTS skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 步骤2：创建表结构
打开文件：`e:\study\bishe1\skill-chain-backend\src\main\resources\schema.sql`

复制文件中的所有SQL语句，在MySQL Workbench中执行。

或者，使用简化版本（无中文注释）：
打开文件：`e:\study\bishe1\skill-chain-backend\src\main\resources\schema-english.sql`

复制所有内容，在MySQL Workbench中执行。

---

## 方式2：使用命令行

```powershell
# 进入MySQL
mysql -u root -p123456

# 在MySQL命令行中执行：
source e:/study/bishe1/skill-chain-backend/src/main/resources/schema.sql
```

---

## 验证数据库

执行以下SQL验证表是否创建成功：

```sql
USE skill_chain;
SHOW TABLES;
```

应该看到以下12个表：
- user
- wallet
- decoration
- skill_category
- skill
- schedule
- order
- transaction_log
- task
- user_task
- review
- appeal
- system_config

---

## 常见问题

### Q1: 中文注释乱码
**原因**：命令行编码问题

**解决**：使用MySQL Workbench或使用schema-english.sql（无中文注释）

### Q2: 表已存在错误
**解决**：先删除数据库重新创建
```sql
DROP DATABASE IF EXISTS skill_chain;
CREATE DATABASE skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Q3: 密码错误
**解决**：确认MySQL密码是123456，如果不是，请修改application.yml中的密码配置

---

## 下一步

数据库初始化完成后，启动后端服务：

```powershell
cd e:\study\bishe1\skill-chain-backend
mvn spring-boot:run
```

等待看到：`Started SkillChainApplication in X.XXX seconds`