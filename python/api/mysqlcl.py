from datetime import datetime
from sqlalchemy import create_engine, __version__ as v, Column, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import pprint
import logging
import json
from people import Person

logging.basicConfig()
log = logging.getLogger('mysqlcl')
log.setLevel(logging.DEBUG)

Base = declarative_base()
engine = create_engine('mysql+mysqlconnector://sam:levin@localhost:3306/checkr_person', echo=True)
Session = sessionmaker()
Session.configure(bind=engine)


class DBPerson(Base):
    __tablename__ = 'people'
    
    _id = Column(Integer, primary_key=True)
    fname = Column(String)
    lname = Column(String)
    createtime = Column(DateTime)
    
    def __init__(self, person):
        self.fname, self.lname, self.createtime = person.fname, person.lname, person.timestamp
        
    def to_person(self):
        return Person({'fname': self.fname,
                       'lname': self.lname,
                       'timestamp': self.createtime})

def client():
    return Session()

    
def post(prsn):
    """Create or update a person, based on presence of ID"""
    # Create the list of people from our data

    cl = client()

    pd = DBPerson(prsn)
    cl.add(pd)
    cl.commit()


# Create a handler for our read (GET) people
def read(id=None):

    cl = client()
    if id is not None:
        dbo = cl.query(DBPerson).filter_by(_id=id).first()
        ppl = dbo.to_person()
        log.debug('person: %s', ppl)
    else:
        pass
    return ppl

    # return [PEOPLE[key] for key in sorted(PEOPLE.keys())]

if __name__ == '__main__':
    c = client()   
    print(engine, c)
    print(v)
    
    