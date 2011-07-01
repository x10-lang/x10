#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# print usage message and return
# usage: printUsage exitcode detail
# the displayed help text is formatted to 72 characters - ie, any line width
# shouldn't exceed the following reference
# 12345678901234567890123456789012345678901234567890123456789012345678901234567890
# global variables used:
# __x10th_progname
function printUsage() {
	local -i exitcode="$1"
	local -i detail="$2"

	printf >&3 "$(printVersion)"
    printf >&3 "
Usage:

${__x10th_progname} <<OPTIONS>> [<test1 test2 ... testN>]

<<OPTIONS>>

  [-timeLimit <secs>] [-logPath <dir>] [-workRoot <dir>] [-testRoot <dir>]
  [-listFile <file>] [-runFile <file>] [-revision <id>] [-polyglot <id>]
  [-rptFormat <text|xml>] [-mailList <user1@host ... userN@host>]
  [-backend <java|c++>] [-java <path>] [-compiler <xlc|gcc>] [-compopts <options>]
  [-pgas <path>] [-transport <sockets|lapi>]
  [-runLevel <id>] [-h|-help|--help] [-v|-version|--version]

"
	if (( $detail == 1 )); then
	printf >&3 "
The command \"${__x10th_progname}\" runs the X10 test harness on the invoking host
with the specified set of options - one can limit the execution to some tests
by providing the test file(s) or file pattern(s) at the end.

-timeLimit <secs>
This parameter specifies how many seconds to wait during the compilation and
execution steps of a test case, before killing the associated process and
declaring that the testcase is failed in processing.
(default = 60 seconds)

-logPath <dir>
Specify the directory path where the log files generated during the current
regression run will be stored.  If no value is specified, this command's
invoking directory will be used for this purpose.

-workRoot <dir>
Provide the home directory for all the x10 modules.  If not specified,
\"x10th.unified\" directory under the system \"tmp\" directory will be used
for this purpose.

-testRoot <dir>
Testcases are found recursively starting at the top level of this directory,
and are executed in the same order as they are found.  By default, \"examples\"
directory found under the module \"x10.tests\" will be used for this purpose.

-listFile <file>
This option indicates a file containing a list of shell file patterns denoting
the testcases to run.  This file can also contain comment lines beginning with
a '#' character, and blank lines for clarity.
(default = all valid testcases)

-runFile <file>
Supply the global data file that provides additional information about the test
cases during their compilation and execution.  By default, \"testrun.dat\"
file found under the module \"x10.tests\" directory \"data\" will be used
for this purpose.  Please look into this data file for adding more entries.

-revision <id>
Provide the subversion revision specifier for checking out the X10 modules.
(default = HEAD)

-polyglot <id>
Provide the subversion revision specifier for checking out the polyglot module.
(default = HEAD)

-rptFormat <text|xml>
Specify the format of the report file to be prepared and possibly mailed.
(default = text)

-mailList <user1@host ... userN@host>
Specify the list of users, who should receive the generated report files.
(default = none)

-backend <java|c++>
Which backend needs to be used for running the regression tests.
(default = java)

-java <path>
Specify path to the java executable for running the current regression.
(default = whichever available on the default command path)

-compiler <xlc|gcc>
Specify which post-processing compiler to use for running c++ backend tests.
(default = platform dependent)

-compopts <options>
Provide additional options to the choosen post-processing compiler.
(default = none)

-pgas <path>
Provide the installed root of pgas headers and libraries.
(default = none)

-transport <sockets|lapi>
Specify which transport to use for running c++ backend tests.
(default = platform dependent)

-runLevel <id>
This options sets the desired runlevel for regression:
    runlevel    performed tasks
    --------    ---------------
        1       (only) checkout
        2       (1+) build
        3       (2+) makedist
        4       (3+) regress
        5       (4+) report
        6       (5+) mail
        7       (6-) checkout and makedist
        8       (7-) mail
        9       (9-) build

(default = 6)

-h|-help|--help
Print this command line help message.

-v|-version|--version
Display the test harness version information.

"
	fi
	exit $exitcode
}

# vim:tabstop=4:shiftwidth=4:expandtab:nu
