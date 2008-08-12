/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.TopLevelResolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.VarDef;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XRef_c;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

/**
 * A TypeSystem implementation for X10.
 *
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 */
public class X10TypeSystem_c extends TypeSystem_c implements X10TypeSystem {
	
	public X10TypeSystem_c() {
		super();
	}
	
	public Type bound(Type container) {
	    if (container instanceof MacroType) {
		MacroType mt = (MacroType) container;
		return mt.definedType();
	    }
//	    if (container instanceof PathType) {
//		PathType pt = (PathType) container;
//		XConstraint c = pt.base().selfConstraint();
//		for (XTerm t : c.constraints()) {
//		}
//		return xxx;
//	    }
	    if (container instanceof ConstrainedType) {
		ConstrainedType ct = (ConstrainedType) container;
		return ct.baseType().get();
	    }
	    return container;
	}
	protected Set<FieldInstance> findFields(Type container, TypeSystem_c.FieldMatcher matcher) {
		assert_(container);
		container = bound(container);
		return super.findFields(container, matcher);
	}
	
	public TypeDefMatcher TypeDefMatcher(Type container, String name, List<Type> typeArgs, List<Type> argTypes) {
	    return new TypeDefMatcher(container, name, typeArgs, argTypes);
	}
	
	@Override
	public Type findMemberType(Type container, String name, ClassDef currClass) throws SemanticException {
	    container = bound(container);
	    try {
		return super.findMemberType(container, name, currClass);
	    }
	    catch (SemanticException e) {
	    }
	    try {
		return this.findTypeDef(container, this.TypeDefMatcher(container, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST), currClass);
	    }
	    catch (SemanticException e) {
	    }
	    try {
		return this.findTypeProperty(container, name, currClass);
	    }
	    catch (SemanticException e) {
	    }
	    
	    throw new NoClassException(name, container);
	}

	public X10ClassDef closureInterfaceDef(ClosureDef def) {
	    int numTypeParams = def.typeParameters().size();
	    int numValueParams = def.formalTypes().size();
	    
	    X10TypeSystem ts = this;
	    Position pos = Position.COMPILER_GENERATED;
	    String name = "Fun_" + numTypeParams + "_" + numValueParams;

	    // Check if the class has already been defined.
	    Named n = ts.systemResolver().check("x10.lang." + name);
	    if (n instanceof X10ClassType) {
		X10ClassType ct = (X10ClassType) n;
		return ct.x10Def();
	    }

	    X10ClassDef cd = (X10ClassDef) ts.createClassDef();
	    cd.position(pos);
	    cd.name(name);
	    try {
		cd.setPackage(Types.ref(ts.packageForName("x10.lang")));
	    }
	    catch (SemanticException e) {
		assert false;
	    }
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.superType(Types.ref(ts.Object()));
	    cd.flags(Flags.PUBLIC.Abstract().Interface());
	    ts.systemResolver().install("x10.lang." + name, cd.asType());

	    cd.addConstructor(ts.defaultConstructor(pos, Types.ref(cd.asType())));
	    
	    // Add type properties and build the apply method.
	    List<Ref<? extends Type>> typeParams = new ArrayList<Ref<? extends Type>>();
	    List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
	    List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>();
	    Ref<XConstraint> whereClause = null; // hmmm.... should be false since it has to imply all subtype constraints -- need to have a boolean property for the constraint?
	    
	    ParameterType returnType = new ParameterType_c(this, pos, "U", Types.ref(cd));
	    cd.addTypeParameter(returnType, TypeProperty.Variance.COVARIANT);

	    for (int i = 0; i < numTypeParams; i++) {
		Type t = new ParameterType_c(this, pos, "X" + i, Types.ref(cd));
		typeParams.add(Types.ref(t));
	    }

	    for (int i = 0; i < numValueParams; i++) {
		ParameterType t = new ParameterType_c(this, pos, "Z" + (i+1), Types.ref(cd));
		cd.addTypeParameter(t, TypeProperty.Variance.CONTRAVARIANT);
		argTypes.add(Types.ref(t));
	    }
	    
	    X10MethodDef mi = ts.methodDef(pos, Types.ref(cd.asType()), Flags.PUBLIC, Types.ref(returnType), "apply", typeParams, argTypes, dummyLocalDefs(argTypes), whereClause, throwTypes, null);
	    cd.addMethod(mi);
	    return cd;
	}
	
	public List<LocalDef> dummyLocalDefs(List<Ref<? extends Type>> types) {
	    List<LocalDef> list = new ArrayList<LocalDef>();
	    for (int i = 0; i < types.size(); i++) {
		LocalDef ld = localDef(Position.COMPILER_GENERATED, Flags.FINAL, types.get(i), "a" + (i+1));
		ld.setNotConstant();
		list.add(ld);
	    }
	    return list;
	}

	@Override
	public List<java.lang.String> defaultOnDemandImports() {
	    List<String> l = new ArrayList<String>(1);
	    l.add("x10.lang");
	    l.add("x10.lang.annotation");
	    return l;
	}
	
	static class TypeDefMatcher {
	    Type container;
	    String name;
	    List<Type> typeArgs;
	    List<Type> argTypes;
	    
	    public TypeDefMatcher(Type container, String name, List<Type> typeArgs, List<Type> argTypes) {
		this.container = container;
		this.name = name;
		this.typeArgs = typeArgs;
		this.argTypes = argTypes;
	    }

	    public String name() {
		return name;
	    }
	    public String argumentString() {
		return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
	    }
	    public String signature() {
		return name + argumentString();
	    }
	    public String toString() {
		return signature();
	    }

	    public MacroType instantiate(MacroType mi) throws SemanticException {
		if (! mi.name().equals(name))
		    return null;
		if (mi.formalTypes().size() != argTypes.size())
		    return null;
		if (mi instanceof MacroType) {
		    MacroType xmi = (MacroType) mi;
		    if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
			return X10MethodInstance_c.instantiate(xmi, container, typeArgs, argTypes);
		}
		return null;
	    }
	}
	
	protected List<MacroType> findAcceptableTypeDefs(Type container, TypeDefMatcher matcher, ClassDef currClass)
	    throws SemanticException {
	    assert_(container);
	    
	    container = bound(container);
	    
	    SemanticException error = null;
	    
	    // The list of acceptable methods. These methods are accessible from
	    // currClass, the method call is valid, and they are not overridden
	    // by an unacceptable method (which can occur with protected methods
	    // only).
	    List<MacroType> acceptable = new ArrayList<MacroType>();
	    
	    // A list of unacceptable methods, where the method call is valid, but
	    // the method is not accessible. This list is needed to make sure that
	    // the acceptable methods are not overridden by an unacceptable method.
	    List<MacroType> unacceptable = new ArrayList<MacroType>();
	    
	    Set<Type> visitedTypes = new HashSet<Type>();
	    
	    LinkedList<Type> typeQueue = new LinkedList<Type>();
	    typeQueue.addLast(container);
	    
	    while (! typeQueue.isEmpty()) {
	    	Type t = typeQueue.removeFirst();
	    
	    	if (! (t instanceof X10ParsedClassType)) {
	    		continue;
	    	}
	    
	    	X10ParsedClassType type = (X10ParsedClassType) t;
	    
	    	if (visitedTypes.contains(type)) {
	    		continue;
	    	}
	    
	    	visitedTypes.add(type);
	    
	    	if (Report.should_report(Report.types, 2))
	    		Report.report(2, "Searching type " + type + " for method " +
	    				matcher.signature());
	    
	    	for (Iterator<Type> i = type.typeMembers().iterator(); i.hasNext(); ) {
	    	    Type ti = i.next();
	    		
	    	    if (!(ti instanceof MacroType)) {
	    		continue;	    		
	    	    }
	    	    
	    	    MacroType mi = (MacroType) ti;
	    	    
	    		if (Report.should_report(Report.types, 3))
	    			Report.report(3, "Trying " + mi);
	    
	    		try {
	    			mi = matcher.instantiate(mi);

	    			if (mi == null) {
	    			    continue;
	    			}
	    			
	    			if (isAccessible(mi, currClass)) {
	    			    if (Report.should_report(Report.types, 3)) {
	    				Report.report(3, "->acceptable: " + mi + " in "
	    				              + mi.container());
	    			    }

	    			    acceptable.add(mi);
	    			}
	    			else {
	    			    // method call is valid, but the method is
	    			    // unacceptable.
	    			    unacceptable.add(mi);
	    			    if (error == null) {
	    				error = new NoMemberException(NoMemberException.METHOD,
	    				                              "Method " + mi.signature() +
	    				                              " in " + container +
	    				" is inaccessible."); 
	    			    }
	    			}

	    			continue;
	    		}
	    		catch (SemanticException e) {
	    		}
	    
	    		if (error == null) {
	    			error = new SemanticException("Type definition " + mi.name() +
	    			                              " in " + container +
	    			                              " cannot be instantiated with arguments " + matcher.argumentString() + ".");
	    		}
	    	}
	    	
	    	if (type instanceof ObjectType) {
	    	    ObjectType ot = (ObjectType) type;
	    
	    	    if (ot.superClass() != null) {
	    		typeQueue.addLast(ot.superClass());
	    	    }
	    
	    	    typeQueue.addAll(ot.interfaces());
	    	}
	    }
	    
	    if (error == null) {
		error = new SemanticException("No type defintion found in "
		                            + container +
		                              " for " + matcher.signature() + ".");
	    }
	    
	    if (acceptable.size() == 0) {
	    	throw error;
	    }
	    
	    // remove any types in acceptable that are overridden by an
	    // unacceptable
	    // type.
	    // TODO
//	    for (Iterator<MacroType> i = unacceptable.iterator(); i.hasNext();) {
//		MacroType mi = i.next();
//	    	acceptable.removeAll(mi.overrides());
//	    }
	    
	    if (acceptable.size() == 0) {
	    	throw error;
	    }
	    
	    return acceptable;
	}
	
	public MacroType findTypeDef(Type container, TypeDefMatcher matcher, ClassDef currClass) throws SemanticException {
		List<MacroType> acceptable = findAcceptableTypeDefs(container, matcher, currClass);
		
		if (acceptable.size() == 0) {
			throw new SemanticException(
					"No valid type definition found for " + matcher.signature() +
					" in " +
					container + ".");
		}
		
		Collection<MacroType> maximal =
			findMostSpecificProcedures(acceptable);
		
		if (maximal.size() > 1) {
			StringBuffer sb = new StringBuffer();
			for (Iterator<MacroType> i = maximal.iterator(); i.hasNext();) {
			    MacroType ma = (MacroType) i.next();
				sb.append(ma.returnType());
				sb.append(" ");
				sb.append(ma.container());
				sb.append(".");
				sb.append(ma.signature());
				if (i.hasNext()) {
					if (maximal.size() == 2) {
						sb.append(" and ");
					}
					else {
						sb.append(", ");
					}
				}
			}
		
			throw new SemanticException("Reference to " + matcher.name() +
					" is ambiguous, multiple type defintions match: "
					+ sb.toString());
		}
		
		MacroType mi = maximal.iterator().next();
		
		return mi;
	}
	
	public List<MacroType> findTypeDefs(Type container, String name, ClassDef currClass) throws SemanticException {
	    assert_(container);
	    
//	    Named n = classContextResolver(container, currClass).find(name);
//	    
//	    if (n instanceof MacroType) {
//		return (MacroType) n;
//	    }
	    
	    throw new NoClassException(name, container);
	}
	
	
	public PathType findTypeProperty(Type container, String name, ClassDef currClass) throws SemanticException {
		assert_(container);
		
		container = bound(container);
		
		Named n = classContextResolver(container, currClass).find(name);
		
		if (n instanceof PathType) {
			return (PathType) n;
		}
		
		throw new NoClassException(name, container);
	}
	
	private final static Set<String> primitiveTypeNames = new HashSet<String>();
	
	static {
		primitiveTypeNames.add("boolean");
		primitiveTypeNames.add("byte");
		primitiveTypeNames.add("char");
		primitiveTypeNames.add("short");
		primitiveTypeNames.add("int");
		primitiveTypeNames.add("long");
		primitiveTypeNames.add("float");
		primitiveTypeNames.add("double");
	}

	public boolean isPrimitiveTypeName(String name) {
		return primitiveTypeNames.contains(name);
	}

	public ClassDef unknownClassDef() {
	    if (unknownClassDef == null) {
	        unknownClassDef = new X10ClassDef_c(this, null);
	        unknownClassDef.name("<unknown class>");
	        unknownClassDef.kind(ClassDef.TOP_LEVEL);
	    }
	    return unknownClassDef;
	}
	
	X10UnknownType_c unknownType;
	
	@Override
	public UnknownType unknownType(Position pos) {
		if (unknownType == null)
			unknownType = new X10UnknownType_c(this);
		return unknownType;
	}

	private X10ParsedClassType refType_;
	public Type Ref() {
		if (refType_ == null)
			refType_ = (X10ParsedClassType) load("x10.lang.Ref");
		return fixGenericType(null, refType_, false);
	}

	private X10ParsedClassType boxType_;
	public Type Box() {
		if (boxType_ == null)
			boxType_ = createBoxType();
		return fixGenericType(null, boxType_, false);
	}
	
	public X10ParsedClassType createBoxType() {
	    X10ClassDef cd = (X10ClassDef) createClassDef();
	    cd.name("Box");
	    cd.position(Position.COMPILER_GENERATED);
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.flags(Flags.PUBLIC.Final());
	    try {
		cd.setPackage(Types.ref(packageForName("x10.lang")));
	    }
	    catch (SemanticException e) {
		throw new InternalCompilerError(e);
	    }
	    
	    Type pt;
	    if (X10ClassDecl_c.CLASS_TYPE_PARAMETERS) {
		ParameterType param = new ParameterType_c(this, Position.COMPILER_GENERATED, "T", Types.ref(cd));
		cd.addTypeParameter(param, TypeProperty.Variance.COVARIANT);
		pt = param;
	    }
	    else {
		TypeProperty prop = new TypeProperty_c(this, Position.COMPILER_GENERATED, Types.ref((X10ClassType) cd.asType()), "T", TypeProperty.Variance.INVARIANT);
		cd.addTypeProperty(prop);
		pt = prop.asType();
	    }

	    X10FieldDef fi = (X10FieldDef) fieldDef(Position.COMPILER_GENERATED, Types.ref(cd.asType()), Flags.FINAL.Public(), Types.ref(pt), "value");
	    fi.setProperty();
	    cd.addField(fi);

//	    ParameterType pt = new ParameterType_c(this, Position.COMPILER_GENERATED, "T", null);
//	    XConstraint c = new XConstraint_c();
//	    c.addBinding(prop.asVar(), param);
//	    c.addBinding(xtypeTranslator().trans(XSelf.Self, fi), value);
//	    Type returnType = X10TypeMixin.xclause(Types.ref(cd.asType()), c);
//	    X10ConstructorDef ci = constructorDef(Position.COMPILER_GENERATED, Types.ref(cd.asType()), Flags.PUBLIC, Types.ref(returnType), params, formals, Collections.EMPTY_LIST);
//	    cd.addConstructor(ci);
	    
	    return (X10ParsedClassType) cd.asType();
	}
	

	protected ClassType valueType_;
	public Type Value() {
		if (valueType_ == null)
//		    valueType_ = load("x10.lang.Value");
			valueType_ = createValueType(); // don't load from disk--the problem is that it's needed to bootstrap
		return valueType_;
	}
	
	public X10ParsedClassType createValueType() {
	    X10ClassDef cd = (X10ClassDef) createClassDef();
	    cd.name("Value");
	    cd.position(Position.COMPILER_GENERATED);
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.flags(Flags.PUBLIC.Abstract().Interface());
	    try {
		cd.setPackage(Types.ref(packageForName("x10.lang")));
	    }
	    catch (SemanticException e) {
		throw new InternalCompilerError(e);
	    }
	    
	    return (X10ParsedClassType) cd.asType();
	}
	
	public Type boxOf(Ref<? extends Type> base) {
		return boxOf(Position.COMPILER_GENERATED, base);
	}
	
	public ClosureType toFunction(Type t) {
	    // TODO: check for uniqueness
	    t = X10TypeMixin.xclause(t, (XConstraint) null);
	    if (t instanceof ClosureType)
		return (ClosureType) t;
	    if (t instanceof MacroType) {
		MacroType mt = (MacroType) t;
		return toFunction(mt.definedType());
	    }
	    if (t instanceof ObjectType) {
		ObjectType ot = (ObjectType) t;
		Type sup = ot.superClass();
		if (sup != null) {
		    ClosureType ct = toFunction(sup);
		    if (ct != null)
			return ct;
		}
		for (Type ti : ot.interfaces()) {
		    ClosureType ct = toFunction(ti);
		    if (ct != null)
			return ct;
		}
	    }
	    return null;
	}
	
	public boolean isFunction(Type t) {
	    return toFunction(t) != null;
	}

	public boolean isBox(Type t) {
		X10TypeSystem ts = this;
		if (ts.descendsFrom(X10TypeMixin.xclause(t, (XConstraint) null), ts.Box())) {
			return true;
		}
		return false;
	}
	
	public boolean isRef(Type t) {
		X10TypeSystem ts = this;
		if (ts.descendsFrom(X10TypeMixin.xclause(t, (XConstraint) null), ts.Ref())) {
			return true;
		}
		return false;
	}
	
	TypeDef boxRefTypeDef_;
	/** type Box[T]{T <: Ref} = T; */
	public TypeDef BoxRefTypeDef() {
		if (boxRefTypeDef_ == null) {
			Ref<TypeDef> r = Types.ref(null);
			Type param = new ParameterType_c(this, Position.COMPILER_GENERATED, "T", r);
			XConstraint subtypeOfRef = new XConstraint_c();
			try {
				subtypeOfRef.addAtom(xtypeTranslator().transSubtype(param, Ref()));
			}
			catch (XFailure e) {
			}
			boxRefTypeDef_ = new TypeDef_c(this, Position.COMPILER_GENERATED, Flags.PUBLIC, "Box", null, (List) Collections.singletonList(Types.ref(param)), Collections.EMPTY_LIST, Collections.EMPTY_LIST, Types.ref(subtypeOfRef), Types.ref(param));
			r.update(boxRefTypeDef_);
		}
		return boxRefTypeDef_;
	}

//	public X10NamedType createBoxFromTemplate(X10ClassDef def) {
//		X10ClassDef_c c = def;
//	}
	
//	private X10ParsedClassType createBox() {
//		X10ClassDef cd = (X10ClassDef) createClassDef();
//		try {
//			cd.package_(Types.ref(packageForName(Types.ref(packageForName("x10")), "lang")));
//		}
//		catch (SemanticException e) {
//		}
//		cd.name("Box");
//		cd.flags(Flags.FINAL.Public());
//		cd.position(Position.COMPILER_GENERATED);
//		cd.kind(ClassDef.TOP_LEVEL);
//		Ref<Type> paramRef = Types.ref(null);
//		X10ConstructorDef ci = constructorDef(Position.COMPILER_GENERATED, Types.ref(cd.asType()), Flags.PUBLIC.Native(), Types.ref(cd.asType()),
//						      (List) Collections.singletonList(paramRef), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
//		Type param = new ParameterType_c(this, Position.COMPILER_GENERATED, "X", Types.ref(ci));
//		paramRef.update(param);
//		cd.addConstructor(ci);
//		X10ParsedClassType ct = (X10ParsedClassType) cd.asType();
//		TypeProperty p = new TypeProperty_c(this, Position.COMPILER_GENERATED, Types.<X10ClassType>ref(ct), "T", TypeProperty.Variance.INVARIANT);
//		cd.addTypeProperty(p);
//		return ct;
//	}
	
	@Override
	public Type arrayOf(Position pos, Ref<? extends Type> type) {
	    // Should be called only by the Java class file loader.
	    Type r = Rail();
	    return X10TypeMixin.instantiate(r, type);
	}

	@Override
	protected ArrayType createArrayType(Position pos, Ref<? extends Type> type) {
	    return new X10ArrayType_c(this, pos, type);
	}
	
	@Override
	public ClassDef createClassDef(Source fromSource) {
	    return new X10ClassDef_c(this, fromSource);
	}
	
	@Override
	public ParsedClassType createClassType(Position pos, Ref<? extends ClassDef> def) {
	    return new X10ParsedClassType_c(this, pos, def);
	}

	@Override
	public ConstructorInstance createConstructorInstance(Position pos, Ref<? extends ConstructorDef> def) {
	    return new X10ConstructorInstance_c(this, pos, (Ref<? extends X10ConstructorDef>) def);
	}

	@Override
	public MethodInstance createMethodInstance(Position pos, Ref<? extends MethodDef> def) {
	    return new X10MethodInstance_c(this, pos, (Ref<? extends X10MethodDef>) def);
	}

	@Override
	public FieldInstance createFieldInstance(Position pos, Ref<? extends FieldDef> def) {
	    return new X10FieldInstance_c(this, pos, (Ref<? extends X10FieldDef>) def);
	}

	@Override
	public LocalInstance createLocalInstance(Position pos, Ref<? extends LocalDef> def) {
	    return new X10LocalInstance_c(this, pos, (Ref<? extends X10LocalDef>) def);
	}
	
	public ClosureInstance createClosureInstance(Position pos, Ref<? extends ClosureDef> def) {
	    return new ClosureInstance_c(this, pos, def);
	}


//	@Override
	//	    public InitializerInstance createInitializerInstance(Position pos, <? extends InitializerDef> def) {
	//	        return new X10InitializerInstance_c(this, pos, def);
	//	    }

	public Context createContext() {
		return new X10Context_c(this);
	}
	
	/** All flags allowed for a method. */
	public Flags legalMethodFlags() {
		X10Flags x = X10Flags.toX10Flags( legalAccessFlags().Abstract().Static().Final().Native().Synchronized().StrictFP());
		x = x.Safe().Local().NonBlocking().Sequential().Incomplete().Property().Pure();
		return x;
		
	}

	/** All flags allowed for a top-level class. */
	public Flags legalTopLevelClassFlags() {
		return X10Flags.toX10Flags(super.legalTopLevelClassFlags()).Safe().Value();
	}
	
	protected final X10Flags X10_TOP_LEVEL_CLASS_FLAGS = (X10Flags) legalTopLevelClassFlags();
	
	/** All flags allowed for an interface. */
	public Flags legalInterfaceFlags() {
		return X10Flags.toX10Flags(super.legalInterfaceFlags()).Safe();
	}
	
	protected final X10Flags X10_INTERFACE_FLAGS = (X10Flags) legalInterfaceFlags();
	
	/** All flags allowed for a member class. */
	public Flags legalMemberClassFlags() {
		return X10Flags.toX10Flags(super.legalMemberClassFlags()).Safe().Value();
	}
	
	protected final Flags X10_MEMBER_CLASS_FLAGS = (X10Flags) legalMemberClassFlags();
	
	/** All flags allowed for a local class. */
	public Flags legalLocalClassFlags() {
		return X10Flags.toX10Flags(super.legalLocalClassFlags()).Safe().Value();
	}
	
	protected final X10Flags X10_LOCAL_CLASS_FLAGS = (X10Flags) legalLocalClassFlags();
	
	
	@Override
	public MethodDef methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, String name,
			List<Ref<? extends Type>> argTypes,
			List<Ref<? extends Type>> excTypes) {
		return methodDef(pos, container, flags, returnType, name, Collections.EMPTY_LIST, argTypes, dummyLocalDefs(argTypes), null, excTypes, null);
	}
	
	public X10MethodDef methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, String name,
	        List<Ref<? extends Type>> typeParams,
	        List<Ref<? extends Type>> argTypes,
	        List<LocalDef> formalNames,
	        Ref<XConstraint> whereClause,
	        List<Ref<? extends Type>> excTypes, Ref<XTerm> body) {
	    assert_(container);
	    assert_(returnType);
	    assert_(typeParams);
	    assert_(argTypes);
	    assert_(excTypes);
	    return new X10MethodDef_c(this, pos, container, flags,
	                              returnType, name, typeParams, argTypes, formalNames, whereClause, excTypes, body);
	}
	
	/**
	 * Return a nullable type based on a given type.
	 * TODO: rename this to nullableType() -- the name is misleading.
	 */
	public Type boxOf(Position pos, Ref<? extends Type> type) {
	    X10ParsedClassType box = (X10ParsedClassType) Box();
	    return fixGenericType(type, box, false);
	}
	
	X10ParsedClassType futureType_;
	public Type futureOf(Position pos, Ref<? extends Type> base) {
		if (futureType_ == null)
			futureType_ = (X10ParsedClassType) load("x10.lang.Future");
		return fixGenericType(base, futureType_, false);
	}
	
	/**
	 * [IP] TODO: this should be a special CodeInstance instead
	 */
	protected CodeDef asyncStaticCodeInstance_;
	protected CodeDef asyncCodeInstance_;
	public CodeDef asyncCodeInstance(boolean isStatic) {
		if (isStatic) {
			if (asyncStaticCodeInstance_ == null)
				asyncStaticCodeInstance_ = 
				    methodDef(Position.COMPILER_GENERATED, Types.ref((StructType) Runtime()), Public().Static(), Types.ref(VOID_), "$dummyAsync$",
                                                     Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			return asyncStaticCodeInstance_;
		} else {
			if (asyncCodeInstance_ == null)
				asyncCodeInstance_ =
				    methodDef(Position.COMPILER_GENERATED, Types.ref((StructType) Runtime()), Public(), Types.ref(VOID_), "$dummyAsync$",
				              Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			return asyncCodeInstance_;
		}
	}

	public ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, List<LocalDef> formalNames, Ref<XConstraint> whereClause, List<Ref<? extends Type>> throwTypes) {
	    return new ClosureDef_c(this, p, typeContainer, methodContainer, returnType, typeParams, argTypes, formalNames, whereClause, throwTypes);
	}

	protected NullType createNull() {
		return new X10NullType_c(this);
	}
	
	/******************** Primitive types as Objects ******************/
	
	private static final String WRAPPER_PACKAGE = "x10.compilergenerated";
	
	@Override
	public PrimitiveType createPrimitive(String name) {
		return new X10PrimitiveType_c(this, name);
	}
	
	protected final PrimitiveType UBYTE_  = createPrimitive("ubyte");
	protected final PrimitiveType USHORT_ = createPrimitive("ushort");
	protected final PrimitiveType UINT_   = createPrimitive("uint");
	protected final PrimitiveType ULONG_  = createPrimitive("ulong");
	
	public PrimitiveType UByte()  { return UBYTE_; }
	public PrimitiveType UShort() { return USHORT_; }
	public PrimitiveType UInt()   { return UINT_; }
	public PrimitiveType ULong()  { return ULONG_; }
	
	protected ClassType nativeValRail_;
	public Type ValRail() {
		if (nativeValRail_ == null)
//		    nativeValRail_ = load("x10.lang.ValRail");
		    nativeValRail_ = load("x10.lang.Rail");
		return nativeValRail_;
	}
	
	protected ClassType nativeRail_;
	public Type Rail() {
	    if (nativeRail_ == null)
		nativeRail_ = load("x10.lang.Rail");
	    return nativeRail_;
	}
	
	public Type Object() {
		if (OBJECT_ == null)
		    OBJECT_ = load("x10.lang.Object");
		return OBJECT_;
	}

	public Type Class() {
	    if (CLASS_ != null)
		return CLASS_;
	    return CLASS_ = load("x10.lang.Class");
	}

	public Type String() {
	    if (STRING_ != null)
		return STRING_;
	    return STRING_ = load("x10.lang.String");
	}

	public Type Throwable() {
	    if (THROWABLE_ != null)
		return THROWABLE_;
	    return THROWABLE_ = load("x10.lang.Throwable");
	}

	public Type Error() {
	    return load("x10.lang.Error");
	}

	public Type Exception() {
	    return load("x10.lang.Exception");
	}

	public Type RuntimeException() {
	    return load("x10.lang.RuntimeException");
	}

	public Type Cloneable() {
	    return load("x10.lang.Cloneable");
	}

	public Type Serializable() {
	    return load("x10.io.Serializable");
	}

	public Type NullPointerException() {
	    return load("x10.lang.NullPointerException");
	}

	public Type ClassCastException() {
	    return load("x10.lang.ClassCastException");
	}

	public Type OutOfBoundsException() {
	    return load("x10.lang.ArrayIndexOutOfBoundsException");
	}

	public Type ArrayStoreException() {
	    return load("x10.lang.ArrayStoreException");
	}

	public Type ArithmeticException() {
	    return load("x10.lang.ArithmeticException");
	}

	protected ClassType comparableType_;
	public Type Comparable() {
	    if (comparableType_ == null)
		comparableType_ = load("x10.lang.Comparable"); // java file
	    return comparableType_;
	}
	
	protected ClassType iterableType_;
	public Type Iterable() {
	    if (iterableType_ == null)
		iterableType_ = load("x10.lang.Iterable"); // java file
	    return iterableType_;
	}
	
	protected ClassType iteratorType_;
	public Type Iterator() {
	    if (iteratorType_ == null)
		iteratorType_ = load("x10.lang.Iterator"); // java file
	    return iteratorType_;
	}
	
	protected ClassType containsType_;
	public Type Contains() {
	    if (containsType_ == null)
		containsType_ = load("x10.lang.Contains"); // java file
	    return containsType_;
	}
	
	protected ClassType settableType_;
	public Type Settable() {
	    if (settableType_ == null)
		settableType_ = load("x10.lang.Settable"); // java file
	    return settableType_;
	}
	
	protected ClassType containsAllType_;
	public Type ContainsAll() {
	    if (containsAllType_ == null)
		containsAllType_ = load("x10.lang.ContainsAll"); // java file
	    return containsAllType_;
	}
	
	protected ClassType placeType_;
	public Type Place() {
		if (placeType_ == null)
			placeType_ = load("x10.lang.Place"); // java file
		return placeType_;
	}

	protected ClassType regionType_;
	public Type Region() {
		if (regionType_ == null)
			regionType_ = load("x10.lang.Region"); // java file
		return regionType_;
	}
	
	protected Type pointType_;
	public Type Point() {
		if (pointType_ == null)
		    try {
			pointType_ = typeForName("x10.lang.Point");
		    }
		    catch (SemanticException e) {
			throw new InternalCompilerError("Could not load x10.lang.Point");
		    } // java file
		return pointType_;
	}
	
	protected ClassType distributionType_;
	public Type Dist() {
		if (distributionType_ == null)
			distributionType_ = load("x10.lang.Dist"); // java file
		return distributionType_;
	}
	
	protected ClassType x10ArrayType_;
	public ClassType array() {
		if (x10ArrayType_ == null)
			x10ArrayType_ = load("x10.lang.x10Array"); // java file
		return x10ArrayType_;
	}
	
	protected ClassType clockType_;
	public Type Clock() {
		if (clockType_ == null)
			clockType_ = load("x10.lang.Clock"); // java file
		return clockType_;
	}
	
	protected ClassType runtimeType_;
	public Type Runtime() {
		if (runtimeType_ == null)
			runtimeType_ = load("x10.lang.Runtime"); // java file
		return runtimeType_;
	}
	
	protected ClassType arrayOperationsType_;
	public ClassType ArrayOperations() {
		if (arrayOperationsType_ == null)
			arrayOperationsType_ = load("x10.lang.ArrayOperations"); // java file
		return arrayOperationsType_;
	}
	
	public Type x10Array(Ref<? extends Type> type, boolean isValueType) {
	    Type at;
	    
	    if (isValueType) {
		at = ValArray();
	    }
	    else {
		at = Array();
	    }
	    
	    at = fixGenericType(type, (X10ParsedClassType) at, isValueType);
	    
	    return at;
	}

	protected X10ParsedClassType genericReferenceArrayType_;
	public Type newAndImprovedArray(Ref<? extends Type> base) {
		if (genericReferenceArrayType_ == null)
			genericReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray");
		return fixGenericType(base, genericReferenceArrayType_, false);
	}
	
	protected X10ParsedClassType genericArrayType_;
	public Type newAndImprovedValueArray(Ref<? extends Type> base) {
		if (genericArrayType_ == null)
			genericArrayType_ = (X10ParsedClassType) load("x10.lang.ValueArray");
		return fixGenericType(base, genericArrayType_, false /* should be true XXX */);
	}

	static
	TypeProperty getFirstTypeProperty(X10ClassDef classDef) {
		if (classDef.typeProperties().size() == 1)
			return classDef.typeProperties().get(0);
		Type t = Types.get(classDef.superType());
		if (t != null && t.isClass())
			return getFirstTypeProperty((X10ClassDef) t.toClass().def());
		return null;
	}
	
	private Type fixGenericType(Ref<? extends Type> base, X10ParsedClassType ct, boolean covariant) {
		X10ClassDef cd = (X10ClassDef) ct.def();
		
		if (base != null) {
			TypeProperty p = getFirstTypeProperty(cd);
			assert p != null;
			
			XConstraint c = new XConstraint_c();
			try {
				X10TypeSystem_c xts = this;
				if (covariant) {
					c.addAtom(xts.xtypeTranslator().transSubtype(p.asType(), base.get()));
				}
				else {
					c.addBinding(p.asVar(), xts.xtypeTranslator().trans(base.get()));
				}
			}
			catch (XFailure e) {
				throw new InternalCompilerError(e);
			}
			
			return X10TypeMixin.xclause(ct, c);
		}
		
		return ct;
	}
	
	protected ClassType arrayType_ = null;
	public Type Array() {
		if (arrayType_ == null)
			arrayType_ = load("x10.lang.Array");
		return arrayType_;
	}
	
	protected ClassType valArrayType_ = null;
	public Type ValArray() {
	    if (valArrayType_ == null)
		valArrayType_ = load("x10.lang.ValArray");
	    return valArrayType_;
	}
	
	public MethodInstance primitiveEquals() {
		String name = WRAPPER_PACKAGE + ".BoxedNumber";
		
		try {
			Type ct = (Type) systemResolver().find(name);
			
			List<Type> args = new LinkedList<Type>();
			args.add(Object());
			args.add(Object());
			
			for (MethodInstance mi : ct.toClass().methods("equals", args) ) {
				return mi;
			}
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e.getMessage());
		}
		
		throw new InternalCompilerError("Could not find equals method.");
	}
	
	public MethodInstance getter(PrimitiveType t) {
		X10PrimitiveType xt = (X10PrimitiveType) t;
		String methodName = xt.typeName() + "Value";
		ConstructorInstance ci = wrapper(t);
		
		for (MethodInstance mi : ci.container().methods(methodName, Collections.EMPTY_LIST)) {
		    return mi;
		}
		
		throw new InternalCompilerError("Could not find getter for " + t);
	}
	
	public X10NamedType boxedType(PrimitiveType primitiveType) {
		X10NamedType namedType = (X10NamedType) wrapper(primitiveType).container();
		return namedType; 
	}
	
	public boolean isBoxedType(Type t) {
		String targetCanonicalName = t.toString();
		return (t instanceof X10ParsedClassType) && 
					targetCanonicalName.startsWith(WRAPPER_PACKAGE + ".Boxed");		
	}

	public String getGetterName(Type t) {
		if (isBoxedType(t)) {
			return this.boxedGetterAsString((X10ParsedClassType) t);
		}
		
		throw new InternalCompilerError(t + " is not a primitive type boxed");
	}
	
	
	public ConstructorInstance wrapper(PrimitiveType t) {
		String name = WRAPPER_PACKAGE + ".Boxed" + wrapperTypeString(t).substring("java.lang.".length());
		
		try {
			ClassType ct = ((Type) systemResolver().find(name)).toClass();
			
			for (ConstructorInstance ci : ct.constructors()) {
			    if (ci.formalTypes().size() == 1) {
			        Type argType = (Type) ci.formalTypes().get(0);
			        if (typeBaseEquals(argType, t)) {
		    			XConstraint c = X10TypeMixin.xclause(t);
		    			if (c != null) {
		    			    StructType container = ci.container();
		    			    return ci.container((StructType) X10TypeMixin.xclause(container, c));
		    			}
			            return ci;
			        }
			    }
			}
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e.getMessage());
		}
		
		throw new InternalCompilerError("Could not find constructor for " + t);
	}

	public Type boxedTypeToPrimitiveType(Type presumedBoxedType){
		if (isBoxedType(presumedBoxedType)) { 
			X10Type t = (X10Type) this.boxedTypeToPrimitiveType(
					(X10ParsedClassType) presumedBoxedType);
	    		return X10TypeMixin.xclause(t, X10TypeMixin.xclause(presumedBoxedType));
		}
		
		throw new InternalCompilerError("can't get primitive type from " + presumedBoxedType 
				+ " because it is not a boxed type");
	}
	
	private String boxedToPrimitiveName (String boxedClassName){
		String boxedType;
		String prefix = "Boxed";
		return boxedClassName.substring(prefix.length(), 
				boxedClassName.length());
	}

    private Type boxedTypeToPrimitiveType(X10ParsedClassType t) {
        assert_(t);
        String boxedTypeName = this.boxedToPrimitiveName(t.name());

    	if (boxedTypeName.equals("Boolean")) {
    	    return this.Boolean();
    	}
    	if (boxedTypeName.equals("Character")) {
    		return this.Char();
    	}
    	if (boxedTypeName.equals("Byte")) {
    		return this.Byte();
    	}
    	if (boxedTypeName.equals("Short")) {
    		return this.Short();
    	}
    	if (boxedTypeName.equals("Integer")) {
    		return this.Int();
    	}
    	if (boxedTypeName.equals("Long")) {
    		return this.Long();
    	}
    	if (boxedTypeName.equals("Float")) {
    		return this.Float();
    	}
    	if (boxedTypeName.equals("Double")) {
    	    return this.Double();
    	}

	throw new InternalCompilerError(t + " is not a primitive type boxed");
    }

    private String boxedGetterAsString(X10ParsedClassType t) {
        assert_(t);
        String boxedTypeName = this.boxedToPrimitiveName(t.name());

    	if (boxedTypeName.equals("Boolean")) {
    	    return "booleanValue";
    	}
    	if (boxedTypeName.equals("Character")) {
    	    return "charValue";
    	}
    	if (boxedTypeName.equals("Byte")) {
    	    return "byteValue";
    	}
    	if (boxedTypeName.equals("Short")) {
    	    return "shortValue";
    	}
    	if (boxedTypeName.equals("Integer")) {
    	    return "intValue";
    	}
    	if (boxedTypeName.equals("Long")) {
    	    return "longValue";
    	}
    	if (boxedTypeName.equals("Float")) {
    	    return "floatValue";
    	}
    	if (boxedTypeName.equals("Double")) {
    	    return "doubleValue";
    	}

	throw new InternalCompilerError(t + " is not a primitive type boxed");
    }

	
	// RMF 11/1/2005 - Not having the "static" qualifier on interfaces causes problems,
	// e.g. for New_c.disambiguate(AmbiguityRemover), which assumes that instantiating
	// non-static types requires a "this" qualifier expression.
	// [IP] FIXME: why does the above matter when we supply the bits?
	public Flags flagsForBits(int bits) {
		Flags sf = super.flagsForBits(bits);
		if (sf.isInterface()) return sf.Static();
		return sf;
	}
	
	@Override
	public FieldDef fieldDef(Position pos,
			Ref<? extends StructType> container, Flags flags,
			Ref<? extends Type> type, String name) {
		assert_(container);
		assert_(type);
		return new X10FieldDef_c(this, pos, container, flags, type, name);
	}
	
	public X10FieldDef propertyInstance(Position pos,
	        Ref<? extends StructType> container, Flags flags,
	        Ref<? extends Type> type, String name) {
		assert_(container);
		assert_(type);
		X10FieldDef fd = new X10FieldDef_c(this, pos, container, flags, type, name);
		fd.setProperty();
		return fd;
	}
	
//	TODO: Extend this for other kinds of X10 arrays
	public  boolean isPrimitiveTypeArray(Type me1) {
		X10Type me = (X10Type) me1;
		return 
		isBooleanArray(me) || 
		isCharArray(me) || 
		isByteArray(me) || 
		isShortArray(me) || 
		isIntArray(me) || 
		isLongArray(me) || 
		isFloatArray(me) || 
		isDoubleArray(me);
	}
	
	public boolean isUByte(Type t) {
	    return isSubtype(t, UByte());
	}
	
	public boolean isUShort(Type t) {
	    return isSubtype(t, UShort());
	}
	
	public boolean isUInt(Type t) {
	    return isSubtype(t, UInt());
	}
	
	public boolean isULong(Type t) {
	    return isSubtype(t, ULong());
	}
	
	public boolean isNumeric(Type t) {
	        return super.isNumeric(t) || isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
	}

	public boolean isIntOrLess(Type t) {
	    return super.isIntOrLess(t) || isUByte(t) || isUShort(t);
	}

	public boolean isLongOrLess(Type t) {
	    return super.isLongOrLess(t) || isUByte(t) || isUShort(t) || isUInt(t);
	}

	@Override
	public boolean isSubtype(Type t1, Type t2) {
		if (t1 == t2)
			return true;
		
		// pretend Box is a typedef:
		// package x10.lang;
		// type Box = x10.lang.BoxInternal;
		// type Box[T]{T <: Ref} = T;
		// type Box[T]{T <: Value} = x10.lang.BoxInternal[T];

		if (t1.isNull() && isRef(t2)) {
			return true;
		}
		
		// Check for Box BEFORE stripping out the constraints.
		if (t1 instanceof NullableType && t2 instanceof NullableType) {
			NullableType nt1 = (NullableType) t1;
			NullableType nt2 = (NullableType) t2;
			return isSubtype(nt1.base(), nt2.base());
		}
		
		if (t1 instanceof ClosureType && t2 instanceof ClosureType) {
			// Permit covariance in the return type, so that a closure that returns a more
			// specific type can be assigned to a closure variable with a less specific
			// return type. Don't permit covariance in the throw types or argument types.
			
			ClosureType ct1 = (ClosureType) t1;
			ClosureType ct2 = (ClosureType) t2;
			
			boolean result = true;
			result &= typeListEquals(ct1.argumentTypes(), ct2.argumentTypes());
			result &= typeListEquals(ct1.throwTypes(), ct2.throwTypes());           // XXX: ORDER shouldn't matter
			result &= isSubtype(ct1.returnType(), ct2.returnType());
			if (result)
				return true;
		}

		if (t1 instanceof ConstrainedType && t2 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			ConstrainedType ct2 = (ConstrainedType) t2;
			XConstraint c1 = ct1.constraint().get();
			XConstraint c2 = ct2.constraint().get();
			Type baseType1 = ct1.baseType().get();
			Type baseType2 = ct2.baseType().get();

			boolean result = true;
			result &= isSubtype(baseType1, baseType2);
			try {
				result &= c1.entails(c2);
			}
			catch (XFailure e) {
				result = false;
			}
			
			if (result)
				return true;
		}
		
		if (t1 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			Type baseType1 = ct1.baseType().get();
			
			boolean result = true;
			result &= isSubtype(baseType1, t2);
			if (result)
				return true;
		}
		
		if (t2 instanceof ConstrainedType) {
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType2 = ct2.baseType().get();
			XConstraint c2 = ct2.constraint().get();
			
			boolean result = true;
			result &= isSubtype(t1, baseType2);
			result &= c2.valid();
			if (result)
				return true;
		}

		if (t1 instanceof ParametrizedType && t2 instanceof ParametrizedType) {
			ParametrizedType pt1 = (ParametrizedType) t1;
			ParametrizedType pt2 = (ParametrizedType) t2;
			
			boolean result = true;
			result &= pt1.def() == pt2.def();
			result &= typeListEquals(pt1.typeParameters(), pt2.typeParameters());
			result &= typeListEquals(pt1.formalTypes(), pt2.formalTypes());
			result &= listEquals(pt1.formals(), pt2.formals()); 
			if (result)
				return true;
		}
		
		if (t1 instanceof MacroType) {
			MacroType at1 = (MacroType) t1;
			if (isSubtype(at1.definedType(), t2))
				return true;
		}
		
		if (t2 instanceof MacroType) {
			MacroType at2 = (MacroType) t2;
			if (isSubtype(t1, at2.definedType()))
				return true;
		}
		
		// p: C{self.T<:S}  implies  p.T <: S
		if (t1 instanceof PathType) {
			PathType pt1 = (PathType) t1;
			TypeProperty px = pt1.property();
			XVar base = PathType_c.pathBase(pt1);
			if (base != null) {
			    final XConstraint c = base.selfConstraint();
//			    if (c != null) {
//				c = c.copy();
//			    }
//			    else {
//				c = new XConstraint_c();
//			    }
//			    try {
//				c.addIn(X10TypeMixin.realX(pt1.baseType()));
//			    }
//			    catch (XFailure e1) {
//			    }
			    if (c != null) {
					try {
						// Check if the constraint on the base p implies that p.T <: S
						// Avoid infinite recursion by removing selfConstraint on base.
						base.setSelfConstraint(null);
						XConstraint c1 = c.substitute(base, XSelf.Self);
						XConstraint c2 = new XConstraint_c();
						c2.addAtom(xtypeTranslator().transSubtype(t1, t2));
						boolean result = c1.entails(c2);
						if (result)
							return true;
					}
					catch (XFailure e) {
					}
					finally {
						base.setSelfConstraint(new XRef_c<XConstraint>() { public XConstraint compute() { return c; } });
					}
				}
			}
		}

		if (t1 instanceof PrimitiveType && t2 instanceof NullableType) {
			NullableType nt = (NullableType) t2;
			return isSubtype(t1, nt.base());
		}

		if (t1 instanceof PrimitiveType && t2.typeEquals(Object())) {
			return true;
		}
		
		if (t1 instanceof PrimitiveType && t2.typeEquals(Value())) {
		    return true;
		}
		
		if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
		    X10ClassType ct1 = (X10ClassType) t1;
		    X10ClassType ct2 = (X10ClassType) t2;
		    if (ct1.def() == ct2.def()) {
			X10ClassDef def = ct1.x10Def();
			if (ct1.typeArguments().size() != def.typeParameters().size())
			    return false;
			if (ct2.typeArguments().size() != def.typeParameters().size())
			    return false;
			if (def.variances().size() != def.typeParameters().size())
			    return false;
			for (int i = 0; i < def.typeParameters().size(); i++) {
			    Type a1 = ct1.typeArguments().get(i);
			    Type a2 = ct2.typeArguments().get(i);
			    TypeProperty.Variance v = def.variances().get(i);
			    switch (v) {
			    case COVARIANT:
				if (! isSubtype(a1, a2)) return false;
				break;
			    case CONTRAVARIANT:
				if (! isSubtype(a2, a1)) return false;
				break;
			    case INVARIANT:
				if (! typeEquals(a1, a2)) return false;
				break;
			    }
			}
			return true;
		    }
		}
		
//		if (t1 instanceof PrimitiveType && ! t1.isVoid() && t2.typeEquals(boxedType((PrimitiveType) t1))) {
//			return true;
//		}

		return super.isSubtype(t1, t2);
	}
	
	@Override
	public boolean typeEquals(Type t1, Type t2) {
		if (t1 == t2)
			return true;
		
		// Check for nullable BEFORE stripping out the constraints.
		if (t1 instanceof NullableType && t2 instanceof NullableType) {
			NullableType nt1 = (NullableType) t1;
			NullableType nt2 = (NullableType) t2;
			boolean result = nt1.base().typeEquals(nt2.base());
			if (result)
				return true;
		}
		
		if (t1 instanceof ConstrainedType && t2 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType1 = ct1.baseType().get();
			Type baseType2 = ct2.baseType().get();
			XConstraint c1 = ct1.constraint().get();
			XConstraint c2 = ct2.constraint().get();

			boolean result = true;
			result &= typeEquals(baseType1, baseType2);
			try {
				result &= c1.equiv(c2);
			}
			catch (XFailure e) {
				result = false;
			}
			if (result)
				return true;
		}
		
		if (t1 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			Type baseType1 = ct1.baseType().get();
			XConstraint c1 = ct1.constraint().get();
			
			boolean result = true;
			result &= typeEquals(baseType1, t2);
			result &= c1.valid();
			if (result)
				return true;
		}
		
		if (t2 instanceof ConstrainedType) {
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType2 = ct2.baseType().get();
			XConstraint c2 = ct2.constraint().get();
			
			boolean result = true;
			result &= typeEquals(t1, baseType2);
			result &= c2.valid();
			if (result)
				return true;
		}

		if (t1 instanceof ClosureType && t2 instanceof ClosureType) {
			ClosureType ct1 = (ClosureType) t1;
			ClosureType ct2 = (ClosureType) t2;
			
			boolean result = true;
			result &= typeListEquals(ct1.argumentTypes(), ct2.argumentTypes());
			result &= typeListEquals(ct1.throwTypes(), ct2.throwTypes());           // XXX: ORDER shouldn't matter
			result &= typeEquals(ct1.returnType(), ct2.returnType());
			if (result)
				return true;
		}
		
		if (t1 instanceof ParametrizedType && t2 instanceof ParametrizedType) {
			if (t1.equals((TypeObject) t2))
				return true;
		}
		
		// TODO: instantiate on constraints
		if (t1 instanceof MacroType) {
			MacroType at1 = (MacroType) t1;
			if (typeEquals(at1.definedType(), t2))
				return true;
		}
		
		if (t2 instanceof MacroType) {
			MacroType at2 = (MacroType) t2;
			if (typeEquals(t1, at2.definedType()))
				return true;
		}
		
		// p: C{self.T==S}  implies  p.T == S
		if (t1 instanceof PathType) {
			PathType pt1 = (PathType) t1;
			TypeProperty px = pt1.property();
			XVar base = PathType_c.pathBase(pt1);
			if (base != null) {
				XConstraint c = base.selfConstraint();
				if (c != null) {
					try {
						// Check if the constraint on the base p implies that T1==T2
						c = c.substitute(base, XSelf.Self);
						XConstraint c2 = new XConstraint_c();
						c2.addBinding(xtypeTranslator().trans(t1), xtypeTranslator().trans(t2));
						boolean result = c.entails(c2);
						if (result)
							return true;
					}
					catch (XFailure e) {
					}
				}
			}
		}

		// p: C{self.T==S}  implies  p.T == S
		if (t2 instanceof PathType) {
			PathType pt2 = (PathType) t2;
			TypeProperty px = pt2.property();
			XVar base = PathType_c.pathBase(pt2);
			if (base != null) {
				XConstraint c = base.selfConstraint();
				if (c != null) {
					try {
						// Check if the constraint on the base p implies that T1==T2
						c = c.substitute(base, XSelf.Self);
						XConstraint c2 = new XConstraint_c();
						c2.addBinding(xtypeTranslator().trans(t1), xtypeTranslator().trans(t2));
						boolean result = c.entails(c2);
						if (result)
							return true;
					}
					catch (XFailure e) {
					}
				}
			}
		}
		
		if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
		    X10ClassType ct1 = (X10ClassType) t1;
		    X10ClassType ct2 = (X10ClassType) t2;
		    X10ClassDef def1 = ct1.x10Def();
		    X10ClassDef def2 = ct2.x10Def();
		    if (def1 != def2)
			return false;
		    if (! CollectionUtil.allElementwise(ct1.typeArguments(), ct2.typeArguments(), new TypeEquals())) {
			return false;
		    }
		    return true;
		}

		return super.typeEquals(t1, t2);
	}
	
	@Override
	public boolean isCastValid(Type fromType, Type toType) {
		if (fromType == toType)
			return true;
		
		if (fromType instanceof NullType) {
			return toType.isNull() || isRef(toType);
		}
		
		if (toType instanceof NullableType) {
			NullableType fromNT = (NullableType) toType;
			return isCastValid(fromType, fromNT.base());
		}
		
		if (toType instanceof MacroType) {
		    MacroType toMT = (MacroType) toType;
		    return isCastValid(fromType, toMT.definedType());
		}
		
		if (fromType instanceof MacroType) {
		    MacroType fromMT = (MacroType) fromType;
		    return isCastValid(fromMT.definedType(), toType);
		}
		
		
//		// If
//		//   x: C(:T==S)
//		// need to compare x.T against S also
//		if (fromType instanceof PathType) {
//			PathType pt = (PathType) fromType;
//			XVar base = pt.base();
//			Type baseType = pt.baseType();
//			TypeProperty prop = pt.property();
//			XConstraint c = X10TypeMixin.xclause(baseType);
//			if (c != null) {
//				Type S = X10TypeMixin.lookupTypeProperty(c, prop);
//				if (S != null && isCastValid(S, toType)) {
//					return true;
//				}
//			}
//		}
//		
//		if (toType instanceof PathType) {
//			PathType pt = (PathType) toType;
//			XVar base = pt.base();
//			Type baseType = pt.baseType();
//			TypeProperty prop = pt.property();
//			XConstraint c = X10TypeMixin.xclause(baseType);
//			if (c != null) {
//				Type S = X10TypeMixin.lookupTypeProperty(c, prop);
//				if (S != null && isCastValid(fromType, S)) {
//					return true;
//				}
//			}
//		}

		if (fromType instanceof ConstrainedType || toType instanceof ConstrainedType) {
			x10.constraint.XConstraint c1 = null;
			x10.constraint.XConstraint c2 = null;
			Type t1 = fromType;
			Type t2 = toType;
			
			if (fromType instanceof ConstrainedType) {
				ConstrainedType fromCT = (ConstrainedType) fromType;
				t1 = fromCT.baseType().get();
				c1 = fromCT.constraint().get();
			}
			else {
				c1 = new x10.constraint.XConstraint_c();
			}
			
			if (toType instanceof ConstrainedType) {
				ConstrainedType toCT = (ConstrainedType) toType;
				t2 = toCT.baseType().get();
				c2 = toCT.constraint().get();
			}
			else {
				c2 = new x10.constraint.XConstraint_c();
			}
			
			if (! isCastValid(t1, t2))
				return false;

			if (! clausesConsistent(c1, c2))
				return false;
			
			return true;
		}
		
		if (fromType instanceof NullableType) {
			NullableType fromNT = (NullableType) fromType;
			return isCastValid(fromNT.base(), toType);
		}
		
		if (fromType instanceof ClassType) {
			ClassType fromCT = (ClassType) fromType;

			if (isBoxedType(toType) || toType instanceof PrimitiveType) {
				if (typeEquals(fromCT, this.Object()))
					return true;
			}

			if (toType instanceof NullableType) {
				NullableType nt = (NullableType) toType;
				return isCastValid(fromCT, nt.base());
			}
		}
		
		if (fromType instanceof PrimitiveType) {
			PrimitiveType fromPT = (PrimitiveType) fromType;
			
			if (typeEquals(toType, Object())) {
				return true;
			}

			if (isBoxedType(toType)) {
				return isCastValid(fromPT, this.boxedTypeToPrimitiveType(toType));
			}
		}

		return super.isCastValid(fromType, toType);
	}

	@Override
	public boolean descendsFrom(Type child, Type ancestor) {
		// Strip off the constraints.
		if (child instanceof ConstrainedType) {
			ConstrainedType cc = (ConstrainedType) child;
			return descendsFrom(Types.get(cc.baseType()), ancestor);
		}
		
		if (ancestor instanceof ConstrainedType) {
			ConstrainedType ac = (ConstrainedType) ancestor;
			return descendsFrom(child, Types.get(ac.baseType()));
		}
		
		if (child instanceof ClosureType) {
			return typeEquals(ancestor, Object());
		}
		
		if (child instanceof PrimitiveType) {
			return typeEquals(ancestor, Object()) || typeEquals(ancestor, Value());
		}

		return super.descendsFrom(child, ancestor);
	}
	
	@Override
	public boolean isImplicitCastValid(Type fromType, Type toType) {
		if (fromType == toType)
			return true;
		
		if (fromType instanceof NullableType) {
			NullableType fromNT = (NullableType) fromType;
			
			if (toType instanceof NullableType) {
				NullableType toNT = (NullableType) toType;
				return fromNT.base().isImplicitCastValid(toNT.base());
			}
			
			return super.isImplicitCastValid(fromType, toType);
		}

		if (fromType instanceof NullType) {
			return isSubtype(fromType, toType);
		}
		
		// Check if toType is nullable before stripping off the constraints.
		// Otherwise; we'll compare C <= nullable<C(:c)> rather than
		// C(:c) <= nullable<C(:c)>.
		if (toType instanceof NullableType) {
			NullableType toNT = (NullableType) toType;
			return isImplicitCastValid(fromType, toNT.base());
		}
		
		if (toType instanceof MacroType) {
		    MacroType toMT = (MacroType) toType;
		    return isImplicitCastValid(fromType, toMT.definedType());
		}
		
		if (fromType instanceof MacroType) {
		    MacroType fromMT = (MacroType) fromType;
		    return isImplicitCastValid(fromMT.definedType(), toType);
		}
		
		// Can convert if there is a static method toType.make(fromType)
		try {
		    MethodInstance mi = findMethod(toType, new X10TypeSystem_c.X10MethodMatcher(toType, "$convert", Collections.singletonList(fromType)), (ClassDef) null);
		    if (mi.flags().isStatic())
			return true;
		}
		catch (SemanticException e) {
		}
		
		try {
		    MethodInstance mi = findMethod(toType, new X10TypeSystem_c.X10MethodMatcher(toType, "make", Collections.singletonList(fromType)), (ClassDef) null);
		    if (mi.flags().isStatic())
			return true;
		}
		catch (SemanticException e) {
		}
		
		Type t1 = fromType;
		Type t2 = toType;
		
		if (t2 instanceof ConstrainedType) {
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType2 = ct2.baseType().get();
			XConstraint c2 = ct2.constraint().get();
			c2 = c2.copy();
			c2 = c2.removeVarBindings(XSelf.Self);
			t2 = X10TypeMixin.xclause(baseType2, c2);
		}
		
		if (t1 instanceof ConstrainedType && t2 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType1 = ct1.baseType().get();
			Type baseType2 = ct2.baseType().get();
			XConstraint c1 = ct1.constraint().get();
			XConstraint c2 = ct2.constraint().get();

			boolean result = true;
			result &= isImplicitCastValid(baseType1, baseType2);
			try {
				result &= c1.entails(c2);
			}
			catch (XFailure e) {
				result = false;
			}
			return result;
		}
		
		if (t1 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			Type baseType1 = ct1.baseType().get();
			
			boolean result = true;
			result &= isImplicitCastValid(baseType1, t2);
			return result;
		}
		
		if (t2 instanceof ConstrainedType) {
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType2 = ct2.baseType().get();
			XConstraint c2 = ct2.constraint().get();
			
			boolean result = true;
			result &= isImplicitCastValid(t1, baseType2);
			result &= c2.valid();
			return result;
		}

		if (fromType instanceof PrimitiveType) {
			if (toType instanceof ArrayType)
				return false;

			if (isSubtype(fromType, toType))
				return true;
		}
		
		return super.isImplicitCastValid(fromType, toType);
	}
	
	@Override
	public boolean numericConversionValid(Type t, java.lang.Object value) {
		X10TypeSystem xts = this;

		if (! super.numericConversionValid(t, value)) {
			return false;
		}

		if (t instanceof PrimitiveType) {
			X10Type xt = (X10Type) t;
			XLit val = XTerms.makeLit(value);

			try {
				XConstraint c = new XConstraint_c();
				c.addSelfBinding(val);
				return xts.entailsClause(c, X10TypeMixin.realX(xt));
			}
			catch (XFailure f) {
				// Adding binding makes real clause inconsistent.
				return false;
			}
		}
		
		return false;
	}

	protected boolean typeRefListEquals(List<Ref<? extends Type>> l1, List<Ref<? extends Type>> l2) {
		return CollectionUtil.<Type>allElementwise(new TransformingList<Ref<? extends Type>, Type>(l1, new DerefTransform<Type>()),
		                                           new TransformingList<Ref<? extends Type>, Type>(l2, new DerefTransform<Type>()),
		                                           new TypeSystem_c.TypeEquals());
	}
	
	protected boolean typeListEquals(List<Type> l1, List<Type> l2) {
		return CollectionUtil.<Type>allElementwise(l1, l2, new TypeSystem_c.TypeEquals());
	}
	
	protected boolean listEquals(List<XVar> l1, List<XVar> l2) {
		return CollectionUtil.<XVar>allEqual(l1, l2);
	}

	protected boolean isX10BaseSubtype(Type me, Type sup) {
		Type xme = X10TypeMixin.xclause(me, (XConstraint) null);
		Type xsup = X10TypeMixin.xclause(sup, (XConstraint) null);
		return isSubtype(xme, xsup);
	}
	
	
	public boolean isRail(Type t) {
	    return hasSameClassDef(t, Rail());
	}
	
	public boolean isValRail(Type t) {
	    return hasSameClassDef(t, ValRail());
	}

	private boolean hasSameClassDef(Type t1, Type t2) {
	    Type b1 = X10TypeMixin.baseType(t1);
	    Type b2 = X10TypeMixin.baseType(t2);
	    if (b1 instanceof ClassType && b2 instanceof ClassType) {
		X10ClassType ct1 = (X10ClassType) b1;
		X10ClassType ct2 = (X10ClassType) b2;
		return ct1.def().equals(ct2.def());
	    }
	    return false;
	}
	
	public Type Rail(Type arg) {
	    return X10TypeMixin.instantiate(Rail(), arg);
	}
	
	public	Type ValRail(Type arg) {
	    return X10TypeMixin.instantiate(Rail(), arg);
	}

	public	Type Settable(Type domain, Type range) {
	    return X10TypeMixin.instantiate(Settable(), domain, range);
	}
	
	public  boolean isSettable(Type me) { 
	    return hasSameClassDef(me, Settable()); 
	}
	public  boolean isX10Array(Type me) { 
		return hasSameClassDef(me, Array()); 
	}
	
	public boolean isTypeConstrained(Type me) {
		return me instanceof ConstrainedType;
	}
	
	protected Type x10Array(Type base) {
	    return x10Array(Types.ref(base), false);
	}
	
	public  boolean isBooleanArray(Type me) {
		return isSubtype(me, x10Array(Boolean())); 
	}
	public boolean isCharArray(Type me) {
		return isSubtype(me, x10Array(Char())); 
	}
	public boolean isByteArray(Type me) {
		return isSubtype(me, x10Array(Byte())); 
	}
	public  boolean isShortArray(Type me) {
		return isSubtype(me, x10Array(Short())); 
	}
	public  boolean isIntArray(Type me) {
		return isSubtype(me, x10Array(Int())); 
	}
	public  boolean isLongArray(Type me) {
		return isSubtype(me, x10Array(Long())); 
	}
	public boolean isFloatArray(Type me) {
		return isSubtype(me, x10Array(Float())); 
	}
	public  boolean isDoubleArray(Type me) {
		return isSubtype(me, x10Array(Double())); 
	}
	public  boolean isClock(Type me) {
		return isSubtype(me, Clock()); 
	}
	public  boolean isPoint(Type me) {
		return isSubtype(me, Point()); 
	}
	public  boolean isPlace(Type me) {
		return isSubtype(me, Place());
	}
	public  boolean isRegion(Type me) {
		return isSubtype(me, Region());
	}
	public  boolean isDistribution(Type me) {
		return isSubtype(me, Dist());
	}
	public  boolean isDistributedArray(Type me) {
		return isX10Array(me);
	}
	public  boolean isValueType(Type me) {
		return isX10BaseSubtype((X10Type) me, Value());
	}
	public  boolean isComparable(Type me) {
	    return isSubtype(me, Comparable());
	}
	public  boolean isIterable(Type me) {
	    return isSubtype(me, Iterable());
	}
	public  boolean isIterator(Type me) {
	    return isSubtype(me, Iterator());
	}
	public  boolean isContains(Type me) {
	    return isSubtype(me, Contains());
	}
	public  boolean isContainsAll(Type me) {
	    return isSubtype(me, ContainsAll());
	}
	public Type arrayBaseType(Type theType) {
		if (theType instanceof X10ClassType) {
			X10ClassType xct = (X10ClassType) theType;
			X10ClassDef def = (X10ClassDef) xct.def();
			ClassType a = (ClassType) Array();
			ClassType v = (ClassType) ValArray();
			if (def == a.def() || def == v.def()) {
				return X10TypeMixin.getPropertyType(xct, "T");
			}
		}
		return null;
	}
	
	public VarDef createSelf(X10Type t) {
	    VarDef v = localDef(Position.COMPILER_GENERATED, Flags.PUBLIC, Types.ref(t), "self");
		return v;
	}
	protected XTypeTranslator xtt = new XTypeTranslator(this);
	public XTypeTranslator xtypeTranslator() {
		return xtt;
	}
	
	@Override
	public void initialize(TopLevelResolver loadedResolver, ExtensionInfo extInfo) throws SemanticException {
	    super.initialize(loadedResolver, extInfo);
	    XTerms.addExternalSolvers(new SubtypeSolver(this));
	}
	
	public boolean equivClause(Type me, Type other) {
	    return entailsClause(me, other) && entailsClause(other, me);
	}
	
	public boolean equivClause(XConstraint c1, XConstraint c2) {
		boolean result;
		try {
			result = (c1==null) ? ((c2==null) ? true : c2.valid())
					: c1.equiv(c2);
		}
		catch (XFailure e) {
			return false;
		}
		return result;
	}
	
	public boolean entailsClause(XConstraint c1, XConstraint c2) {
		boolean result;
		try {
			result = c1==null ? (c2==null || c2.valid()) : c1.entails(c2);
		}
		catch (XFailure e) {
			return false;
		}
		return result;
		
	}
	public boolean entailsClause(Type me, Type other) {
		try {
			XConstraint c1 = X10TypeMixin.realX(me);
			XConstraint c2 = X10TypeMixin.xclause(other);
			return entailsClause(c1,c2);
		}
		catch (InternalCompilerError e) {
			if (e.getCause() instanceof XFailure) {
				return false;
			}
			throw e;
		}
	}

	protected XLit hereConstraintLit; // Maybe this should be declared as C_Lit instead of a concrete impl class?
	public XLit here() {
	    if (hereConstraintLit == null)
		hereConstraintLit= xtypeTranslator().transHere();
	    return hereConstraintLit;
	}

	protected XLit FALSE;
	public XLit FALSE() {
	    if (FALSE == null)
		FALSE= xtypeTranslator().trans(false);
	    return FALSE;
	}
	protected XLit TRUE;
	public XLit TRUE() {
	    if (TRUE == null)
		TRUE= xtypeTranslator().trans(true);
	    return TRUE;
	}
	protected XLit NEG_ONE;
	public XLit NEG_ONE() {
	    if (NEG_ONE == null)
		NEG_ONE=  xtypeTranslator().trans(-1);
	    return NEG_ONE;
	}
	protected XLit ZERO;
	public XLit ZERO() {
	    if (ZERO == null)
		ZERO=  xtypeTranslator().trans(0);
	    return ZERO;
	}
	protected XLit ONE;
	public XLit ONE() {
	    if (ONE == null)
		ONE=  xtypeTranslator().trans(1);
	    return ONE;
	}
	protected XLit TWO;
	public XLit TWO() {
	    if (TWO == null)
		TWO=  xtypeTranslator().trans(2);
	    return TWO;
	}
	protected XLit THREE;
	public XLit THREE() {
	    if (THREE == null)
		THREE=  xtypeTranslator().trans(3);
	    return THREE;
	}
	protected XLit NULL;
	public XLit NULL() {
	    if (NULL == null)
		NULL=  xtypeTranslator().transNull();
	    return NULL;
	}

	@Override
	public Flags createNewFlag(String name, Flags after) {
		Flags f = X10Flags.createFlag(name, after);
		flagsForName.put(name, f);
		return f;
	}
	@Override
	protected void initFlags() {
		super.initFlags();
		flagsForName.put("local", X10Flags.LOCAL);
		flagsForName.put("nonblocking", X10Flags.NON_BLOCKING);
		flagsForName.put("safe", X10Flags.SAFE);
		flagsForName.put("sequential", X10Flags.SEQUENTIAL);
		flagsForName.put("incomplete", X10Flags.INCOMPLETE);
		flagsForName.put("property", X10Flags.PROPERTY);
		flagsForName.put("pure", X10Flags.PURE);
		flagsForName.put("atomic", X10Flags.ATOMIC);
		
	}
	
	protected final Flags X10_METHOD_FLAGS = legalMethodFlags();
	
	@Override
	public void checkMethodFlags(Flags f) throws SemanticException {
		//Report.report(1, "X10TypeSystem_c:method_flags are |" + X10_METHOD_FLAGS + "|");
		if (! f.clear(X10_METHOD_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
					"Cannot declare method with flags " +
					f.clear(X10_METHOD_FLAGS) + ".");
		}
		
		if (f.isAbstract() && ! f.clear(ABSTRACT_METHOD_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
					"Cannot declare abstract method with flags " +
					f.clear(ABSTRACT_METHOD_FLAGS) + ".");
		}
		
		checkAccessFlags(f);
	}
	@Override
	public void checkTopLevelClassFlags(Flags f) throws SemanticException {
		if (! f.clear(X10_TOP_LEVEL_CLASS_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
					"Cannot declare a top-level class with flag(s) " +
					f.clear(X10_TOP_LEVEL_CLASS_FLAGS) + ".");
		}
		
		
		if (f.isInterface() && ! f.clear(X10_INTERFACE_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException("Cannot declare interface with flags " +
					f.clear(X10_INTERFACE_FLAGS) + ".");
		}
		
		checkAccessFlags(f);
	}
	
	@Override
	public void checkMemberClassFlags(Flags f) throws SemanticException {
		if (! f.clear(X10_MEMBER_CLASS_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
					"Cannot declare a member class with flag(s) " +
					f.clear(X10_MEMBER_CLASS_FLAGS) + ".");
		}
		
		checkAccessFlags(f);
	}
	
	@Override
	public void checkLocalClassFlags(Flags f) throws SemanticException {
		if (f.isInterface()) {
			throw new SemanticException("Cannot declare a local interface.");
		}
		
		if (! f.clear(X10_LOCAL_CLASS_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
					"Cannot declare a local class with flag(s) " +
					f.clear(X10_LOCAL_CLASS_FLAGS) + ".");
		}
		
		checkAccessFlags(f);
	}
    
	public boolean isSigned(Type t) {
	return isByte(t) || isShort(t) || isInt(t) || isLong(t);
}
	public boolean isUnsigned(Type t) {
	    return isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
	}

	 public Type promote2(Type t1, Type t2) throws SemanticException {
	     if (isDouble(t1) || isDouble(t2))
		 return Double();

	     if (isFloat(t1) || isFloat(t2))
		 return Float();

	     if (isLong(t1) || isLong(t2))
		 return Long();
	     
	     if (isULong(t1) || isULong(t2))
		 return Long();

	     if (isInt(t1) || isInt(t2))
		 return Int();
	     
	     if (isUInt(t1) || isUInt(t2))
		 return Int();
	     
	     if (isShort(t1) || isShort(t2))
		 return Int();
	     
	     if (isChar(t1) || isChar(t2))
		 return Int();
	     
	     if (isByte(t1) || isByte(t2))
		 return Int();
	     
	     if (isUShort(t1) || isUShort(t2))
		 return Int();
	     
	     if (isUByte(t1) || isUByte(t2))
		 return Int();
	     
	     throw new SemanticException("Cannot promote non-numeric type " + t1);
	 }

	 public Type promote2(Type t) throws SemanticException {
	     if (isUByte(t) || isUShort(t))
		 return UInt();

	     if (isUInt(t))
		 return UInt();

	     if (isULong(t))
		 return ULong();

	     if (isByte(t) || isChar(t) || isShort(t) || isInt(t))
		 return Int();

	     if (isLong(t))
		 return Long();

	     if (isFloat(t))
		 return Float();

	     if (isDouble(t))
		 return Double();

	     throw new SemanticException("Cannot promote non-numeric type " + t);
	 }

	@Override
	public Type promote(Type t) throws SemanticException {
	    Type pt = promote2(t);
	    return X10TypeMixin.xclause(pt, (XConstraint) null);
	}
	@Override
	public Type promote(Type t1, Type t2) throws SemanticException {
		Type pt = promote2(t1, t2);
		return X10TypeMixin.xclause(pt, (XConstraint) null);
	}
	@Override
	public Type leastCommonAncestor(Type type1, Type type2)
	throws SemanticException
	{
		assert_(type1);
		assert_(type2);
		try { 
		if (typeBaseEquals(type1, type2)) {
			Type base1 = X10TypeMixin.xclause(type1, (XConstraint) null);
			return base1;
		}
		
		if (type1.isNumeric() && type2.isNumeric()) {
			if (isImplicitCastValid(type1, type2)) {
				return type2;
			}
			
			if (isImplicitCastValid(type2, type1)) {
				return type1;
			}
			
			if (type1.isChar() && type2.isByte() ||
					type1.isByte() && type2.isChar()) {
				return Int();
			}
			
			if (type1.isChar() && type2.isShort() ||
					type1.isShort() && type2.isChar()) {
				return Int();
			}
		}
		
		// TODO: handle covariant instantiated types.
		// XYZ
		if (type1.isArray() && type2.isArray()) {
			return arrayOf(leastCommonAncestor(
					type1.toArray().base(), type2.toArray().base()));
		}
		
		if (type1.isReference() && type2.isNull()) return type1;
		if (type2.isReference() && type1.isNull()) return type2;
		
			// Don't consider interfaces.
			if (type1.isClass() && type1.toClass().flags().isInterface()) {
				return Object();
			}
			
			if (type2.isClass() && type2.toClass().flags().isInterface()) {
				return Object();
			}
			
			// Check against Object to ensure superType() is not null.
			if (typeEquals(type1, Object())) return type1;
			if (typeEquals(type2, Object())) return type2;
			
			if (isSubtype(type1, type2)) return type2;
			if (isSubtype(type2, type1)) return type1;
			
			if (type1 instanceof ObjectType && type2 instanceof ObjectType) {
			// Walk up the hierarchy
			Type t1 = leastCommonAncestor(((ObjectType) type1).superClass(), type2);
			Type t2 = leastCommonAncestor(((ObjectType) type2).superClass(), type1);
			
			if (typeEquals(t1, t2)) return t1;
			
			return Object();
		}
		} finally {
			//Report.report(1, "X10TypeSystem_c: The LCA of "  + type1 + " " + type2 + " is " + result + ".");
		}
		throw new SemanticException(
				"No least common ancestor found for types \"" + type1 +
				"\" and \"" + type2 + "\".");
	}
	 public boolean typeBaseEquals(Type type1, Type type2) {
	        assert_(type1);
	        assert_(type2);
		if (type1 == type2) return true;
		if (type1 == null || type2 == null) return false;
	        return typeEquals(X10TypeMixin.xclause(type1, (XConstraint) null), X10TypeMixin.xclause(type2, (XConstraint) null));
	    }
	 @Override
	 public LocalDef localDef(Position pos,
			 Flags flags, Ref<? extends Type> type, String name) {
		 assert_(type);
		 return new X10LocalDef_c(this, pos, flags, type, name);
	 }
	  public boolean equalTypeParameters(List<Type> a, List<Type> b) {
		 if (a == null || a.isEmpty()) return b==null || b.isEmpty();
		 if (b==null || b.isEmpty()) return false;
		 int i = a.size(), j=b.size();
		 if (i != j) return false;
		 boolean result = true;
		 for (int k=0; result && k < i; k++) {
			 result = typeEquals(a.get(k), b.get(k));
		 }
		 return result;
	 }
	 
	 @Override
	 public ConstructorDef constructorDef(Position pos,
                 Ref<? extends ClassType> container,
                 Flags flags, List<Ref<? extends Type>> argTypes,
                 List<Ref<? extends Type>> throwTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(throwTypes);
		 return constructorDef(pos, container, flags, container, Collections.EMPTY_LIST, argTypes, dummyLocalDefs(argTypes), null, throwTypes);
	 }
	
	 public X10ConstructorDef constructorDef(Position pos,
			 Ref<? extends ClassType> container,
			 Flags flags, Ref<? extends ClassType> returnType, 
			 List<Ref<? extends Type>> typeParams,
			 List<Ref<? extends Type>> argTypes,
			 List<LocalDef> formalNames,
			 Ref<XConstraint> whereClause, 
			 List<Ref<? extends Type>> excTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(excTypes);
		 return new X10ConstructorDef_c(this, pos, container, flags,
				 returnType, typeParams, argTypes, formalNames, whereClause, excTypes);
	 }

	public void addAnnotation(X10Def o, X10ClassType annoType, boolean replace) {
	    List<Ref<? extends X10ClassType>> newATs = new ArrayList<Ref<? extends X10ClassType>>();

	    if (replace) {
	        for (Ref<? extends X10ClassType> at : o.defAnnotations()) {
	            if (! at.get().isSubtype(X10TypeMixin.xclause(annoType, (XConstraint) null))) {
	                newATs.add(at);
	            }
	        }
	    }
	    else {
	        newATs.addAll(o.defAnnotations());
	    }

	    newATs.add(Types.ref(annoType));

	    o.setDefAnnotations(newATs);
	}
	
	public boolean primitiveClausesConsistent(x10.constraint.XConstraint c1, x10.constraint.XConstraint c2) {
//		try {
//			x10.constraint.Promise p1 = c1.lookup(x10.constraint.C_Self.Self);
//			x10.constraint.Promise p2 = c2.lookup(x10.constraint.C_Self.Self);
//			if (p1 != null && p2 != null) {
//				x10.constraint.C_Term t1 = p1.term();
//				x10.constraint.C_Term t2 = p2.term();
//				return t1 == null || t2 == null || t1.equals(t2);
//			}
//		}
//		catch (x10.constraint.Failure e) {
//			return true;
//		}
		return true;
	}
	
	public boolean clausesConsistent(x10.constraint.XConstraint c1, x10.constraint.XConstraint c2) {
		if (primitiveClausesConsistent(c1, c2)) {
			x10.constraint.XConstraint r = c1.copy();
			try {
				r.addIn(c2);
				return r.consistent();
			}
			catch (x10.constraint.XFailure e) {
				return false;
			}
		}
		return false;
	}

    public Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op) {
        XConstraint cl = X10TypeMixin.realX(l);
        XConstraint cr = X10TypeMixin.realX(r);
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        XConstraint c = xts.xtypeTranslator().binaryOp(op, cl, cr);
        return X10TypeMixin.xclause(t, c);
    }

    public Type performUnaryOperation(Type t, Type a, polyglot.ast.Unary.Operator op) {
        XConstraint ca = X10TypeMixin.realX(a);
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        XConstraint c = xts.xtypeTranslator().unaryOp(op, ca);
        return X10TypeMixin.xclause(t, c);
    }

	/**
	 * Returns true iff an object of type <type> may be thrown.
	 **/
	public boolean isThrowable(Type type) {
		assert_(type);
		return isSubtype(type, Throwable());
	}

	/**
	 * Returns a true iff the type or a supertype is in the list
	 * returned by uncheckedExceptions().
	 */
	public boolean isUncheckedException(Type type) {
		assert_(type);

		if (type.isThrowable()) {
		    for (Type t : uncheckedExceptions()) {
		        if (isSubtype(type, t)) {
		            return true;
		        }
		    }
		}
		
		return false;
	}
	

	@Override
	public X10MethodInstance findMethod(Type container, polyglot.types.TypeSystem_c.MethodMatcher matcher, ClassDef currClass) throws SemanticException {
	    return (X10MethodInstance) super.findMethod(container, matcher, currClass);
	}

	@Override
	public X10ConstructorInstance findConstructor(Type container, polyglot.types.TypeSystem_c.ConstructorMatcher matcher, ClassDef currClass)
	throws SemanticException {
	    return (X10ConstructorInstance) super.findConstructor(container, matcher, currClass);
	}
	
	    public X10MethodMatcher MethodMatcher(Type container, String name, List<Type> argTypes) {
		return new X10MethodMatcher( container,  name, argTypes);
	    }
	    public X10MethodMatcher MethodMatcher(Type container, String name, List<Type> typeArgs, List<Type> argTypes) {
		return new X10MethodMatcher( container,  name,  typeArgs, argTypes);
	    }
	    
	    public X10ConstructorMatcher ConstructorMatcher(Type container, List<Type> argTypes) {
		return new X10ConstructorMatcher(container, argTypes);
	    }
	    public X10ConstructorMatcher ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes) {
		return new X10ConstructorMatcher(container, typeArgs, argTypes);
	    }
	    public X10FieldMatcher FieldMatcher(Type container, String name) {
		return new X10FieldMatcher(container, name);
	    }

	    public static class X10FieldMatcher extends TypeSystem_c.FieldMatcher {
		private X10FieldMatcher(Type container, String name) {
		    super(container, name);
		}

		@Override
		public FieldInstance instantiate(FieldInstance mi) throws SemanticException {
		    FieldInstance fi =  super.instantiate(mi);
		    if (fi == null)
			return null;
		    Type t = fi.type();
		    XVar v = X10TypeMixin.selfVar(container);
		    if (v == null) v = new XConstraint_c().genEQV();
		    X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		    XLocal oldThis = ts.xtypeTranslator().transThisWithoutTypeConstraint();
		    Type newT = X10MethodInstance_c.subst(t, new XVar[] { v }, new XRoot[] { oldThis });
		    if (newT != t)
			return fi.type(newT);
		    return fi;
		}
	    }


	public static class X10MethodMatcher extends TypeSystem_c.MethodMatcher {
	    List<Type> typeArgs;

	    private X10MethodMatcher(Type container, String name, List<Type> argTypes) {
		this(container, name, Collections.EMPTY_LIST, argTypes);
	    }
	    private X10MethodMatcher(Type container, String name, List<Type> typeArgs, List<Type> argTypes) {
		super(container, name, argTypes);
		this.typeArgs = typeArgs;
	    }
	    
	    @Override
	    public String argumentString() {
		return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
	    }
	    
	    @Override
	    public MethodInstance instantiate(MethodInstance mi) throws SemanticException {
		if (! mi.name().equals(name))
		    return null;
		if (mi.formalTypes().size() != argTypes.size())
		    return null;
		if (mi instanceof X10MethodInstance) {
		    X10MethodInstance xmi = (X10MethodInstance) mi;
		    if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
			return X10MethodInstance_c.instantiate(xmi, container, typeArgs, argTypes);
		}
		return null;
	    }
	}
	
	public static class X10ConstructorMatcher extends TypeSystem_c.ConstructorMatcher {
	    List<Type> typeArgs;
	    
	    private X10ConstructorMatcher(Type container, List<Type> argTypes) {
		this(container, Collections.EMPTY_LIST, argTypes);
	    }
	    private X10ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes) {
		super(container, argTypes);
		this.typeArgs = typeArgs;
	    }
	    
	    @Override
	    public String argumentString() {
		return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
	    }
	    
	    @Override
	    public ConstructorInstance instantiate(ConstructorInstance ci) throws SemanticException {
		if (ci.formalTypes().size() != argTypes.size())
		    return null;
		if (ci instanceof X10ConstructorInstance) {
		    X10ConstructorInstance xmi = (X10ConstructorInstance) ci;
		    if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
			return X10MethodInstance_c.instantiate(xmi, container, typeArgs, argTypes);
		}
		return null;
	    }
	}
}
