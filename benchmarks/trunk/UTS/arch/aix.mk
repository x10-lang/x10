# User customizable platform specific stuff
#


# AIX with XLC
#
export POSTCOMPILE_NATIVE_LIB_FLAGS ?= -O3 -q64 -qarch=auto -qtune=auto -qhot -qinline -qrtti=all -DNDEBUG

export USE_MEDIUM_PAGES ?= 1

export POSTCOMPILE_CXX		?= mpCC
export POSTCOMPILE_CXXFLAGS 	?= -w -q64 -DTRANSPORT=${PGASTRANSPORT} -brtl

export POSTCOMPILE_OPTFLAGS	?= -O3 -qhot -qinline -qrtti=all -DNDEBUG -DNO_CHECKS

export POSTCOMPILE_AR		?= ar
export POSTCOMPILE_ARFLAGS	?= -X64 -rv
export POSTCOMPILE_RANLIB   	?= ranlib
export MKDIR	?= mkdir -p
export CP	?= cp 

