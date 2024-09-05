#!/usr/bin/env bash
native-image \
  --no-server \
  --no-fallback \
  --enable-url-protocols=http \
  --report-unsupported-elements-at-runtime \
  -H:+UnlockExperimentalVMOptions \
  -jar fat.jar \
  -H:Name=bootstrap \

cp bootstrap native-artifacts
