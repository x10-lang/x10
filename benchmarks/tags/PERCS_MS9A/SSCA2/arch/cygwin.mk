# User customizable platform specific stuff
#
# Cygwin with G++
#
export POSTCOMPILE_CXX		?= g++-4
export POSTCOMPILE_CXXFLAGS 	?= -w -DTRANSPORT=${PGASTRANSPORT}
export POSTCOMPILE_LDFLAGS	?= 
export POSTCOMPILE_LIBS		?= -lm -lpthread

export POSTCOMPILE_OPTFLAGS	?= -O3 -DNDEBUG -DNO_CHECKS
export POSTCOMPILE_DBGFLAGS	?= -g

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3

export POSTCOMPILE_INCLUDEOPT ?= -include 

export BLAS_LIB		?= -lblas
export EXTRA_LIBS	?= 

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp -r
