#!/usr/bin/env python
# coding: utf-8

# In[498]:


import sys, math, random, logging, time
import numpy as np
import pandas as pd

# global tracker for recursive depth
level = 0

# binary-search-like algorithm
def unsorted_search(arr, num):
    global level
    level = 0
    return _us(arr, num, 0, len(arr)-1, 0)

def _us(arr, num, l, r, lev):
    global level
    level = max(lev, level)
    if l > r:
        return -1
    
    m = (l+r)//2
    if arr[m] == num:
        return m
    else:
        step1 = _us(arr, num, l, m-1, lev+1)
        if step1 == -1:
            step2 = _us(arr, num, m+1, r, lev+1)
            return max(step1, step2)
        else:
            return step1
    
# linear search
def recSearch(arr, x):
    global level
    level = 0
    return _recSearch(arr, 0, len(arr)-1, x, 0)
    
def _recSearch( arr, l, r, x, lev):
    global level
    level = max(level, lev)
    if r < l: 
        return -1
    if arr[l] == x: 
        return l 
    if arr[r] == x: 
        return r 
    return _recSearch(arr, l+1, r-1, x, lev+1) 
  


la = np.random.randint(10000, size=990000)
# log.debug("array: %s", la)
times = [time.time()]
print('Binary-like search:')
print(unsorted_search(la, 50))
print("max level:", level)
times.append(time.time())

print('Linear recursive search:')
print(recSearch(la, 50))
print("max level:", level)
times.append(time.time())

# construct times
df = pd.DataFrame({'starts': times[0:2], 'ends': times[1:3]})
df['diffs'] = df['ends'] - df['starts']
print(df)

