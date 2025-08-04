#!/bin/bash
pg_dump -h <azure-db-host>.postgres.database.azure.com \
  -U mads@<azure-db-host> -d mads -F c -b -v -f backup.dump
