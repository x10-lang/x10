package x10.types;

import java.util.List;

import polyglot.types.Type;

public interface Annotated {

	//public Type baseType();
	//public AnnotatedType baseType(Type baseType);
	public List<Type> annotations();
	public Type annotations(List<Type> annotations);

}
