ifeq ($(X10RT_PLATFORM), cygwin)

# DAVE G: Disabled because the rule doesn't work in
#         in a "clean" source tree.  You can't attempt to
#         build an exe here that is going to link against
#         x10.dist/lib/x10rt_sockets.so because the dist rule
#         for the overall x10rt rule hasn't run yet to cause
#         the library to be copied to x10.dist/lib
#
#JNI_EXECUTABLES = bin/cyglaunch.exe
#EXECUTABLES += $(JNI_EXECUTABLES)

${JNI_EXECUTABLES:bin/%=jni/cyglaunch/%}:
	-$(MAKE) -C jni/cyglaunch $^

${JNI_EXECUTABLES}: ${JNI_EXECUTABLES:bin/%=jni/cyglaunch/%}
	$(CP) $^ bin/
endif
