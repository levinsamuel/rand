package LISPL::Number;

use warnings;
use strict;
use Scalar::Util qw(looks_like_number blessed);

sub new {
    
    my $class = shift;
    my $value = shift;
    
    unless (looks_like_number($value)) {return undef};
    
    my $self = bless {}, $class;
    
    $self->{"value"} = $value;
    
    return $self;
}

sub getnum {
    my $self = shift;
    
    $self->{"value"};
}

1;