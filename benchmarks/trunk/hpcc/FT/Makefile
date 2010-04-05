SRCS	:= fft.x10 Random.x10 Comm.x10

#NATIVE_HDR	:= ft_natives.h
#NATIVE_SRC 	:= ft_natives.cc Comm.cc
#NATIVE_LIB	:= ../FT/fft-hpcc/libfftw3.a
#NATIVE_INC	:= ../FT/fft-hpcc/include
#NATIVE_COMM_HDR := Comm.h

all: fft-opt fft-dbg


fft-opt: ${SRCS}
	$(MAKE) -C ../FT/fft-hpcc
	${MKDIR} opt-gen/FT
	${X10CXX} -O -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@


fft-dbg: ${SRCS}
	$(MAKE) -C ../FT/fft-hpcc
	${MKDIR} dbg-gen/FT
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	$(MAKE) -C ../FT/fft-hpcc clean
	rm -rf opt-gen dbg-gen
	rm -f fft-opt fft-dbg
	rm -f fft-opt.exe fft-dbg.exe
