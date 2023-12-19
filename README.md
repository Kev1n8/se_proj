# se_proj
a proj of software engineering

## 后端部署说明

首先，确保Redis和MySQL已经安装并启动（端口号分别为6379和3306）

### 方式1 直接运行
1. 安装jdk17
2. 安装依赖，编译
```
./gradlew build
```
3. 运行

>注意把用户名和密码换成自己的
```
SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/attend?useSSL=false' SPRING_DATASOURCE_USERNAME='root' SPRING_DATASOURCE_PASSWORD='root' REDIS_HOST='localhost' ./gradlew bootRun
```

### 方式2 使用docker

1. 安装docker
2. 制作镜像
```
docker build -t backendapp .
```
3. 运行容器

>注意把用户名和密码换成自己的
```
docker run -d -p 8088:8088 -e SPRING_DATASOURCE_URL='jdbc:mysql://host.docker.internal:3306/attend?useSSL=false' -e SPRING_DATASOURCE_USERNAME='root' -e SPRING_DATASOURCE_PASSWORD='root' -e REDIS_HOST='host.docker.internal' backendapp
```