# 比特币价格追踪器 H5 应用

这是一个使用 Go 开发的实时比特币价格追踪 H5 应用，具有以下特性：

## 功能特性

- 🚀 **实时价格更新**: 通过 WebSocket 每5秒自动更新比特币价格
- 📱 **响应式设计**: 完美适配手机、平板和桌面设备
- 💹 **价格变化显示**: 显示24小时价格变化百分比
- 🔄 **自动重连**: WebSocket 断线自动重连机制
- 🎨 **现代化UI**: 美观的渐变背景和动画效果
- ⚡ **高性能**: 使用 Gin 框架，轻量级高性能

## 技术栈

- **后端**: Go + Gin 框架
- **WebSocket**: Gorilla WebSocket
- **前端**: 原生 HTML5 + CSS3 + JavaScript
- **API**: CoinGecko 免费API
- **部署**: 单个可执行文件

## 快速开始

### 1. 安装依赖

确保你的系统已安装 Go 1.21 或更高版本。

```bash
# 下载依赖
go mod tidy
```

### 2. 运行应用

```bash
# 启动服务器
go run main.go
```

服务器将在 `http://localhost:8080` 启动。

### 3. 访问应用

打开浏览器访问：
- 主页: http://localhost:8080
- API接口: http://localhost:8080/api/price

## API 接口

### 获取当前比特币价格

```http
GET /api/price
```

响应示例：
```json
{
  "price": 43250.67,
  "currency": "USD", 
  "timestamp": "2024-01-15T10:30:00Z",
  "change_24h": 2.45
}
```

### WebSocket 实时推送

```javascript
// 连接WebSocket
const ws = new WebSocket('ws://localhost:8080/ws');

// 接收价格更新
ws.onmessage = function(event) {
    const data = JSON.parse(event.data);
    console.log('当前价格:', data.price);
};
```

## 项目结构

```
bitcoin-tracker/
├── main.go          # 主程序文件
├── go.mod           # Go模块文件
└── README.md        # 说明文档
```

## 部署

### 构建可执行文件

```bash
# Linux/Mac
go build -o bitcoin-tracker main.go

# Windows
go build -o bitcoin-tracker.exe main.go
```

### Docker 部署

创建 `Dockerfile`:

```dockerfile
FROM golang:1.21-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o main .

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/main .
EXPOSE 8080
CMD ["./main"]
```

构建和运行：

```bash
docker build -t bitcoin-tracker .
docker run -p 8080:8080 bitcoin-tracker
```

## 配置选项

可以通过环境变量配置：

- `PORT`: 服务器端口 (默认: 8080)
- `UPDATE_INTERVAL`: 价格更新间隔秒数 (默认: 5)

```bash
export PORT=3000
export UPDATE_INTERVAL=10
go run main.go
```

## 特性说明

### 实时更新机制

- 使用 WebSocket 建立持久连接
- 服务器每5秒从 CoinGecko API 获取最新价格
- 自动广播给所有连接的客户端
- 客户端断线自动重连

### 响应式设计

- 使用 CSS Grid 和 Flexbox 布局
- 支持移动设备触摸操作
- 自适应不同屏幕尺寸
- 现代化的视觉效果

### 错误处理

- API 请求超时处理
- WebSocket 连接异常处理
- 网络断线自动重连
- 优雅的错误提示

## 开发说明

### 添加新功能

1. 修改 `main.go` 添加新的 API 路由
2. 更新前端 HTML 模板
3. 测试功能是否正常工作

### 自定义样式

所有 CSS 样式都内嵌在 HTML 模板中，可以直接修改 `htmlTemplate` 常量。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！