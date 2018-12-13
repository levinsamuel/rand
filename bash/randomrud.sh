#!/bin/bash

IFSBAK=$IFS
IFS=$'\n'

me=$(readlink $0 | xargs dirname)

i=0
for url in $(cat $me/../other/rudies2.txt); do

    urls[$i]=$url
    i=$((i+1))
done

num=${#urls}
ind=$(($RANDOM % num))

chrome chrome:\\newtab ${urls[$ind]}
