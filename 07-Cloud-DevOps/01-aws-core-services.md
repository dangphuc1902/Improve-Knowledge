# ☁️ AWS Core Services — Overview cho phỏng vấn

> **Phase:** 2 | **Time Block:** T7 13:30-15:30  
> **Quan trọng cho:** NAB, TymeX, Tiki — heavy AWS users

---

## 1. Compute

### EC2 (Elastic Compute Cloud)
- Virtual servers — full control OS, networking
- Instance types: t3 (burstable), m5 (general), c5 (compute), r5 (memory)
- Auto Scaling Group: tự scale instances theo metrics (CPU, request count)

### Lambda (Serverless)
```
Trigger (API Gateway, S3, SQS) → Lambda Function → Response
```
- Pay per invocation (100ms granularity)
- Max 15 min execution, 10GB memory
- **Use case:** Lightweight tasks, webhooks, event processing
- **Không dùng khi:** Long-running, stateful, low-latency requirements

### ECS/EKS (Container Services)
| Service | Mô tả | Khi nào dùng |
|:--------|:-------|:-------------|
| **ECS** | AWS-managed container orchestration | Simple, AWS-native |
| **EKS** | Managed Kubernetes | K8s expertise, multi-cloud |
| **Fargate** | Serverless containers (no EC2 mgmt) | Don't manage servers |

---

## 2. Storage

### S3 (Simple Storage Service)
- Object storage: unlimited files, max 5TB per object
- Storage classes: Standard → Infrequent Access → Glacier (cost ↓, latency ↑)
- Use cases: static files, backups, data lake, CDN origin

### RDS (Relational Database Service)
- Managed DB: MySQL, PostgreSQL, Oracle, SQL Server, Aurora
- Multi-AZ: automatic failover (standby in another AZ)
- Read Replicas: scale reads (up to 15 replicas)
- **Aurora:** AWS-optimized MySQL/PostgreSQL, 5x throughput, auto-scaling storage

### DynamoDB (NoSQL)
- Key-value + document store, single-digit ms latency
- Auto-scaling, serverless option (on-demand)
- Partition key + sort key, GSI/LSI for queries
- **Use case:** Session store, gaming leaderboards, IoT data

### ElastiCache
- Managed Redis or Memcached
- Cluster mode: horizontal scaling
- **Use case:** Session cache, API cache, real-time leaderboards

---

## 3. Networking

### VPC (Virtual Private Cloud)
```
┌─────────────────── VPC (10.0.0.0/16) ───────────────────┐
│                                                          │
│  ┌──── Public Subnet ────┐  ┌──── Private Subnet ────┐  │
│  │ 10.0.1.0/24           │  │ 10.0.2.0/24            │  │
│  │                       │  │                        │  │
│  │  ┌─── ALB ───┐        │  │  ┌─── App Server ───┐  │  │
│  │  │ (Internet │        │  │  │ (ECS/EC2)        │  │  │
│  │  │  facing)  │────────│──│──│                   │  │  │
│  │  └───────────┘        │  │  └────────┬──────────┘  │  │
│  │                       │  │           │             │  │
│  │  ┌── NAT Gateway ──┐  │  │  ┌───────▼──────────┐  │  │
│  │  │ (outbound only) │  │  │  │  RDS (Database)  │  │  │
│  │  └─────────────────┘  │  │  └──────────────────┘  │  │
│  └───────────────────────┘  └────────────────────────┘  │
│                                                          │
│  Internet Gateway ←→ Internet                            │
└──────────────────────────────────────────────────────────┘
```

### Security Groups vs NACLs
| Feature | Security Group | NACL |
|:--------|:--------------|:-----|
| Level | Instance | Subnet |
| Rules | Allow only | Allow + Deny |
| State | Stateful | Stateless |
| Evaluation | All rules | Ordered rules |

---

## 4. Messaging

### SQS (Simple Queue Service)
```
Producer → SQS Queue → Consumer(s)
```
- Standard: at-least-once, best-effort ordering
- FIFO: exactly-once, strict ordering (300 msg/s)
- Dead Letter Queue (DLQ) for failed messages
- Long polling reduces costs

### SNS (Simple Notification Service)
```
Publisher → SNS Topic → SQS Queue
                      → Lambda
                      → Email
                      → HTTP endpoint
```
- Pub/Sub: 1 message → many subscribers
- Fan-out pattern: SNS → multiple SQS queues

### So sánh: SQS vs SNS vs Kafka
| Feature | SQS | SNS | Kafka |
|:--------|:----|:----|:------|
| Pattern | Queue (1:1) | Pub/Sub (1:N) | Pub/Sub + Streaming |
| Ordering | FIFO optional | No ordering | Per partition |
| Replay | ❌ Consumed = gone | ❌ | ✅ Log retention |
| Throughput | Unlimited (standard) | Unlimited | Very high |
| Managed | ✅ Serverless | ✅ Serverless | MSK (managed) or self-hosted |
| Cost model | Per request | Per publish | Per broker/hour |

> **Relate kinh nghiệm:** FPM dùng Kafka cho transaction streaming (cần replay + ordering), RabbitMQ cho domain events (cần routing flexibility). AWS equivalent: Kafka → MSK, RabbitMQ → Amazon MQ.

---

## 5. Monitoring & Security

### CloudWatch
- Metrics: CPU, memory, custom metrics
- Logs: centralized log aggregation
- Alarms: trigger actions when thresholds exceeded

### IAM (Identity & Access Management)
```
User / Role / Group → Policy → AWS Resources
```
- Principle of least privilege
- Roles for services (EC2 role, Lambda role)
- Never hardcode credentials — use IAM roles + SDK

---

## 6. Common Architecture Pattern

```
                    ┌──── CloudFront (CDN) ────┐
                    │                           │
                    │  ┌──── S3 (Static) ────┐  │
                    │  └────────────────────┘  │
                    └───────────┬───────────────┘
                                │
Route 53 (DNS) → ALB (Load Balancer)
                                │
                    ┌───────────┼───────────┐
                    │           │           │
                 ECS/EKS     ECS/EKS     ECS/EKS
                (Service A)  (Service B) (Service C)
                    │           │           │
                    └───────────┼───────────┘
                                │
                    ┌───────────┼───────────┐
                    │           │           │
                 Aurora      ElastiCache   SQS/SNS
                (Primary)    (Redis)      (Async)
                    │
                 Aurora
                (Read Replica)
```

---

## Câu Hỏi Phỏng Vấn

### Q1: Khi nào dùng EC2 vs Lambda vs ECS?
**A:** EC2: full control, long-running, stateful. Lambda: short tasks (<15min), event-driven, cost-effective cho sporadic traffic. ECS/EKS: containerized apps, microservices, need scaling + orchestration. Rule of thumb: microservices → ECS/EKS, utilities/glue → Lambda, legacy → EC2.

### Q2: SQS vs Kafka — khi nào dùng cái nào?
**A:** SQS: fully managed, no ops, simple queue pattern, unlimited throughput. Kafka/MSK: cần message replay, event streaming, complex routing, cross-service event bus. SQS cho simple async tasks. Kafka cho event-driven architecture + data pipeline.

### Q3: Multi-AZ vs Multi-Region?
**A:** Multi-AZ: HA trong 1 region (failover tự động, RDS Multi-AZ). Multi-Region: disaster recovery, global users (higher cost, data replication complexity). Most apps: Multi-AZ đủ. Global apps: Multi-Region.

### Q4: Làm sao secure credentials trong microservices?
**A:** IAM roles (not access keys), Secrets Manager/Parameter Store for DB passwords, Environment variables via ECS task definition, KMS for encryption. NEVER commit credentials to git.
