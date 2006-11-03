# Makefile for Support to build library allowing threads to be bound to CPUs

CLASSES	= $(X10)/x10.runtime/classes
CCC	= xlC_r
CCC_OPTS= -g -O2 -qnolm -qnoeh -qnotempinc -bM:SHR -bnoentry -bexpall -I$(CLASSES) -I$(JAVA_HOME)/bin/include -I$(JAVA_HOME)/include
LIB_SUFFIX=.a

default: $(X10)/x10.common/lib/$(X10_PLATFORM)/libX10ThreadSupp.$(LIB_SUFFIX)

$(X10)/x10.common/lib/$(X10_PLATFORM)/libX10ThreadSupp.$(LIB_SUFFIX): X10ThreadSupp.cpp ../VMInterface.java
	javah -classpath $(CLASSES) x10.runtime.VMInterface
	$(CCC) $(CCC_OPTS) -o $@ X10ThreadSupp.cpp
