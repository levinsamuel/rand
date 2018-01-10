#!/usr/bin/env perl

use strict;
use warnings;
use Time::Piece;
use DateTime qw( );
use feature qw(say);

my $cffile = shift;

open (my $in, "<", $cffile) or die "Could not open $cffile";

my $firstdt;
my $numofdates = 0;
while (<$in>) {

    my ($m,$d,$y) = $_ =~ /^([0-9]{2})\/([0-9]{2})\/([0-9]{4})/;

    if (defined($y) && defined($m) && defined($d)) {
    
        $numofdates++;
        # say "date : ".$d."/".$m."/".$y;
        my $dt = DateTime->new(
           year      => $y,
           month     => $m,
           day       => $d,
           time_zone => 'local'
        );

        my $e = $dt->epoch();
        # say "Epoch time: ".$e;
        
        if (!defined($firstdt)) {
            $firstdt = $dt;
        } elsif ($firstdt->compare($dt) > 0) {
            $firstdt = $dt;
        }
    }
}
close $in;

say "First date: ".$firstdt->ymd();
my $lastdt = $firstdt->clone();
# my $lastm = $lastdt->month() + 7;
# $lastdt->set_month(($lastm - 1)%12 + 1);
# $lastdt->set_year(int(($lastm - 1)/12));
$lastdt->add(DateTime::Duration->new(months => 7));
say "Last date: ".$lastdt->ymd();

my $dtit = $firstdt->clone();

my $total = 0;
while ($dtit->compare($lastdt) < 0) {
    $total++;
    $dtit->add(DateTime::Duration->new(days => 7));
}

say "Shipments received: $numofdates";
say "Total shipments expected: $total";
my $totalcost = 572;
my $costused = $totalcost * (($numofdates)/$total);
my $costremaining = $totalcost - $costused;
say "Total cost: $totalcost";
say "Cost used: $costused";
say "Cost remaining: $costremaining";
say "Remaining cost split three ways: ".($costremaining/3);
say "Cost of already received, split two ways: ".($costused/2);
say "Total cost for prior recipients: ".($costused/2 + $costremaining/3);
