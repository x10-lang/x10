SRCS= \
ssca2/BetweenessCentrality.x10\
ssca2/PTimer.x10\
ssca2/SSCA2.x10\
ssca2/findSubGraphs_dist.x10 \
ssca2/genScaleData_dist.x10 \
ssca2/genScaleData.x10 \
ssca2/computeGraph.x10 \
ssca2/computeGraph_dist.x10\
ssca2/getStartLists_dist.x10\
ssca2/Comm.x10\
ssca2/runtime_consts.x10\
ssca2/types.x10\
ssca2/defs.x10\
ssca2/util.x10 \
ssca2/Triplet.x10 \
ssca2/Quad.x10

CSRCS= \
$(OUTDIR)/ssca2/BetweenessCentrality.cc\
$(OUTDIR)/ssca2/BetweenessCentrality__WrapRail.cc\
$(OUTDIR)/ssca2/LinearArray.cc\
$(OUTDIR)/ssca2/PTimer.cc\
$(OUTDIR)/ssca2/SSCA2.cc\
$(OUTDIR)/ssca2/genScaleData.cc\
$(OUTDIR)/ssca2/genScaleData_dist.cc\
$(OUTDIR)/ssca2/computeGraph.cc\
$(OUTDIR)/ssca2/computeGraph_dist.cc\
$(OUTDIR)/ssca2/getStartLists_dist.cc\
$(OUTDIR)/ssca2/Comm.cc\
$(OUTDIR)/ssca2/Quad.cc\
$(OUTDIR)/ssca2/Triplet.cc\
$(OUTDIR)/ssca2/Comm__Integer.cc\
$(OUTDIR)/ssca2/defs.cc\
$(OUTDIR)/ssca2/defs__edge.cc\
$(OUTDIR)/ssca2/defs__graph.cc\
$(OUTDIR)/ssca2/defs__graphSDG.cc\
$(OUTDIR)/ssca2/defs__graphSDG_dist.cc\
$(OUTDIR)/ssca2/defs__pGraph.cc\
$(OUTDIR)/ssca2/defs__pGraphLocal.cc\
$(OUTDIR)/ssca2/findSubGraphs_dist.cc\
$(OUTDIR)/ssca2/runtime_consts.cc\
$(OUTDIR)/ssca2/types.cc\
$(OUTDIR)/ssca2/util.cc

OBJS= \
$(OUTDIR)/ssca2/Quad.o\
$(OUTDIR)/ssca2/Triplet.o\
$(OUTDIR)/ssca2/Comm__Integer.o\
$(OUTDIR)/ssca2/defs.o\
$(OUTDIR)/ssca2/defs__edge.o\
$(OUTDIR)/ssca2/defs__graph.o\
$(OUTDIR)/ssca2/defs__graphSDG.o\
$(OUTDIR)/ssca2/defs__graphSDG_dist.o\
$(OUTDIR)/ssca2/defs__pGraph.o\
$(OUTDIR)/ssca2/defs__pGraphLocal.o\
$(OUTDIR)/ssca2/runtime_consts.o\
$(OUTDIR)/ssca2/types.o\
$(OUTDIR)/ssca2/util.o \
$(OUTDIR)/ssca2/Comm.o\
$(OUTDIR)/ssca2/SSCA2.o\
$(OUTDIR)/ssca2/genScaleData.o\
$(OUTDIR)/ssca2/genScaleData_dist.o\
$(OUTDIR)/ssca2/computeGraph.o\
$(OUTDIR)/ssca2/computeGraph_dist.o\
$(OUTDIR)/ssca2/getStartLists_dist.o\
$(OUTDIR)/ssca2/findSubGraphs_dist.o\
$(OUTDIR)/ssca2/BetweenessCentrality.o\
$(OUTDIR)/ssca2/BetweenessCentrality__WrapRail.o\
$(OUTDIR)/ssca2/LinearArray.o\
$(OUTDIR)/ssca2/PTimer.o

.SUFFIXES: .cc .cxx .cpp .C .c .o .h .x10

LIBS = -L$(X10HOME)/lib -lx10 -lx10rt_$(X10RTTRANSPORT)

NATIVE_LIBS = $(PWD)/sprng2.0/lib/libsprng.a

FINAL = $(OUTDIR)/ssca2

dist-1d-dbg: 
	rm -f templateregistry
	$(MAKE) $(OUTDIR)/CCGEN  
	$(MAKE) $(FINAL)-dbg

dist-1d-opt:
	rm -f templateregistry
	$(MAKE) $(OUTDIR)/CCGEN  
	$(MAKE) $(FINAL)-opt

clean: 
	rm -r -f $(OUTDIR)/ssca2
	rm -r -f $(OUTDIR)/CCGEN
	rm -f $(FINAL)-dbg
	rm -f $(FINAL)-opt
	rm -f templateregistry

$(OUTDIR)/CCGEN: $(SRCS)
	$(X10CXX) -c -d $(OUTDIR) $(X10CXXFLAGS) $?
	touch $@

$(FINAL)-opt: OPT=$(POSTCOMPILE_OPTFLAGS) 
$(FINAL)-opt: $(CCSRCS) $(OBJS)
	${POSTCOMPILE_CXX}  $(OPT) $(POSTCOMPILE_CXXFLAGS) -o $@ $(OBJS)  $(LIBS) $(POSTCOMPILE_LDFLAGS) $(POSTCOMPILE_LIBS) $(NATIVE_LIBS)

$(FINAL)-dbg: OPT=$(POSTCOMPILE_DBGFLAGS) 
$(FINAL)-dbg: $(CCSRCS) $(OBJS)
	${POSTCOMPILE_CXX}  $(OPT) $(POSTCOMPILE_CXXFLAGS) -o $@ $(OBJS)  $(LIBS) $(POSTCOMPILE_LDFLAGS) $(POSTCOMPILE_LIBS) $(NATIVE_LIBS)

.cc.o:
	time $(POSTCOMPILE_CXX) $(OPT) $(POSTCOMPILE_CXXFLAGS) -I$(OUTDIR) -I$(X10HOME)/include -I$(PWD)/sprng2.0/include -c $< -o $@
