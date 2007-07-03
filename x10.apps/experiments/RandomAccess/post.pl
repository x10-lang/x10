#!/usr/bin/perl
    
    use POSIX qw (ceil floor);

    open(INPUT, "$ARGV[0]") or die( "Error: cannot open file $SCRATCH" );

@bench0 = ('0', '0', '0', '0', '0');
@bench1 = ('0', '0', '0', '0', '0');
@bench2 = ('0', '0', '0', '0', '0');
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
 }

  if ($aline =~ /X10LIB/) {
    $i = 0;
  } 

  if ($aline =~ /X10C/) {
    $i = 1;
  } 

  if ($aline =~ /HPCC/) {
    $i = 2;
  } 

  if ($aline =~ /CPU time used\ *=\ *(\d*.\d*) seconds/) {
    $time[$i][$procs] = $time[$i][$procs] + $1;
   # print "$1  $time[$i][$procs]";
  }

  if ($aline =~/\ *(\d*.\d*) Billion/) {
  #  print "$1  ";
    $gups[$i][$procs] = $gups[$i][$procs] + $1;
    $total[$i][$procs] = $total[$i][$procs] + 1;
  }
}

  $n = $#bench0;
  print "==========GUPS=========== \n";
  printf ("X10LIB\tX10C\tHPCC\n");
  for ($i= 0; $i <= $n; $i++) {
    $gups[0][$i] = $gups[0][$i] / $total[0][$i];
    $gups[1][$i] = $gups[1][$i] / $total[1][$i];
    $gups[2][$i] = $gups[2][$i] / $total[2][$i]; 

    printf ( "%0.5f\t%0.5f\t%0.5f\n", $gups[0][$i], $gups[1][$i], $gups[2][$i]);
  }

  print "==========TIME(Seconds)=========== \n";
  printf ("X10LIB\tX10C\tHPCC\n");
  for ($i= 0; $i <= $n; $i++) {
    $time[0][$i] = $time[0][$i] / $total[0][$i];
    $time[1][$i] = $time[1][$i] / $total[1][$i];
    $time[2][$i] = $time[2][$i] / $total[2][$i]; 

    printf ( "%5.2f\t%5.2f\t%5.2f\n", $time[0][$i], $time[1][$i], $time[2][$i]);
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



