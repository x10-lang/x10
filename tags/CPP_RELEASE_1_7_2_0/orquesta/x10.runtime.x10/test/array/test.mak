SHELL=/bin/bash

ifndef X10C
  X10C=x10c
endif

ifndef X10
  X10=x10
endif

FILES = $(wildcard Test[0-9][0-9]*.x10)
TESTS = $(FILES:.x10=) CG

LIB = ../../src/x10/classes

default:
	$(X10C) -J-Xmx1024m -cp $(LIB) -d $(LIB) *.x10
	-$(X10) -cp $(LIB) Tester $(TESTS)
	diff dbg.ref dbg.out
	rm *.out

bless:
	for file in *.out; do mv $${file} $${file%.out}.ref; done

%: %.x10
	$(X10) -cp ../classes Tester $*
	cat $*.out
