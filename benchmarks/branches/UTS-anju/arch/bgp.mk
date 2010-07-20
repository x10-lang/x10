# User customizable platform specific stuff
#


# BGP specific settings
#
export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -DNDEBUG

export BLAS_LIB		= -L/opt/ibmmath/lib -lesslbg -lm

export USE_MEDIUM_PAGES ?= 0  # TODO: ask George
export X10RTTRANSPORT = -x10rt pgas_bgp

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= -X64 -rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp -r
