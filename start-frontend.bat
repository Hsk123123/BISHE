@echo off
chcp 65001 >nul
echo ====================================
echo    技能链前端启动脚本
echo ====================================
echo.

cd /d e:\study\bishe1\skill-chain-frontend

echo [1/3] 检查Node.js环境...
node --version
if %errorlevel% neq 0 (
    echo [错误] Node.js未安装或未配置环境变量
    echo.
    echo 请从以下地址下载安装Node.js:
    echo https://nodejs.org/
    echo.
    pause
    exit /b 1
)
echo [OK] Node.js已安装
echo.

echo [2/3] 检查npm环境...
npm --version
if %errorlevel% neq 0 (
    echo [错误] npm未安装
    pause
    exit /b 1
)
echo [OK] npm已安装
echo.

echo [3/3] 检查依赖...
if not exist "node_modules" (
    echo [提示] 首次运行，需要安装依赖...
    echo 正在执行 npm install...
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
    echo [OK] 依赖安装成功
    echo.
) else (
    echo [OK] 依赖已安装
    echo.
)

echo ====================================
echo    正在启动前端服务...
echo ====================================
echo.
echo 服务地址: http://localhost:3000
echo.
echo 按 Ctrl+C 停止服务
echo ====================================
echo.

call npm run dev

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    pause
)
