#!/usr/bin/env python

from flask import Flask, render_template, request, jsonify
import people
import dbadapter
import json
import logging

logging.basicConfig()
log = logging.getLogger('peopleservice')
log.setLevel(logging.DEBUG)

app = Flask(__name__, template_folder='templates')


@app.route('/')
def home():
    return render_template('index.html')


@app.route('/api/people')
def read_people():

    # Create the list of people from our data
    prsns = dbadapter.read()

    return jsonify([p.to_dict() for p in prsns]),\
           {'content-type': 'application/json'}


@app.route('/api/people/<id>')
def read_person(id):

    # Create the list of people from our data
    person = dbadapter.read(id)

    if person is None:
        return 'Person not found for id: ' + id, 404
    else:
        return people.marshal(person), {'content-type': 'application/json'}


@app.route('/api/people', methods=['POST'])
def add_person():

    """Create or update a person, based on presence of ID"""
    # Create the list of people from our data

    data = request.get_json()
    log.debug(data)
    person = people.Person(data)
    dbadapter.post(person)
    return '', 204


if __name__ == '__main__':
    app.run(debug=True)
