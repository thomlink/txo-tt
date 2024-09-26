FROM openjdk:11
WORKDIR /app
COPY . /app
RUN sbt compile
ENTRYPOINT ["sbt", "run"]
