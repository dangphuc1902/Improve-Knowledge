# 🐳 PHASE 3.2 — Docker & Deployment

> **Timeline**: Month 5, tuần 3-4  
> **Mục tiêu**: Containerize, deploy, và vận hành game server production

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### 1. Multi-stage Dockerfile

```dockerfile
# Stage 1: Build
FROM gcc:13 AS builder
WORKDIR /app
COPY . .
RUN mkdir build && cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release .. && \
    make -j$(nproc)

# Stage 2: Runtime (minimal image)
FROM debian:bookworm-slim
RUN apt-get update && apt-get install -y libstdc++6 && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/build/game_server /usr/local/bin/
EXPOSE 8080
CMD ["game_server", "--config", "/etc/game/config.yaml"]

# Result: 800MB build image → 50MB runtime image
```

### 2. Docker Compose — Multi-Service

```yaml
version: '3.8'
services:
  game-server:
    build: ./game-server
    ports: ["8080:8080"]
    depends_on: [redis, postgres]
    environment:
      - REDIS_URL=redis://redis:6379
      - DB_URL=postgres://user:pass@postgres:5432/game
    deploy:
      replicas: 2  # 2 instances
    networks: [game-net]
    
  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]
    networks: [game-net]
    
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: game
      POSTGRES_PASSWORD: dev_password
    volumes: ["pgdata:/var/lib/postgresql/data"]
    networks: [game-net]
    
  nginx:
    image: nginx:alpine
    ports: ["80:80"]
    volumes: ["./nginx.conf:/etc/nginx/nginx.conf"]
    depends_on: [game-server]
    networks: [game-net]

  prometheus:
    image: prom/prometheus
    ports: ["9090:9090"]
    networks: [game-net]
    
  grafana:
    image: grafana/grafana
    ports: ["3000:3000"]
    networks: [game-net]

volumes:
  pgdata:
networks:
  game-net:
```

### 3. Nginx Reverse Proxy

```nginx
upstream game_servers {
    least_conn;  # Load balance by least connections
    server game-server:8080;
}

server {
    listen 80;
    
    location / {
        proxy_pass http://game_servers;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Request-ID $request_id;
    }
    
    location /ws {
        proxy_pass http://game_servers;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;  # WebSocket support
        proxy_set_header Connection "upgrade";
    }
    
    location /metrics {
        proxy_pass http://prometheus:9090;
    }
}
```

### 4. CI/CD Cơ Bản (GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Build & Deploy
on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build & Test
        run: |
          docker compose build
          docker compose run game-server ./run_tests
      - name: Push to Registry
        run: |
          docker tag game-server:latest registry/game-server:${{ github.sha }}
          docker push registry/game-server:${{ github.sha }}
      - name: Deploy to VPS
        run: |
          ssh deploy@vps "cd /app && docker compose pull && docker compose up -d"
```

### 5. Kubernetes Căn Bản (Bonus)

```yaml
# Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: game-server
spec:
  replicas: 3
  selector:
    matchLabels:
      app: game-server
  template:
    spec:
      containers:
      - name: game-server
        image: registry/game-server:latest
        ports: [{ containerPort: 8080 }]
        resources:
          requests: { cpu: "250m", memory: "256Mi" }
          limits: { cpu: "500m", memory: "512Mi" }
---
# HPA - Auto-scaling
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: game-server
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target: { type: Utilization, averageUtilization: 70 }
```

---

## 🔨 PROJECT

1. Write multi-stage Dockerfile cho CafePho game server
2. Docker Compose: game-server (2 replicas) + Redis + Postgres + Nginx + Prometheus + Grafana
3. CI/CD pipeline: build → test → deploy to VPS
4. Load test: wrk hoặc k6 → confirm 1K concurrent connections

## ✅ CHECKLIST

- [ ] Multi-stage Dockerfile (build image < 100MB)
- [ ] Docker Compose with networking
- [ ] Nginx reverse proxy + load balancing
- [ ] CI/CD pipeline (build → test → deploy)
- [ ] Deploy to VPS successfully
- [ ] Kubernetes deployment concept understood
- [ ] Auto-scaling concept understood

## 🎯 MILESTONE: Full stack running in Docker Compose, accessible via Nginx, metrics in Grafana
