# User customizable platform specific stuff
#


# AIX with XLC
#
export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -q64 -qarch=auto -qtune=auto -qhot -qinline -qrtti=all -DNDEBUG

export BLAS_LIB		?= -lessl

export USE_MEDIUM_PAGES ?= 1

# AIX with G++
#
#export POSTCOMPILE_CXX		?= mpCC
#export POSTCOMPILE_CXXFLAGS 	?= -w -q64 -DTRANSPORT=${PGASTRANSPORT}

#export POSTCOMPILE_OPTFLAGS	?= -O3 -qarch=auto -qtune=auto -qhot -qinline -qrtti=all -DNDEBUG -DNO_CHECKS

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= -X64 -rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp 
