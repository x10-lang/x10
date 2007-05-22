package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.TypeObject;

public interface X10TypeObject extends TypeObject {
	public List<X10ClassType> annotations();
	public void setAnnotations(List<X10ClassType> annotations);
	public X10TypeObject annotations(List<X10ClassType> annotations);
	public X10ClassType annotationNamed(String name);
}
