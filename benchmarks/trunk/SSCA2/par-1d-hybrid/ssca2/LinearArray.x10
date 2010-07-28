package ssca2;

import x10.compiler.Native;

class LinearArray[T] {
	
	val size: Int;
val data: Rail[T];
val ptr: Long;
val dummy: T;

public static def make[T] (r: Region(1), init:(Int)=>T)  = new LinearArray[T](r, init);

public def this (r: Region(1)){
	size = r.size();
	data = Rail.make[T](size);
	val offset = r.min(0);
	ptr = 0;
	dummy = data(0);
	{ @Native ("c++", "FMGL(ptr) = (x10_long) (FMGL(data)->raw() - offset);") {} }
}

public def this (r: Region(1), init:(Int)=>T) {
	size = r.size();
	data = Rail.make[T](size, init);
	val offset = r.min(0);
	ptr = 0;
	dummy = data(0);
	{ @Native ("c++", "FMGL(ptr) = (x10_long) (FMGL(data)->raw() - offset);") {} }
}

@Native("c++", "((#2*) (#0)->FMGL(ptr))[#1]")
public native safe def apply(i: Int): T  ;


@Native("c++", "((#3*) (#0)->FMGL(ptr))[#2] = #1;")
public native safe def set(elem:T, i: Int): T;
};


