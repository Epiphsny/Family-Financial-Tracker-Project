#!/usr/bin/env bash

USERS=(
    "hypnotics"
    "justin"
    "Eric"
)

for i in ${USERS[@]}; do
    printf "$i\n"
    git log --author="$i" --abbrev-commit --no-decorate --no-merges | awk '$1 == "commit" {print "\t"$2}'
done

