#!/bin/bash

# Usage: ./list_tasks.sh <projectPublicId> [status] [priority] [page] [size]
if [ -z "$1" ]; then
  echo "Usage: $0 <projectPublicId> [status] [priority] [page] [size]"
  exit 1
fi

PROJECT_ID=$1
TOKEN=$(cat token.txt)
STATUS=${2:-}
PRIORITY=${3:-}
PAGE=${4:-0}
SIZE=${5:-3}

QS="page=$PAGE&size=$SIZE"
if [ -n "$STATUS" ]; then
  QS="$QS&status=$STATUS"
fi
if [ -n "$PRIORITY" ]; then
  QS="$QS&taskPriority=$PRIORITY"
fi

echo -e "\n\n--- LIST TASKS ---"
curl -X GET "http://localhost:8081/api/projects/$PROJECT_ID/tasks?$QS" \
  -H "Authorization: Bearer $TOKEN"


