package LISPL::Symbol;

use warnings;
use strict;

use base 'Exporter';
our @EXPORT = ('issym');

sub new {
    
    my $class = shift;
    my $value = shift;
    
    my $self = {};
    
    bless $self, $class;
    
    $self->{"value"} = $value;
    
    return $self;
}

sub getval {
    my $self = shift;
    
    $self->{"value"};
}

sub equals {
    
    #shift;
    my $arg1 = shift;
    my $arg2 = shift;
    
    if (ref($arg1) eq "Symbol" and ref($arg2) eq "Symbol") {
    
        return $arg1->getval eq $arg2->getval;
        
    } else {
    
        return $arg1 eq $arg2;
        
    }
}

sub issym {
    ref($_[0]) eq "LISPL::Symbol";
}

1;