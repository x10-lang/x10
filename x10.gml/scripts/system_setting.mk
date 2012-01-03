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

# Two settings:
# 1) Default:
# 2) Server: computing server host settings
server		=triloka

#leave empty, if do not want build with lapack library, otherwise uncomment the following line
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

## JAVA include
JNI_inc     =$(JAVA_HOME)/include

#--------------------------------
# Test server host or localhost
ifeq ($(server), $(findstring $(server), $(shell hostname)))
#--------- Server settings --------
#home_path	:=$(HOME)
blas_path   =/usr/lib64
blas_name	=blas
blas_lib	= $(blas_path)/lib$(blas_name).so
#
lapack_path =/usr/lib64
lapack_name =lapack
lapack_lib  =$(lapack_path)/lib$(lapack_name).so
#
#post compile for mpi transport 
#mpi_path 	= -L/usr/lib64/slurm 
#mpi_lib		= -lpmi
else
## for your local setting, change as you need.
blas_path  	=/usr/lib
blas_name	=blas
blas_lib	= $(blas_path)/lib$(blas_name).so
#
lapack_path =/usr/lib
lapack_name =lapack
lapack_lib  =$(lapack_path)/lib$(lapack_name).so
#
server		= localhost
endif

#------------------------------------------------------
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
# add lapack, optional
ifdef add_lapack
POST_PATH	+= -L$(lapack_path)
POST_LIBS	+= -l$(lapack_name)
add_jlapack =chk_jlapack
endif
