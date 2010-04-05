# A top-level Makefile to compile all X10 sources

# <BEGIN> user settings #

export X10CXX 		?= x10c++

#leave blank by default, let x10c++ choose a sensible default
#export X10RTTRANSPORT	?= -x10rt pgas_lapi

export X10CXXFLAGS 	?= -report postcompile=5 $(X10RTTRANSPORT)



# <END> user settings #

ifeq ($(shell uname -s),AIX)
   include arch/aix.mk
endif
ifeq ($(shell uname -s),Linux)
   include arch/linux.mk
endif
ifeq ($(shell uname -s),Darwin)
   include arch/darwin.mk
endif
ifeq ($(firstword $(subst _, ,$(shell uname -s))),CYGWIN)
   include arch/cygwin.mk
endif
ifeq ($(BGP_CROSS_COMPILE), 1)
   include arch/bgp.mk
endif


all: all_dbg all_opt

all_dbg: FT-dbg \
	LU-dbg \
	STREAM-dbg \
	RA-dbg\
        FT-alltoall-dbg \
        FT-global-dbg

all_opt: FT-opt \
	LU-opt \
	STREAM-opt \
	RA-opt \
        FT-alltoall-opt\
        FT-global-opt

FT: FT-opt FT-dbg

FT-opt:
	$(MAKE) -C FT fft-opt

FT-dbg:
	$(MAKE) -C FT fft-dbg

FT-clean:
	$(MAKE) -C FT clean

LU: LU-opt LU-dbg

LU-opt:
	$(MAKE) -C LU lu-opt

LU-dbg:
	$(MAKE) -C LU lu-dbg

LU-clean:
	$(MAKE) -C LU clean

STREAM: STREAM-opt STREAM-dbg

STREAM-opt:
	$(MAKE) -C STREAM stream-opt

STREAM-dbg:
	$(MAKE) -C STREAM stream-dbg

STREAM-clean:
	$(MAKE) -C STREAM clean

RA: RA-opt RA-dbg

RA-opt:
	$(MAKE) -C RA ra-opt

RA-dbg:
	$(MAKE) -C RA ra-dbg

RA-clean:
	$(MAKE) -C RA clean

FT-alltoall: FT-alltoall-opt FT-alltoall-dbg

FT-alltoall-opt:
	$(MAKE) -C FT-alltoall fft-opt

FT-alltoall-dbg:
	$(MAKE) -C FT-alltoall fft-dbg

FT-alltoall-clean:
	$(MAKE) -C FT-alltoall clean

FT-global-opt:
	$(MAKE) -C FT-global fft-opt

FT-global-dbg:
	$(MAKE) -C FT-global fft-dbg

FT-global-clean:
	$(MAKE) -C FT-global clean

clean: FT-clean LU-clean STREAM-clean RA-clean FT-alltoall-clean

.PHONY: FT LU STREAM RA
.PHONY: clean
