SRCS	:= fft.x10 Random.x10 ../util/*.x10

#NATIVE_HDR	:= ft_natives.h
#NATIVE_SRC 	:= ft_natives.cc Comm.cc
#NATIVE_LIB	:= ../FT/fft-hpcc/libfftw3.a
#NATIVE_INC	:= ../FT/fft-hpcc/include
#NATIVE_COMM_HDR := Comm.h
#XRX_INC         := /vol/x10/x10-2.0/x10.runtime/src-cpp/

all: fft-opt fft-dbg


fft-opt: ${SRCS}
	${MKDIR} opt-gen/FT
	${X10CXX} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@


fft-dbg: ${SRCS}
	${MKDIR} dbg-gen/FT
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	rm -rf opt-gen dbg-gen
	rm -f fft-opt fft-dbg
	rm -f fft-opt.exe fft-dbg.exe
