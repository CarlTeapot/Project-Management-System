#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <publicId>"
  exit 1
fi

PUBLIC_ID=$1
TOKEN=$(cat token.txt)

echo "Using publicId: $PUBLIC_ID"
echo "Using token from token.txt"

echo -e "\n--- DELETE PROJECT ---"
curl -X DELETE "http://localhost:8081/api/projects/$PUBLIC_ID" \
  -H "Authorization: Bearer $TOKEN"