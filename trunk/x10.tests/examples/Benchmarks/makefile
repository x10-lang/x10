#KIND=java
#KIND=x10-dbg-java
#KIND=x10-opt-java
KIND=summary

#BENCHMARK=SeqMatMultAdd
#BENCHMARK=SeqMethodCall
#BENCHMARK=SeqPseudoArray1b
#BENCHMARK=SeqRail
BENCHMARK=SeqUTS

X10_HOME = ~/x10/1.7
JAVA_HOME = /ibm-java-sdk-60

#
#
#

default: $(KIND)!$(BENCHMARK)

SEQ=$(wildcard Seq*.x10)

summary: $(SEQ:%.x10=summary!%)

all: $(SEQ:%.x10=$(KIND)!%)


#
# list of common files in runtime and test harness
# compile into lib-...
#

EXCEPT=\
./x10/io/FileSystem.x10\
./x10/io/PutbackReader.x10\
./x10/io/Readable.x10\
./x10/io/Writable.x10\
./x10/io/ByteRailWriter.x10\
./x10/io/ByteValRailWriter.x10\
./x10/io/ByteWriter.x10\
./x10/util/MapSet.x10\
./x10/util/HashSet.x10

SRC = $(X10_HOME)/x10.runtime.17/src-x10
TEST = $(X10_HOME)/x10.tests/examples/x10lib

LIB := $(shell cd $(SRC); find . -name '*.x10')
LIB := $(filter-out $(EXCEPT),$(LIB))

LIB := \
$(LIB:%=$(SRC)/%)\
$(TEST)/harness/x10Test.x10\
Benchmark.x10

.SECONDARY:


#
#
#

X10 = $(X10_HOME)/x10.dist/bin/x10
X10C = $(X10_HOME)/x10.dist/bin/x10c -commandlineonly -rtdev -sourcepath $(SRC) -sourcepath $(TEST)

#
# x10 with debugging code, java back end
#

KIND1 = x10-dbg-java

$(KIND1)!%: lib-$(KIND1)/%.class
	-$(X10) -cp lib-$(KIND1) $* `hostname` $(KIND1)

lib-$(KIND1)/%.class: %.x10 lib-$(KIND1)-build
	$(X10C) -d lib-$(KIND1) $*.x10

lib-$(KIND1)-build: $(LIB)
	$(X10C) -d lib-$(KIND1) $?
	touch $@


#
# x10 without debugging code, java back end
#

KIND2 = x10-opt-java
OPTSC2 = -BAD_PLACE_RUNTIME_CHECK=false
#OPTSR2 = -J-Xjit:optLevel=scorching

$(KIND2)!%: lib-$(KIND2)/%.class
	-$(X10) -cp lib-$(KIND2) $(OPTSR2) $* `hostname` $(KIND2)

summary!%: lib-$(KIND2)/%.class
	-$(X10) -cp lib-$(KIND2) $(OPTSR2) $* `hostname`

lib-$(KIND2)/%.class: %.x10 lib-$(KIND2)-build
	$(X10C) -d lib-$(KIND2) $(OPTSC2) $*.x10

lib-$(KIND2)-build: $(LIB)
	$(X10C) -d lib-$(KIND2) $(OPTSC2) $?
	touch $@



#
#
#

JAVAC = $(JAVA_HOME)/bin/javac
JAVA = $(JAVA_HOME)/bin/java

#
# java
#

KIND3 = java
OPTS3 = 

$(KIND3)!%: lib-$(KIND3)/%.class
	$(JAVA) -cp lib-$(KIND3) $* `hostname` $(KIND3)

lib-$(KIND3)/%.class: %.java
	$(JAVAC) -d lib-$(KIND3) $(OPTS3) $*.java


