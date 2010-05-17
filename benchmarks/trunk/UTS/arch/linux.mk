# User customizable platform specific stuff
#
# Linux with G++
#
ifeq ($(USE_MPI), 1)
export POSTCOMPILE_CXXFLAGS ?= -I/usr/local/mpi/include -I$(HOME)/mpi/libnbc-install/include
export POSTCOMPILE_LDFLAGS	?= -L$(HOME)/mpi/libnbc-install/lib -lnbc
endif

export POSTCOMPILE_OPTFLAGS	?= -O3

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -DNDEBUG

ifeq ($(USE_ACML), 1)
export BLAS_LIB		?= /opt/acml4.3.0/gfortran64/lib/libacml.a /usr/lib/gcc/x86_64-redhat-linux/4.1.1/libgfortran.a
else
export BLAS_LIB		?= -lblas
endif

export POSTCOMPILE_CXX		?= g++
export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp -r

LDFLAGS+= -Wl,--rpath -Wl,$(X10_LIB_DIR) -Wl,--rpath -Wl,.