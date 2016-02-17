compile_and_run:
	x10c -x10lib ~/x10/x10-trunk/x10.gml/managed_gml_double.properties SpMV.x10
	GML_ELEM_TYPE=double x10 -classpath ~/x10/x10-trunk/x10.gml/lib/managed_gml_double.jar -libpath ~/x10/x10-trunk/x10.gml/lib SpMV

clean:
	rm -f SpMV*.class SpMV.java
cleanall: clean
	rm -f *~
