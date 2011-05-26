#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2010.
#

ifeq ($(X10RT_PLATFORM), cygwin)

JNI_EXECUTABLES = bin/cyglaunch.exe
EXECUTABLES += $(JNI_EXECUTABLES)

jni/cyglaunch/cyglaunch.exe: jni/cyglaunch/cyglaunch.cc
	$(MAKE) X10_HOME="$(X10_HOME)" -C jni/cyglaunch all

${JNI_EXECUTABLES}: ${JNI_EXECUTABLES:bin/%=jni/cyglaunch/%}
	$(CP) $^ bin/

clean: jni_clean
jni_clean:
	$(MAKE) X10_HOME="$(X10_HOME)" -C jni/cyglaunch clean
endif
