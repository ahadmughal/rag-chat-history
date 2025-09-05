# 1️⃣ Use a lightweight Java 17 image
FROM eclipse-temurin:17-jdk-alpine
  
  # 2️⃣ Set working directory inside the container
WORKDIR /app
  
  # 3️⃣ Copy the JAR built by Gradle
COPY app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar
  
  # 4️⃣ Expose the port your app listens on
EXPOSE 8989
  
  # 5️⃣ Run the application
ENTRYPOINT ["java","-jar","app.jar"]