#!/usr/bin/env bash

cd "$(dirname "$0")"

rm -rf ./frontend/src/scala
mkdir -p ./frontend/src/scala
cp -rf ./domain/target/scala-2.13/domain*.{js,map} ./frontend/src/scala
cp -rf ./protocol/target/scala-2.13/protocol*.{js,map} ./frontend/src/scala
