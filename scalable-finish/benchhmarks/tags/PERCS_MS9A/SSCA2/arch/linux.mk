# User customizable platform specific stuff
#
# Linux with G++
#
ifeq ($(PGASTRANSPORT), mpi)
export POSTCOMPILE_CXX		?= mpicxx
export POSTCOMPILE_CXXFLAGS 	?= -m64 -I/usr/local/mpi/include -I$(HOME)/mpi/libnbc-install/include
export POSTCOMPILE_LDFLAGS	?= -L$(HOME)/mpi/libnbc-install/lib
export POSTCOMPILE_LIBS		?= -lnbc -ldl -lm -lpthread
endif
ifeq ($(PGASTRANSPORT), lapi)
export POSTCOMPILE_CXX		?= mpCC
export POSTCOMPILE_CXXFLAGS 	?= -m64 -DTRANSPORT=${PGASTRANSPORT}
export POSTCOMPILE_LDFLAGS	?= 
export POSTCOMPILE_LIBS		?=  -ldl -lm -lpthread -lrt 
else
export POSTCOMPILE_CXX		?= g++
export POSTCOMPILE_CXXFLAGS 	?=  -m64 
export POSTCOMPILE_LDFLAGS	?= 
export POSTCOMPILE_LIBS		?=  -ldl -lm -lpthread -lrt 
endif

export POSTCOMPILE_OPTFLAGS	?= -O3 -DNO_CHECKS -DNDEBUG 
export POSTCOMPILE_DBGFLAGS	?= -g -pg

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -DNDEBUG

export POSTCOMPILE_INCLUDEOPT ?= -include 

ifeq ($(USE_ACML), 1)
export BLAS_LIB		?= /opt/acml4.3.0/gfortran64/lib/libacml.a
export EXTRA_LIBS	?= /usr/lib/gcc/x86_64-redhat-linux/4.1.1/libgfortran.a
else
export BLAS_LIB		?= -lblas
export EXTRA_LIBS	?=
endif

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp -r
export OUTDIR    ?= out
