# 使用官方 OpenJDK 镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 jar 文件到容器中
COPY target/leasingSystem-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（假设你用的是 8088）
EXPOSE 8088
# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]