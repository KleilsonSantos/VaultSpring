#!/bin/sh
echo "⏳ Waiting for PostgreSQL..."

if command -v pg_isready >/dev/null 2>&1; then
  until pg_isready -h localhost -p 5432 -U "$POSTGRES_USER"; do
    sleep 1
  done
elif command -v psql >/dev/null 2>&1; then
  until PGPASSWORD=$POSTGRES_PASSWORD psql -h localhost -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c '\q' 2>/dev/null; do
    sleep 1
  done
else
  echo "⚠️ No pg_isready or psql found. Trying port with nc..."
  until nc -z localhost 5432; do
    sleep 1
  done
fi

echo "✅ PostgreSQL is ready!"
