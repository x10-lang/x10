include $(SIMPLE_DIR)/Makefile.common
include $(SIMPLE_DIR)/$(SRC_DIR)/Makefile.var

MAKEORIG  		:= Makefile.orig
MAKEFILE 		:= Makefile
LIBSIMPLE_RELPATH 	:= $(SIMPLE_DIR)/$(LIB_DIR)/$(LIBSIMPLE)

CFLAGS1     		:= $(CFLAGS) -I$(SIMPLE_DIR)/$(INC_DIR) \
			   $(CFLAGS_SPRNG) -DRRANDOM
LIBS1       		:= -L$(SIMPLE_DIR)/$(LIB_DIR) $(LIBS) $(THR_LIB) \
			   $(LIBS_SPRNG)

SIMPLELIBSRCS		:= simple.c smp_node.c types.c alg_random.c alg_load.c simple-f.c umd.c umd-f.c
SIMPLELIBOBJS  		:= $(SIMPLELIBSRCS:.c=.o)
SIMPLELIBPOBJS 		:= $(addprefix $(OBJ_DIR)/, $(SIMPLELIBOBJS))

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

$(EXECS): $(LIBSIMPLE_RELPATH) $(POBJS)
ifeq (,$(wildcard *.C))
	$(MPI_CC) $(CFLAGS1) -o $@ $(POBJS) $(LIBS1) $(FLIBS)
else
	$(MPI_CXX) $(CFLAGS1) -o $@ $(POBJS) $(LIBS1) $(FLIBS)
endif
	@echo Success: Finished building $@ 

$(MAKEFILE) :
	$(MAKE) -f $(MAKEORIG) depend

depend:
	$(CP) $(MAKEORIG) $(MAKEFILE)
	-$(MAKEDEPEND) -f$(MAKEFILE) -p$(OBJ_DIR)/ -- $(CFLAGS1) -I$(MPI_INC) -- $(SRCS) 

.SUFFIXES: .c .C .f .F

$(OBJ_DIR)/%.o : %.c
	$(MPI_CC) $(CFLAGS1) -c $< -o $@

$(OBJ_DIR)/%.o : %.C
	$(MPI_CXX) $(CFLAGS1) -c $< -o $@

$(OBJ_DIR)/%.o : %.f
	$(MPI_F77) $(CFLAGS1) -c $< -o $@

$(OBJ_DIR)/%.o : %.F
	$(MPI_F77) $(CFLAGS1) -c $< -o $@

objclean:
	$(RM) -f $(OBJ_DIR)/*.o

clean :
	$(RM) -fr core *.o $(EXECS) $(OBJ_DIR) \
	mon.out PI* *~ Makefile $(MAKEFILE).bak $(EXTRA_CLEAN)

