# 🚀 项目运行指南

## 一、环境准备

### 1.1 必需软件
- ✅ **JDK 17+** - 已检测到
- ✅ **MySQL 8.0+** - 已安装（版本8.0.44）
- ⚠️ **Node.js 18+** - 需要检查
- ⚠️ **Maven 3.6+** - 需要检查
- ⚠️ **Redis 6.0+** - 可选（已提供无Redis配置）

### 1.2 检查环境

```powershell
# 检查Java版本
java -version

# 检查Maven版本
mvn -version

# 检查Node.js版本
node -version

# 检查MySQL版本
mysql --version
```

---

## 二、数据库初始化

### 2.1 方式1：使用MySQL Workbench（推荐）

1. 打开MySQL Workbench
2. 连接到本地MySQL服务器
3. 执行以下SQL命令：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE skill_chain;
```

4. 打开文件：`e:\study\bishe1\skill-chain-backend\src\main\resources\schema.sql`
5. 复制文件内容，在MySQL Workbench中执行

### 2.2 方式2：使用命令行

```powershell
# 进入MySQL（请替换为您的密码）
mysql -u root -p

# 在MySQL命令行中执行：
source e:/study/bishe1/skill-chain-backend/src/main/resources/schema.sql

# 或者直接执行：
mysql -u root -p < e:/study/bishe1/skill-chain-backend/src/main/resources/schema.sql
```

### 2.3 修改数据库密码

如果您的MySQL密码不是`root`，请修改配置文件：

打开文件：`skill-chain-backend/src/main/resources/application.yml`

修改第14行：
```yaml
password: your_mysql_password  # 改为您的MySQL密码
```

---

## 三、启动后端服务

### 3.1 方式1：使用Maven命令（推荐）

```powershell
cd e:\study\bishe1\skill-chain-backend

# 清理并编译
mvn clean compile

# 启动服务
mvn spring-boot:run
```

### 3.2 方式2：使用IDE（IntelliJ IDEA）

1. 用IntelliJ IDEA打开项目：`skill-chain-backend`
2. 等待Maven依赖下载完成
3. 找到主类：`com.skillchain.SkillChainApplication`
4. 右键 → Run 'SkillChainApplication'

### 3.3 方式3：打包后运行

```powershell
cd e:\study\bishe1\skill-chain-backend

# 打包
mvn clean package

# 运行jar包
java -jar target/skill-chain-backend-1.0.0.jar
```

### 3.4 验证后端启动成功

看到以下日志表示启动成功：
```
Started SkillChainApplication in X.XXX seconds
```

后端服务地址：`http://localhost:8080/api`

---

## 四、启动前端服务

### 4.1 安装依赖

```powershell
cd e:\study\bishe1\skill-chain-frontend

# 安装npm依赖
npm install
```

如果安装速度慢，可以使用淘宝镜像：
```powershell
npm install --registry=https://registry.npmmirror.com
```

### 4.2 启动开发服务器

```powershell
# 启动开发服务器
npm run dev
```

### 4.3 验证前端启动成功

看到以下日志表示启动成功：
```
VITE v5.0.8  ready in XXX ms

➜  Local:   http://localhost:3000/
```

前端服务地址：`http://localhost:3000`

---

## 五、访问系统

### 5.1 移动端（用户门户）

打开浏览器访问：`http://localhost:3000`

- **首页**：浏览技能、分类
- **发现**：搜索技能
- **订单**：查看我的订单
- **我的**：个人中心、钱包

### 5.2 后台管理

打开浏览器访问：`http://localhost:3000/admin`

需要管理员权限登录（需要在数据库中手动创建管理员账号）

### 5.3 API接口测试

使用Postman或浏览器访问：
- GET `http://localhost:8080/api/skill/list` - 获取技能列表
- POST `http://localhost:8080/api/auth/register` - 注册用户

---

## 六、常见问题

### 6.1 MySQL连接失败

**错误信息**：`Access denied for user 'root'@'localhost'`

**解决方案**：
1. 检查MySQL密码是否正确
2. 修改`application.yml`中的密码配置

### 6.2 端口被占用

**错误信息**：`Port 8080 was already in use`

**解决方案**：
1. 修改`application.yml`中的端口：
```yaml
server:
  port: 8081  # 改为其他端口
```

2. 或者关闭占用8080端口的程序

### 6.3 Maven依赖下载慢

**解决方案**：
配置Maven阿里云镜像，修改`settings.xml`：

```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <name>Aliyun Maven</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### 6.4 npm install失败

**解决方案**：
```powershell
# 清除缓存
npm cache clean --force

# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
npm install
```

### 6.5 Redis连接失败（如果启用Redis）

**错误信息**：`Unable to connect to Redis`

**解决方案**：
1. 确保Redis服务已启动
2. 或者使用无Redis配置文件：
   - 修改启动参数使用`application-no-redis.yml`
   - 或在`application.yml`中注释掉Redis配置

---

## 七、快速启动脚本

### 7.1 Windows批处理脚本

创建文件 `start-backend.bat`：

```batch
@echo off
echo Starting Skill Chain Backend...
cd e:\study\bishe1\skill-chain-backend
mvn spring-boot:run
pause
```

创建文件 `start-frontend.bat`：

```batch
@echo off
echo Starting Skill Chain Frontend...
cd e:\study\bishe1\skill-chain-frontend
npm run dev
pause
```

双击运行即可启动！

### 7.2 PowerShell脚本

创建文件 `start-all.ps1`：

```powershell
# 启动后端
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd e:\study\bishe1\skill-chain-backend; mvn spring-boot:run"

# 等待5秒
Start-Sleep -Seconds 5

# 启动前端
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd e:\study\bishe1\skill-chain-frontend; npm run dev"
```

---

## 八、测试账号

### 8.1 创建测试用户

方式1：通过注册页面
- 访问：`http://localhost:3000/register`
- 填写用户名、密码、手机号

方式2：直接在数据库插入
```sql
INSERT INTO user (username, password, phone, role, real_name_status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKI', '13800138000', 2, 2);
```

### 8.2 默认测试账号

| 用户名 | 密码 | 角色 | 说明 |
|---------|--------|------|------|
| testuser | 123456 | 普通用户 | 需要注册 |
| worker | 123456 | 工作者 | 需要注册 |
| admin | admin123 | 管理员 | 需要手动创建 |

---

## 九、开发调试

### 9.1 后端调试

在IntelliJ IDEA中：
1. 在代码行号左侧点击，设置断点
2. 以Debug模式运行
3. 触发接口，程序会在断点处暂停

### 9.2 前端调试

在浏览器中：
1. 按F12打开开发者工具
2. 切换到Console标签查看日志
3. 切换到Network标签查看网络请求

---

## 十、项目结构说明

### 后端主要文件

```
skill-chain-backend/
├── src/main/java/com/skillchain/
│   ├── SkillChainApplication.java  # 启动类
│   ├── controller/               # 控制器（10个）
│   ├── service/                  # 服务层（12个）
│   ├── mapper/                   # 数据访问层（10个）
│   ├── entity/                   # 实体类（11个）
│   ├── config/                   # 配置类（6个）
│   └── ...
└── src/main/resources/
    ├── application.yml            # 主配置文件
    └── schema.sql               # 数据库脚本
```

### 前端主要文件

```
skill-chain-frontend/
├── src/
│   ├── main.ts                 # 入口文件
│   ├── App.vue                 # 根组件
│   ├── router/                 # 路由配置
│   ├── store/                  # 状态管理
│   ├── api/                    # API接口
│   ├── views/                  # 页面组件（12个）
│   └── ...
├── package.json
└── vite.config.ts
```

---

## 十一、下一步

1. ✅ 完成数据库初始化
2. ✅ 启动后端服务
3. ✅ 启动前端服务
4. ✅ 访问系统进行测试
5. 📝 开始编写毕业论文
6. 🎯 准备答辩材料

---

## 十二、技术支持

如遇到问题，请检查：

1. **日志文件**：查看控制台输出
2. **数据库**：确认表已创建
3. **端口**：确认端口未被占用
4. **依赖**：确认所有依赖已下载

祝您毕业设计顺利！🎓