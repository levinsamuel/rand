#!/usr/bin/env perl

use strict;
use warnings;
use feature qw(say);
use lib '/home/samuel.levin/lib/';
use Encode qw(decode encode find_encoding);
use Switch;
use Getopt::Std;

getopts("d");
our($opt_d);
 
my $file = shift;

if (! defined $file) {
    die "No file argument provided";
}

my $ptn = "[^+-<>.,\[\]]";

sub fromfile {

    open (my $in, "<", $_[0]) or die "Could not open this file: $file";

    my @script;
    while (<$in>) {

        # Remove non-script characters
        s/$ptn//g;
        
        push @script, $_;
    }
    return @script;
}

sub fromarg {

    my @script = [];
    $_[0] =~ s/$ptn//g;
    $script[0] = $_[0];
    
    return @script;
}

sub parse {

}

my @lines;
if (-f $file) {
    @lines = fromfile $file;
} else {
    @lines = fromarg $file;
}

my @script;
for my $line (@lines) {

    foreach my $char (split //, $line) {
        push @script, $char;
    }
}

my @l;
my @tape = (0) x 16;
my $len = 16;
my $slen = scalar @script;
my $pos = 0;
my @inarr;
        
for (my $i = 0; $i < $slen; $i++) {

    my $char = $script[$i];
    switch ($char) {
    
        case '<' {
            
            if ($pos == 0) {
                my @news = (0) x 16;
                unshift @tape, @news;
                $pos = 15;
                $len += 16;
            } else {
                
                $pos--;
            }
            #say "Position: $pos";
        }
        case '>' {
            
            if ($pos +1 == $len) {
                my @news = (0) x 16;
                push @tape, @news;
                
            }   
            $pos++;
            #say "Position: $pos";
            
        }
        case '.' {
        
            print chr $tape[$pos];
            # for debuging remove chr
            #print $tape[$pos];
        }
        case ',' {
        
            if (scalar @inarr == 0) {
            
                my $input = <STDIN>;
                foreach my $i (split //, $input) {
                    push @inarr, ord $i;
                }
            }
            my $first = shift @inarr;
            $tape[$pos] = $first;
        }
        case '+' {
        
            #print '+';
            my $val = $tape[$pos];
            $tape[$pos] = ($val + 1) % 256;
        }
        case '-' {
        
            #print '-';
            my $val = $tape[$pos];
            $tape[$pos] = ($val + 255) % 256;
            #say "new val".$tape[$pos];
            
        }
        case '[' {
        
            if ($tape[$pos] == 0) {
            
                my $here = $i;
                while ($script[$i] ne ']') {
                    $i++;
                    die "Expected matching ']' for '[', script position $here" if ($i == $slen);
                }
                
            } else {
            
                unshift @l, $i;
            }
            #print '[';
        }
        case ']' {
            
            my $v;
            if ($tape[$pos] == 0) {
                $v = shift @l;
            } else {
                $v = $l[0];
                $i = $v if (defined $v);
            }
            die "found ']' missing opening '[' at position $i" if (! defined $v);
        }
    }
    
}

if (defined $opt_d) {

    say '';
    say '';
    say 'Program finished';
    say "array size: ".scalar @tape;
    say "final position: $pos";
    say '';
    print "Array:\n[".$tape[0];
    my $f = 1;
    foreach my $el (@tape) {
        
        if ($f) {
            $f = 0;} else {
            print ",".$el;
        }
    }
    print "]";
}

=head1 WHAT IS THIS

brainfuk interpreter written in perl, buddy.

=head1 OPTIONS

=over 4

=item -d

run in debug mode

=back

=cut