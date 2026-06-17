# Build command
# Adjust docker registry path (timpamungkas/...) into (your-docker-username/...)
#
# 1. [Linux/Mac] : ./gradlew clean bootJar 
#    [Windows]   : .\gradlew clean bootJar
# 2. docker build --tag devops-white:2.0.1 . 
# 3. docker login (optional)
# 4. docker tag devops-white:2.0.1 timpamungkas/devops-white:2.0.1
# 5. docker push timpamungkas/devops-white:2.0.1

# .\gradlew clean bootJar && docker build --tag devops-white:2.0.1 . && docker tag devops-white:2.0.1 timpamungkas/devops-white:2.0.1 && docker push timpamungkas/devops-white:2.0.1

FROM amazoncorretto:25-alpine
ARG JAR_FILE=build/libs/devops.white-2.0.1.jar
ADD ${JAR_FILE} devops-white.jar
ENTRYPOINT ["java", "-jar", "devops-white.jar"]