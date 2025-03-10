#!/bin/bash
set -e

# Ensure Git is configured
if ! git config user.name > /dev/null; then
  git config --global user.name "Deepak"
fi
if ! git config user.email > /dev/null; then
  git config --global user.email "your_email@example.com"
fi

# Build the project
echo "Building the project to verify functionality..."
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

# Locate the generated .jar file
jar_file=$(find target build/libs -name "*.jar" | head -n 1)

if [[ -n "$jar_file" ]]; then
  echo "Build successful! .jar file created at: $jar_file"
else
  echo "Build failed or .jar file not found. Exiting."
  exit 1
fi

# Run the application in the background
echo "Running the application in the background to verify functionality..."
java -jar "$jar_file" > application.log 2>&1 &
app_pid=$!
echo "Application is running with PID $app_pid. Logs are being written to application.log."

# Wait for a few seconds to verify the application starts correctly
sleep 10

# Check if the application is still running
if ps -p $app_pid > /dev/null; then
  echo "Application is running successfully. Proceeding with the next steps..."
else
  echo "Application failed to run. Check application.log for details. Exiting."
  exit 1
fi

# Create a new branch
echo "Enter the name of the new branch:"
read new_branch
git checkout -b "$new_branch"

# Add the built .jar file before committing
if [[ -n "$jar_file" ]]; then
  cp "$jar_file" .
  git add "$(basename $jar_file)"
fi

# Check for uncommitted changes
if [[ -n "$(git status --porcelain)" ]]; then
  echo "Uncommitted changes found. Adding and committing changes..."

  # Add and commit changes
  git add .
  git commit -m "Automated commit by Deepak with built .jar file"

  # Push the new branch to the remote repository
  git push origin "$new_branch"
else
  echo "No changes to commit. Skipping commit and push."
fi

# Switch back to the main branch
echo "Switching back to the main branch..."
git checkout main

# Pull the latest changes from the main branch
echo "Pulling the latest changes from the main branch..."
git pull origin main

# Kill the application running in the background
echo "Stopping the application..."
kill $app_pid
echo "Application stopped successfully."

# Build the .jar file from the main branch
echo "Building the .jar file from the main branch..."
if [[ -f "pom.xml" ]]; then
  mvn clean package
elif [[ -f "build.gradle" ]]; then
  gradle clean build
else
  echo "No build tool configuration file (pom.xml or build.gradle) found. Skipping build."
  exit 1
fi

# Locate the generated .jar file from the main branch
jar_file_main=$(find target build/libs -name "*.jar" | head -n 1)

if [[ -n "$jar_file_main" ]]; then
  echo "Build successful from main branch! .jar file created at: $jar_file_main"
else
  echo "Build failed or .jar file not found in main branch."
  exit 1
fi
