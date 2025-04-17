## Tech stack

* Build tool: maven 4.0.0
* Java: 21
* Framework: Spring boot 3.2.5
* RDBMS: MySQL 8.0.36
* Kafka: 3.0.0

## Prerequisites

* Java SDK 21

**Admin Credentials:**

- username: admin
- password: admin

### **I.Start the application in Docker**

1. **Start the application**

Go to the project directory( `2425_CS445AC_NHOM1/` ) and run:

```bash
$ docker-compose up -d 
```

### **II.Connect to MySQL**

**a. Install MySQL Workbench to manage the database <br>**
https://dev.mysql.com/downloads/workbench/ <br>

**b. Config connection in application.properties or application.yml**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Data_Firstt
    driverClassName: "com.mysql.cj.jdbc.Driver"
    username: root
    password: root
```

**c. Create database trong Mysql Workbench : create database Data_Firstt**

**Note:** trên url jdbc:mysql://localhost:3306/Data_Firstt, Data_Firstt là tên database

### **III.Connect to MongoDB**

**a. Install MongoDb Compass to manage the database <br>**
https://www.mongodb.com/try/download/compass <br>
**b. Config connection in application.properties or application.yml**

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://root:root@localhost:27017/post-service?authSource=admin
```

**Note:** trên url post-service là tên database
