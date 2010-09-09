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


default: compile tests

compile: $(CLASSES)

test: serializationtest countplaces simplemessagetest latency

serializationtest: SerializationTest.class
	mpirun -n 2 java -cp $(X10RT_JAR):. -Djava.library.path=$(X10RT_LIBS) SerializationTest

countplaces: CountPlaces.class
	mpirun -n 4 java -cp $(X10RT_JAR):. -Djava.library.path=$(X10RT_LIBS) CountPlaces

simplemessagetest: SimpleMessageTest.class
	mpirun -n 4 java -cp $(X10RT_JAR):. -Djava.library.path=$(X10RT_LIBS) SimpleMessageTest

latency: Latency.class
	mpirun -n 2 java -cp $(X10RT_JAR):. -Djava.library.path=$(X10RT_LIBS) Latency

.PHONY:	compile test 
