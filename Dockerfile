# 1. 编译阶段：用 Maven 官方镜像把代码打包
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# 开始打包（跳过测试，节省时间）
RUN mvn clean package -DskipTests

# 2. 运行阶段：用轻量级 JDK 镜像运行
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# 把刚才编译好的 jar 包拿过来
COPY --from=build /app/target/*.jar app.jar
# 暴露端口（Render 默认分配的端口）
ENV PORT=8080
EXPOSE 8080
# 启动命令
ENTRYPOINT ["java","-jar","app.jar"]