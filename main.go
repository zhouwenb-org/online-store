package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
)

// BitcoinPrice 比特币价格结构
type BitcoinPrice struct {
	Price     float64   `json:"price"`
	Currency  string    `json:"currency"`
	Timestamp time.Time `json:"timestamp"`
	Change24h float64   `json:"change_24h"`
}

// CoinGeckoResponse CoinGecko API响应结构
type CoinGeckoResponse struct {
	Bitcoin struct {
		USD           float64 `json:"usd"`
		USD24hChange  float64 `json:"usd_24h_change"`
	} `json:"bitcoin"`
}

// WebSocket升级器
var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true // 允许所有来源
	},
}

// 客户端连接管理
type Hub struct {
	clients    map[*websocket.Conn]bool
	broadcast  chan BitcoinPrice
	register   chan *websocket.Conn
	unregister chan *websocket.Conn
	mutex      sync.RWMutex
}

func newHub() *Hub {
	return &Hub{
		clients:    make(map[*websocket.Conn]bool),
		broadcast:  make(chan BitcoinPrice),
		register:   make(chan *websocket.Conn),
		unregister: make(chan *websocket.Conn),
	}
}

func (h *Hub) run() {
	for {
		select {
		case client := <-h.register:
			h.mutex.Lock()
			h.clients[client] = true
			h.mutex.Unlock()
			log.Printf("客户端已连接，当前连接数: %d", len(h.clients))

		case client := <-h.unregister:
			h.mutex.Lock()
			if _, ok := h.clients[client]; ok {
				delete(h.clients, client)
				client.Close()
			}
			h.mutex.Unlock()
			log.Printf("客户端已断开，当前连接数: %d", len(h.clients))

		case price := <-h.broadcast:
			h.mutex.RLock()
			for client := range h.clients {
				err := client.WriteJSON(price)
				if err != nil {
					log.Printf("发送消息失败: %v", err)
					delete(h.clients, client)
					client.Close()
				}
			}
			h.mutex.RUnlock()
		}
	}
}

// 获取比特币价格
func getBitcoinPrice() (*BitcoinPrice, error) {
	url := "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd&include_24hr_change=true"
	
	client := &http.Client{Timeout: 10 * time.Second}
	resp, err := client.Get(url)
	if err != nil {
		return nil, fmt.Errorf("请求失败: %v", err)
	}
	defer resp.Body.Close()

	var cgResp CoinGeckoResponse
	if err := json.NewDecoder(resp.Body).Decode(&cgResp); err != nil {
		return nil, fmt.Errorf("解析响应失败: %v", err)
	}

	return &BitcoinPrice{
		Price:     cgResp.Bitcoin.USD,
		Currency:  "USD",
		Timestamp: time.Now(),
		Change24h: cgResp.Bitcoin.USD24hChange,
	}, nil
}

// 价格更新协程
func priceUpdater(hub *Hub) {
	ticker := time.NewTicker(5 * time.Second) // 每5秒更新一次
	defer ticker.Stop()

	for range ticker.C {
		price, err := getBitcoinPrice()
		if err != nil {
			log.Printf("获取价格失败: %v", err)
			continue
		}

		log.Printf("比特币价格: $%.2f (24h变化: %.2f%%)", price.Price, price.Change24h)
		hub.broadcast <- *price
	}
}

func main() {
	// 创建Hub
	hub := newHub()
	go hub.run()
	go priceUpdater(hub)

	// 创建Gin路由
	r := gin.Default()

	// 静态文件服务
	r.Static("/static", "./static")
	
	// 主页路由
	r.GET("/", func(c *gin.Context) {
		c.Header("Content-Type", "text/html; charset=utf-8")
		c.String(200, htmlTemplate)
	})

	// WebSocket路由
	r.GET("/ws", func(c *gin.Context) {
		conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
		if err != nil {
			log.Printf("WebSocket升级失败: %v", err)
			return
		}

		hub.register <- conn

		// 发送当前价格
		go func() {
			defer func() {
				hub.unregister <- conn
			}()

			// 立即发送当前价格
			if price, err := getBitcoinPrice(); err == nil {
				conn.WriteJSON(*price)
			}

			// 保持连接
			for {
				_, _, err := conn.ReadMessage()
				if err != nil {
					break
				}
			}
		}()
	})

	// API路由 - 获取当前价格
	r.GET("/api/price", func(c *gin.Context) {
		price, err := getBitcoinPrice()
		if err != nil {
			c.JSON(500, gin.H{"error": err.Error()})
			return
		}
		c.JSON(200, price)
	})

	log.Println("服务器启动在端口 8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}

// HTML模板
const htmlTemplate = `
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>比特币价格追踪器</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            max-width: 500px;
            width: 100%;
            text-align: center;
        }

        .header {
            margin-bottom: 30px;
        }

        .title {
            font-size: 2.5rem;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }

        .subtitle {
            color: #666;
            font-size: 1.1rem;
        }

        .price-display {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border-radius: 15px;
            padding: 30px;
            margin: 30px 0;
            color: white;
            position: relative;
            overflow: hidden;
        }

        .price-display::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: linear-gradient(45deg, transparent, rgba(255,255,255,0.1), transparent);
            animation: shine 3s infinite;
        }

        @keyframes shine {
            0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
            100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
        }

        .current-price {
            font-size: 3rem;
            font-weight: bold;
            margin-bottom: 10px;
            position: relative;
            z-index: 1;
        }

        .price-change {
            font-size: 1.2rem;
            position: relative;
            z-index: 1;
        }

        .positive {
            color: #4CAF50;
        }

        .negative {
            color: #f44336;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-top: 30px;
        }

        .info-card {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
        }

        .info-label {
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 5px;
        }

        .info-value {
            color: #333;
            font-size: 1.1rem;
            font-weight: bold;
        }

        .status {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            margin-top: 20px;
            padding: 15px;
            background: #e8f5e8;
            border-radius: 10px;
            color: #2e7d32;
        }

        .status.disconnected {
            background: #ffebee;
            color: #c62828;
        }

        .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #4CAF50;
            animation: pulse 2s infinite;
        }

        .status.disconnected .status-dot {
            background: #f44336;
        }

        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.5; }
            100% { opacity: 1; }
        }

        .loading {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            color: #666;
        }

        .spinner {
            width: 20px;
            height: 20px;
            border: 2px solid #f3f3f3;
            border-top: 2px solid #667eea;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        @media (max-width: 480px) {
            .container {
                padding: 20px;
            }
            
            .title {
                font-size: 2rem;
            }
            
            .current-price {
                font-size: 2.5rem;
            }
            
            .info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1 class="title">₿ 比特币追踪器</h1>
            <p class="subtitle">实时价格监控</p>
        </div>

        <div id="loading" class="loading">
            <div class="spinner"></div>
            <span>正在连接...</span>
        </div>

        <div id="price-container" style="display: none;">
            <div class="price-display">
                <div class="current-price" id="current-price">$0.00</div>
                <div class="price-change" id="price-change">--</div>
            </div>

            <div class="info-grid">
                <div class="info-card">
                    <div class="info-label">货币</div>
                    <div class="info-value">USD</div>
                </div>
                <div class="info-card">
                    <div class="info-label">最后更新</div>
                    <div class="info-value" id="last-update">--</div>
                </div>
            </div>

            <div class="status" id="status">
                <div class="status-dot"></div>
                <span>实时连接</span>
            </div>
        </div>
    </div>

    <script>
        let ws;
        let reconnectInterval;

        function formatPrice(price) {
            return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }).format(price);
        }

        function formatChange(change) {
            const sign = change >= 0 ? '+' : '';
            return sign + change.toFixed(2) + '%';
        }

        function formatTime(timestamp) {
            return new Date(timestamp).toLocaleTimeString('zh-CN');
        }

        function updatePrice(data) {
            document.getElementById('current-price').textContent = formatPrice(data.price);
            
            const changeElement = document.getElementById('price-change');
            changeElement.textContent = '24h: ' + formatChange(data.change_24h);
            changeElement.className = 'price-change ' + (data.change_24h >= 0 ? 'positive' : 'negative');
            
            document.getElementById('last-update').textContent = formatTime(data.timestamp);
            
            // 隐藏加载状态，显示价格
            document.getElementById('loading').style.display = 'none';
            document.getElementById('price-container').style.display = 'block';
        }

        function updateStatus(connected) {
            const statusElement = document.getElementById('status');
            if (connected) {
                statusElement.className = 'status';
                statusElement.innerHTML = '<div class="status-dot"></div><span>实时连接</span>';
            } else {
                statusElement.className = 'status disconnected';
                statusElement.innerHTML = '<div class="status-dot"></div><span>连接断开</span>';
            }
        }

        function connectWebSocket() {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = protocol + '//' + window.location.host + '/ws';
            
            ws = new WebSocket(wsUrl);

            ws.onopen = function() {
                console.log('WebSocket连接已建立');
                updateStatus(true);
                if (reconnectInterval) {
                    clearInterval(reconnectInterval);
                    reconnectInterval = null;
                }
            };

            ws.onmessage = function(event) {
                const data = JSON.parse(event.data);
                updatePrice(data);
            };

            ws.onclose = function() {
                console.log('WebSocket连接已关闭');
                updateStatus(false);
                
                // 自动重连
                if (!reconnectInterval) {
                    reconnectInterval = setInterval(function() {
                        console.log('尝试重新连接...');
                        connectWebSocket();
                    }, 3000);
                }
            };

            ws.onerror = function(error) {
                console.error('WebSocket错误:', error);
                updateStatus(false);
            };
        }

        // 页面加载完成后连接WebSocket
        document.addEventListener('DOMContentLoaded', function() {
            connectWebSocket();
        });

        // 页面可见性变化时处理连接
        document.addEventListener('visibilitychange', function() {
            if (document.visibilityState === 'visible' && (!ws || ws.readyState === WebSocket.CLOSED)) {
                connectWebSocket();
            }
        });
    </script>
</body>
</html>
`