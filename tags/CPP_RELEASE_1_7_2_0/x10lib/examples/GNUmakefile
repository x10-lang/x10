include $(X10BASE)/src/makefile.h

INCLUDES += -I../include

CXXFLAGS += -g

SUFFIXES: .cc 

.cc.o:
	$(CXX) $(CXXFLAGS) $(DEFINES) $(INCLUDES) -o $*.o -c $*.cc

%.x: %.o
	$(CXX) $(CXXFLAGS) -o $*.x $*.o $(LIBS)

clean:
	$(RM) -f *.o
	$(RM) -f *~
	$(RM) -f *.x

