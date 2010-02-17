#!/usr/bin/perl -i

$packageFound = 0;

while (<>) {
    if (m/^package /) {
	$packageFound = 1;
	print "/*\n";
	print " *  This file is part of the X10 project (http://x10-lang.org).\n";
	print " *\n";
	print " *  This file is licensed to You under the Eclipse Public License (EPL);\n";
	print " *  You may not use this file except in compliance with the License.\n";
	print " *  You may obtain a copy of the License at\n";
	print " *      http://www.opensource.org/licenses/eclipse-1.0.php\n";
	print " *\n";
	print " *  (C) Copyright IBM Corporation 2006-2010.\n";
	print " */\n\n";
    }
    if ($packageFound) {
	print $_;
    }
}

