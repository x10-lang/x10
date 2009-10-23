package x10doc.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Ref;

import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10MethodDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class X10MethodDoc extends X10Doc implements MethodDoc {
	private X10MethodDef methodDef;
	private X10ClassDoc containingClass;
	private X10RootDoc rootDoc;
	private Type returnType;
	private ArrayList<X10Parameter> parameters;
	private X10TypeVariable[] typeParams;
	// private LinkedHashMap<String, X10TypeVariable> typeParams;

	public X10MethodDoc() {
		super("");
	}

	public X10MethodDoc(X10MethodDef methodDef, X10ClassDoc containingClass, String comment) {
		super(comment);
		this.methodDef = methodDef;
		this.containingClass = containingClass;
		this.rootDoc = X10RootDoc.getRootDoc();

		// the following lines simply retrieve the formal parameter types from 
		// methodDef, to ensure that methodDef.signature() does not contain <unknown> in place
		// of formal parameter types; this is a hack, and ideally there should be no need to do it;
		// X10ClassDoc.addMethod(...) uses X10MethodDef.signature() as the key against which X10MethodDoc 
		// objects are mapped 
//		List<Ref<? extends polyglot.types.Type>> ls = methodDef.formalTypes();
//		for (Ref<? extends polyglot.types.Type> ref: ls) {
//			polyglot.types.Type formalType = ref.get();
//		}
		
		// type parameters should be initialized before return types and parameter types 
		// because the latter may use the former; further, getType requires access to this 
		// X10MethodDoc object to find the type parameters, hence the need to add this object to 
		// the containing class's ClassDoc object
		initTypeParameters();
		// containingClass.addMethod(this);

		// initialize returnType
		returnType = rootDoc.getType(methodDef.returnType().get(), typeParams);
		
		// initialize parameters
		List<LocalDef> formals = methodDef.formalNames();
		int n = ((formals == null) ? 0 : formals.size());
		parameters = new ArrayList<X10Parameter>(n);
		for (LocalDef ld: formals) {
			String paramName = ld.name().toString();
			polyglot.types.Type paramType = ld.type().get();
			parameters.add(new X10Parameter(paramName, rootDoc.getType(paramType, typeParams)));
		}
	}
	
	void initTypeParameters() {
		List<Ref<? extends polyglot.types.Type>> params = methodDef.typeParameters();
		// typeParams = new LinkedHashMap<String, X10TypeVariable>(params.size());
		typeParams = new X10TypeVariable[params.size()];
		int i = 0;
		for (Ref<? extends polyglot.types.Type> ref: params) {
			ParameterType p = (ParameterType) ref.get();
			X10TypeVariable v = new X10TypeVariable(p, this);
			typeParams[i++] = v;
			// typeParams.put(typeParameterKey(p), v);
		}
	}
	
	public X10MethodDef getMethodDef() {
		return methodDef;
	}

	public static String typeParameterKey(ParameterType p) {
		return p.name().toString();
	}
	
	public String declString() {
		// the X10 method declaration needs to be displayed in the method's comments only if a param type 
		// or the return type contains constraints; at a later point, closures will also be displayed
		// through comments
		if (!(X10Type.hasConstraints(returnType))) {
			boolean hasConstraints = false;
			for (X10Parameter p: parameters) {
				if (p.hasConstraints()) {
					hasConstraints = true;
					break;
				}
			}
			if (!hasConstraints) {
				return "";
			}
		}
		String result = "<PRE>\n</PRE><B>Declaration</B>: " + methodDef.signature() +
		                ": " + X10Type.toString(returnType);
		return result; 
	}

	public boolean isAbstract() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isAbstract() called.");
		return methodDef.flags().isAbstract();
	}

	@Override
	public boolean isIncluded() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isIncluded() called.");
		return true;
	}

	@Override
	public boolean isMethod() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isMethod() called.");
		return true;
	}

	@Override
	public String name() {
		if (X10RootDoc.printSwitch)
			System.out.println(methodDef.name() + ".name() called.");
		return methodDef.name().toString();
	}

	public ClassDoc overriddenClass() {
		// TODO Auto-generated method stub
		System.out.println(name() + ".overriddenClass() called.");
		return null;
	}

	public MethodDoc overriddenMethod() {
		// TODO Auto-generated method stub
		// System.out.println(name() + ".overriddenMethod() called.");
		return null;
	}

	public Type overriddenType() {
		// TODO Auto-generated method stub
		System.out.println(name() + ".overriddenType() called.");
		return null;
	}

	public boolean overrides(MethodDoc arg0) {
		// TODO Auto-generated method stub
		// System.out.println(name() + ".overrides(" + arg0.name() + ") called.");
		return false; 
	}

	public Type returnType() {
		return returnType;
//		System.out.print(name() + ".returnType() called. ");
//		if (retType == null) return null;
//		if (retType.isPrimitive()) {
//			System.out.println("Primitive X10Type returned.");
//			return new X10Type(retType);
//		}
//		if (retType.isClass()) {
//			System.out.println("X10ClassDoc returned.");
//
//			X10ClassDef classDef = (X10ClassDef) retType.toClass().def();
//			return createRecClassDoc(classDef);
//		}
//		System.out.println("null returned.");
//		return null;
	}
	
	public String flatSignature() {
		// System.out.println(name() + ".flatSignature() called.");
		return signature();
	}

	public boolean isNative() {
		System.out.println(name() + ".isNative() called.");
		return methodDef.flags().isNative();
	}

	public boolean isSynchronized() {
		System.out.println(name() + ".isSynchronized() called.");
		return methodDef.flags().isSynchronized();
	}

	public boolean isVarArgs() {
		// System.out.println(name() + ".isVarArgs() called.");
		return false; // no var. args. methods in current implementation of X10
	}

	public ParamTag[] paramTags() {
		// TODO Auto-generated method stub
		// System.out.println(name() + ".paramTags() called.");
		return new ParamTag[0];
	}

	public Parameter[] parameters() {
		// System.out.println(name() + ".parameters() called.");
		return parameters.toArray(new Parameter[0]);
	}

	public String signature() {
		// System.out.println(name() + ".signature() called. result = " + signature(methodDef));
		return signature(methodDef);
		// String sig = methodDef.signature();
		// return sig.substring(sig.indexOf('('));
	}
	
	public static String signature(X10MethodDef md) {
		String sig = md.signature();
		return sig.substring(sig.indexOf('('));
	}

	public Type[] thrownExceptionTypes() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".thrownExceptionTypes() called.");
		return new Type[0];
	}

	public ClassDoc[] thrownExceptions() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".thrownExceptions() called.");
		return new ClassDoc[0];
	}

	public ThrowsTag[] throwsTags() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".throwsTags() called.");
		return new ThrowsTag[0];
	}

	public ParamTag[] typeParamTags() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".typeParamTags() called.");
		return new ParamTag[0];
	}

	public TypeVariable[] typeParameters() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".typeParameters() called.");
		// return typeParams.values().toArray(new TypeVariable[0]);
		return typeParams;
	}
	
	public TypeVariable getTypeVariable(ParameterType p) {
		// return typeParams.get(typeParameterKey(p));
		return null;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isSynthetic() called.");
		return false;
	}

	public AnnotationDesc[] annotations() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".annotations() called.");
		return new AnnotationDesc[0];
	}

	public ClassDoc containingClass() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".containingClass() called.");
		return containingClass;
	}

	public PackageDoc containingPackage() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".containingPackage() called.");
		return containingClass.containingPackage();
	}

	public boolean isFinal() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isFinal() called.");
		return methodDef.flags().isFinal();
	}

	public boolean isPackagePrivate() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isPackagePrivate() called.");
		return methodDef.flags().isPackage();
	}

	public boolean isPrivate() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isPrivate() called.");
		return methodDef.flags().isPrivate();
	}

	public boolean isProtected() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isProtected() called.");
		return methodDef.flags().isProtected();
	}

	public boolean isPublic() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isPublic() called.");
		return methodDef.flags().isPublic();
	}

	public boolean isStatic() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".isStatic() called.");
		return methodDef.flags().isStatic();
	}

	public int modifierSpecifier() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".modifierSpecifier() called.");
//		int r = 0;
//		for (Object flag : methodDef.flags().flags()) {
//			// flag could be "property" which is not in flagsToHex (and not recognized by 
//			// the standard doclet)
//			if (flagsToHex.containsKey((String)flag)) {
//				r |= flagsToHex.get((String)flag);
//			}
//		}
		// return r;
		return X10Doc.flagsToModifierSpecifier(methodDef.flags().flags());
	}

	public String modifiers() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".modifiers() called.");
		return methodDef.flags().toString();
	}

	public String qualifiedName() {
		if (X10RootDoc.printSwitch)
			System.out.println(name() + ".qualifiedName() called.");
		return methodDef.name().toString();
	}
}
