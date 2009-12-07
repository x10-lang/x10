# User customizable platform specific stuff
#


# AIX with XLC
#
export POSTCOMPILE_CXX		?= mpCC_r
export POSTCOMPILE_CXXFLAGS 	?=
export POSTCOMPILE_LDFLAGS	?= 
export POSTCOMPILE_LIBS		?= 

export POSTCOMPILE_OPTFLAGS	?= -DNO_CHECKS
export POSTCOMPILE_DBGFLAGS	?= -g

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -q64 -qarch=pwr5 -qtune=pwr5 -qhot -qinline -qrtti=all -DNDEBUG

export POSTCOMPILE_INCLUDEOPT ?= -qinclude=

export BLAS_LIB		?= -lessl
export EXTRA_LIBS	?= 

export USE_MEDIUM_PAGES ?= 1

# AIX with G++
#
#export POSTCOMPILE_CXX		?= mpCC
#export POSTCOMPILE_CXXFLAGS 	?= -w -q64 -DTRANSPORT=${X10TRANSPORT}

#export POSTCOMPILE_OPTFLAGS	?= -O3 -qarch=pwr5 -qtune=pwr5 -qhot -qinline -qrtti=all -DNDEBUG -DNO_CHECKS
#export POSTCOMPILE_DBGFLAGS	?= -g

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= -X64 -rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp 
