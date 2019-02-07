#!/usr/bin/env python

# import people
import json
import logging
import mongocl
import mysqlcl

logging.basicConfig()
log = logging.getLogger('dbadapter')
log.setLevel(logging.DEBUG)


client = mongocl
# client = mysqlcl


def read(id=None):
    return client.read(id)


def post(prsn):
    client.post(prsn)