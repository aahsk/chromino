#!/usr/bin/env bash

cd "$(dirname "$0")"

sbt scalafix
sbt scalafmtAll
