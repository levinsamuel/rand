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
                # a demonstration of an interesting concept
                # First always returns the self's own type.
                # 'Second' or 'Third'
                self.whoami(),
                # This always returns the super of wherever it is *declared*
                # meaning always 'First' since declared in 'Second'
                super().whoami(),
                # This is more like the self-relative super, since it is
                # the super of whatever 'self' is, and self's type remains
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
        log.debug('init A: my type: %s, remaining args: %s',
                  type(self), kwargs)
        super().__init__(**kwargs)

    def whoami(self):
        return 'A'


class B(A, Third):

    def __init__(self, letter):
        kwargs = {'value': letter}
        super().__init__(letter, **kwargs)


class C(Third, A):

    def __init__(self, letter):
        super().__init__(value=letter, letter=letter)
