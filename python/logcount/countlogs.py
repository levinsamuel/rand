#!/usr/local/bin/python

import sys

if len(sys.argv) < 2:
    raise Exception(('Please provide a log file to search. Usage: {}'
                     ' [log_files...]'.format(sys.argv[0])
                     ))


class Log:
    def __init__(self, name, count, size):
        self.name, self.count, self.size = name, count, size

    def add_bytes(self, size):
        self.count += 1
        self.size += size


logfs = sys.argv[1:]
results = {}

for logf in logfs:
    with open(logf) as f:
        for line in f:
            try:
                parts = line.rstrip().split()
                if parts[2][1:] == 'GET' and parts[5].startswith('2'):
                    bytes = int(parts[6])
                    try:
                        results[parts[3]].add_bytes(bytes)
                    except KeyError:
                        results[parts[3]] = Log(parts[3], 1, bytes)
            except IndexError:
                # ignore lines that may empty, different format
                pass

top = sorted(results.values(), reverse=True, key=lambda l: l.count)
for file in top[0:min(len(top), 10)]:
    print(file.name, file.size)
