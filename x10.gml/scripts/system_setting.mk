# Platform-specific settings for building GML library and application codes
GML_ELEM_TYPE ?=double
X10CXX ?= x10c++
X10C ?= x10c -verbose 
CXX ?= g++
JAR ?= jar
MAKE ?= make



# JNI include path, for managed GML
ifdef JAVA_HOME
  jarch=$(shell uname -p)
  ifeq ($(jarch),unknown)
    jarch=$(shell uname -m)
  endif

  ifeq ($(shell uname -s),Linux)
    JNI_INCLUDES = -I"$(JAVA_HOME)"/include -I"$(JAVA_HOME)"/include/linux
    ifeq ($(jarch),x86_64)
      jarch=amd64
    endif
    JNI_LIBS = -L"$(JAVA_HOME)"/jre/lib/$(jarch)/j9vm -L"$(JAVA_HOME)"/jre/lib/$(jarch)/server -L"$(JAVA_HOME)"/jre/lib/$(jarch)/client
  else
  ifeq ($(firstword $(subst _, ,$(shell uname -s))),CYGWIN)
    # Intentionally not setting JNI_INCLUDES and JNI_LIBS
    # We don't want to build the jni-bindings for x10rt on cygwin
  else
  ifeq ($(shell uname -s),Darwin)
    JNI_INCLUDES = -I"$(JAVA_HOME)"/include -I"$(JAVA_HOME)"/include/darwin
    ifeq ($(jarch),x86_64)
      jarch=amd64
    endif
    JNI_LIBS = -L"$(JAVA_HOME)"/jre/lib/$(jarch)/server -L"$(JAVA_HOME)"/jre/lib/$(jarch)/client
  else
  ifeq ($(shell uname -s),FreeBSD)
    JNI_INCLUDES = -I"$(JAVA_HOME)"/include -I"$(JAVA_HOME)"/include/freebsd
    ifeq ($(jarch),x86_64)
      jarch=amd64
    endif
    JNI_LIBS = -L"$(JAVA_HOME)"/jre/lib/$(jarch)/server -L"$(JAVA_HOME)"/jre/lib/$(jarch)/client
  endif
  endif
  endif
  endif
endif

JBLAS_JNILIB = libjblas_$(GML_ELEM_TYPE).so
JLAPACK_JNILIB = libjlapack_$(GML_ELEM_TYPE).so
ifeq ($(shell uname -s),Darwin)
    # MacOS JNI libs require extension .jnilib instead of .so
    JBLAS_JNILIB = libjblas_$(GML_ELEM_TYPE).jnilib
    JLAPACK_JNILIB = libjlapack_$(GML_ELEM_TYPE).jnilib
endif

# BLAS and LAPACK compiler options
ifndef DISABLE_BLAS
    X10CXX_PREARGS += -cxx-prearg -DENABLE_BLAS
	add_jblas	= chk_jblas
    ifndef DISABLE_LAPACK
        X10CXX_PREARGS += -cxx-prearg -DENABLE_LAPACK
	add_jlapack	= chk_jlapack
    endif
endif

# BLAS and LAPACK linker options
ifdef BGQ
    # Blue Gene/Q compiler settings
    CXX = /bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc64-bgq-linux-g++
    # IBM ESSL on Blue Gene/Q
    BLASLIB = ESSL
    ifndef DISABLE_BLAS
        IBMCMP_ROOT ?= /opt/ibmcmp
        XLSMP_LIB_PATH ?= $(IBMCMP_ROOT)/xlsmp/bg/3.1/bglib64
        XLMASS_LIB_PATH ?= $(IBMCMP_ROOT)/xlmass/bg/7.3/bglib64
        XLF_LIB_PATH ?= $(IBMCMP_ROOT)/xlf/bg/14.1/bglib64
        ESSL_LIB_PATH ?= /opt/ibmmath/essl/5.1/lib64
        ESSL_LIB = esslsmpbg
        # need mass lib on BG/Q
        X10CXX_POSTARGS += -cxx-postarg -Wl,--allow-multiple-definition -cxx-postarg -L$(XLMASS_LIB_PATH) -cxx-postarg -lmassv -cxx-postarg -lmass
        #X10CXXFLAGS += -cxx-postarg -L/opt/ibmcmp/vac/bg/9.0/lib
    endif
endif

# choose BLAS implementation
BLASLIB ?= NetLIB

ifeq ($(BLASLIB),ESSL)
    # IBM ESSL
    X10CXX_PREARGS += -cxx-prearg -D__essl__
    ifndef DISABLE_BLAS
        IBMCMP_ROOT ?= /opt/ibmcmp
        XLSMP_LIB_PATH ?= $(IBMCMP_ROOT)/xlsmp/3.1/lib64
        XLF_LIB_PATH ?= $(IBMCMP_ROOT)/xlf/14.1/lib64
        ESSL_LIB_PATH ?= /usr/lib64
        ESSL_LIB ?= esslsmp6464
        X10CXX_POSTARGS += -cxx-postarg -L$(ESSL_LIB_PATH) -cxx-postarg -l$(ESSL_LIB) -cxx-postarg -L$(XLF_LIB_PATH) -cxx-postarg -lxlf90_r -cxx-postarg -L$(XLSMP_LIB_PATH) -cxx-postarg -lxlsmp -cxx-postarg -lxlopt -cxx-postarg -lxlfmath -cxx-postarg -lxl

    endif
else
ifeq ($(BLASLIB),OpenBLAS)
    # OpenBLAS
    OPENBLAS_LIB_PATH ?= /opt/OpenBLAS
    ifndef DISABLE_BLAS
        X10CXX_POSTARGS += -cxx-postarg -L$(OPENBLAS_LIB_PATH) -cxx-postarg -lopenblas
        ifeq ($(shell uname -s),Darwin)
            GFORTRAN_LIB ?= /usr/local/lib
            X10CXX_POSTARGS += -cxx-postarg -L$(GFORTRAN_LIB) -cxx-postarg -lgfortran
        endif
    endif
else
ifeq ($(BLASLIB),GotoBLAS2)
    # GotoBLAS2
    GOTOBLAS2_LIB_PATH ?= $(HOME)/GotoBLAS2
    ifndef DISABLE_BLAS
        X10CXX_POSTARGS += -cxx-postarg -L$(GOTOBLAS2_LIB_PATH) -cxx-postarg -lgoto2
        ifndef DISABLE_LAPACK
            X10CXX_POSTARGS += -cxx-postarg -llapack
        endif
    endif
else
ifeq ($(BLASLIB),ATLAS)
    # ATLAS
    ATLAS_LIB_PATH ?= /usr/lib64/atlas
    ifndef DISABLE_BLAS
        X10CXX_POSTARGS += -cxx-postarg -L$(ATLAS_LIB_PATH) -cxx-postarg -latlas -cxx-postarg -lf77blas
        ifndef DISABLE_LAPACK
            X10CXX_POSTARGS += -cxx-postarg -llapack
        endif
    endif
else
ifeq ($(BLASLIB),MKL)
    # Intel Math Kernel Library (LP64, 64-bit, libgomp)
    ifndef DISABLE_BLAS
        X10CXX_POSTARGS += -cxx-postarg -L$(MKLROOT)/lib/intel64 -cxx-postarg -lmkl_intel_lp64 -cxx-postarg -lmkl_core -cxx-postarg -lmkl_gnu_thread -cxx-postarg -ldl
        X10CXX_PREARGS += -cxx-prearg -fopenmp -cxx-prearg -m64 -cxx-prearg -I$(MKLROOT)/include
    endif
else
    # assume NetLib reference BLAS/LAPACK
    ifndef DISABLE_BLAS
        X10CXX_POSTARGS += -cxx-postarg -L/usr/lib64 -cxx-postarg -lblas
        ifndef DISABLE_LAPACK
            X10CXX_POSTARGS += -cxx-postarg -llapack
        endif
    endif
endif
endif
endif
endif
endif
