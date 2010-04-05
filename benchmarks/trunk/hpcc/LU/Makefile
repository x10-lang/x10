PACKAGE := rc7

SRCS = ${PACKAGE}/LU.x10 ${PACKAGE}/BlockedArray.x10 ${PACKAGE}/Comm.x10 ${PACKAGE}/Timer.x10
NATIVES = ${PACKAGE}/essl_natives.cc ${PACKAGE}/essl_natives.h

all: lu-opt lu-dbg

lu-opt: $(SRCS) $(NATIVES)
	${MKDIR} opt-gen/${PACKAGE}
	${X10CXX} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS} ${BLAS_LIB}' -o $@


lu-dbg: $(SRCS) $(NATIVES)
	${MKDIR} dbg-gen/${PACKAGE}
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS} ${BLAS_LIB}' -o $@


clean:
	rm -rf opt-gen dbg-gen
	rm -f lu-opt lu-dbg
	rm -f lu-opt.exe lu-dbg.exe

