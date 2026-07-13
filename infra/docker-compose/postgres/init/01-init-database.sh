#!/bin/bash
set -e

DB_NAME="logiroute-menu"
DB_EXISTS=$(psql -U ${POSTGRES_USER} -tAc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'")

if [ "${DB_EXISTS}" != "1" ]; then
  # Create the database
  createdb -U ${POSTGRES_USER} -O ${POSTGRES_USER} ${DB_NAME}
  echo "Database ${DB_NAME} created."
else
  echo "Database ${DB_NAME} already exists."
fi

DB_NAME="logiroute-order"
DB_EXISTS=$(psql -U ${POSTGRES_USER} -tAc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'")

if [ "${DB_EXISTS}" != "1" ]; then
  # Create the database
  createdb -U ${POSTGRES_USER} -O ${POSTGRES_USER} ${DB_NAME}
  echo "Database ${DB_NAME} created."
else
  echo "Database ${DB_NAME} already exists."
fi
