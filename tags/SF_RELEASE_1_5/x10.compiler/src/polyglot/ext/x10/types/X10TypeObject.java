package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Type;
import polyglot.types.TypeObject;

public interface X10TypeObject extends TypeObject {
	/** Return all annotations. */
	public List<X10ClassType> annotations();
	/** Set the annotations, destructively. */
	public void setAnnotations(List<X10ClassType> annotations);
	/** Set the annotations in a copy of this. */
	public X10TypeObject annotations(List<X10ClassType> annotations);
	/** Return all annotations that are subtypes of <code>ct</code>. */
	public List<X10ClassType> annotationMatching(Type t);
	/** Return true if the annotations have been set. */
	public boolean annotationsSet();
}
