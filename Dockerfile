FROM openjdk:17
ADD target/devinsight.jar devinsight.jar
ENTRYPOINT ["java","-jar","/devinsight.jar"]