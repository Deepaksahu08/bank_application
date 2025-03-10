#!/bin/bash
set -e

# Ensure Git is configured
if ! git config user.name > /dev/null; then
  git config --global user.name "Deepak"
fi
if ! git config user.email > /dev/null; then
  git config --global user.email "ddshu8060@gmail.com"
fi

# Run the application to verify functionality
echo "Starting the application to verify functionality..."
if [[ -f "pom.xml" ]]; then
  # Use Maven to run the application
  mvn spring-boot:run > application.log 2>&1 &
elif [[ -f "build.gradle" ]]; then
  # Use Gradle to run the application
  gradle bootRun > application.log 2>&1 &
else
  echo "No build tool configuration file (pom.xml or build.gradle) found. Exiting."
  exit 1
fi

app_pid=$!
echo "Application is running with PID $app_pid. Logs are being written to application.log."

# Wait for a few seconds to verify the application starts correctly
sleep 10

# Check if the application is still running
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

# Build the JAR file
echo "Building the JAR file..."
if [[ -f "pom.xml" ]]; then
  # Use Maven to build
  mvn clean package
elif [[ -f "build.gradle" ]]; then
  # Use Gradle to build
  gradle clean build
else
  echo "No build tool configuration file (pom.xml or build.gradle) found. Exiting."
  exit 1
fi

# Locate the generated JAR file
jar_file=$(find target -name "*.jar" | head -n 1)

if [[ -n "$jar_file" ]]; then
  echo "Build successful! JAR file created at: $jar_file"
else
  echo "Build failed or JAR file not found in 'target' directory. Exiting."
  exit 1
fi

# Add the JAR file and other changes
if [[ -n "$jar_file" ]]; then
  cp "$jar_file" .
  git add "$(basename $jar_file)"
fi

# Check for uncommitted changes and commit
if [[ -n "$(git status --porcelain)" ]]; then
  echo "Uncommitted changes found. Adding and committing changes..."

  git add .
  git commit -m "Automated commit by Deepak with built JAR file"

  # Push changes to the main branch
  echo "Pushing changes to the main branch..."
  git push origin main
else
  echo "No changes to commit. Skipping push."
fi

echo "Process completed successfully."
