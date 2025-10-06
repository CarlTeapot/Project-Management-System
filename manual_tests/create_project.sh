#!/bin/bash

curl -X POST http://localhost:8081/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $(cat token.txt)" \
  -d '{
    "name": "New Project",
    "description": " It is very good",
    "deadline": "2025.12.31 ",
    "members": []
  }'