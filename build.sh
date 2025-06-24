#!/bin/bash

mvn package && \
echo "Built $(readlink -f target/nft-stagenfcore*.jar)"