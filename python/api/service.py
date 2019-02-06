#!/usr/bin/env python

from flask import Flask, render_template
import people
import json

app = Flask(__name__, template_folder='templates')


@app.route('/')
def home():
    return render_template('index.html')
    
    
@app.route('/api/people')
def read_people():
    return json.dumps(people.read())

if __name__ == '__main__':
    app.run(debug=True)