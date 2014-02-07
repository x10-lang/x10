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

#====================================================================================================

my @PanelList ;
my $itnum;
my $BatchModeOpt; # Job queue name on Triloka.
my $gml_path;
my $jopts;        # Execution parameters for managed backend

#-------------------------------------------------
#
my @NodeList;           # Node list, number of nodes for all tests
my $ProcPerNode;		# Number of processors used for each node
my $NZDensity;          # Sparse matrix nonzero sparsity
my @TestExecList;

my $RunOpts;
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

#-------------------------------------------------
#
# Default value settings. These valus can be replaced by input parameters
#
my $RunningFlag = 0;            # Default value only allows printing the test cases, or dry run
                                # Using "-r" to run the all test cases
my $ParsingFlag = 0;

#==================================================================
#
my %opt;           # Input options

autoflush STDOUT 1;
getopts('rpb:', \%opt);

if (defined $opt{p}) {$ParsingFlag = 1;} # parsing out log
if (defined $opt{r}) {$RunningFlag = 1;} # start batch executions
if (defined $opt{b}) {$BatchModeOpt= "-p $opt{b}";}
if (defined $opt{h}) {PrintHelp; exit;}
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
Modification may be needed to run on other batch systems, such as PBS, LSF, or LoadLeveler.
Alternatively, you can use dry-run to get list benchmark test comands and creat job
submission scripts.\n";

}
#==================================================================

my @TestLog = ();
my %Result ;

###########################################################################

sub RunTest {
  my $cmd = "$_[0]";

  if ($ParsingFlag != 0) {
	return ParseData($cmd);
  } elsif ($RunningFlag != 0){
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
  my $p; my $t; my $f; my $tt; my $tf;

  while(<TEST_IN>) {
	if ($_ =~ m/$cmd/) {
	  while (<TEST_IN>) {
		if ($_ =~ m/mult --- Panelsize:[\s]*([\d]+), Time:[\s]+([\d.]+) Sec, Mfps:[\s]*([\d.]+)/ ) {
		  $p= $1; $t=$2; $f=$3;

		  while (<TEST_IN>) {
			if ($_ =~ m/multTrans --- Panelsize:[\s]*[\d]+, Time:[\s]*([\d.]+) Sec, Mfps:[\s]*([\d.]+)/ ) {
			  $tt=$1; $tf=$2;
			
			  return ($t, $f, $tt, $tf);
			}
		  }
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
  print("# Scalability test on panel size\n");
  print("# NumProc $NodeList[0] ($ProcPerNode per node)\n");
  print("# Time in second and mega floating point per second (MFPS) per iteration\n");
  print("#       |");
  foreach my $test (@TestExecList) {
	printf("|  %32s   |", $test->{name});
  }
  print("\n");
  print("# Panel |");
  foreach my $test (@TestExecList) {
      printf("|mult:Time,  MFPS  |multTran:Time,MFPS|");
  }
  print("\n");
  #-----------------------------------------
  for my $ms (@PanelList) {
	for my $np (@NodeList) {
	  printf("  %4i  |", $ms);
	  for my $t (@TestExecList) {
		my $exe = $t->{name};
		my $t = 1.0 * $Result{$exe}{$ms}{$np}[0];
		my $f = 1.0 * $Result{$exe}{$ms}{$np}[1];
		my $tt = 1.0 * $Result{$exe}{$ms}{$np}[2];
		my $tf = 1.0 * $Result{$exe}{$ms}{$np}[3];
		printf("| %7.3f , %6.1f | %7.3f , %6.1f |", $t, $f, $tt, $tf);
	  }
	}
	printf("\n");
  }
  print "\n";
}

###########################################################################
sub RunTestSuite {
  foreach my $test (@TestExecList) {
	my $testexe = $test->{name};
	my $launch  = $test->{alloc};
	my $wrap    = $test->{wrapper};
	my %dms=();

	foreach my $ms (@PanelList) {
	  my %dnp=();
	  foreach my $nn (@NodeList) {
		my $np = $nn * $ProcPerNode;
	
		my @retval = RunTest("$launch -N$nn -n$np $BatchModeOpt $wrap $testexe $RunOpts $ms");

		$dnp{$nn} = [ @retval ]; #{ %dps };
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

} else  {
  print "This is dry run, no actual running\n";
  RunTestSuite;
}

