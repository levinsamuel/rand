package LISPL::Parser;

use strict;
use warnings;

use Data::Dumper;
use Scalar::Util qw(looks_like_number blessed);
use Carp;
use feature qw(say);
use LISPL::Symbol;
use LISPL::Env;

use base 'Exporter';
our @EXPORT = ('parse', 'evalisp', 'schemestr');

sub parse {
    read_from_tokens(tokenize($_[0]));
}

sub tokenize {

    my $str = $_[0];
    
    if (!defined($str)) {
        return;
    }
    
    chomp $str;
    
    if ($str eq '') {
        return;
    }

    $str =~ s/\(/ ( /g;
    $str =~ s/\)/ ) /g;
    
    # Return a reference to array
    my @retarr = split ' ', $str;
    
    if ($retarr[0] ne '(') {
        unshift @retarr, "(";
        push @retarr, ")";
    }
    
    return \@retarr;
}

sub read_from_tokens {

    my $arr = $_[0];

    if (ref $arr ne "ARRAY") {
        warn "must pass array ref to 'parse' sub";
        return 0;
    }
    
    if (scalar @$arr == 0) {
        warn "Unexpected EOF parsing program";
        return 0;
    }
    
    my $token = shift @$arr;
    
    if ($token eq "(") {
    
        my @L = ();
        
        while ($arr->[0] ne ")") {
            
            my $val = read_from_tokens($arr);
            if ($val) {
                push @L, $val;
            } else {
                return $val;
            }
            
        }
        
        shift @$arr;
        
        return [@L];
        
    } elsif ($token eq ")") {
        warn "Unexpected )";
        return 0;
    } else {
        atom($token);
    }
}

sub atom {
    
    my $token = $_[0];
    
    unless (looks_like_number($token)) {
        return LISPL::Symbol->new($token);
    }
    
    $token;
}

sub islist {
    ref($_[0]) eq "ARRAY";
}

sub evalisp {

    my $arg = $_[0];
    my $env = $_[1];
    
    #say ref($arg);
    
    if (!defined $env) {
        $env = LISPL::Env->new;
    }

    if (issym($arg)) {
    
        #say "found symbol: ".Dumper($arg);
        #say "evaluating with this Env: ".Dumper($env);
    
        my $proc = $env->get($arg);
        
        #say "evaluated to: ".Dumper($proc);
        
        return $proc;
        
    } elsif (!islist($arg)) {
    
        return $arg;
        
    } else {
    
        #say "array found";
        
        my @arr = @$arg;
        my $emt1 = $arr[0]->getval;
    
        if ($emt1 eq "quote") {
            
            return @arr[1..$#arr];
        
        } elsif ($emt1 eq "if") {
            
            #say "if found";
            
            my ($null, $test, $conseq, $alt) = @arr;
            
            #say Dumper($test);
            
            my $exp = evalisp($test, $env) ? $conseq : $alt;
            
            return evalisp($exp, $env);
            
        } elsif ($emt1 eq "define") {
            
            $env->put($arr[1], evalisp($arr[2], $env));
            
        } else {
        
            #say "found proc: ".Dumper($arr[0]);
            
            my $proc = evalisp($arr[0], $env);
            
            #say "evaluated proc: ".Dumper($proc);
            
            unless (ref($proc)){
                cluck "Expression must start with proc, not literal: $proc";
            }
            
            my @args = ();
            foreach my $arremt (@arr[1..$#arr]) {
                
                push @args, evalisp($arremt, $env);
                
                #say Dumper($env);
            }
            
            return $proc->(@args);
        }
    
    }

}

sub schemestr {
    
    my @retarr = ();
    
    for my $emt (@_) {
        if (issym($emt)) {
            push @retarr, $emt->getval;
        } elsif (ref $emt eq "ARRAY") {
            push @retarr, "(".(join ' ', schemestr(@$emt)).")";
        } else {
            push @retarr, $emt;
        }
    }
    
    return @retarr;
}

1;