ifeq ($(X10RT_PLATFORM), cygwin)

JNI_EXECUTABLES = bin/cyglaunch.exe
EXECUTABLES += $(JNI_EXECUTABLES)

${JNI_EXECUTABLES:bin/%=jni/cyglaunch/%}:
	-$(MAKE) -C jni/cyglaunch $^

${JNI_EXECUTABLES}: ${JNI_EXECUTABLES:bin/%=jni/cyglaunch/%}
	$(CP) $^ bin/
endif
