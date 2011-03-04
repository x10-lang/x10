package x10doc.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.Ref;

import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.ParametrizedType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10TypeMixin;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;

public class X10ParameterizedType extends X10Type implements ParameterizedType {
	boolean depType; // true if this object represents an X10 ConstrainedType (with/without type parameters)
	X10ClassDoc classDoc;
	X10RootDoc rootDoc;
	Type[] typeArgs;
	Type superclassType;
	ArrayList<Type> interfaceTypes;

	// the created X10ParameterizedType object represents a parameterized type with/without constraints 
	public X10ParameterizedType(x10.types.X10Type t, X10TypeVariable[] methodTypeVars, boolean depType) {
		super(t);

		this.depType = depType;
		rootDoc = X10RootDoc.getRootDoc();
		classDoc = rootDoc.getUnspecClass(((X10ClassType)X10TypeMixin.baseType(t)).x10Def());

		x10.types.X10Type b = (depType ? ((x10.types.X10Type) X10TypeMixin.baseType(t)) : t);
		List<polyglot.types.Type> args = ((X10ClassType)b).typeArguments();
		typeArgs = new Type[args.size()];
		for (int i = 0; i < typeArgs.length; i++) {
			typeArgs[i] = rootDoc.getType(args.get(i), methodTypeVars);
		}
		// System.out.println("X10ParameterizedType{" + t + "}.typeArgs = " + Arrays.toString(typeArgs));
		// System.out.println("X10ParameterizedType{" + t + "}: t.getClass() = " + t.getClass());

		ClassType c = t.toClass();
		superclassType = ((c.def().flags().isInterface()) ? null : 
		                  rootDoc.getType(c.superClass(), methodTypeVars));

		this.interfaceTypes = new ArrayList<Type>();
		for (polyglot.types.Type y: c.interfaces()) {
			this.interfaceTypes.add(rootDoc.getType(y, methodTypeVars));
		}
		
//		System.out.println("X10ParameterizedType(" + t + "): superclassType = " + 
//		                   superclassType + ", interfaceTypes = " + Arrays.toString(interfaceTypes.toArray(new Type[0])));
	}

	// creates an X10ParameterizedType representing an X10 ConstrainedType that does not have any type 
	// parameters, in other words a ConstrainedType for whose base type b, |b.typeParameters()| == 0  
	public X10ParameterizedType(x10.types.X10Type t) {
		super(t);
		this.depType = true;
		X10RootDoc rootDoc = X10RootDoc.getRootDoc();
		classDoc = rootDoc.getUnspecClass(((X10ClassType)X10TypeMixin.baseType(t)).x10Def());

		// the following may need to be changed, e.g., if the superclass of a ConstrainedType ct is the super class
		// obtained below with the constraints of ct; similar argument for interfaceTypes
		this.superclassType = classDoc.superclassType();
		this.interfaceTypes = new ArrayList<Type>();
		for (Type y: classDoc.interfaceTypes()) {
			this.interfaceTypes.add(y);
		}
		typeArgs = new Type[0];
	}

	public boolean isX10Specific() {
		if (pType instanceof FunctionType) { // earlier test: "(classDoc.classDef.asType() instanceof FunctionType)"
			return true;
		}
		if (depType) {
			return true;
		}
		for (Type t: this.typeArgs) {
			if (X10Type.isX10Specific(t)) {
				return true;
			}
		}
		return false;
	}

	public Type containingType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Type[] interfaceTypes() {
		return interfaceTypes.toArray(new Type[0]);
		// return new Type[0];
	}

	public Type superclassType() {
		return superclassType;
		// return null;
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

	// temporary defn used in println statement in constructor 
	public String toString() {
		return pType.toString();
	}
}
