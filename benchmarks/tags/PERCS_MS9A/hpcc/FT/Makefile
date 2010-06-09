SRCS	:= fft.x10 Random.x10

all: fft-opt fft-dbg


fft-opt: ${SRCS}
	${MKDIR} opt-gen/FT
	${X10CXX} -O -NO_CHECKS -d opt-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@
ifeq ($(USE_MEDIUM_PAGES), 1)
ifeq ($(shell uname -s), AIX)
	ldedit -bnolpdata $@
	ldedit -btextpsize:64K -bdatapsize:64K -bstackpsize:64K $@
endif
endif


fft-dbg: ${SRCS}
	${MKDIR} dbg-gen/FT
	${X10CXX} -d dbg-gen ${X10CXXFLAGS} ${SRCS} -post '# ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_DBGFLAGS} # # ${POSTCOMPILE_LDFLAGS}' -o $@

clean:
	rm -rf opt-gen dbg-gen
	rm -f fft-opt fft-dbg
	rm -f fft-opt.exe fft-dbg.exe
