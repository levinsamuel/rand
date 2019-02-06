from datetime import datetime
from pymongo import MongoClient
import pprint
import logging
import unittest

logging.basicConfig()
log = logging.getLogger('peopleapitest')
log.setLevel(logging.DEBUG)


class PeopleTest(unittest.TestCase):

    def test_parse(self):
        self.assertTrue(True)


if __name__ == '__main__':
    unittest.main()
