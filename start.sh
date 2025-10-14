#!/bin/bash

# 比特币价格追踪器启动脚本

echo "🚀 启动比特币价格追踪器..."

# 检查Go是否安装
if ! command -v go &> /dev/null; then
    echo "❌ 错误: 未找到Go，请先安装Go 1.21或更高版本"
    exit 1
fi

# 检查Go版本
GO_VERSION=$(go version | grep -o 'go[0-9]\+\.[0-9]\+' | head -1)
echo "✅ 检测到Go版本: $GO_VERSION"

# 下载依赖
echo "📦 下载依赖包..."
go mod tidy

if [ $? -ne 0 ]; then
    echo "❌ 依赖下载失败"
    exit 1
fi

# 设置端口（如果未设置）
if [ -z "$PORT" ]; then
    export PORT=8080
fi

echo "🌐 服务器将在端口 $PORT 启动"
echo "📱 访问地址: http://localhost:$PORT"
echo "🔗 API接口: http://localhost:$PORT/api/price"
echo ""
echo "按 Ctrl+C 停止服务器"
echo "==========================================="

# 启动应用
go run main.go