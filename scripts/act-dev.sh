#!/bin/bash

act push \
-P ubuntu-latest=ghcr.io/catthehacker/ubuntu:act-latest \
--container-options "-v maven-cache:/root/.m2/repository" \
-s GITHUB_TOKEN="${ACT_TOKEN}"