#!/bin/bash
set -euo pipefail

: "${POSTGRES_USER:?POSTGRES_USER is required}"

DBS=("logiroute-menu" "logiroute-order")

for db in "${DBS[@]}"; do
  if createdb -U "${POSTGRES_USER}" -O "${POSTGRES_USER}" --if-not-exists "${db}"; then
    echo "Database ${db} ensured."
  fi
done
