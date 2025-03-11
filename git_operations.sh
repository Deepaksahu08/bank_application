#!/bin/bash
set -e


if ! git config user.name > /dev/null; then
  git config --global user.name "Deepak"
fi
if ! git config user.email > /dev/null; then
  git config --global user.email "ddshu8060@gmail.com"
fi

echo "Starting the application to verify functionality..."
if [[ -f "pom.xml" ]]; then
  mvn spring-boot:run > application.log 2>&1 &
elif [[ -f "build.gradle" ]]; then
  gradle bootRun > application.log 2>&1 &
else
  echo "No build tool configuration file (pom.xml or build.gradle) found. Exiting."
  exit 1
fi

app_pid=$!
echo "Application is running with PID $app_pid. Logs are being written to application.log."

sleep 10

if ps -p $app_pid > /dev/null; then
  echo "Application is running successfully. Stopping the application..."
  kill $app_pid
  echo "Application stopped successfully."
else
  echo "Application failed to run. Showing the last 10 lines of application.log:"
  tail -n 10 application.log
  echo "Exiting."
  exit 1
fi

echo "Building the JAR file..."
if [[ -f "pom.xml" ]]; then
  mvn clean package
elif [[ -f "build.gradle" ]]; then
  gradle clean build
else
  echo "No build tool configuration file (pom.xml or build.gradle) found. Exiting."
  exit 1
fi

jar_file=$(find target -name "*.jar" | head -n 1)

if [[ -n "$jar_file" ]]; then
  echo "Build successful! JAR file created at: $jar_file"
else
  echo "Build failed or JAR file not found in 'target' directory. Exiting."
  exit 1
fi

if [[ -n "$jar_file" ]]; then
  cp "$jar_file" .
  git add "$(basename $jar_file)"
fi

if [[ -n "$(git status --porcelain)" ]]; then
  echo "Uncommitted changes found. Adding and committing changes..."

  git add .
  git commit -m "Automated commit by Deepak with built JAR file"

  echo "Pushing changes to the main branch..."
  git push origin main
else
  echo "No changes to commit. Skipping push."
fi

echo "Process completed successfully."
