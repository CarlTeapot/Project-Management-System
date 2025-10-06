#!/bin/bash

# Ensure a publicId argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <publicId>"
  exit 1
fi

PUBLIC_ID=$1
TOKEN=$(cat token.txt)

echo -e "\n\n--- UPDATE PROJECT ---"
curl -X PUT "http://localhost:8081/api/projects/$PUBLIC_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Project Name",
    "description": "Updated project description",
    "deadline": "2025-12-31",
    "members": []
  }'
