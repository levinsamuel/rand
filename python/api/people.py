from datetime import datetime
from pymongo import MongoClient
from bson.objectid import ObjectId
import pprint
import logging
import json

logging.basicConfig()
log = logging.getLogger('peopleapi')
log.setLevel(logging.DEBUG)


class Person:

    def __init__(self, pdct):
        self.fname = pdct['fname']
        self.lname = pdct['lname']
        self.timestamp = pdct['timestamp']\
            if 'timestamp' in pdct else _get_timestamp()

    def __repr__(self):
        return '{} {}'.format(self.fname, self.lname)

    def to_dict(self, id=None):
        return {
            'fname': self.fname,
            'lname': self.lname,
            'timestamp': self.timestamp
        }


def _get_timestamp():
    return datetime.now().strftime(("%Y-%m-%d %H:%M:%S"))


def _mongocl():
    return MongoClient('localhost', 27017)


def unmarshal(pstr):

    pdct = json.loads(pstr)
    log.debug(pstr)
    return Person(pdct)


def marshal(prsn):

    pdct = prsn.to_dict()
    log.debug(pdct)
    return json.dumps(pdct)


def post(prsn, id=None):
    """Create or update a person, based on presence of ID"""
    # Create the list of people from our data

    person = Person(prsn)
    with _mongocl() as mcl:

        # Database
        ppldb = mcl.ppldb
        # collection (kind of like a table)
        pplclxn = ppldb.people

        pd = person.to_dict(id)
        pplclxn.insert_one(pd)


# Create a handler for our read (GET) people
def read(id=None):
    """
    This function responds to a request for /api/people
    with the complete lists of people

    :return:        sorted list of people
    """

    # Create the list of people from our data
    with _mongocl() as mcl:

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
