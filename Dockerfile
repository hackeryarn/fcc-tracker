FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/fcc_tracker.jar /fcc_tracker/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/fcc_tracker/app.jar"]
