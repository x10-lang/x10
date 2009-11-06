# User customizable platform specific stuff
#
# Linux with G++
#
export POSTCOMPILE_CXX		?= g++
export POSTCOMPILE_CXXFLAGS 	?= 
export POSTCOMPILE_LDFLAGS	?= 
export POSTCOMPILE_LIBS		?= 

export POSTCOMPILE_OPTFLAGS	?= -DNO_CHECKS
export POSTCOMPILE_DBGFLAGS	?= -g

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -DNDEBUG

export POSTCOMPILE_INCLUDEOPT ?= -include 

export BLAS_LIB		?= /opt/acml4.3.0/gfortran64/lib/libacml.a
export EXTRA_LIBS	?= /usr/lib/gcc/x86_64-redhat-linux/4.1.1/libgfortran.a

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp -r
