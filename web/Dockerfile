FROM node:slim AS builder

ARG HTTP_BASE_URL=http://api.tafei-mall.szcq.top
ARG HTTP_TIMEOUT=5000

RUN apt-get update && \
    apt-get install -y git && \
    git clone https://github.com/rouwanyuanyuanyuan/myshoppingmall.git /app && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN mkdir -p Config && \
    echo "http:" > Config/application.yaml && \
    echo "  baseURL: \"${HTTP_BASE_URL}\"" >> Config/application.yaml && \
    echo "  timeout: ${HTTP_TIMEOUT}" >> Config/application.yaml

# 复制 package.json 和 package-lock.json (或 yarn.lock)
COPY package*.json ./

# 安装项目依赖 (使用 npm 或 yarn)，同时清除 npm 缓存
RUN npm install --no-audit --no-fund --cache /tmp/npm-cache && npm cache clean --force

# 复制整个项目源代码
COPY . .

# 构建 Vue 应用
RUN npm run build

# --- Production Stage ---

# 使用 Nginx Alpine 作为生产镜像
FROM nginx:alpine-slim

# 从 builder 阶段复制构建好的静态文件
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制自定义 Nginx 配置 (可选)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Nginx 默认监听 80 端口
EXPOSE 80

# Nginx 默认启动
CMD ["nginx", "-g", "daemon off;"]
