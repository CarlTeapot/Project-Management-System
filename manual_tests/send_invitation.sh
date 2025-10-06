#!/bin/bash

# Usage: ./send_invitation.sh <projectPublicId> <userEmail>
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <projectPublicId> <userEmail>"
  exit 1
fi

PROJECT_ID=$1
USER_EMAIL=$2
TOKEN=$(cat token.txt)

echo -e "\n\n--- SEND INVITATION ---"
curl -X POST "http://localhost:8081/api/invitations?projectPublicId=$PROJECT_ID&userEmail=$USER_EMAIL" \
  -H "Authorization: Bearer $TOKEN"


