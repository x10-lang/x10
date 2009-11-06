# A top-level Makefile to compile all X10 sources

# <BEGIN> user settings #

export X10CXX 		?= x10c++
export X10CXXFLAGS 	?= -commandlineonly -report postcompile=5 -DISABLE_GC
export X10TRANSPORT	?= lapi


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
	RA-dbg

all_opt: FT-opt \
	LU-opt \
	STREAM-opt \
	RA-opt

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

clean: FT-clean LU-clean STREAM-clean RA-clean

.PHONY: FT LU STREAM RA
.PHONY: clean
