package x10.lang;

import x10.lang.annotations.*;

/**
The interface satisfied by native rails. 

x10c compiling x10 code to Java "knows" that NativeValRail@T will be implemented by a Java class that
wraps Java's T[]. When this class is serialized the contents of the contained T[] are written out.

@see NativeRailMaker

@author vj 06/10/08
*/
@NativeValRail.T
public interface NativeValRail(int length) extends ClassAnnotation, TypeAnnotation {
	public static  interface T extends Parameter(:x==1) {}
	T get(int i);
	// No setter method.
}