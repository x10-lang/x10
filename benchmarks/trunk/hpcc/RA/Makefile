
SRCS	:= FRASimpleDist.x10

all: ra-opt ra-dbg

ra-opt: ${SRCS}
	${MKDIR} opt-gen
	${X10CXX} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

ra-dbg: ${SRCS}
	${MKDIR} dbg-gen
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	rm -rf opt-gen dbg-gen
	rm -f ra-opt ra-dbg
	rm -f ra-opt.exe ra-dbg.exe
