import unittest
import logging
from classes import First, Second, Third, B, C

logging.basicConfig()
log = logging.getLogger('classestest')
log.setLevel(logging.DEBUG)
logging.getLogger('classes').setLevel(logging.DEBUG)

class ClassesTest(unittest.TestCase):

    def test_inheritance(self):

        value = 4
        log.debug("original value: %d", value)
        o1 = First(value)
        o2 = Second(value)
        log.debug("o1 transform, o2 transform: (%s, %s)", o1, o2)
        o3 = Third(value)
        log.debug('Third: %s', o3)

        log.debug('supers: second: %s, third: %s',
                  o2.print_super(), o3.print_super())

    def test_multiinheritance(self):

        value = 7
        b = B(value)
        try:
            log.debug('value: %d, letter: %d', b.value, b.letter)
        except AttributeError as e:
            log.debug('Error thrown: %s', e)

        c = C(value)
        try:
            log.debug('value: %d, letter: %d', c.value, c.letter)
        except AttributeError as e:
            log.debug('Error thrown: %s', e)
