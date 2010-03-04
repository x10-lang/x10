include Makefile.var

SRCS	:= SSCA2.x10 runtime_consts.x10 types.x10 defs.x10 util.x10 genScaleData.x10 computeGraph.x10 getStartLists.x10 findSubGraphs.x10


NATIVE_HDR	:= /vol/x10/users/ganesh/work-17/hpcc/SSCA2/smp/sprng2.0/sprng.h
NATIVE_LIB	:= /vol/x10/users/ganesh//work-17/hpcc/SSCA2/smp/sprng2.0/lib/libsprng.a
NATIVE_INC	:= 
NATIVE_COMM_HDR := 
XRX_INC         := 

all: ssca2-opt


ssca2-opt: ${SRCS}
	$(MAKE) -C sprng2.0
	${X10CXX} -x10rt ${X10RTTRANSPORT} ${X10CXXFLAGS} -d /bench1/ganesh ${SRCS} \
		-post '${POSTCOMPILE_CXX} ${POSTCOMPILE_INCLUDEOPT}${NATIVE_HDR} ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} -DTRANSPORT=${PGASTRANSPORT} # ${NATIVE_SRC} -q32 # ${POSTCOMPILE_LDFLAGS} ${POSTCOMPILE_LIBS} ${NATIVE_LIB}' 

clean:
	$(MAKE) -C sprng2.0 clean
