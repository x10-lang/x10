###############################################################################
#
# Copyright 2001 by the <a href="http://ali-www.cs.umass.edu/">Scale Compiler Group</a>,<br>
# <a href="http://www.cs.umass.edu/">Department of Computer Science</a><br>
# <a href="http://www.umass.edu/">University of Massachusetts</a.,<br>
# Amherst MA. 01003, USA<br>
# All Rights Reserved.<br>
# <p>
# Compiles the java classes and recurses to sub-directories
#
# Makefiles that include this makefile should have rules called
#   all    - make everything
#   clean  - remove everything that is built
# These rules should depend on java, javaall, and javaclean.
# <p>
# $Id: Makefile,v 1.1 2008-03-06 18:02:23 nystrom Exp $
###############################################################################
PACKAGE    := scale/score/dependence/omega/omegaLib
DEST       := $(CLASSDEST)/$(PACKAGE)

CLASSFILES :=  	$(DEST)/Conjunct.class \
		$(DEST)/ConstraintHandle.class \
		$(DEST)/DNF.class \
		$(DEST)/EQHandle.class \
		$(DEST)/FAnd.class \
		$(DEST)/FDeclaration.class \
		$(DEST)/FExists.class \
		$(DEST)/FForall.class \
		$(DEST)/FNot.class \
		$(DEST)/FOr.class \
		$(DEST)/Formula.class \
		$(DEST)/FreeVarDecl.class \
		$(DEST)/GEQHandle.class \
		$(DEST)/GlobalVarDecl.class \
		$(DEST)/Mapping.class \
		$(DEST)/Problem.class \
		$(DEST)/RelBody.class \
		$(DEST)/Relation.class \
		$(DEST)/RememberRedConstraint.class \
		$(DEST)/VarDecl.class \
		$(DEST)/Equation.class \
		$(DEST)/CName.class \
		$(DEST)/OmegaLib.class

omega: java

all: javaall

clean: javaclean

include $(SCALE)/$(SCALERELEASE)/scale/Makefile.defs
