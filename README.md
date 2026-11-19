# 跨境商城

> 前后端分离的跨境零售演示系统 · By [LazySheep101](https://github.com/LazySheep101)

基于 **Spring Boot 3.5 + Vue 3** 的交易型商城：商品 / SKU、购物车、下单、模拟支付、超时关单，以及 Redis Lua 秒杀。

## 先读哪份文档

| 你想了解 | 打开 |
|----------|------|
| **分层总览** | [docs/项目分层介绍.md](docs/项目分层介绍.md) |
| 建库 | [docs/数据库初始化说明.md](docs/数据库初始化说明.md) |
| 产品范围 | [docs/完整跨境商城产品规划.md](docs/完整跨境商城产品规划.md) |
| 落地勾选 | [docs/完整商城落地清单.md](docs/完整商城落地清单.md) |
| 改造对照 | [docs/改造总览.md](docs/改造总览.md) |

| 子目录 | 说明 | 技术 |
|--------|------|------|
| [`crossborder-mall-backend`](./crossborder-mall-backend) | 后端 | Spring Boot 3.5、Java 21、MyBatis-Plus、JWT、Redis、RabbitMQ |
| [`crossborder-mall-frontend`](./crossborder-mall-frontend) | 前端 | Vue 3、Vite、Pinia、Element Plus |

## 功能概览

**买家**：商品浏览与搜索、购物车、收货地址、下单、模拟支付、订单、确认收货、限时秒杀  

**管理端**（管理员手机号登录后）：商品 / SKU、分类、订单发货、秒杀活动与库存预热、看板

演示管理员：把 `.env` / 配置里的 `ADMIN_PHONE` 设成你用来登录的手机号（种子里常用 `13800138000`）。验证码打印在后端日志，不发送真实短信。

## 目录结构

```text
crossborder-mall/
├── README.md
├── docs/                        # 分层与产品说明
├── docker-compose.yml
├── .env.example
├── crossborder-mall-backend/    # 后端与 SQL
└── crossborder-mall-frontend/   # 前端
```

## 快速开始

### 环境要求

- JDK **21**、Maven **3.8+**
- Node.js **18+**（推荐 20 LTS）
- MySQL **8.0**、Redis、RabbitMQ

### 后端

```bash
cd crossborder-mall-backend

cp src/main/resources/application.yml.example \
   src/main/resources/application.yml

cp src/main/resources/application-dev.yml.example \
   src/main/resources/application-dev.yml
```

创建库 `crossborder_mall`（utf8mb4），按顺序执行：

```text
src/main/resources/db/crossborder_mall.sql
src/main/resources/db/cb_mall_commerce.sql
src/main/resources/db/cb_mall_trade.sql
src/main/resources/db/cb_mall_seckill.sql
src/main/resources/db/cb_mall_drop_legacy.sql
```

把 MySQL、Redis、RabbitMQ、JWT 配进配置或环境变量后：

```bash
mvn clean package -DskipTests
java -jar target/crossborder-mall-backend-1.0.0-SNAPSHOT.jar
```

默认端口 **8081**。Swagger：`http://localhost:8081/swagger-ui.html`

### 前端

```bash
cd crossborder-mall-frontend
npm install
npm run dev
```

接口默认走 Vite 代理到 `http://localhost:8081`。前台：`http://localhost:3000/`

### Docker Compose

```bash
cp .env.example .env
docker compose --env-file .env up -d --build
```

## 常见问题

**登录没有短信？** 验证码写在后端日志里。  
**管理后台进不去？** 登录手机号需等于 `ADMIN_PHONE`。  
**秒杀提示未预热？** 管理端上线活动或点「预热库存」。

## License

仅供学习与交流。参考或衍生请保留原作者信息。

## 联系

- GitHub：[@LazySheep101](https://github.com/LazySheep101)
