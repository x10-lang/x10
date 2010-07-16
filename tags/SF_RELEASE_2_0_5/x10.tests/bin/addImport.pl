#!/usr/bin/perl -i

$iclass = shift @ARGV;
$done = 0;
while (<>) {
    if ($done == 0 && m/^import harness\.x10Test/) {
	print $_;
	print "import $iclass;\n";
        $done = 1;
    } else {
        print $_;
    }
}
