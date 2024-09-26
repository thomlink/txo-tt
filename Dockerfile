# Use a commonly used Scala-SBT image that includes OpenJDK and SBT
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Run sbt compile to pre-compile the project
RUN sbt compile

EXPOSE 8080

# Define the default command to run the app
ENTRYPOINT ["sbt", "run"]
