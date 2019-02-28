import logging

logging.basicConfig()
log = logging.getLogger('classes')


class First:

    def __init__(self, value, **kwargs):
        self.value = value
        super().__init__(**kwargs)

    def transform(self):
        return self.value + 2

    def __repr__(self):
        return 'Me:{}, value: {}'.format(self.whoami(), self.transform())

    def whoami(self):
        return 'First'


class Second(First):

    def __init__(self, value, **kwargs):
        super().__init__(value, **kwargs)

    def transform(self):
        return super().transform() * 3

    def whoami(self):
        return 'Second'

    def print_super(self):
        return '\nwho am i: {}\nsuper type: {}\nparent type: {}\n'\
            .format(
                self.whoami(), super().whoami(),
                super(type(self), self).whoami()
            )


class Third(Second):

    def __init__(self, value, **kwargs):
        super().__init__(value + 1, **kwargs)

    def whoami(self):
        return 'Third'


class A:

    def __init__(self, letter, **kwargs):
        self.letter = letter
        log.debug('init A: %s', kwargs)
        super().__init__(**kwargs)

    def whoami(self):
        return 'A'


class B(A, Third):

    def __init__(self, letter, **kwargs):
        kwargs['value'] = letter
        super().__init__(letter, **kwargs)


class C(Third, A):

    def __init__(self, letter, **kwargs):
        kwargs['value'] = letter
        kwargs['letter'] = letter
        super().__init__(**kwargs)
