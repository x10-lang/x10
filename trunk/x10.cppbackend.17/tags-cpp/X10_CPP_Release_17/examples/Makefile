X10LANG = ../x10lang
X10LIB ?= ../../x10.lib
override CXXFLAGS += -g
override CXXFLAGS += -I$(X10LIB)/include -I$(X10LANG)
override LDFLAGS += -L$(X10LIB)/lib -L$(X10LANG)
override LDLIBS += -lx10lang -lx10
ifneq ($(wildcard /usr/include/lapi.h),) # AIX
CXX = mpCC_r
override CXXFLAGS -= -g
override CXXFLAGS += -q64
override CXXFLAGS += -O3 -qinline -qarch=pwr5 -qtune=pwr5 -qhot
override CXXFLAGS += -qrtti=all -DNDEBUG
override LDLIBS += -llapi_r
else
override LDLIBS += -llapi
endif

PROGS = HW HWArrays HWCast HWCond HWDistArrays HWException HWExtern HWFinishAtEach HWInterface HelloWorld

all:	$(PROGS)

%:	%.o $(X10LANG)/libx10lang.a
	$(LINK.cc) $(filter %.o,$^) $(LOADLIBES) $(LDLIBS) -o $@

%:	%.cc $(X10LANG)/libx10lang.a
	$(LINK.cc) $(filter %.cc,$^) $(LOADLIBES) $(LDLIBS) -o $@

%.o:	%.cc %.h $(X10LANG)/x10lang.h
	$(CXX) $(CXXFLAGS) -o $@ -c $<

HWExtern: HWExtern.cc HWExtern_externs.cc

.PHONY: all

