package x10.types.constraints;
import x10.constraint.XField;
import x10.constraint.XVar;
import polyglot.types.Type;

public interface CQualifiedVar extends XField<Type> {
	Type type();
	XVar var(); 
}
