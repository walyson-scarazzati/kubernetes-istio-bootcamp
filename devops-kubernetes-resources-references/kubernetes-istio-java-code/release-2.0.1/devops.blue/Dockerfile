# Build command
# Adjust docker registry path (timpamungkas/...) into (your-docker-username/...)
#
# 1. [Linux/Mac] : ./gradlew clean bootJar 
#    [Windows]   : .\gradlew clean bootJar
# 2. docker build --tag devops-blue:2.0.1 . 
# 3. docker login (optional)
# 4. docker tag devops-blue:2.0.1 timpamungkas/devops-blue:2.0.1
# 5. docker push timpamungkas/devops-blue:2.0.1

# .\gradlew clean bootJar && docker build --tag devops-blue:2.0.1 . && docker tag devops-blue:2.0.1 timpamungkas/devops-blue:2.0.1 && docker push timpamungkas/devops-blue:2.0.1

FROM amazoncorretto:25-alpine
ARG JAR_FILE=build/libs/devops.blue-2.0.1.jar
ADD ${JAR_FILE} devops-blue.jar
ENTRYPOINT ["java", "-jar", "devops-blue.jar"]