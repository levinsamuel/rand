from datetime import datetime
from pymongo import MongoClient
from bson.objectid import ObjectId
import pprint
import logging
import json
from people import Person

logging.basicConfig()
log = logging.getLogger('peopleapi')
log.setLevel(logging.DEBUG)


def client():
    return MongoClient('localhost', 27017)

    
def post(prsn):
    """Create or update a person, based on presence of ID"""
    # Create the list of people from our data

    with client() as mcl:

        # Database
        ppldb = mcl.ppldb
        # collection (kind of like a table)
        pplclxn = ppldb.people

        pd = prsn.to_dict()
        pplclxn.insert_one(pd)


# Create a handler for our read (GET) people
def read(id=None):
    """
    This function responds to a request for /api/people
    with the complete lists of people

    :return:        sorted list of people
    """

    # Create the list of people from our data
    with client() as mcl:

        # Database
        ppldb = mcl.ppldb
        # collection (kind of like a table)
        pplclxn = ppldb.people
        log.debug(pplclxn)

        if id is None:
            ppl = [Person(p) for p in pplclxn.find()]
            log.debug(ppl)
        else:
            p = pplclxn.find_one({'lname': id})
            return Person(p)

    return ppl

    # return [PEOPLE[key] for key in sorted(PEOPLE.keys())]
