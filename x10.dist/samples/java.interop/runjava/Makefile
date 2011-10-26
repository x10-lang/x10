#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2011.
#

X10C	?= x10c
X10J	?= x10j
X10	?= x10

all:	RunJava.jar

RunJava.jar:	RunJava.x10
	$(X10C) -o RunJava.jar RunJava.x10

clean:
	rm -f RunJava.jar
