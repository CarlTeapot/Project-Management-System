#!/bin/bash

# Usage: ./delete_task.sh <projectPublicId> <taskId>
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <projectPublicId> <taskId>"
  exit 1
fi

PROJECT_ID=$1
TASK_ID=$2
TOKEN=$(cat token.txt)

echo -e "\n\n--- DELETE TASK ---"
curl -X DELETE "http://localhost:8081/api/projects/$PROJECT_ID/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN"


