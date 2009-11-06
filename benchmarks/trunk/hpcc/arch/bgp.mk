# User customizable platform specific stuff
#


# BGP specific settings
#
export POSTCOMPILE_CXX		= /bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc-bgp-linux-g++
export POSTCOMPILE_CXXFLAGS 	=
export POSTCOMPILE_LDFLAGS	= -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI
export POSTCOMPILE_LIBS		= -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm

export POSTCOMPILE_OPTFLAGS	?= -DNO_CHECKS
export POSTCOMPILE_DBGFLAGS	?= -g

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -DNDEBUG

export POSTCOMPILE_INCLUDEOPT ?= -qinclude=

export BLAS_LIB		= -lessl
export EXTRA_LIBS	= 

export USE_MEDIUM_PAGES ?= 0  # TODO: ask George
export X10TRANSPORT = bgp     # Override default

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
export CP	?= cp -r
