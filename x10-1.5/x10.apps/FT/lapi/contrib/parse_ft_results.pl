#!/usr/bin/perl

my $file = $ARGV[0];

open(FH, $file) || die $!;

my $type = undef;
my $class = undef;
my $mflops = undef;
my %results;
my %breakdown;
my $num = 0;
my $prevnum = 0;
my $procs;
my $prevtype = undef;

undef $type;

while (<FH>) {
    if (/NAS Parallel Benchmarks 2.3/) {
	$type = "MPI-Fortran";
	$num++;
    }
    elsif (m/Done Global Allocation and Initialization/) {
	$type = "MPI-C";
	$num++;
    }
    elsif (m/Berkeley UPC NAS FT 2.3 \(NonBlocking=(\w+)/i) {
	if ($1 =~ m/yes/i) {
	    $type = "BerkeleyUPC-NonBlocking";
	}
	else {
	    $type = "BerkeleyUPC-Blocking";
	}
	$num++;
    }
    elsif (m/ft_mpi.(BB|CC)/) {
	$type = "MPI-C";
	if ($1 eq "BB") {
	    $class = "B";
	} elsif ($1 eq "CC") {
	    $class = "C";
	}
	#printf("NEW!!!!\n");
	$num++;
    }
    elsif (m/ft_mpi.CC/) {
	$type = "MPI-C";
	$class = "C";
	$num++;
    }
    elsif (m/Class[^\w]+(\w)/i) {
	#printf("class is \"$class\"\n");
	#die unless !defined $class;
	#printf("class is prev $class\n");
	$class = $1;
    }
    elsif (m/Mop\/s.*total[^\d]+(\d+.?\d+)/i) {
	#die unless !defined $mflops;
	$mflops = $1;
	#printf("okay now i got what $type.$class $1\n");
    }
    elsif (m/(\d+) Threads MFlops Rate:[^\d]+(\d+.?\d+)/i) {
	$procs = $1;
	$mflops = $2;
	#printf("okay what $type $1 $2\n");
    }
    elsif (m/Number of processes.*:[^\d]+(\d+)/i) {
	$procs = $1;
    }
    elsif (m/Verification/) {
	if (! m/successful/i) {
	    printf STDERR "$type.$class failed to verify!\n";
	}
    }

    # Add some Berkeley profiling
    if ($type =~ /BerkeleyUPC/) {
	if (m/0>\s+([^:]+):\s*(\d+.?\d+)\s+\(/) {
	    #printf ("gotcha $1 $2\n");
	}
    }
    
    if ($num > $prevnum) { # && $num > 1 && $type ne $prevtype && $num > 1) {
	next unless $class && $mflops && $procs;
	my $bench = "$type.$class.$procs";
	push(@{$results{$bench}},$mflops);
	#printf("%-30s Mflops = %8.6f\n", $bench, $mflops);
	#printf("New Benchmark: $prevtype.$class.$procs\n");
	#printf("Mflops = $mflops\n");
	$num++;
	$class = 0;
	$mflops = 0;
	$procs = 0;
    }
    $prevtype = $type;
    $prevnum = $num;
}

close(FH);


sub find_median_stdev {
    my (@vals) = sort { $a <=> $b } @_;
    my $median = $vals[($#vals+1)/2];
    my $stdev = 0.0;

    if ($#vals > 0) {
	my $sumsq = 0.0;
	foreach my $value (@vals) {
	    my $devmean = $value - $median;
	    $sumsq += $devmean * $devmean;
	}
	$stdev = sqrt( $sumsq / $#vals );
    }
    return ($median, $stdev);
}


printf("%-30s Mops/s\n", "Benchmark.Class.Procs");
foreach my $k (sort keys %results) {
    my @a = @{ $results{$k} };
    foreach my $flops (@a) {
	printf("%-30s %8.6f\n", $k, $flops);
    }
}

printf("\nMedians:\n");

foreach my $k (sort keys %results) {
    my @a = @{ $results{$k} };
    my ($med, $stdev) = find_median_stdev(@a);
    printf("%-30s %8.6f\n", $k, $med);
}


