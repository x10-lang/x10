#!/usr/bin/perl

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_hpcc.pl,v 1.1 2007-08-02 13:09:59 srkodali Exp $
# This file is part of X10 Applications.
#

## Script for running hpcc RandomAccess Benchmark.
    
use POSIX qw(ceil floor sqrt);

sub log2 {
	$log = 0;
	$start = $_[0];
	while ($start > 1) {
		$log = $log + 1;
		$start = $start/2;
	}
	return ($log);
}

$infin = "../_hpccinf.txt";
open(INPUT, $infin)
	or die("Error: can't open file $infin for reading: $!");

while($nextline = <INPUT>) {
	$inputtext = $inputtext.$nextline;
}
close(INPUT);

$inputsize = ceil(sqrt(2 ** ($ARGV[2] + &log2($ARGV[1]))));

$tmp = $inputtext;
$inputtext = $tmp;
$inputtext =~ s/[0-9]+[ 	]*Ns/$inputsize Ns/;
$inputtext =~ s/[0-9]+[ 	]*Ps/$ARGV[1] Ps/;

$infout = "hpccinf.txt";
open(OUTPUT, "> $infout")
	or die ("Error: can't open file $infout for writing: $!");
print OUTPUT $inputtext;
close(OUTPUT);

$resout = "hpccoutf.txt";
`rm -f $resout`;
`poe $ARGV[0] -procs $ARGV[1]`;

$outfile = "$ARGV[1]x$ARGV[2].out";
`sed -n '/Begin of MPIRandomAccess/,/End of MPIRandomAccess/p' $resout >> $outfile`;
