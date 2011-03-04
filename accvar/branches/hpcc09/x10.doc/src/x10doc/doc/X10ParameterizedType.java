package x10doc.doc;

import java.util.List;

import x10.types.X10ClassType;
import x10.types.X10TypeMixin;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;

public class X10ParameterizedType extends X10Type implements ParameterizedType {
	X10ClassDoc classDoc;
	Type[] typeArgs;

	public X10ParameterizedType(x10.types.X10Type t) {
		super(t);
		
		X10RootDoc rootDoc = X10RootDoc.getRootDoc();
		classDoc = rootDoc.getUnspecClass(((X10ClassType)X10TypeMixin.baseType(t)).x10Def());

		List<polyglot.types.Type> args = ((x10.types.X10ClassType)t).typeArguments();
		typeArgs = new Type[args.size()];
		for (int i = 0; i < typeArgs.length; i++) {
			typeArgs[i] = rootDoc.getType(args.get(i));
		}
	}

	public Type containingType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Type[] interfaceTypes() {
		// TODO Auto-generated method stub
		return new Type[0];
	}

	public Type superclassType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Type[] typeArguments() {
		return typeArgs;
	}

	@Override
	public ClassDoc asClassDoc() {
		// return X10RootDoc.getRootDoc().getUnspecClass(((X10ClassType)X10TypeMixin.baseType(pType)).x10Def());
		return classDoc;
	}

	@Override
	public ParameterizedType asParameterizedType() {
		return this;
	}

	@Override
	public String qualifiedTypeName() {
		// TODO Auto-generated method stub
		return super.qualifiedTypeName();
	}

	@Override
	public String simpleTypeName() {
		// TODO Auto-generated method stub
		return super.simpleTypeName();
	}

	@Override
	public String typeName() {
		// TODO Auto-generated method stub
		return super.typeName();
	}

}
