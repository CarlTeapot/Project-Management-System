#!/bin/bash

curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "xd2@example.com",
    "password": "StrongPass123!",
    "confirmPassword": "StrongPass123!"
  }'  | jq -r '.token' > token.txt