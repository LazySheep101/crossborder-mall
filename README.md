# 跨境商城

> 前后端分离的跨境零售演示系统 · By [LazySheep101](https://github.com/LazySheep101)

基于 **Spring Boot 3.5 + Vue 3** 的交易型商城：商品 / SKU、购物车、下单、模拟支付、超时关单，以及 Redis Lua 秒杀。

| 子目录 | 说明 | 技术 |
|--------|------|------|
| [`crossborder-mall-backend`](./crossborder-mall-backend) | 后端 | Spring Boot 3.5、Java 21、MyBatis-Plus、JWT、Redis、RabbitMQ |
| [`crossborder-mall-frontend`](./crossborder-mall-frontend) | 前端 | Vue 3、Vite 5、Pinia、Element Plus |

---

## 功能概览

**买家**：商品浏览与搜索、购物车、收货地址、下单、模拟支付、订单、确认收货、限时秒杀  

**管理端**（管理员手机号登录后）：商品 / SKU、分类、订单发货、秒杀活动与库存预热、看板

演示管理员：把 `.env` 里的 `ADMIN_PHONE` 设成你用来登录的手机号。种子数据里的 `13800138000` 可直接作为管理员号。验证码打印在后端日志，不发送真实短信。

---

## 目录结构

```text
crossborder-mall/
├── README.md
├── docker-compose.yml
├── .env.example
├── crossborder-mall-backend/    # 后端与 SQL
└── crossborder-mall-frontend/   # 前端
```

---

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

`crossborder_mall.sql` 提供登录用户。后面的脚本建立商品、交易和秒杀表，并去掉不再使用的旧表。

把 MySQL、Redis、RabbitMQ、JWT 配进 `application.yml` 或环境变量后：

```bash
mvn clean package -DskipTests
java -jar target/crossborder-mall-backend-1.0.0-SNAPSHOT.jar
```

### 前端

```bash
cd crossborder-mall-frontend
npm install
npm run dev
```

接口默认走 Vite 代理到 `http://localhost:8081`。

- 前台：`http://localhost:3000/`
- 接口文档：`http://localhost:8081/swagger-ui.html`

### Docker Compose

```bash
cp .env.example .env
# 编辑 .env 中的数据库、Redis、JWT
docker compose --env-file .env up -d --build
```

---

## 常见问题

**登录没有短信？**  
验证码写在后端日志里，本地不会真正发短信。

**管理后台进不去？**  
当前登录手机号需要等于 `ADMIN_PHONE`（默认 `13800138000`）。

**秒杀提示活动未预热？**  
管理端把活动上线，或点击「预热库存」，再从商品秒杀页下单。

---

## License

仅供学习与交流。参考或衍生请保留原作者信息。

## 联系

- GitHub：[@LazySheep101](https://github.com/LazySheep101)
