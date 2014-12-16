#!/usr/bin/perl -i

$packageFound = 0;


sub printHeader {
    print "/*\n";
    print " *  This file is part of the X10 project (http://x10-lang.org).\n";
    print " *\n";
    print " *  This file is licensed to You under the Eclipse Public License (EPL);\n";
    print " *  You may not use this file except in compliance with the License.\n";
    print " *  You may obtain a copy of the License at\n";
    print " *      http://www.opensource.org/licenses/eclipse-1.0.php\n";
    print " *\n";
    print " * This file was originally derived from the Polyglot extensible compiler framework.\n";
    print " *\n";
    print " *  (C) Copyright 2000-2007 Polyglot project group, Cornell University\n";
    print " *  (C) Copyright IBM Corporation 2007-2014.\n";
    print " */\n\n";
}

while (<>) {
    if ($packageFound) {
	print $_;
    } else {
	# Mainly want to look for the opening 'package statement', but we
	# also have to deal with the fact that some of the programs (tests, samples) 
	# are in the default package and therefore don't have opening package statements.
        if (m/^package / || m/^import / || m/^interface / || m/^class / || m/^public / || m/^final / || m/LIMITATION/ || m/STATUS/ || m/OPTION/) {
	    $packageFound = 1;
	    printHeader();
	    print $_;
	}
    }	
}

