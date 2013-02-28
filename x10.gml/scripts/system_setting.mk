###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
###################################################
# System settings for building GML library and X10 applications 
# based on GML
# 1) Set BLAS name and path
# 2) JNI path

# Two build settings:
# Set your server name to allow different build settings on two systems.
# Change server to your computing system hostname (run "hostname" to get it)
server		=triloka

#Comment following line, if do not want build with lapack library.
add_lapack	= yes

###################################################
## Compiler settings
###################################################
FC = gfortran
XC = x10c++
XJ = x10c
CC = gcc
MCC = mpic++
CPP = g++
MAKE= make
JAR	= jar
XDOC= x10doc

###################################################
##-------------------------------------
## BlueGene/P post link options. BLAS and LAPACK settings are not used for BG/P build
## To build for BGP, uncommon following line, or "make BGP=yes ..." 
#BGP=yes
ifdef BGP
	CPP = /bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc-bgp-linux-g++
	MCC = /bgsys/drivers/ppcfloor/comm/bin/mpicxx

	POST_PATH	+= -L/opt/ibmmath/lib -L/opt/ibmcmp/xlf/bg/11.1/lib -L/opt/ibmcmp/xlsmp/bg/1.7/lib -L/opt/ibmcmp/vac/bg/9.0/lib
	POST_LIBS	+= -lx10 -lesslbg -lxlf90_r -lxl -lxlsmp -lrt
endif

##################################################
ifndef BGP

        ## JAVA include
	JNI_inc     =$(JAVA_HOME)/include	
        #---------------------------------------------------------------
        # Two different systems support: 
        #  Default - application development and debugging on localhost (default)
        #  Server  - application production or performance benchmark on clusters

        #------ Default settings --------
        # BLAS settings:
	blas_path  	=/usr/lib
	blas_name	=blas
	blas_lib	= $(blas_path)/lib$(blas_name).so
        # LAPACK settings:
	lapack_path =/usr/lib
	lapack_name =lapack
	lapack_lib  =$(lapack_path)/lib$(lapack_name).so
        #

        #--------- Server settings --------
        # Redefine settings
	ifdef server 
		ifeq ($(server), $(findstring $(server), $(shell hostname)))

                #BLAS settings:
		blas_path   =/usr/lib64
		blas_name	=blas
		blas_lib	=$(blas_path)/lib$(blas_name).so
                #LAPACK settings:
		lapack_path =/usr/lib64
		lapack_name =lapack
		lapack_lib  =$(lapack_path)/lib$(lapack_name).so
                #
                #post compile for mpi transport on slurm 
                #mpi_path 	= -L/usr/lib64/slurm 
                #mpi_lib	= -lpmi

		endif
	endif
	server=$(shell hostname)

        #------------------------------------------------------
        #------------------------------------------------------
        #
        # Post compiling options
	ifdef blas_path
		POST_PATH   += -L$(blas_path)
	endif
	ifdef  blas_name
		POST_LIBS	+= -l$(blas_name)
	endif
	ifdef  gfortran
		POST_LIBS	+= -l$(gfortran)
	endif

        #------------------------------------------------------
        # add lapack, optional.
        # lapack_path and lapack_name must be defined correctly

	ifdef add_lapack
		POST_PATH	+= -L$(lapack_path)
		POST_LIBS	+= -l$(lapack_name)		
		LAPACK_CFLAG = -cxx-prearg -DENABLE_LAPACK
		add_jlapack =chk_jlapack

	endif
endif
