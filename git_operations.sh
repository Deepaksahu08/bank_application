#!/bin/bash
set -e

# Ensure Git is configured
if ! git config user.name > /dev/null; then
  git config --global user.name "Your Name"
fi
if ! git config user.email > /dev/null; then
  git config --global user.email "your_email@example.com"
fi

# Add and commit changes
git add .
git commit -m "Automated commit by Docker"

# Push changes
git push origin HEAD

# Switch to the main branch
git checkout main

# Pull the latest changes
git pull origin main
