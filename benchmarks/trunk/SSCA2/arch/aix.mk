# User customizable platform specific stuff
#


ifeq ($(USE_GCC),1)

# AIX with G++
#
export POSTCOMPILE_CC	?= gcc
export POSTCOMPILE_CXX		?= g++
export POSTCOMPILE_CXXFLAGS 	?= -w 

export POSTCOMPILE_OPTFLAGS	?= -O2
export POSTCOMPILE_DBGFLAGS	?= -g
export POSTCOMPILE_INCLUDEOPT ?= -include 
export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -maix64  -DNDEBUG


else

#AIX with XLC
#
export POSTCOMPILE_CXX		?= mpCC_r
export POSTCOMPILE_CXXFLAGS 	?= -q64 -w -DTRANSPORT=${PGASTRANSPORT}
export POSTCOMPILE_LDFLAGS	?= -brtl -bbigtoc
export POSTCOMPILE_LIBS		?= -ldl -lm -lpthread -lptools_ptr

export POSTCOMPILE_OPTFLAGS		?= -O3 -qrtti=all -qinline -DNO_CHECKS -DNDEBUG

export POSTCOMPILE_DBGFLAGS	?= -g

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -q64 -qarch=pwr5 -qtune=pwr5 -qhot -qinline -qrtti=all -DNDEBUG

export POSTCOMPILE_INCLUDEOPT ?= -qinclude=

export BLAS_LIB		?= -lessl
export EXTRA_LIBS	?= 

export USE_MEDIUM_PAGES ?= 1
endif

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= -X64 -rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp 
export OUTDIR ?= /bench1/ganesh
