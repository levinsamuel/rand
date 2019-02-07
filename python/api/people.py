from datetime import datetime
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
        if 'timestamp' in pdct:
            ts = pdct['timestamp']
            if isinstance(ts, datetime):
                ts = datetime_std_format(ts)
            self.timestamp = ts
        else:
            self.timestamp = _get_timestamp()

    def __repr__(self):
        return '{} {}'.format(self.fname, self.lname)

    def to_dict(self, id=None):
        return {
            'fname': self.fname,
            'lname': self.lname,
            'timestamp': self.timestamp
        }


def _get_timestamp():
    return datetime_std_format(datetime.now())
    
    
def datetime_std_format(dt):
    return dt.strftime(("%Y-%m-%d %H:%M:%S"))

        
def unmarshal(pstr):

    pdct = json.loads(pstr)
    log.debug(pstr)
    return Person(pdct)


def marshal(prsn):

    pdct = prsn.to_dict()
    log.debug(pdct)
    return json.dumps(pdct)

