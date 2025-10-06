#!/bin/bash

# Usage: ./decline_invitation.sh <projectPublicId>
if [ -z "$1" ]; then
  echo "Usage: $0 <projectPublicId>"
  exit 1
fi

PROJECT_ID=$1
TOKEN=$(cat token.txt)

echo -e "\n\n--- DECLINE INVITATION ---"
curl -X POST "http://localhost:8081/api/invitations/decline?projectPublicId=$PROJECT_ID" \
  -H "Authorization: Bearer $TOKEN"


