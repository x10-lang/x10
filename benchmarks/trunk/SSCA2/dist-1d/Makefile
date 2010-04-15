
SRCS	:= SSCA2.x10 runtime_consts.x10 types.x10 defs.x10 util.x10 genScaleData.x10 genScaleData_dist.x10 computeGraph.x10 findSubGraphs_dist.x10 Comm.x10 getStartLists_dist.x10 computeGraph_dist.x10 Triplet.x10 betweenessCentrality.x10 Quad.x10 PTimer.x10
  

NATIVE_HDR	:= $(PWD)/sprng2.0/sprng.h  ${POSTCOMPILE_INCLUDEOPT}mpi.h ${POSTCOMPILE_INCLUDEOPT}/bench1/x10-2.0/x10.dist/include/pgasrt.h
NATIVE_LIB	:= $(PWD)/sprng2.0/lib/libsprng.a
NATIVE_INC	:= 
NATIVE_COMM_HDR := 
XRX_INC         := 

all: dist-1d-dbg dist-1d-opt


dist-1d-dbg: ${SRCS}
	${X10CXX} -x10rt ${X10RTTRANSPORT} ${X10CXXFLAGS} -d ${OUTDIR} ${SRCS} \
		-post '${POSTCOMPILE_CXX} ${POSTCOMPILE_INCLUDEOPT}${NATIVE_HDR} ${POSTCOMPILE_CXXFLAGS} -g -DTRANSPORT=${PGASTRANSPORT} # ${NATIVE_SRC}  # ${POSTCOMPILE_LDFLAGS} ${POSTCOMPILE_LIBS} ${NATIVE_LIB}' 

clean:
dist-1d-opt: ${SRCS}
	${X10CXX} -x10rt ${X10RTTRANSPORT} ${X10CXXFLAGS} -d ${OUTDIR} ${SRCS} \
		-post '${POSTCOMPILE_CXX} ${POSTCOMPILE_INCLUDEOPT}${NATIVE_HDR} ${POSTCOMPILE_CXXFLAGS} ${POSTCOMPILE_OPTFLAGS} -DTRANSPORT=${PGASTRANSPORT} # ${NATIVE_SRC}  # ${POSTCOMPILE_LDFLAGS} ${POSTCOMPILE_LIBS} ${NATIVE_LIB}' 

clean:
	$(MAKE) -C sprng2.0 clean
