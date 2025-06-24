#!/bin/bash
nf-test test tests/**/*.nf.test --plugins target/nft-stagenfcore-0.1.0.jar "${@}"
