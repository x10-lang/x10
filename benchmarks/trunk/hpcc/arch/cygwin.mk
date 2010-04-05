# User customizable platform specific stuff
#
# Cygwin with G++
#
export POSTCOMPILE_OPTFLAGS	?= -O3

export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3

export BLAS_LIB		?= -lblas

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp -r
