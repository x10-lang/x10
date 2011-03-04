PACKAGE := rc7

#SRCS = ${PACKAGE}/*.x10 ../util/*.x10
SRCS = ${PACKAGE}/*.x10
NATIVES = ${PACKAGE}/essl_natives.cc ${PACKAGE}/essl_natives.h

all: lu-opt lu-dbg

lu-opt: $(SRCS) $(NATIVES)
	${MKDIR} opt-gen/${PACKAGE}
	${X10CXX} -STATIC_CALLS -O -NO_CHECKS -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS} ${BLAS_LIB}' -o $@
ifeq ($(USE_MEDIUM_PAGES), 1)
ifeq ($(shell uname -s), AIX)
	ldedit -bnolpdata $@
	ldedit -btextpsize:64K -bdatapsize:64K -bstackpsize:64K $@
endif
endif


lu-exp: $(SRCS) $(NATIVES)
	${MKDIR} exp-gen/${PACKAGE}
	${X10CXX} -STATIC_CALLS -O -NO_CHECKS -EXPERIMENTAL -d exp-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS} ${BLAS_LIB}' -o $@
ifeq ($(USE_MEDIUM_PAGES), 1)
ifeq ($(shell uname -s), AIX)
	ldedit -bnolpdata $@
	ldedit -btextpsize:64K -bdatapsize:64K -bstackpsize:64K $@
endif
endif


lu-dbg: $(SRCS) $(NATIVES)
	${MKDIR} dbg-gen/${PACKAGE}
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS} ${BLAS_LIB}' -o $@


clean:
	rm -rf opt-gen dbg-gen exp-gen
	rm -f lu-opt lu-dbg lu-exp
	rm -f lu-opt.exe lu-dbg.exe lu-exp.exe

