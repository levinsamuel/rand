#!/usr/bin/env bash

for file in `ls $1`; do

  echo "checking $file"
  hd=(`head -n 1 $file | tr -d '\n' | tr , ' '`)
  echo "fields: ${#hd[@]}"
  for i in `seq 1 ${#hd[@]}`; do

    blanks=`cut -f $i -d , $file | grep -v '.' | wc -l`
    echo "field is ${hd[i]}, $blanks blanks"
  done

done
