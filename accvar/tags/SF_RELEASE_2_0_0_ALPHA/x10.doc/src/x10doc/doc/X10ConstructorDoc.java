package x10doc.doc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import polyglot.types.LocalDef;
import polyglot.types.Ref;
import x10.types.ParameterType;
import x10.types.X10ConstructorDef;
import x10.types.X10MethodDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class X10ConstructorDoc extends X10Doc implements ConstructorDoc {
	private X10ConstructorDef constrDef;
	private X10ClassDoc containingClass;
	private X10RootDoc rootDoc;
	private ArrayList<Parameter> parameters;
	private X10TypeVariable[] typeParams;
	// private LinkedHashMap<String, X10TypeVariable> typeParams;
	
	public X10ConstructorDoc() {
		super("");
	}

	public X10ConstructorDoc(X10ConstructorDef constrDef, X10ClassDoc containingClass, String comment) {
		super(comment);
		this.constrDef = constrDef;
		this.containingClass = containingClass;
		this.rootDoc = X10RootDoc.getRootDoc();

		// the following ensure that methodDef.signature() does not contain <unknown> in place
		// of formal parameter types; see constructor X10MethodDoc(...) for more details
//		List<Ref<? extends polyglot.types.Type>> ls = constrDef.formalTypes();
//		for (Ref<? extends polyglot.types.Type> ref: ls) {
//			polyglot.types.Type formalType = ref.get();
//		}
		
		// type parameters should be initialized before parameter types because the latter may use the former
		initTypeParameters();
		// containingClass.addConstructor(this);

		// initialize parameters
		List<LocalDef> formals = constrDef.formalNames();
		int n = ((formals == null) ? 0 : formals.size());
		parameters = new ArrayList<Parameter>(n);
		for (LocalDef ld: formals) {
			String paramName = ld.name().toString();
			polyglot.types.Type paramType = ld.type().get();
			parameters.add(new X10Parameter(paramName, rootDoc.getType(paramType, typeParams)));
		}
	}
	
	void initTypeParameters() {
		List<Ref<? extends polyglot.types.Type>> params = constrDef.typeParameters();
		// typeParams = new LinkedHashMap<String, X10TypeVariable>(params.size());
		typeParams = new X10TypeVariable[params.size()];
		int i = 0;
		for (Ref<? extends polyglot.types.Type> ref: params) {
			ParameterType p = (ParameterType) ref.get();
			X10TypeVariable v = new X10TypeVariable(p, this);
			// typeParams.put(typeParameterKey(p), v);
			typeParams[i++] = v;
		}
	}
	
	public X10ConstructorDef getConstructorDef() {
		return constrDef;
	}
	
	@Override
	public boolean isConstructor() {
		return true;
	}

	@Override
	public boolean isIncluded() {
		return true;
	}

	@Override
	public String name() {
		return containingClass.name();
	}

	public String flatSignature() {
		return signature();
	}

	public boolean isNative() {
		return constrDef.flags().isNative();
	}

	public boolean isSynchronized() {
		return constrDef.flags().isSynchronized();
	}

	public boolean isVarArgs() {
		return false; // no variable args. constructors in current X10 implementation
	}

	public ParamTag[] paramTags() {
		// TODO Auto-generated method stub
		return new ParamTag[0];
	}

	public Parameter[] parameters() {
		return parameters.toArray(new Parameter[0]);
	}

	public String signature() {
		String sig = constrDef.signature();
		return sig.substring(sig.indexOf('('));
	}

	public Type[] thrownExceptionTypes() {
		// TODO Auto-generated method stub
		return new Type[0];
	}

	public ClassDoc[] thrownExceptions() {
		// TODO Auto-generated method stub
		return new ClassDoc[0];
	}

	public ThrowsTag[] throwsTags() {
		// TODO Auto-generated method stub
		return new ThrowsTag[0];
	}

	public ParamTag[] typeParamTags() {
		// TODO Auto-generated method stub
		return new ParamTag[0];
	}

	public TypeVariable[] typeParameters() {
		// return typeParams.values().toArray(new TypeVariable[0]);
		return typeParams;
	}

	public TypeVariable getTypeVariable(ParameterType p) {
		// return typeParams.get(typeParameterKey(p));
		return null;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

	public AnnotationDesc[] annotations() {
		// TODO Auto-generated method stub
		return new AnnotationDesc[0];
	}

	public ClassDoc containingClass() {
		return containingClass;
	}

	public PackageDoc containingPackage() {
		return containingClass.containingPackage();
	}

	public boolean isFinal() {
		return constrDef.flags().isFinal();
	}

	public boolean isPackagePrivate() {
		return constrDef.flags().isPackage();
	}

	public boolean isPrivate() {
		return constrDef.flags().isPrivate();
	}

	public boolean isProtected() {
		return constrDef.flags().isProtected();
	}

	public boolean isPublic() {
		return constrDef.flags().isPublic();
	}

	public boolean isStatic() {
		return constrDef.flags().isStatic();
	}

	public int modifierSpecifier() {
		return X10Doc.flagsToModifierSpecifier(constrDef.flags().flags());
	}

	public String modifiers() {
		return constrDef.flags().toString();
	}

	public String qualifiedName() {
		return containingClass.qualifiedName();
	}

}
