# This makefile is used to build the launcher outside of the X10 runtime

CC=g++
CFLAGS=-c -Wall
LDFLAGS=
SOURCES=main.cc Launcher_Init.cc Launcher.cc tcp.cc
OBJECTS=$(SOURCES:.cc=.o)
EXECUTABLE=X10Launcher

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS) 
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@

.cc.o:
	$(CC) $(CFLAGS) $< -o $@
 
	 