#!/usr/bin/env bash
set -euo pipefail

# Defaults
MONGO_HOST="${MONGO_HOST:-mongodb:27017}"
REPL_SET_NAME="${REPL_SET_NAME:-rs0}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-60}"
SLEEP_INTERVAL="${SLEEP_INTERVAL:-1}"

HOST="${MONGO_HOST%:*}"
PORT="${MONGO_HOST##*:}"
if [ "$HOST" = "$PORT" ]; then
  HOST="$MONGO_HOST"
  PORT="27017"
fi

echo "Waiting for MongoDB at ${HOST}:${PORT}..."
attempt=0
until mongosh --host "$HOST" --port "$PORT" --eval "db.adminCommand('ping')" --quiet >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  if [ "$attempt" -ge "$MAX_ATTEMPTS" ]; then
    echo "Timed out waiting for MongoDB at ${HOST}:${PORT}" >&2
    exit 1
  fi
  sleep "$SLEEP_INTERVAL"
done

echo "MongoDB is up. Checking replica set..."

if mongosh --quiet --host "$HOST" --port "$PORT" --eval "try { rs.status(); print('RS_OK'); } catch(e) { print('RS_NOT_INIT'); }" | grep -q "RS_NOT_INIT"; then
  echo "Initializing replica set ${REPL_SET_NAME}..."
  mongosh --quiet --host "$HOST" --port "$PORT" --eval "rs.initiate({_id: '${REPL_SET_NAME}', members: [{ _id: 0, host: '${HOST}:${PORT}' }]});"
else
  echo "Replica set already initialized"
fi

echo "Waiting for replica set to become PRIMARY or SECONDARY..."
attempt=0
while true; do
  state=$(mongosh --quiet --host "$HOST" --port "$PORT" --eval "var s=rs.status(); print(s.myState);" | tr -d '\r\n ' || echo "error")
  if [ "$state" = "1" ] || [ "$state" = "2" ]; then
    echo "Replica set member state: $state"
    break
  fi
  attempt=$((attempt + 1))
  if [ "$attempt" -ge "$MAX_ATTEMPTS" ]; then
    echo "Replica set did not reach a healthy state in time" >&2
    mongosh --host "$HOST" --port "$PORT" --eval "printjson(rs.status())"
    exit 1
  fi
  sleep "$SLEEP_INTERVAL"
done

echo "Replica set ${REPL_SET_NAME} configured and healthy"
exit 0