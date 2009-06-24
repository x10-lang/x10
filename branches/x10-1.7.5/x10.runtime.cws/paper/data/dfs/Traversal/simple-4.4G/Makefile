MAKECOMMON     = Makefile.common

include $(MAKECOMMON)
export TARGET_ARCH
export S_VER_MAJ
export S_VER_MIN
export S_VER_REL
export SRC_DIR
export INC_DIR
export LIB_DIR
include $(SRC_DIR)/Makefile.var

INSTALL_INC_DIR = $(PREFIX_DIR)/$(INC_DIR)
INSTALL_LIB_DIR = $(PREFIX_DIR)/$(LIB_DIR)
INSTALL_RUN_DIR = $(PREFIX_DIR)/$(RUN_DIR)

SUBDIRS		= testprog testsmp testsprng testsprng_SMP \
		  testfortF testfortF_SMP \
		  examples isort_SMP memread_SMP symbreak_SMP \
		  prefixTree_SMP prefixBlock_SMP \
		  imagery
DISTCP_SUBDIRS  =  $(addprefix distcp_,$(SUBDIRS))
CLEAN_SUBDIRS   =  $(addprefix clean_,$(SUBDIRS))

PATCH_DIR	= PATCHES

DIST_DIR   	:= simple-$(S_VER_MAJ).$(S_VER_MIN)$(S_VER_REL)
DIST_NAME	:= $(DIST_DIR).tar
DIST_SAVE	:= dist
DIST_WWW	:= $(HOME)/apache/htdocs/HPC
SDIR		:= ${shell pwd}

SRC_MAKEORIG	:= Makefile.orig
SRC_MAKESMPORIG	:= Makefile.smp.orig
HEADERS	:= $(notdir $(wildcard $(SRC_DIR)/*.h))
RUN_FILES 	:= ${wildcard $(RUN_DIR)/machines*} \
		   $(RUN_DIR)/mrun.mpich.[ABC] \
		   $(RUN_DIR)/mrun.mpich \
		   $(RUN_DIR)/mrun.lam.[A] \
		   $(RUN_DIR)/mrun.lam \
		   $(RUN_DIR)/mrun.freebsd \
		   $(RUN_DIR)/mrun.aix

.PHONY: all default simple install depend dist distwww clean \
	$(SUBDIRS) $(DISTCP_SUBDIRS) $(CLEAN_SUBDIRS)

default: simple

all: clean simple install

$(LIB_DIR) :
	-$(MKDIR) $(LIB_DIR)

$(INC_DIR) :
	-$(MKDIR) $(INC_DIR)

simple: $(LIB_DIR) $(INC_DIR)
	cd $(SRC_DIR) ; $(MAKE) -f $(SRC_MAKEORIG)
	$(CP) $(SRC_DIR)/$(LIBSIMPLE) $(LIB_DIR)
	cd $(SRC_DIR) ; $(MAKE) -f $(SRC_MAKESMPORIG)
	$(CP) $(SRC_DIR)/$(LIBSMPNODE) $(LIB_DIR)
	cd $(SRC_DIR) ; $(CP) $(HEADERS) ../$(INC_DIR)
	$(CP) $(SRC_DIR)/$(OBJ_DIR)/simple-f-main.o $(LIB_DIR)
	@echo Success: Finished building $@ 

$(PREFIX_DIR) :
ifneq (,$(PREFIX_DIR))
	-$(MKDIR) $(PREFIX_DIR)
endif

$(INSTALL_INC_DIR) :
ifneq (,$(PREFIX_DIR))
	-$(MKDIR) $(INSTALL_INC_DIR)
endif

$(INSTALL_LIB_DIR) :
ifneq (,$(PREFIX_DIR))
	-$(MKDIR) $(INSTALL_LIB_DIR)
endif

$(INSTALL_RUN_DIR) :
ifneq (,$(PREFIX_DIR))
	-$(MKDIR) $(INSTALL_RUN_DIR)
endif

install: $(PREFIX_DIR) $(INSTALL_INC_DIR) $(INSTALL_LIB_DIR) $(INSTALL_RUN_DIR)
ifneq (,$(PREFIX_DIR))
	$(CP) $(SRC_DIR)/$(LIBSIMPLE) $(INSTALL_LIB_DIR)
	$(CP) $(wildcard $(SRC_DIR)/*.h) $(INSTALL_INC_DIR)
	$(CP) $(RUN_FILES) $(INSTALL_RUN_DIR)
	@echo Success: Finished installing $(SIMPLE)
endif

dist:
	$(RM) -fr $(DIST_DIR) $(DIST_NAME).*
	$(MKDIR) $(DIST_DIR)
	$(CP) COPYRIGHT README \
		Makefile \
		$(MAKECOMMON) \
		Makefile.mk \
		Makefile.smp.mk \
		$(DIST_DIR)
	$(MKDIR) $(DIST_DIR)/$(PATCH_DIR)
	$(CP) $(PATCH_DIR)/README-linux \
		$(PATCH_DIR)/linux-mpich-patch \
		$(DIST_DIR)/$(PATCH_DIR)
	$(MKDIR) $(DIST_DIR)/$(SRC_DIR)
	$(CP) ${wildcard $(SRC_DIR)/*.[ch]} \
		$(SRC_DIR)/Makefile.var \
		$(SRC_DIR)/Makefile.orig \
		$(SRC_DIR)/Makefile.smp.orig \
		$(DIST_DIR)/$(SRC_DIR)
	$(MKDIR) $(DIST_DIR)/$(RUN_DIR)
	$(CP) $(RUN_FILES) \
		$(DIST_DIR)/$(RUN_DIR)
	chmod 755 $(DIST_DIR)/$(RUN_DIR)/mrun.*
	$(MAKE) $(DISTCP_SUBDIRS)
	$(TAR) cvf $(DIST_NAME) $(DIST_DIR)
	$(ZIP) $(DIST_NAME)
	if [ ! -d $(DIST_SAVE) ] ; then \
	   $(MKDIR) $(DIST_SAVE) ; \
	fi
	$(MV) $(DIST_NAME).* $(DIST_SAVE)
	$(CP) README $(DIST_SAVE)
	$(CP) COPYRIGHT $(DIST_SAVE)
	chown dbader $(DIST_SAVE)/*
	chgrp tty $(DIST_SAVE)/*
	chmod 640 $(DIST_SAVE)/*
	$(RM) -fr $(DIST_DIR)
	-$(RM) $(DIST_SAVE)/simple.tar.gz
	cd $(DIST_SAVE) ; ln -s $(DIST_NAME).gz simple.tar.gz

distwww: dist
	$(CP) $(SDIR)/$(DIST_SAVE)/$(DIST_NAME).gz $(DIST_WWW)
	$(CP) $(SDIR)/$(DIST_SAVE)/README $(DIST_WWW)/SIMPLE-README.txt
	$(CP) $(SDIR)/$(DIST_SAVE)/COPYRIGHT $(DIST_WWW)/SIMPLE-COPYRIGHT.txt
	cd $(DIST_WWW)
	chgrp hpcweb $(DIST_WWW)/*
	chmod 640 $(DIST_WWW)/*
	-$(RM) $(DIST_WWW)/simple.tar.gz
	cd $(DIST_WWW) ; ln -s $(DIST_NAME).gz simple.tar.gz

define DIST_CP_DIR
$(subst distcp_,,$@)
endef
define DIST_TARGET_DIR
$(addprefix $(DIST_DIR)/,$(DIST_CP_DIR))
endef

$(DISTCP_SUBDIRS):
	$(MKDIR) $(DIST_TARGET_DIR)
	$(CP) ${wildcard $(DIST_CP_DIR)/*.[chfF]} \
		$(DIST_CP_DIR)/Makefile.orig \
		$(DIST_TARGET_DIR)

$(SUBDIRS):
	cd $@ ; $(MAKE) -f $(SRC_MAKEORIG)

$(CLEAN_SUBDIRS):
	cd $(subst clean_,,$@) ; $(MAKE) -f Makefile.orig clean

clean : $(CLEAN_SUBDIRS)
	cd $(SRC_DIR) ; $(MAKE) -f $(SRC_MAKEORIG) clean
	-$(RM) $(LIB_DIR)/$(LIBSIMPLE)
	cd $(SRC_DIR) ; $(MAKE) -f $(SRC_MAKESMPORIG) clean
	-$(RM) $(LIB_DIR)/$(LIBSMPNODE)
	-$(RM) $(LIB_DIR)/simple-f-main.o
	-$(RMDIR) $(LIB_DIR)
	-$(RM) $(addprefix $(INC_DIR)/,$(HEADERS))
	-$(RMDIR) $(INC_DIR)
	-$(RM) *~


