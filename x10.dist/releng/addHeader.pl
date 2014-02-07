#!/usr/bin/perl -i

sub printCHeader {
    print "/*\n";
    print " *  This file is part of the X10 project (http://x10-lang.org).\n";
    print " *\n";
    print " *  This file is licensed to You under the Eclipse Public License (EPL);\n";
    print " *  You may not use this file except in compliance with the License.\n";
    print " *  You may obtain a copy of the License at\n";
    print " *      http://www.opensource.org/licenses/eclipse-1.0.php\n";
    print " *\n";
    print " *  (C) Copyright IBM Corporation 2006-2014.\n";
    print " */\n\n";
}

sub printMakeHeader {
    print "#\n";
    print "#  This file is part of the X10 project (http://x10-lang.org).\n";
    print "#\n";
    print "#  This file is licensed to You under the Eclipse Public License (EPL);\n";
    print "#  You may not use this file except in compliance with the License.\n";
    print "#  You may obtain a copy of the License at\n";
    print "#      http://www.opensource.org/licenses/eclipse-1.0.php\n";
    print "#\n";
    print "#  (C) Copyright IBM Corporation 2006-2014.\n";
    print "#\n\n";
}

$first = 1;
while (<>) {
    if ($first) {
	$first = 0;
	printMakeHeader()
    }
    print $_;
}
