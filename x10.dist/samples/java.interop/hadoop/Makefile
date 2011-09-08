all: .download/classpath compile

X10C = ../../../bin/x10c
X10 = ../../../bin/x10
CLASSPATH = $(shell cat .download/classpath)

.download/classpath:
	ant prepare

compile:
	$(X10C) -classpath "$(CLASSPATH)" x10/hadoop/*.x10

run:
	rm -rf output
	$(X10) -classpath "$(CLASSPATH)" x10.hadoop.WordCount x10/hadoop/WordCount.x10 output
