#!/bin/bash

IFSBAK=$IFS
IFS=$'\n'

i=0
for url in $(cat ~/stuff/rudies.txt); do

    urls[$i]=$url
    i=$((i+1))
done

num=${#urls}
ind=$(($RANDOM % num))

chrome chrome:\\newtab ${urls[$ind]}
