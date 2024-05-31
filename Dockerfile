FROM registry.cn-hangzhou.aliyuncs.com/jz-prod/openjdk:17.0.2-jdk-bullseye

MAINTAINER saisiawa

LABEL image.biz.id="tspi-nas-server"

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD nas-0.0.1-SNAPSHOT.jar root.jar

EXPOSE 8080

ENTRYPOINT ["java", "-server", "-XX:InitialRAMPercentage=75.0", "-XX:MaxRAMPercentage=75.0", "-Xss512k", "-XX:MetaspaceSize=256m", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100","-Dfile.encoding=UTF-8", "-Dnetworkaddress.cache.ttl=10", "-jar", "/root.jar","--spring.profiles.active=dev"]