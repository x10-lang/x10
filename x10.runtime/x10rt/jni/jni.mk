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
