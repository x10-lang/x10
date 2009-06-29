package x10.lang;


import x10.lang.annotations.*;

/**
The interface satisfied by native rails. 

x10c compiling x10 code to Java "knows" that NativeRail@T will be implemented by Java's T[]; hence
for o of type NativeArray@T, o.get(i) operations are replaced by o[i], o.set(i, v) by o[i]=v.

Calls to NativeRailMaker@T.make(l) are replaced by new T[l];

@see NativeArrayMaker

@author vj 06/10/08
*/
@NativeRail.T
public interface NativeRail(int length) extends ClassAnnotation, TypeAnnotation {
	public static  interface T extends Parameter(:x==1) {}
	T get(int i);
	void set(int i, T v);
	
}