# Api Gateway

This microservice is responsible for:

* Authentication and Authorization
* Cross-Origin Resource Sharing (CORS)
* Routing and Load Balancing

## Tech stack

* Build tool: maven 4.0.0
* Java: 21
* Framework: Spring boot 3.2.5

## Prerequisites

* Java SDK 21

**Admin Credentials:**

- username: admin
- password: admin

## Requirements

1. Java 11

2. In order to be able to save `Photos` you need to sign up to [Cloudinary](https://cloudinary.com/) and enter your
   credentials in the `application.properties` file of the Spring Boot app (
   `SocialNetwork\Server\src\main\resources\application.properties`)

## Start the app

### **Option 1 - Start the Client and the Server manually**

#### 1. Start the Client

To start the Client you need to enter the `SocialNetwork/Client` folder:

```bash
$ cd SocialNetwork/Client
```

Install all dependencies:

```bash
$ npm install
```

Run the app in the development mode:

```bash
$ npm start
```

Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

#### 2. Start the Server

Go to the root directory of the Spring Boot app:

```bash
$ cd SocialNetwork/Server
```

Start the Server:

```bash
$ mvn spring-boot:run
```

The Server is running on port `8000`.

### **Option 2 - Start the application in Docker**

1. **Start the application**

Go to the project directory( `SocialNetwork/` ) and run:

```bash
$ docker-compose up -d
```

The front-end server will start on port `9090`. To open it enter in your browser:

```bash
$ http://localhost:9090
```

2. **Stop the application**

You can stop the containers with:

 ```bash 
 $ docker-compose down
 ```

## App screenshots

1. **Home Page**

![App Screenshot](readme-images/kl-social-network-home-gregor.PNG)

2. **Friends Page**

![App Screenshot](readme-images/kl-social-network-friends-gregor.PNG)

3. **Photos Page**

![App Screenshot](readme-images/kl-social-network-photos-gregor.PNG)

## Docker guideline

### Build docker image

`docker build -t <account>/identity-service:0.9.0 .`

### Push docker image to Docker Hub

`docker image push <account>/identity-service:0.9.0`

### Create network:

`docker network create devteria-network`

### Start MySQL in devteria-network

`docker run --network devteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian`

### Run your application in devteria-network

`docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service identity-service:0.9.0`

## Install Docker on ubuntu

# Add Docker's official GPG key:

sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:

echo \
"deb [
arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
$(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update

sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

sudo docker run hello-world