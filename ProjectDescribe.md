# rucc 项目启动/操作指南 
> 本项目是一款简易校园卡在线管理平台. 同时支持 redis 集群模式, 后端服务集群模式, 同时使用 Nginx 进行负载均衡配置. 编译后的项目执行文件已包含 Tomcat 服务器, 因此不需要额外配置 Tomcat 服务器即可使用 openjdk 25 启动. 

## 项目参数 
  - Springboot `v3.5.0`
  - Redis `alpine3.19`
  - Nginx `1.27.0-bookworm-perl`
  - Tomcat `3.1.5`
    > 在Spring Boot项目中, Tomcat的版本是由Spring Boot的父POM (spring-boot-starter-parent) 管理.

### 集群模式 
  - 后端/Tomcat节点数量: `6`
  - Redis节点数量: `6`
  - Nginx主入口数量: `1`

## 1. 项目启动 (必选) 
### 1.0 加载容器所需镜像

```bash
docker load --input images/openjdk_25-jdk-oraclelinux8.tar
docker load --input images/redis_alpine3.19.tar
docker load --input images/nginx_1.27.0-bookworm-perl.tar
```

### 1.1 启动 redis 集群 

1.1.1. 创建 redis 集群相关容器
```bash
docker compose up -d redis-node-6 redis-node-5 redis-node-4 redis-node-3 redis-node-2 redis-node-1
```

1.1.2. 进入任一 redis 容器并创建集群
```bash
docker exec -it redis-node-1 ash 
redis-cli --cluster create redis-node-1:6379 redis-node-2:6379 redis-node-3:6379 redis-node-4:6379 redis-node-5:6379 redis-node-6:6379 --cluster-replicas 1
```

### 1.2 创建并启动 后端/Tomcat 服务集群
```bash
docker compose up -d rucc-node-6 rucc-node-5 rucc-node-4 rucc-node-3 rucc-node-2 rucc-node-1
```

### 1.3 创建并启动 Nginx 主入口 
```bash
docker compose up -d ngx 
```

## 2. Docker 相关 (可选) 
> 所有节点均搭建在 Docker 上, 可以使用 docker 的 exec 命令进入容器, 以执行相关操作/命令.

  - 进入 `Nginx`容器
  ```bash
  docker exec -it ngx bash
  ```

  - 进入`Redis`容器
  ```bash
  docker exec -it redis-node-1 ash
  ```

  - 进入`后端`/`Tomcat`服务器容器
  ```bash
  docker exec -it rucc-node-1 bash
  ```

  - 查看某个容器的日志
  ```bash
  docker logs redis-node-1
  ```

## 3. Redis 初始化及读写操作 (教程) 
> 以下操作均需在 redis cli 内执行

#### 3.0.0. 进入 redis 容器
```bash
docker exec -it redis-node-1 ash
```

#### 3.0.1. 进入 redis-cli 
```bash
redis-cli -c
```

#### 3.0.1.1 进入集群内其他节点 (可选)
```bash
redis-cli -c -h redis-node-2
```

#### 3.0.2. 切换到数据库 10 (不再使用, 仅适用于单节点模式)
```bash 
select 10
```

### 3.1. 获取数据 

#### 3.1.1. 查看所有卡片Id
```bash 
smembers cards:all
```

#### 3.1.2. 查看卡片详情(所有信息)
```bash 
hgetall card:{bc9abff1-544e-4c0f-93ca-0d1a01259b81}
```

#### 3.1.2.1. 查看卡片信息(学生Id)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} studentId
```

#### 3.1.2.2. 查看卡片信息(学生姓名)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} studentName
```

#### 3.1.2.3. 查看卡片信息(余额)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} balance
```

#### 3.1.2.4. 查看卡片信息(状态)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} status
```

#### 3.1.2.5. 查看卡片信息(创建时间)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} createdAt
```

#### 3.1.2.6. 查看卡片信息(上次修改时间)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} updatedAt
```

#### 3.1.2.7. 查看卡片信息(卡片Id)
```bash 
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} id
```

#### 3.1.3. 查看学生对应的卡片Id
```bash 
hget student_cards 103
```

#### 3.1.4. 查看所有学生-卡片映射
```bash 
hgetall student_cards
```

### 3.2. 设置或更新单个新卡片信息

#### 3.2.1. 生成 UUID (外部生成) 
```bash
cat /proc/sys/kernel/random/uuid
```

#### 3.2.2. 添加卡片详情
```bash 
hset card:{"{uuid}"} id "{uuid}" studentId "{studentId}" studentName "{name}" balance "0.00" status "ACTIVE" createdAt "{timestamp}" updatedAt "{timestamp}"

# example
hset card:{54b64c64-4781-40a9-9ffa-88156ba3678a} id "54b64c64-4781-40a9-9ffa-88156ba3678a" studentId "103" studentName "test" balance "0.00" status "ACTIVE" createdAt "1749298908000" updatedAt "1749298908000"
```

#### 3.2.3. 添加到卡片集合 
```bash 
sadd cards:all {uuid}

#example
sadd cards:all 54b64c64-4781-40a9-9ffa-88156ba3678a
```

#### 3.2.4 添加学生映射
```bash 
hset student_cards {studentId} "{uuid}"

#example
```bash 
hset student_cards 103 "54b64c64-4781-40a9-9ffa-88156ba3678a"
```

#### 3.2.5. 修改卡片余额
```bash 
hset card:{uuid} balance "{newBalance}" updatedAt "{timestamp}"

#example
hset card:{54b64c64-4781-40a9-9ffa-88156ba3678a} balance "100" updatedAt "1749298908000"
```

#### 3.2.6. 修改卡片状态
```bash 
hset card:{uuid} status "{newStatus}" updatedAt "{timestamp}"

#example
#set card status to ACTIVE
hset card:{54b64c64-4781-40a9-9ffa-88156ba3678a} status "ACTIVE" updatedAt "1749298908000"
#set card status to INACTIVE
hset card:{54b64c64-4781-40a9-9ffa-88156ba3678a} status "INACTIVE" updatedAt "1749298908000"
```

### 3.3. 删除数据

#### 3.3.1. 获取学生 Id (可选) 
```bash 
hget card:{uuid} studentId

#example
hget card:{54b64c64-4781-40a9-9ffa-88156ba3678a} studentId
```

#### 3.3.2. 从集合中删除 
```bash 
srem cards:all {uuid}

#example
srem cards:all {54b64c64-4781-40a9-9ffa-88156ba3678a}
```

#### 3.3.3. 删除学生映射
```bash
hdel student_cards {studentId}

#example
hdel student_cards 103
```

#### 3.3.4. 删除卡片详情
```bash
del card:{uuid} 

#example
del card:{54b64c64-4781-40a9-9ffa-88156ba3678a} 
```

### 3.4 集群管理 

#### 3.4.1 查看集群节点
```bash
redis-cli -h redis-node-2 cluster nodes
```

#### 3.4.2 查看集群信息
```bash
redis-cli -h redis-node-2 cluster info
```
