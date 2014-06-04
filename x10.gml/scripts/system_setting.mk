# Platform-specific settings for building GML library and application codes

X10CXX ?= x10c++
X10C ?= x10c
CXX ?= g++
JAR ?= jar
MAKE ?= make

# JNI include path, for managed GML
ifdef JAVA_HOME
  jarch=$(shell uname -p)
  ifeq ($(jarch),unknown)
    jarch=$(shell uname -m)
  endif

  ifeq ($(shell uname -s),AIX)
    JNI_INCLUDES = -I"$(JAVA_HOME)"/include -I"$(JAVA_HOME)"/include/aix
    JNI_LIBS = -L"$(JAVA_HOME)"/jre/lib/$(jarch)/j9vm
  else
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
  ifeq ($(shell uname -s),SunOS)
    JNI_INCLUDES = -I"$(JAVA_HOME)"/include -I"$(JAVA_HOME)"/include/solaris
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
  endif
endif

# comment the following lines to disable BLAS and LAPACK wrappers
ADD_BLAS	= yes
ADD_LAPACK	= yes

# BLAS and LAPACK compiler options
ifdef ADD_BLAS
    X10CXXFLAGS += -cxx-prearg -DENABLE_BLAS
	add_jblas	= chk_jblas
    ifdef ADD_LAPACK
        X10CXXFLAGS += -cxx-prearg -DENABLE_LAPACK
		add_jlapack	= chk_jlapack
    endif
endif

# BLAS and LAPACK linker options
ifdef BGQ
# Blue Gene/Q compiler settings
    BLASLIB = ESSL
    CXX = /bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc64-bgq-linux-g++
    ifdef ADD_BLAS
        XLSMP_LIB_PATH = /opt/ibmcmp/xlsmp/bg/1.7/lib
        XLF_LIB_PATH = /opt/ibmcmp/xlf/bg/11.1/lib
        POST_LDFLAGS += -L/opt/ibmmath/lib -L/opt/ibmcmp/vac/bg/9.0/lib
    endif
endif

# choose BLAS implementation
BLASLIB ?= 

ifeq ($(BLASLIB),ESSL)
    # IBM ESSL
    X10CXXFLAGS += -cxx-prearg -D__essl__
    ifdef ADD_BLAS
        XLSMP_LIB_PATH ?= /opt/ibmcmp/xlsmp/3.1/lib64
        XLF_LIB_PATH ?= /opt/ibmcmp/xlf/14.1/lib64
        POST_LDFLAGS += -L/usr/lib64 -L$(XLSMP_LIB_PATH) -L$(XLF_LIB_PATH) -lesslsmp6464 -lxlf90_r -lxl -lxlsmp -lxlfmath
        ifdef ADD_LAPACK
            POST_LDFLAGS += -llapack
        endif
    endif
else
ifeq ($(BLASLIB),GotoBLAS2)
    # GotoBLAS2
    ifdef ADD_BLAS
        POST_LDFLAGS += -L$(HOME)/GotoBLAS2 -lgoto2
        ifdef ADD_LAPACK
            POST_LDFLAGS += -llapack
        endif
    endif
else
ifeq ($(BLASLIB),MKL)
    # Intel Math Kernel Library (LP64, 64-bit, libgomp)
    ifdef ADD_BLAS
        POST_LDFLAGS += -L$(MKLROOT)/lib/intel64 -lmkl_intel_lp64 -lmkl_core -lmkl_gnu_thread -ldl
        POST_CXXFLAGS += -fopenmp -m64 -I$(MKLROOT)/include
    endif
else
    # assume NetLib reference BLAS/LAPACK
    ifdef ADD_BLAS
        POST_LDFLAGS += -L/usr/lib64 -lblas
        ifdef ADD_LAPACK
            POST_LDFLAGS += -llapack
        endif
    endif
endif
endif
endif
