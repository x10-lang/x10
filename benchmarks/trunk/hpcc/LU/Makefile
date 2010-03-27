

PACKAGE	:= rc7

SRCS = ${PACKAGE}/*.x10

all: lu-opt lu-dbg

lu-opt: $(SRCS)
	${MKDIR} opt-gen/${PACKAGE}
	${X10CXX} -x10rt ${X10RTTRANSPORT} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} -o $@


lu-dbg: $(SRCS)
	${MKDIR} dbg-gen/${PACKAGE}
	${X10CXX} -x10rt ${X10RTTRANSPORT} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -o $@


clean:
	rm -rf opt-gen dbg-gen
	rm -f lu-opt lu-dbg
	rm -f lu-opt.exe lu-dbg.exe
