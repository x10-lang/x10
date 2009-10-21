package x10doc.doc;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

public class X10Type implements Type {
	protected polyglot.types.Type pType;

	public X10Type(polyglot.types.Type t) {
		pType = t;
	}
	
	public AnnotationTypeDoc asAnnotationTypeDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassDoc asClassDoc() {
		return null;
	}

	public ParameterizedType asParameterizedType() {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeVariable asTypeVariable() {
		// TODO Auto-generated method stub
		return null;
	}

	public WildcardType asWildcardType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String dimension() {
		return (pType.isArray() ? String.valueOf(pType.toClass().toArray().dims()) : "");
	}

	public boolean isPrimitive() {
		return pType.isPrimitive();
	}

	public String qualifiedTypeName() {
		// return pType.toString();	
		return "!!QTYPENAME!!";
	}

	public String simpleTypeName() {
		// return pType.toString();
		return "!!SIMPLETYPENAME!!";
	}

	public String typeName() {
		return pType.toString();
		// return "!!TYPENAME!!";
	}

}
