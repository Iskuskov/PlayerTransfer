#!/bin/sh
createdb di-example
createuser -P di-example
psql -h localhost -U di-example < ../resources/create-tables.sql
