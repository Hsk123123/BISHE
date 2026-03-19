@echo off
chcp 65001 >nul
echo ====================================
echo    技能链后端启动脚本
echo ====================================
echo.

cd /d e:\study\bishe1\skill-chain-backend

echo [1/3] 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo [错误] Java未安装或未配置环境变量
    pause
    exit /b 1
)
echo [OK] Java已安装
echo.

echo [2/3] 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo [错误] Maven未安装或未配置环境变量
    pause
    exit /b 1
)
echo [OK] Maven已安装
echo.

echo [3/3] 检查MySQL连接...
echo 正在尝试连接MySQL...
mysql -u root -p123456 -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] MySQL连接失败，请检查：
    echo   1. MySQL服务是否启动
    echo   2. 密码是否为123456
    echo   3. 数据库skill_chain是否已创建
    echo.
    echo 继续启动可能会失败，请先初始化数据库
    echo 详细步骤请查看 DATABASE_SETUP.md
    echo.
)
echo.

echo ====================================
echo    正在启动后端服务...
echo ====================================
echo.
echo 服务地址: http://localhost:8080/api
echo API文档: http://localhost:8080/api/swagger-ui.html
echo.
echo 按 Ctrl+C 停止服务
echo ====================================
echo.

call mvn spring-boot:run

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    pause
)
