package x10.lang;


import x10.lang.annotations.*;
import x10.lang.Object;

@fun.D
@fun.R
public interface fun {
	public static interface D extends Parameter(:x==1) {}
	public static interface R extends Parameter(:x==2) {}

	Object@R apply(Object@D d);
	
}