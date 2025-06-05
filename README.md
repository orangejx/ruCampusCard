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
