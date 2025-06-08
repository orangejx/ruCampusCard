# Campus Card System

## Configuration Guide

### Redis and Server Port Configuration

The project supports three ways to configure Redis connection and server port:

1. Command line arguments (highest priority)
2. External configuration file 
3. Internal default configuration (lowest priority)

#### 1. Using Command Line Arguments

With Gradle:
```bash
./gradlew bootRun --args='--server.port=8081 --spring.data.redis.host=localhost --spring.data.redis.port=6379 --spring.data.redis.database=0'
```

With compiled JAR:
```bash
java -jar build/libs/CampusCard-0.0.2.jar --server.port=8081 --spring.data.redis.host=localhost --spring.data.redis.port=6379 --spring.data.redis.database=0
```

#### 2. Using External Configuration File

1. Copy the template:
```bash
cp config/application.properties /path/to/your/config/
```

2. Modify the external config file:
```properties
server.port=8081
spring.data.redis.host=localhost  
spring.data.redis.port=6379
spring.data.redis.database=0
```

3. Run with config location:

With Gradle:
```bash
./gradlew bootRun --args='--spring.config.location=file:/path/to/your/config/application.properties'
```

With JAR:
```bash
java -jar build/libs/CampusCard-0.0.2.jar --spring.config.location=file:/path/to/your/config/application.properties'
```

#### 3. Default Configuration

If no arguments or external config provided, the system will use default configuration from `src/main/resources/application.properties`:

```properties
server.port=8080
spring.data.redis.host=10.12.0.12
spring.data.redis.port=6379  
spring.data.redis.database=10
```

### Configuration Priority

From highest to lowest:
1. Command line arguments
2. External config file  
3. Internal default config

### Configuration Reference

| Property | Description | Default |
|---------|-------------|---------|
| server.port | Server port | 8080 |
| spring.data.redis.host | Redis host | 10.12.0.12 |  
| spring.data.redis.port | Redis port | 6379 |
| spring.data.redis.database | Redis DB index | 10 |
| spring.data.redis.password | Redis password (optional) | (none) |

## Test Data Guide

### Adding Test Data Manually

You can use Redis CLI to manually add test data for development and testing purposes.

#### 1. Connect to Redis CLI

```bash
# Connect to Redis on localhost
redis-cli 

# Select the correct database (default is 10)
SELECT 10
```

#### 1.1 Connect to Redis CLI (Cluster)

```bash
# Connect to Redis on localhost
redis-cli -c 

```

#### 2. Add Test Card Data

Here's an example of adding a test campus card:

```redis
# Add card details
hset card:{54b64c64-4781-40a9-9ffa-88156ba3678a} \
    id "54b64c64-4781-40a9-9ffa-88156ba3678a" \
    studentId "103" \
    studentName "test" \
    balance "0.00" \
    status "ACTIVE" \
    createdAt "1749298908000" \
    updatedAt "1749298908000"

# Add card ID to the set of all cards
sadd cards:all "54b64c64-4781-40a9-9ffa-88156ba3678a"

# Map student ID to card ID
hset student_cards 103 "54b64c64-4781-40a9-9ffa-88156ba3678a"
```

#### 3. Verify Test Data

```redis
# Check card details
hgetall card:{54b64c64-4781-40a9-9ffa-88156ba3678a}

# Check if card ID exists in the set
smembers cards:all

# Check student-card mapping
hget student_cards 103
```

#### 4. Clean Test Data

```redis
# Clean specific test data
del card:{54b64c64-4781-40a9-9ffa-88156ba3678a}
srem cards:all "54b64c64-4781-40a9-9ffa-88156ba3678a"
hdel student_cards 103

# Or clean all data in current database
FLUSHDB

# Or clean all databases (use with caution)
FLUSHALL
```

Note: When adding test data manually, make sure to:
1. Use the correct database number (default is 10)
2. Follow the exact data format (no extra quotes)
3. Use valid UUIDs for card IDs
4. Use consistent timestamp format for dates

#### 5. Verify Through API

After adding test data, you can verify it through the API endpoints:

```bash
# Get card by ID
curl -X GET "http://localhost:8080/api/v1/cards/54b64c64-4781-40a9-9ffa-88156ba3678a"

# Get card by student ID
curl -X GET "http://localhost:8080/api/v1/cards/student/103"

# Check card balance
curl -X GET "http://localhost:8080/api/v1/cards/54b64c64-4781-40a9-9ffa-88156ba3678a/balance"
```

Expected response for card details:
```json
{
  "id": "54b64c64-4781-40a9-9ffa-88156ba3678a",
  "studentId": "103",
  "studentName": "test",
  "balance": "0.00",
  "status": "ACTIVE",
  "createdAt": "2025-04-07T03:55:08.000+00:00",
  "updatedAt": "2025-04-07T03:55:08.000+00:00"
}
```
