package x10.lang;

import x10.lang.annotations.TypeAnnotation;
import x10.lang.annotations.ClassAnnotation;

@Arithmetic.T
public interface Arithmetic extends TypeAnnotation, ClassAnnotation {
	static @Bound@Arithmetic interface T extends Parameter(:x==1) {}
	
	T add(T x);
	T sub(T x);
	T mul(T x);
	T div(T x);
	T neginv();
	T mulinv();
	T zero();
	T unit();
	
}