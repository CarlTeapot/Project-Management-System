#!/bin/bash

# Usage: ./update_task.sh <projectPublicId> <taskId>
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <projectPublicId> <taskId>"
  exit 1
fi

PROJECT_ID=$1
TASK_ID=$2
TOKEN=$(cat token.txt)

echo -e "\n\n--- UPDATE TASK ---"
curl -X PUT "http://localhost:8081/api/projects/$PROJECT_ID/tasks/$TASK_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Updated Task Title",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "taskPriority": "HIGH",
    "dueDate": "2025-12-31T12:00:00",
    "assignedUserId": 1
  }'


