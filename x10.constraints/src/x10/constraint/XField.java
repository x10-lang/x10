package x10.constraint;

public interface XField<T extends XType, F> extends XExpr<T> {
	F field(); 
	XTerm<T> receiver(); 
}
