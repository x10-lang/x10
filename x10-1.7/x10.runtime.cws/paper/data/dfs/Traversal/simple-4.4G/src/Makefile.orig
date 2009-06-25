SIMPLE_DIR  =  ..

EXTRA_CLEAN = $(LIBSIMPLE)

OPT         = -O$(O_LEVEL)
# OPT       = -g

include $(SIMPLE_DIR)/Makefile.mk

default : $(POE_CFG) objclean $(LIBSIMPLE)

$(LIBSIMPLE): $(SIMPLELIBPOBJS)
	@$(RM) -f $(SIMPLELIBDIR)/$(LIBSIMPLE)
	$(AR) r$(ARZ) $(SIMPLELIBDIR)/$(LIBSIMPLE) $(SIMPLELIBPOBJS)
ifeq (,$(findstring SGI,$(TARGET_ARCH)))
	$(RANLIB) $(SIMPLELIBDIR)/$(LIBSIMPLE)
endif




