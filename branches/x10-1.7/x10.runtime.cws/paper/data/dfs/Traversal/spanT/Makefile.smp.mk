include $(SIMPLE_DIR)/Makefile.common
include $(SIMPLE_DIR)/$(SRC_DIR)/Makefile.var

MAKEORIG  		:= Makefile.orig
MAKEFILE 		:= Makefile
LIBSMPNODE_RELPATH 	:= $(SIMPLE_DIR)/$(LIB_DIR)/$(LIBSMPNODE)

PROCTYPE=AMD

ifeq ($(PROCTYPE),SPARC)
CFLAGS1     		:= CAS.il  -DSPARC -DSMPONLY $(CFLAGS_GEN) $(CFLAGS_THR) \
			   -I$(SIMPLE_DIR)/$(INC_DIR) \
			   $(CFLAGS_SPRNG) -g 
LIBS1       		:= -L$(SIMPLE_DIR)/$(LIB_DIR) $(SMPLIBS) $(THR_LIB) \
			   $(LIBS_SPRNG)
else
CFLAGS1     		:= -DSMPONLY $(CFLAGS_GEN) $(CFLAGS_THR) \
			   -I$(SIMPLE_DIR)/$(INC_DIR) \
			   $(CFLAGS_SPRNG) -g 
LIBS1       		:= -L$(SIMPLE_DIR)/$(LIB_DIR) $(SMPLIBS) $(THR_LIB) \
			   $(LIBS_SPRNG) XCHG.o
endif


SMPLIBSRCS		:= simple.c smp_node.c types.c alg_random.c simple-f.c
SMPLIBOBJS   		:= $(SMPLIBSRCS:.c=.o)
SMPLIBPOBJS  		:= $(addprefix $(OBJ_DIR)/, $(SMPLIBOBJS))

SRCS_C			:= $(wildcard *.c)
SRCS_CXX		:= $(wildcard *.C)
SRCS_F			:= $(wildcard *.[fF])
SRCS			:= $(SRCS_C) $(SRCS_CXX) $(SRCS_F)
OBJS_C 			:= $(SRCS_C:.c=.o)
OBJS_CXX		:= $(SRCS_CXX:.C=.o)
OBJS_F 			:= $(patsubst %.F,%.o,$(patsubst %.f,%.o,$(SRCS_F)))
OBJS			:= $(OBJS_C) $(OBJS_CXX) $(OBJS_F)
POBJS  			:= $(addprefix $(OBJ_DIR)/, $(OBJS))

ifneq (,$(SRCS_F))
FLIBS := $(SIMPLE_DIR)/$(LIB_DIR)/simple-f-main.o
ifeq (gcc,${findstring gcc,$(CC)})
FLIBS := $(FLIBS) -lg2c 
endif
ifeq (SUNW,${findstring SUNW,$(CC)})
FLIBS := $(FLIBS) -lF77 -lM77 -I77 -lsunmath -lcx -lc 
endif
endif

.PHONY: all depend objclean clean

default: $(MAKEFILE) $(OBJ_DIR)
ifneq (,$(EXECS))
	@$(MAKE) -f $(MAKEFILE) $(EXECS)
endif

all: default

$(OBJ_DIR) :
	-$(MKDIR) $(OBJ_DIR)

$(EXECS): $(LIBSMPNODE_RELPATH) $(POBJS)
ifeq (,$(wildcard *.C))
	$(CC) $(CFLAGS1) -o $@ $(POBJS) $(LIBS1) $(FLIBS)
else
	$(CXX) $(CFLAGS1) -o $@ $(POBJS) $(LIBS1) $(FLIBS)
endif
	@echo Success: Finished building $@ 

$(MAKEFILE) :
	$(MAKE) -f $(MAKEORIG) depend

depend:
	$(CP) $(MAKEORIG) $(MAKEFILE)
	-$(MAKEDEPEND) -f$(MAKEFILE) -p$(OBJ_DIR)/ -- $(CFLAGS1) -- $(SRCS) 

.SUFFIXES: .c .C .f .F

$(OBJ_DIR)/%.o : %.c
	$(CC) $(CFLAGS1) -c $< -o $@

$(OBJ_DIR)/%.o : %.C
	$(CXX) $(CFLAGS1) -c $< -o $@

$(OBJ_DIR)/%.o : %.f
	$(F77) $(CFLAGS1) -c $< -o $@

$(OBJ_DIR)/%.o : %.F
	$(F77) $(CFLAGS1) -c $< -o $@

objclean:
	$(RM) -f $(OBJ_DIR)/*.o

clean :
	$(RM) -fr core *.o $(EXECS) $(OBJ_DIR) \
	mon.out PI* *~ Makefile $(MAKEFILE).bak $(EXTRA_CLEAN)

