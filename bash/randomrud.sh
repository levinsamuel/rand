#!/bin/bash

IFSBAK=$IFS
IFS=$'\n'

link=$(readlink $0)
if [[ -z $link ]]; then
    link=$0
fi
me=$(dirname $link)

i=0
for url in $(cat $me/../other/rudies2.txt); do

    urls[$i]=$url
    i=$((i+1))
done

num=${#urls}
ind=$(($RANDOM % num))

# this \\newtab thing doesn't seem to work on macs
# chrome:\\newtab 
open ${urls[$ind]}
