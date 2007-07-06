#!/usr/bin/perl
#Script for post-processing of the results
    
use POSIX qw (ceil floor);
open(INPUT, "$ARGV[0]") or die( "Error: cannot open file $SCRATCH" );

@bench0 = ('0');
@bench1 = ('0');
@bench2 = ('0');
my @time = (\@bench0, \@bench1, \@bench2);
my @total = ([], [], []);
$procs = 0;
$skip=1;
$nprocs = 0;
while( $aline = <INPUT> ){

 if ($aline =~ /Verification/) {
    next; 
  }

 if ($aline =~ /Updates\/PE/) {
    next; 
  }

 if ($aline =~ /NPROCS\ *=\ *(\d*)/ ){
   $procs = log2($1);
   $nprocs++;
   $i = 0;
 }

  if ($aline =~ /EXPT1/) {
    $i = 0;
  } 

  if ($aline =~ /EXPT2/) {
    $i = 1;
  } 

  if ($aline =~ /EXPT3/) {
    $i = 2;
  } 

  if ($aline =~ /CPU time used\ *=\ *(\d*.\d*) seconds/) {
    $time[$i][$procs] = $time[$i][$procs] + $1;
    print "$1  $time[$i][$procs]";
  }

  if ($aline =~/\ *(\d*.\d*) Billion/) {
    $gups[$i][$procs] = $gups[$i][$procs] + $1;
    $total[$i][$procs] = $total[$i][$procs] + 1;
  }
}

  $n = max ($#bench0, $#bench1, $#bench2);
  print "==========GUPS===========$n \n";
  printf ("PROCS\tEXPT1\tEXPT2\tEXPT3\n");
  for ($i= 0; $i <= $n; $i++) {
    $gups[0][$i] = $gups[0][$i] / max ($total[0][$i], 1);
    $gups[1][$i] = $gups[1][$i] / max ($total[1][$i], 1);
    $gups[2][$i] = $gups[2][$i] / max ($total[2][$i], 1); 

    printf ( "%d\t%0.5f\t%0.5f\t%0.5f\n", 2**$i, $gups[0][$i], $gups[1][$i], $gups[2][$i]);
  }

  print "==========TIME(Seconds)=========== \n";
  printf ("PROCS\tEXPT1\tEXPT2\tEXPT3\n");
  for ($i= 0; $i <= $n; $i++) {
    $time[0][$i] = $time[0][$i] / max ($total[0][$i], 1);
    $time[1][$i] = $time[1][$i] / max ($total[1][$i], 1);
    $time[2][$i] = $time[2][$i] / max ($total[2][$i], 1); 

    printf ( "%d\t%5.2f\t%5.2f\t%5.2f\n", 2**$i, $time[0][$i], $time[1][$i], $time[2][$i]);
  } 


close(INPUT);

sub log2 {

    $log = 0;

    my($start) = shift (@_);

    while ($start > 1) {

        $log = $log + 1;
        $start = $start / 2;
    }

    return ($log);
}

sub max {

   my($max) = shift(@_);

   foreach (@_) {

     $max = $_ if $_  > $max;
   }

   return ($max);
}


