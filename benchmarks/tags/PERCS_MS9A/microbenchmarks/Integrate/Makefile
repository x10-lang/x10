all:	cppint2 Integrate2.class compilex10

JAVA_SDK=/etc/alternatives/java_sdk_1.6.0


cppint2: int2.cpp
	g++ -O3 -msse2 int2.cpp -o cppint2 -lrt

Integrate2.class: Integrate2.java
	$(JAVA_SDK)/bin/javac -cp lib/jsr166y.jar Integrate2.java

runjava: Integrate2.class
	$(JAVA_SDK)/bin/java -Xbootclasspath/p:lib/jsr166y.jar Integrate2 $(ARGS)

gen-x10:
	mkdir gen-x10

compilex10: gen-x10 Integrate2.x10
	x10c -commandlineonly -optimize -d gen-x10 Integrate2.x10
	x10c++ -optimize -d gen-x10 -report postcompile=2 -o x10int2 Integrate2.x10

runjavax10: compilex10
	cd gen-x10 && x10 Integrate2 $(ARGS)

runcppx10: compilex10
	cd mpirun -n $(PLACES) x10int2 $(ARGS)


experiments: cppint2 compilex10
	./cppint2
	$(MAKE) runjava ARGS="1 s"
	$(MAKE) runjava ARGS="1 f"
	$(MAKE) runjava ARGS="1 d"
	$(MAKE) runjava ARGS="2 f"
	$(MAKE) runjava ARGS="2 d"

serial_experiments: cppint2 compilex10
	@echo "Performing Serial Experiments"
	@echo "Running C++ version"
	./cppint2
	@echo "Running Java version"
	$(MAKE) runjava ARGS="1 s"
	@echo "Running X10-Java version"
	$(MAKE) runjavax10 ARGS="s"
	@echo "Running X10-c++ version"
	$(MAKE) runcpp10 ARGS="s" PLACES=1

clean:
	rm -rf gen-x10
	rm -f *.class
	rm -f cppint2
	rm -f x10int2
