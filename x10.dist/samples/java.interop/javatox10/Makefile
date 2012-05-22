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
X10J	?= java -jar "$(PKG_CONF_LIB)/ecj.jar" -1.5 -nowarn -encoding utf-8 -classpath "$(PKG_CONF_STDLIB)/x10.jar$(PATH_SEP)$(PKG_CONF_LIB)/commons-math-2.2.jar"
X10	?= x10
PATH_SEP=$(if $(filter CYGWIN%, $(shell uname -s)),;,:)
PKG_CONF_LIB:=$(shell $(X10) -pkg-conf=lib)
PKG_CONF_STDLIB:=$(shell $(X10) -pkg-conf=stdlib)

all:	bin-java/JClass.class

bin/XClass.class:	src/XClass.x10
	$(X10C) -d bin src/XClass.x10

bin-java/JClass.class:	src-java/JClass.java bin/XClass.class
	$(X10J) -d bin-java -cp bin src-java/JClass.java

run:	bin-java/JClass.class
	$(X10) -cp "../runjava/RunJava.jar$(PATH_SEP)bin$(PATH_SEP)bin-java" -Dx10.EXTRA_PRELOAD_CLASSES=XClass RunJava JClass

runmulti:	bin-java/JClass.class
	X10_NPLACES=4 $(X10) -cp "../runjava/RunJava.jar$(PATH_SEP)bin$(PATH_SEP)bin-java" -Dx10.EXTRA_PRELOAD_CLASSES=XClass RunJava JClass

clean:
	rm -f bin-java/* bin/*
