# Build command
# Adjust docker registry path (timpamungkas/...) into (your-docker-username/...)
#
# 1. [Linux/Mac] : ./gradlew clean bootJar 
#    [Windows]   : .\gradlew clean bootJar
# 2. docker build --tag devops-red:2.0.0 . 
# 3. docker login (optional)
# 4. docker tag devops-red:2.0.0 timpamungkas/devops-red:2.0.0
# 5. docker push timpamungkas/devops-red:2.0.0

# [Linux/Mac] In one line (assuming already login to Docker)
# ./gradlew clean bootJar && docker build --tag devops-red:2.0.0 . && docker tag devops-red:2.0.0 timpamungkas/devops-red:2.0.0 && docker push timpamungkas/devops-red:2.0.0

# [Windows] In one line (assuming already login to Docker)
# .\gradlew clean bootJar && docker build --tag devops-red:2.0.0 . && docker tag devops-red:2.0.0 timpamungkas/devops-red:2.0.0 && docker push timpamungkas/devops-red:2.0.0

FROM amazoncorretto:25-alpine
ARG JAR_FILE=build/libs/devops.red-2.0.0.jar
ADD ${JAR_FILE} devops-red.jar
ENTRYPOINT ["java", "-jar", "devops-red.jar"]