# copyright (c) 2006 IBM
# @author Sriram Krishnamoorthy

#Symbols that can be externally defined
#These should be specified as symbols to be defined by the user

#GA_BASE        = /home/osu3382/g/ga-4-0-ia64
MPI_BASE       = /usr/local/mpich
CC             = icc
CXX            = icpc
FC             = efc

include $(GA_BASE)/armci/config/makemp.h
include $(GA_BASE)/armci/config/makecoms.h

GA_INCLUDES += -I$(GA_BASE)/include

INCLUDES    += $(GA_INCLUDES) $(MP_INCLUDES) $(COMM_INCLUDES) -I../.. -I.

DEFINES     += -DARMCI_ENABLE_GPC_CALLS

LIBS        += -L$(GA_BASE)/lib/$(TARGET) -larmci $(MP_LIBS) $(COMM_LIBS) 

OBJS        += system/armci/system.o

//INCFILES += 
