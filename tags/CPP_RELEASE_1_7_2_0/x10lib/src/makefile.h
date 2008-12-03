# copyright (c) 2006 IBM
# @author Sriram Krishnamoorthy

INCLUDES += -I/home/osu3382/install/ia64/include/boost-1_33_1

LIBS     += -L../lib -lx10lib -L/home/osu3382/install/ia64/lib -lboost_serialization-il-mt-d

#/home/osu3382/install/ia64/lib/libboost_serialization-il-mt-d.a
#-L/home/osu3382/install/ia64/lib -lboost_serialization-il-mt-d

INCFILES += x10base.h Activity.h ActivityMaker.h FinishScope.h ActivityManager.h system.h NonThreadSafeInvokable.h FinishScopeManager.h FinishRecordManager.h SltType.h Finish.h FinishRecord.h DsFinishRecord.h


ifeq ($(ARMCI),y)
     include $(X10BASE)/src/system/armci/makefile.h
endif

ifeq ($(PTHREADS),y)
     include $(X10BASE)/src/system/mutex/pthread-makefile.h
endif

