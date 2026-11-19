# 跨境商城

> 前后端分离的跨境零售演示系统 · By [LazySheep101](https://github.com/LazySheep101)  
> **整体说明（推荐先读）**：[架构说明.md](架构说明.md)  
> **分层索引**：[docs/项目分层介绍.md](docs/项目分层介绍.md)

跨境商城是一套可演示的**交易型跨境零售系统**，技术栈为 **Java 21 / Spring Boot 3.5 + Vue 3（Vite / Pinia / Element Plus）**，存储与中间件使用 **MySQL、Redis、RabbitMQ**。

它覆盖买家完整交易闭环：分类浏览与搜索、商品详情（含多规格 SKU）、购物车、收货地址、下单扣库存、**模拟支付**、订单查询与确认收货；未支付订单经 RabbitMQ TTL 延迟消息**自动关单并回库存**。限时秒杀单独成链：管理端预热 Redis 库存 → 买家侧 **Lua 原子扣减 + 一人一单** → **MQ 异步落待支付单** → 前端轮询结果再支付。

配套能力包括：手机验证码登录 + JWT（登出黑名单）、商品详情逻辑过期缓存、订单支付与关单 CAS 互斥。**支付不接真实三方商户**；短信验证码打印在后端日志，便于本地演示。更完整的角色说明、分层图与两条主链路，见 [架构说明.md](架构说明.md)。

## 先读哪份文档

| 你想了解 | 打开 |
|----------|------|
| **整体说明** | [架构说明.md](架构说明.md) |
| **分层总览 + 各层入口** | [docs/项目分层介绍.md](docs/项目分层介绍.md) |
| 某一层仔细讲 | [docs/层/使用层.md](docs/层/使用层.md) 起 |
| 建库 | [docs/数据库初始化说明.md](docs/数据库初始化说明.md) |
| 产品范围 | [docs/完整跨境商城产品规划.md](docs/完整跨境商城产品规划.md) |
| 落地勾选 | [docs/完整商城落地清单.md](docs/完整商城落地清单.md) |
| 改造对照 | [docs/改造总览.md](docs/改造总览.md) |

| 子目录 | 说明 | 技术 |
|--------|------|------|
| [`crossborder-mall-backend`](./crossborder-mall-backend) | 后端 | Spring Boot 3.5、Java 21、MyBatis-Plus、JWT、Redis、RabbitMQ |
| [`crossborder-mall-frontend`](./crossborder-mall-frontend) | 前端 | Vue 3、Vite、Pinia、Element Plus |

## 功能概览

**买家**

- 首页分类与热门商品、商品列表 / 搜索 / 详情（SKU）
- 购物车勾选与结算、收货地址管理
- 下单、模拟支付（支付宝 / 微信 / Stripe 渠道名落库）、订单列表与详情
- 取消未支付单、确认收货；限时秒杀抢购与结果轮询

**管理端**（登录手机号需配置为 `ADMIN_PHONE`）

- 数据看板、商品 / SKU 上下架、分类维护
- 订单发货与取消
- 秒杀活动配置、上线与 **Redis 库存预热**

演示管理员：把 `.env` / 配置里的 `ADMIN_PHONE` 设成你用来登录的手机号。验证码打印在后端日志，不发送真实短信。

## 目录结构

```text
crossborder-mall/
├── README.md
├── 架构说明.md                 # 项目总说明
├── docs/                       # 分层、各层细分、产品与建库
│   └── 层/                     # 使用 / 接入 / 安全 / 业务 / 异步 / 缓存 / 数据
├── docker-compose.yml
├── .env.example
├── crossborder-mall-backend/   # 后端与 SQL
└── crossborder-mall-frontend/  # 前端
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

创建库 `crossborder_mall`（utf8mb4），按顺序执行（详见 [docs/数据库初始化说明.md](docs/数据库初始化说明.md)）：

```text
src/main/resources/db/crossborder_mall.sql
src/main/resources/db/cb_mall_commerce.sql
src/main/resources/db/cb_mall_trade.sql
src/main/resources/db/cb_mall_seckill.sql
src/main/resources/db/cb_mall_demo_seed.sql   # 可选：更多带图演示商品
src/main/resources/db/cb_mall_drop_legacy.sql
```

把 MySQL、Redis、RabbitMQ、JWT、`ADMIN_PHONE` 配进配置或环境变量后：

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

接口默认走 Vite 代理到后端。前台：`http://localhost:3000/`

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
