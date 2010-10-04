SRCS	:= fft.x10 HPL_Dist.x10 ../util/*.x10

all: fft-opt fft-dbg

fft-opt: ${SRCS}
	${MKDIR} opt-gen
	${X10CXX} -O -NO_CHECKS -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@


fft-dbg: ${SRCS}
	${MKDIR} dbg-gen
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	rm -rf opt-gen dbg-gen
	rm -f fft-opt fft-dbg
	rm -f fft-opt.exe fft-dbg.exe
