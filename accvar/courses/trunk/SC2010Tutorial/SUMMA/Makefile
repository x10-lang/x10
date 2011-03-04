###################################################
###################################################
## Name:  	X10 SUMMA implementation using Team
###################################################
###################################################

target	 	= RunSummaXT
btarget 	= RunSummaXTBlas

###################################################
# Source files and paths
###################################################
#
src_files	= RunSummaXT.x10 SummaTeam.x10

###################################################
## Compiler settings
###################################################
FC = gfortran
#XC = ~/x10/x10-trunk/x10.dist/bin/x10c++
XC = ~/x102101/x10.dist/bin/x10c++
MCC = mpicxx 
CPP = g++
MAKE = make

###################################################
VPATH = 
###################################################


###################################################
# Compile and link flags
###################################################
#
X_LINK_PATH    = -L/usr/lib64/slurm 
X_LINK_LIB     = -lpmi
X10_FLAG	= -x10rt mpi -report postcompile=2 -v -errors 5 -O -VERBOSE_CALLS -NO_CHECKS=true

## Post compiler options
X_POST_PATH	= #put your blas lib path here, or use system default
X_POST_LIB  = -lblas -lgfortran -lpmi
###-------------------------------------------

###################################################
# X10 file built rules
###################################################
default		: $(target)
xt			: $(target)
blasxt		: $(btarget)
 
all			: xt blasxt
#-----
XT			: $(target)
#-----

$(target)	: RunSummaXT.x10 SummaTeam.x10
			$(XC) $(X10_FLAG) $< -o $@ 

$(btarget)	: RunSummaXTBlas.x10 SummaTeamBlas.x10 cleanblas
			$(XC) $(X10_FLAG) $< -o $@ -post '$(CPP) # $(X_POST_PATH) # $(X_POST_LIB)'
 

###------------------------------------------------
clean	: cleanblas
		rm -f *.cc *.h *.inc *.o $(target)

cleanblas:
		rm -f blas/MatrixBlas.cc blas/MatrixBlas.h blas/MatrixBlas.inc $(btarget)

################################################
.PHONY	: clean blasxt
###########################################

