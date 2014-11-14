#!/usr/bin/perl

#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2014.
#

use strict;
use warnings;
use diagnostics;
use Getopt::Std;
use File::stat;
use FileHandle;

# Following settings are defined in config file
#---------------------------
my @MatrixSizeList;
my $NZDensity;
#---------------------------
# List of number of node used in tests
my @NodeList;
my $ProcPerNode;
#--------------------------
# Batch job submition options. The following is an example used for Triloka cluster.
my $BatchModeOpt;
my $gml_path;         # x10.gml root path
my $jopts;            # Execution parameters for managed backend
#--------------------------
my @TestExecList;
my $itnum;
my $LogFile;

#==================================================================
#==================================================================
# Load configuration settings, overide default settings

my $bench_cfg = "ptest_config.pl";
eval `cat $bench_cfg` or die $!;

#==================================================================
#subroutine declear

sub PrintHelp;
sub RunCmd;
sub RunTest;
sub RunTestSuite;
sub ParseData;
sub PrintOut;

#==================================================================

my $RunningFlag = 0;            # Default value only allows printing the test cases, or dry run
                                # Using "-r" to run the all test cases
my $ParsingFlag = 0;
my %opt;						# Input options

autoflush STDOUT 1;
getopts('rpb:h', \%opt);

if (defined $opt{p}) {
  $ParsingFlag = 1;
}
if (defined $opt{r}) {
  $RunningFlag = 1;
}

if (defined $opt{b}) {
  $BatchModeOpt   = "-p $opt{b}";
}

if (defined $opt{h}) {
  PrintHelp;
  exit;
}

#==================================================================

my @TestLog = ();
my %Result ;

###########################################################################
sub PrintHelp {
  my $cmd=$0;

  printf "
--- Benchmark test suite help ---
$cmd     : Dry run. Print all commands in benchmark test suite
$cmd -r  : Launch benchmark test suite
$cmd -p  : Parsing output logs of all test output, and printing result in stdout
$cmd -h  : Print help info

--- NOTE --- 
This script is created for submitting jobs to slurm system.
Modification may be needed to run on different batch systems, such as PBS, LSF, or LoadLeveler.
Alternatively, you can use dry-run to get list benchmark test comands and creat job
submission scripts.\n";

}

###########################################################################

sub RunTest {
  my $cmd = "$_[0]";

  if ($ParsingFlag != 0) {
	return ParseData($cmd);
  } elsif ($RunningFlag != 0) {
	return RunCmd($cmd);
  }
  print $cmd, "\n";
  return 0;
}

##########################################################################

sub RunCmd {

  my $cmd = "$_[0]";
  my $tryleft = 10;

  print"$cmd\n";
  TESTOUT->autoflush(1);

  while ($tryleft > 0 ) {
	open TESTOUT, "$cmd | " or die "Can't fork command executin process\n";

	print OUTFILE_H "*****************************************************\n";
	print OUTFILE_H "$cmd\n";
	
	while (<TESTOUT>) {
	  print "$_";
	  print OUTFILE_H "$_";
	  $tryleft = 0;
	} 
	close TESTOUT or print "Cannot close TEST pipe";
	if ($tryleft > 0 ) {
	  print "Test failed, start another one. $tryleft tries left\n";
	  $tryleft--;
	  sleep 5;
	}
	sleep 3;
  }
}

###########################################################################

sub ParseData {
  my $cmd = "$_[0]";
  while(<TEST_IN>) {
      if ($_ =~ m/$cmd/) {
          while (<TEST_IN>) {
                if ($_ =~ m/iter: ([\d.]+) ms, comm: ([\d.]+) ms, seq calc: ([-\d.]+) ms/ ) {
                  return ($1, $2, $3);
                }
          }
      }
  }
  print "Parse error:", "$cmd", "\n";
  die "Parsing error";
}

###########################################################################

sub PrintOut {

  #------------------------------------------------------------------------
  print("# Time in seconds per iteration\n");
  print("# Nonzero |");
  foreach my $test (@TestExecList) {
     printf("|  %20s   |", $test->{name});
  }
  print("\n");
  print("# million |");
  foreach my $test (@TestExecList) {
     printf("|  Total(s)  |  Commu(s)  |", $test->{name});
  }
  print("\n");

  #-----------------------------------------
  for my $np (@NodeList)      {
        for my $ms (@MatrixSizeList) {
          printf("%8.2f  |", $ms*$ms*$NZDensity/1000/1000);
          for my $t (@TestExecList)  {
                my $exe = $t->{name};
                my $v = 1.0 * $Result{$exe}{$ms}{$np}[0] / $itnum /1000;
                my $a = 1.0 * $Result{$exe}{$ms}{$np}[1] / $itnum /1000;
                my $c = 1.0 *  $Result{$exe}{$ms}{$np}[2] / $itnum /1000;
                printf("| %10.4f | %10.4f |", $v, $a);
          }
          print "\n";
        }
  }
}

###########################################################################
sub RunTestSuite {

  foreach my $test (@TestExecList) {
	my $testexe = $test->{name};
	my $launch  = $test->{alloc};
	my $wrap    = $test->{wrapper};
	my %dms=();

	foreach my $ms (@MatrixSizeList) {

	  my %dnp=();
	  foreach my $nn (@NodeList) {
		my $np        = $nn * $ProcPerNode;
		my $exe_param = "-m $ms --iterations --density $NZDensity";
                my @retval = RunTest("$launch -N$nn -n$np $BatchModeOpt $wrap $testexe $exe_param");

		$dnp{$nn} = [ @retval ];
	  }
	  $dms{$ms} = { %dnp };
	}
	$Result{$testexe} = { %dms };
  }
}

###########################################################################
###########################################################################

if ($RunningFlag != 0) {
  #------------
  open(OUTFILE_H, "> $LogFile") or die "Can't open output file: $LogFile to store output";
  RunTestSuite();
  close OUTFILE_H;
  #------------
} elsif ($ParsingFlag != 0) {
  #
  open(TEST_IN, "< $LogFile") or die "Can't open test log file: $LogFile to parse";
  #while(<TEST_IN>) { push @TestLog, $_; }
  RunTestSuite();
  close TEST_IN;
  #
  PrintOut();

} else {
  print "This is dry run, no actual running\n";
  RunTestSuite;
}

