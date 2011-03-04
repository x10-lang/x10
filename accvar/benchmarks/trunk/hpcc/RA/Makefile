
SRCS	:= FRASimpleDist.x10

all: ra-opt ra-dbg

ra-opt: ${SRCS}
	${MKDIR} opt-gen
	${X10CXX} -O -NO_CHECKS -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@
ifeq ($(USE_MEDIUM_PAGES), 1)
ifeq ($(shell uname -s), AIX)
	ldedit -bnolpdata $@
	ldedit -btextpsize:64K -bdatapsize:64K -bstackpsize:64K $@
endif
endif

ra-exp: ${SRCS}
	${MKDIR} exp-gen
	${X10CXX} -O -NO_CHECKS -EXPERIMENTAL -d exp-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@
ifeq ($(USE_MEDIUM_PAGES), 1)
ifeq ($(shell uname -s), AIX)
	ldedit -bnolpdata $@
	ldedit -btextpsize:64K -bdatapsize:64K -bstackpsize:64K $@
endif
endif

ra-dbg: ${SRCS}
	${MKDIR} dbg-gen
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	rm -rf opt-gen dbg-gen exp-gen
	rm -f ra-opt ra-dbg ra-exp
	rm -f ra-opt.exe ra-dbg.exe ra-exp.exe
