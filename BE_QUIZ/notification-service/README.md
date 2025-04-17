1. **Start database MongoDB with docker**
```yml
  mongodb:
  image: bitnami/mongodb:7.0.11
  container_name: mongodb-7.0.11
  ports:
    - "27017:27017"
  environment:
    - MONGODB_ROOT_USER=root
    - MONGODB_ROOT_PASSWORD=root
  volumes:
    - mongodb_data:/bitnami/mongodb
```
**Note:** install MongoDB Compass to manage the database
https://www.mongodb.com/try/download/compass <br>
2. **Config MongoDB in Spring Boot**

**a. Add dependency**
```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```
**b. Config connection in application.properties or application.yml**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://root:root@localhost:27017/post-service?authSource=admin
```
**Note:** trên url post-service là tên database

