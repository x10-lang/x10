X10_DIST=../../../x10.dist
X10RT_JAR=$(X10_DIST)/lib/x10.jar
X10RT_LIBS=$(X10_DIST)/lib

%.class: %.java
	javac -cp $(X10RT_JAR) $<

SRCS = 	SerializationTest.java \
	CountPlaces.java \
	SimpleMessageTest.java \
	Latency.java

CLASSES = ${SRCS:.java=.class}


default: compile test

compile: $(CLASSES)

test: serializationtest countplaces simplemessagetest latency

serializationtest: SerializationTest.class
	mpirun -n 2 $(X10_DIST)/bin/java_x10rt SerializationTest

countplaces: CountPlaces.class
	mpirun -n 4 $(X10_DIST)/bin/java_x10rt CountPlaces

simplemessagetest: SimpleMessageTest.class
	mpirun -n 4 $(X10_DIST)/bin/java_x10rt SimpleMessageTest

latency: Latency.class
	mpirun -n 2 $(X10_DIST)/bin/java_x10rt Latency

.PHONY:	compile test 
