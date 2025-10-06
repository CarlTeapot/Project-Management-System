curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"xd@example.com","password":"StrongPass123!"}' \
  | tee /dev/tty | jq -r '.token' > token.txt
