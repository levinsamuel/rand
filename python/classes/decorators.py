import logging
import functools

log = logging.getLogger('decorators')
log.setLevel(logging.INFO)

decorated = set()


def deflog(f):

    log.debug('decorating function: %s', f.__name__)
    decorated.add(f.__name__)

    @functools.wraps(f)
    def wrapped(*args, **kwargs):
        log.debug('calling function: %s', f.__name__)
        ret = f(*args, **kwargs)
        return ret

    return wrapped


class DecTest:

    @deflog
    def hi(self):
        log.debug('hi')
