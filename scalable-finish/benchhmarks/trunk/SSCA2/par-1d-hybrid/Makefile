SRCS= \
ssca2/BetweenessCentrality.x10\
ssca2/BetweenessAsync.x10\
ssca2/BetweenessAlltoall.x10\
ssca2/AbsStat.x10\
ssca2/PTimer.x10\
ssca2/PStat.x10\
ssca2/SSCA2.x10\
ssca2/LinearArray.x10\
ssca2/WrapRail.x10\
ssca2/FindSubGraphs.x10\
ssca2/GenScaleData.x10\
ssca2/GenScaleDataSeq.x10\
ssca2/ComputeGraph.x10\
ssca2/GetStartLists.x10\
ssca2/Comm.x10\
ssca2/Consts.x10\
ssca2/Types.x10\
ssca2/Defs.x10\
ssca2/Triplet.x10 \
ssca2/Quad.x10

CSRCS= \
$(OUTDIR)/ssca2/BetweenessCentrality.cc\
$(OUTDIR)/ssca2/BetweenessAsync.cc\
$(OUTDIR)/ssca2/LinearArray.cc\
$(OUTDIR)/ssca2/AbsStat.cc\
$(OUTDIR)/ssca2/PTimer.cc\
$(OUTDIR)/ssca2/PStat.cc\
$(OUTDIR)/ssca2/LinearArray.cc\
$(OUTDIR)/ssca2/WrapRail.cc\
$(OUTDIR)/ssca2/SSCA2.cc\
$(OUTDIR)/ssca2/GenScaleData.cc\
$(OUTDIR)/ssca2/GenScaleDataSeq.cc\
$(OUTDIR)/ssca2/ComputeGraph.cc\
$(OUTDIR)/ssca2/GetStartLists.cc\
$(OUTDIR)/ssca2/FindSubGraphs.cc\
$(OUTDIR)/ssca2/Comm.cc\
$(OUTDIR)/ssca2/Quad.cc\
$(OUTDIR)/ssca2/Triplet.cc\
$(OUTDIR)/ssca2/Comm__Integer.cc\
$(OUTDIR)/ssca2/Defs.cc\
$(OUTDIR)/ssca2/Defs__edge.cc\
$(OUTDIR)/ssca2/Defs__pGraph.cc\
$(OUTDIR)/ssca2/Defs__pGraphLocal.cc\
$(OUTDIR)/ssca2/Consts.cc\
$(OUTDIR)/ssca2/Types.cc

OBJS= \
$(OUTDIR)/ssca2/Quad.o\
$(OUTDIR)/ssca2/Triplet.o\
$(OUTDIR)/ssca2/Comm__Integer.o\
$(OUTDIR)/ssca2/Defs.o\
$(OUTDIR)/ssca2/Defs__edge.o\
$(OUTDIR)/ssca2/Defs__pGraph.o\
$(OUTDIR)/ssca2/Defs__pGraphLocal.o\
$(OUTDIR)/ssca2/Consts.o\
$(OUTDIR)/ssca2/Types.o\
$(OUTDIR)/ssca2/LinearArray.o\
$(OUTDIR)/ssca2/WrapRail.o\
$(OUTDIR)/ssca2/Comm.o\
$(OUTDIR)/ssca2/SSCA2.o\
$(OUTDIR)/ssca2/GenScaleData.o\
$(OUTDIR)/ssca2/GenScaleDataSeq.o\
$(OUTDIR)/ssca2/ComputeGraph.o\
$(OUTDIR)/ssca2/GetStartLists.o\
$(OUTDIR)/ssca2/FindSubGraphs.o\
$(OUTDIR)/ssca2/BetweenessCentrality.o\
$(OUTDIR)/ssca2/BetweenessAsync.o\
$(OUTDIR)/ssca2/BetweenessAlltoall.o\
$(OUTDIR)/ssca2/AbsStat.o\
$(OUTDIR)/ssca2/PTimer.o\
$(OUTDIR)/ssca2/PStat.o

.SUFFIXES: .cc .cxx .cpp .C .c .o .h .x10

LIBS = -L$(X10HOME)/lib -lx10 -lx10rt_$(X10RTTRANSPORT)

NATIVE_LIBS = $(PWD)/sprng2.0/lib/libsprng.a

FINAL = $(OUTDIR)/ssca2

par-1d-dbg: 
	rm -f templateregistry
	$(MAKE) $(OUTDIR)/CCGEN  
	$(MAKE) $(FINAL)-dbg

par-1d-opt:
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
