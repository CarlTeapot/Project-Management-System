#!/bin/bash

# Usage: ./create_task.sh <projectPublicId>
if [ -z "$1" ]; then
  echo "Usage: $0 <projectPublicId>"
  exit 1
fi

PROJECT_ID=$1
TOKEN=$(cat token.txt)

echo -e "\n\n--- CREATE TASK ---"
curl -X POST "http://localhost:8081/api/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "New Task1",
    "description": "Example task description",
    "taskPriority": "MEDIUM",
    "dueDate": "2025-12-31T23:59:59",
    "assignedUserEmail": "xd2@example.com"
  }'


