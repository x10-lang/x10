SRCS	:= fft.x10 Random.x10

NATIVE_HDR	:= ft_natives.h
NATIVE_SRC 	:= ft_natives.cc
NATIVE_LIB	:= fft-hpcc/libfftw3.a
NATIVE_INC	:= fft-hpcc/include

all: fft-opt fft-dbg


fft-opt: ${SRCS}
	$(MAKE) -C fft-hpcc
	${MKDIR} opt-gen
	${CP} ${NATIVE_SRC} opt-gen
	${X10CXX} -x10rt ${X10TRANSPORT} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} \
		-post '${POSTCOMPILE_CXX} ${POSTCOMPILE_INCLUDEOPT}../${NATIVE_HDR} -I../${NATIVE_INC} ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # ${NATIVE_SRC} # ${POSTCOMPILE_LDFLAGS} ${POSTCOMPILE_LIBS} ../${NATIVE_LIB}' -o $@


fft-dbg: ${SRCS}
	$(MAKE) -C fft-hpcc
	${MKDIR} dbg-gen
	${CP} ${NATIVE_SRC} dbg-gen
	${X10CXX} -x10rt ${X10TRANSPORT} -d dbg-gen ${X10CXXFLAGS} ${SRCS} \
		-post '${POSTCOMPILE_CXX} ${POSTCOMPILE_INCLUDEOPT}../${NATIVE_HDR} -I../${NATIVE_INC} ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # ${NATIVE_SRC} # ${POSTCOMPILE_LDFLAGS} ${POSTCOMPILE_LIBS} ../${NATIVE_LIB}' -o $@

clean:
	$(MAKE) -C fft-hpcc clean
	rm -rf opt-gen dbg-gen
	rm -f fft-opt fft-dbg
	rm -f fft-opt.exe fft-dbg.exe
