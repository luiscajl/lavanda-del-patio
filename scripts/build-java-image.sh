#!/usr/bin/env bash

export APP=$1
gradle "$APP":jib 2>&1 | tee /dev/stderr