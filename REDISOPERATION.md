

## Redis 读写操作
> 以下操作均需在 redis cli 内执行

#### 0.1. 进入 redis-cli
```bash
redis-cli 
```

#### 0.2. 切换到数据库 10
```bash 
select 10
```

### 1. 获取数据

#### 1.1. 查看所有卡片Id
```bash 
smembers cards:all
```

#### 1.2. 查看卡片详情
```bash 
hget cards:bc9abff1-544e-4c0f-93ca-0d1a01259b81
```

#### 1.3. 查看学生对应的卡片Id
```bash 
hget student_cards 101
```

#### 1.4. 查看所有学生-卡片映射
```bash 
hgetall student_cards
```

### 2. 设置或更新单个新卡片信息

#### 2.1. 生成 UUID (外部生成)
```bash
cat /proc/sys/kernel/random/uuid
```

#### 2.2. 添加卡片详情
```bash 
hset card:{uuid} id "{uuid}" studentId "{studentId}" studentName "{name}" balance "0.00" status "ACTIVE" createdAt "{timestamp}" updatedAt "{timestamp}"
```

#### 2.3. 添加到卡片集合
```bash 
sadd cards:all {uuid}
```

#### 2.4 添加学生映射
```bash 
hset student_cards {studentId} "{uuid}"
```

#### 2.5. 修改卡片余额
```bash 
hset card:{uuid} balance "{newBalance}" updatedAt "{timestamp}"
```

#### 2.6. 修改卡片状态
```bash 
hset card:{uuid} status "{newStatus}" updatedAt "{timestamp}"
```

### 3. 删除数据

#### 3.1. 获取学生 Id (可选)
```bash 
hget card:{uuid} studentId
```

#### 3.2. 从集合中删除
```bash 
srem cards:all {uuid}
```

#### 3.3. 删除学生映射
```bash
hdel student_cards {studentId}
```

#### 3.4. 删除卡片详情
```bash
del card:{uuid} 
```
