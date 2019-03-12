import unittest
import logging
from classes import First, Second, Third, B, C, log as cllog
from decorators import deflog, log as dlog, DecTest, decorated

logging.basicConfig()
log = logging.getLogger('classestest')


class ClassesTest(unittest.TestCase):

    def setUp(self):
        log.setLevel(logging.INFO)
        cllog.setLevel(logging.INFO)

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

    def test_multiple_super(self):

        log.setLevel(logging.DEBUG)
        # cllog.setLevel(logging.DEBUG)
        c = C(1)
        log.debug('c: %s', c)
        log.debug('c supers: %s', c.print_super_chain())
        log.debug('c mro: %s', type(c).mro())


class DecoratorsTest(unittest.TestCase):

    def setUp(self):
        log.setLevel(logging.INFO)
        dlog.setLevel(logging.INFO)

    def test_decorators(self):

        @deflog
        def hey(a):
            return a*2

        log.debug('hey called, result: %d', hey(3))

    def test_class_decorator(self):

        log.setLevel(logging.DEBUG)
        dlog.setLevel(logging.DEBUG)

        log.debug('decorated before: %s', decorated)
        self.assertIn('hi', decorated)
        dt = DecTest()
        log.debug('decorated after: %s', decorated)
