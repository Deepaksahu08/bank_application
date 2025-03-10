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

# Add and commit changes
git add .
git commit -m "Automated commit by Deepak"

# Push the new branch to the remote repository
git push origin "$new_branch"

# Switch back to the main branch
git checkout main

# Pull the latest changes
git pull origin main
