package LISPL::Env;

use warnings;
use strict;

use LISPL::Symbol;
use Scalar::Util;
use Math::Trig;

use Carp;

# Set up ENV
my %def_env = (
    "*" => sub { $_[0] * $_[1] },
    "/" => sub { $_[0] / $_[1] },
    "+" => sub { $_[0] + $_[1] },
    "-" => sub { $_[0] - $_[1] },
    ">" => sub { $_[0] > $_[1] },
    "<" => sub { $_[0] < $_[1] },
    ">=" => sub { $_[0] >= $_[1] },
    "<=" => sub { $_[0] <= $_[1] },
    "=" => sub { $_[0] == $_[1] },
    abs => sub { abs $_[0] },
    append => sub { $_[0] + $_[1] },
    apply => sub { eval $_[0]->getval." ".(join ' ', @{$_[1]}); },
    begin => sub { return $_[$#_] },
    car => sub { return $_[0]->[0] },
    cdr => sub { return @{$_[0]}[1..$#{$_[0]}] },
    cons => sub { return [ @{$_[0]}, @{$_[1]} ] },
    'eq?' => sub { ref($_[0]) and ref($_[1]) and refaddr($_[0]) == refaddr($_[1]) },
    'equal?' => sub { LISPL::Symbol::equals($_[0], $_[1]) },
    len => sub { length @{$_[0]} },
    list => sub { [ @_ ] },
    'list?' => sub { ref($_[0]) eq "ARRAY" },
    max => sub {
                my $max = $_[0];
                foreach my $num (@_) { 
                    $max = $num if ($num > $max);
                }
                return $max;
            },
    min => sub { 
                my $min = $_[0];
                foreach my $num (@_) { 
                    $min = $num if ($num < $min);
                }
                return $min;
            },
    not => sub { ! LISPL::Symbol::equals($_[0], $_[1]) },
    'null?' => sub { ! defined $_[0] },
    'number?' => sub { looks_like_number($_[0]) },
    pi => pi,
    'procedure?' => sub { ref($_[0]) eq "CODE" },
    'round' => sub { 0; },
    'symbol?' => sub { issym($_[0]) }
);

sub new {
    
    my $class = shift;
    $class = ref($class) || $class;
    
    my $self = {};
    
    my $hash = shift;
    
    # copy default environment
    my %local_env = %def_env;
    
    if (ref($hash) eq "HASH") {
        
        $self->{"env"} = { %local_env, %$hash};
        
    } else {
    
        $self->{"env"} = \%local_env;
    }
    
    return bless $self, $class;
}

sub get {
    
    my $self = shift;
    my $symbol = shift;
    
    if (issym($symbol)) {
    
        my $ret = $self->{"env"}->{$symbol->getval};
        
        #say $symbol->getval;
        
        # ret can be undefined, will return null.
        #if (! defined $ret) {
        #    confess "Symbol value '".$symbol->getval."' not defined in this env";
        #}
        
        return $ret;
    }
    
}

sub put {

    my $self = shift;
    my $key = shift;
    my $val = shift;
    
    if (issym($key)) {
    
        $self->{"env"}->{$key->getval} = $val;
    }
}

1;