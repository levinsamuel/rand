from datetime import datetime
from pymongo import MongoClient
import pprint
import logging
import unittest
import people
from people import Person

logging.basicConfig()
log = logging.getLogger('peopleapitest')
log.setLevel(logging.DEBUG)


class PeopleTest(unittest.TestCase):

    def test_marshal(self):
        pdict = {'fname': 'me', 'lname': 'meme', 'unused': 'nothing'}
        person = Person(pdict)
        pstr = people.marshal(person)
        self.assertTrue('{"fname": "me", "lname": "meme"' in pstr)

    def test_unmarshal(self):
        person = people.unmarshal('{"fname":"me","lname":"meme"}')
        self.assertEqual('me', person.fname)
        self.assertEqual('meme', person.lname)


if __name__ == '__main__':
    unittest.main()
