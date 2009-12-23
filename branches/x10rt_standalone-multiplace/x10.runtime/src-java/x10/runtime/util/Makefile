# Makefile for Support to build library allowing threads to be bound to CPUs

# This assumes the setting of JAVA_HOME to the jre subdirectory, as X10_cronJob sets it
X10 = ../../../../..
CLASSES	= $(X10)/x10.runtime/classes
LIBDIR = $(X10)/x10.common/lib
CCC	= xlC_r
CCC_OPTS= -g -O2 -qnolm -qnoeh -qnotempinc -bM:SHR -bnoentry -bexpall -I$(CLASSES) -I$(JAVA_HOME)/bin/include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/../include
LIB_SUFFIX=so

default: $(LIBDIR)/$(X10_PLATFORM)/libX10ThreadSupp.$(LIB_SUFFIX)

$(LIBDIR)/$(X10_PLATFORM)/libX10ThreadSupp.$(LIB_SUFFIX): X10ThreadSupp.cpp ../VMInterface.java
	$(JAVA_HOME)/../bin/javah -classpath $(CLASSES) x10.runtime.VMInterface
	$(CCC) $(CCC_OPTS) -o $@ X10ThreadSupp.cpp

clobber: clean
	rm -f $(LIBDIR)/$(X10_PLATFORM)/libX10ThreadSupp.$(LIB_SUFFIX)

clean:
	rm -f x10_runtime_VMInterface.h
