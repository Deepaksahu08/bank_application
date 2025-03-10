#!/bin/bash
set -e

# Ensure Git is configured
if ! git config user.name > /dev/null; then
  git config --global user.name "Deepak"
fi
if ! git config user.email > /dev/null; then
  git config --global user.email "your_email@example.com"
fi

# Create a new branch
echo "Enter the name of the new branch:"
read new_branch
git checkout -b "$new_branch"

# Check for uncommitted changes
if [[ -n "$(git status --porcelain)" ]]; then
  echo "Uncommitted changes found. Adding and committing changes..."

  # Add and commit changes
  git add .
  git commit -m "Automated commit by Deepak"

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

# Build the .war file
echo "Building the .war file..."
if [[ -f "pom.xml" ]]; then
  # Use Maven to build a WAR file
  mvn clean package -Dpackaging=war
elif [[ -f "build.gradle" ]]; then
  # Use Gradle to build a WAR file
  gradle clean build -Pwar
else
  echo "No build tool configuration file (pom.xml or build.gradle) found. Skipping build."
  exit 1
fi

# Locate the generated .war file
echo "Searching for the generated .war file..."
war_file=$(find target build/libs -name "*.war" | head -n 1)

if [[ -n "$war_file" ]]; then
  echo "Build successful! .war file created at: $war_file"
else
  echo "Build failed or .war file not found."
  exit 1
fi