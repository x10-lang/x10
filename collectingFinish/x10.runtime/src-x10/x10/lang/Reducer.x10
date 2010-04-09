package x10.lang;

public interface Reducer[T] extends (T,T)=>T {
	zero():T;
	
}