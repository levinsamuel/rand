#!/usr/bin/env python

from flask import Flask, render_template, request, jsonify
import people
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
    return jsonify([p.to_dict() for p in people.read()]),\
           {'content-type': 'application/json'}


@app.route('/api/people/<id>')
def read_person(id):
    person = people.read(id)
    if person is None:
        return 'Person not found for id: ' + id, 404
    else:
        return people.marshal(person), {'content-type': 'application/json'}


@app.route('/api/people', methods=['POST'])
def add_person():
    data = request.get_json()
    log.debug(data)
    people.post(data)
    return '', 204


if __name__ == '__main__':
    app.run(debug=True)
