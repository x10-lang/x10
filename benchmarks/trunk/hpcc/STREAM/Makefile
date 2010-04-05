
SRCS	:= FSSimpleDist.x10

all: stream-opt stream-dbg

stream-opt: ${SRCS}
	${MKDIR} opt-gen
	${X10CXX} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@
ifeq ($(USE_MEDIUM_PAGES), 1)
ifeq ($(shell uname -s), AIX)
	ldedit -bnolpdata $@
	ldedit -btextpsize:64K -bdatapsize:64K -bstackpsize:64K $@
endif
endif

stream-dbg: ${SRCS}
	${MKDIR} dbg-gen
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	rm -rf opt-gen dbg-gen
	rm -f stream-opt stream-dbg
	rm -f stream-opt.exe stream-dbg.exe
