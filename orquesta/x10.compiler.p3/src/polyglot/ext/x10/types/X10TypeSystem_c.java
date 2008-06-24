/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ext.x10.ast.FunctionTypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Source;
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
import polyglot.types.LazyRef_c;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.NullType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem_c;
import polyglot.types.Type_c;
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
import x10.constraint.XSelf;
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
	
	public PathType findTypeProperty(ClassType container, String name, ClassDef currClass) throws SemanticException {
		assert_(container);
		
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
			boxType_ = (X10ParsedClassType) load("x10.lang.Box");
		return fixGenericType(null, boxType_, false);
	}
	
	public Type boxOf(Ref<? extends Type> base) {
		return boxOf(Position.COMPILER_GENERATED, base);
	}
	
	public boolean isFunction(Type t) {
	    t = X10TypeMixin.xclause(t, null);
	    return t instanceof ClosureType;
	}
	public boolean isBox(Type t) {
		X10TypeSystem ts = this;
		if (ts.descendsFrom(X10TypeMixin.xclause(t, null), ts.Box())) {
			return true;
		}
		return false;
	}
	
	public boolean isRef(Type t) {
		X10TypeSystem ts = this;
		if (ts.descendsFrom(X10TypeMixin.xclause(t, null), ts.Ref())) {
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
		x = x.Safe().Local().NonBlocking().Sequential();
		return x;
		
	}

	/** All flags allowed for a top-level class. */
	public Flags legalTopLevelClassFlags() {
		return X10Flags.toX10Flags(super.legalTopLevelClassFlags()).Safe();
	}
	
	protected final X10Flags X10_TOP_LEVEL_CLASS_FLAGS = (X10Flags) legalTopLevelClassFlags();
	
	/** All flags allowed for an interface. */
	public Flags legalInterfaceFlags() {
		return X10Flags.toX10Flags(super.legalInterfaceFlags()).Safe();
	}
	
	protected final X10Flags X10_INTERFACE_FLAGS = (X10Flags) legalInterfaceFlags();
	
	/** All flags allowed for a member class. */
	public Flags legalMemberClassFlags() {
		return X10Flags.toX10Flags(super.legalMemberClassFlags()).Safe();
	}
	
	protected final Flags X10_MEMBER_CLASS_FLAGS = (X10Flags) legalMemberClassFlags();
	
	/** All flags allowed for a local class. */
	public Flags legalLocalClassFlags() {
		return X10Flags.toX10Flags(super.legalLocalClassFlags()).Safe();
	}
	
	protected final X10Flags X10_LOCAL_CLASS_FLAGS = (X10Flags) legalLocalClassFlags();
	
	
	@Override
	public MethodDef methodDef(Position pos, Ref<? extends ReferenceType> container, Flags flags, Ref<? extends Type> returnType, String name,
			List<Ref<? extends Type>> argTypes,
			List<Ref<? extends Type>> excTypes) {
		return methodDef(pos, container, flags, returnType, name, Collections.EMPTY_LIST, argTypes, null, excTypes);
	}
	
	public X10MethodDef methodDef(Position pos, Ref<? extends ReferenceType> container, Flags flags, Ref<? extends Type> returnType, String name,
	        List<Ref<? extends Type>> typeParams,
	        List<Ref<? extends Type>> argTypes,
	        Ref<? extends XConstraint> whereClause,
	        List<Ref<? extends Type>> excTypes) {
	    assert_(container);
	    assert_(returnType);
	    assert_(typeParams);
	    assert_(argTypes);
	    assert_(excTypes);
	    return new X10MethodDef_c(this, pos, container, flags,
	                              returnType, name, typeParams, argTypes, whereClause, excTypes);
	}
	
	/**
	 * Return a nullable type based on a given type.
	 * TODO: rename this to nullableType() -- the name is misleading.
	 */
	public Type boxOf(Position pos, Ref<? extends Type> type) {
		if (boxType_ == null)
			boxType_ = (X10ParsedClassType) load("x10.lang.Box");
		return fixGenericType(type, boxType_, false);
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
				    methodDef(Position.COMPILER_GENERATED, Types.ref(Runtime()), Public().Static(), Types.ref(VOID_), "$dummyAsync$",
                                                     Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			return asyncStaticCodeInstance_;
		} else {
			if (asyncCodeInstance_ == null)
				asyncCodeInstance_ =
				    methodDef(Position.COMPILER_GENERATED, Types.ref(Runtime()), Public(), Types.ref(VOID_), "$dummyAsync$",
				              Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			return asyncCodeInstance_;
		}
	}

	public ClosureType closure(Position p, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, Ref<? extends XConstraint> whereClause, List<Ref<? extends Type>> throwTypes) {
	    return new ClosureType_c(this, p, returnType, typeParams, argTypes, whereClause, throwTypes);
	}
	
	public ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, Ref<? extends XConstraint> whereClause, List<Ref<? extends Type>> throwTypes) {
	    return new ClosureDef_c(this, p, typeContainer, methodContainer, returnType, typeParams, argTypes, whereClause, throwTypes);
	}

	protected NullType createNull() {
		return new X10NullType_c(this);
	}
	
	/******************** Primitive types as Objects ******************/
	
	private static final String WRAPPER_PACKAGE = "x10.compilergenerated";
	
	@Override
	public PrimitiveType createPrimitive(PrimitiveType.Kind kind) {
		return new X10PrimitiveType_c(this, kind);
	}
	
	/* Predefined x10.lang classes that need not be translated by polyglot */
	protected ClassType x10ObjectType_;
	public ClassType X10Object() {
		if (x10ObjectType_ == null)
			x10ObjectType_ = load("x10.lang.Object"); // java file
		return x10ObjectType_;
	}
	
	protected ClassType parameter1Type_;
	public ClassType parameter1() {
		if (parameter1Type_ == null)
			parameter1Type_ = load(WRAPPER_PACKAGE + ".Parameter1"); // java file
		return parameter1Type_;
	}
	
	protected ClassType placeType_;
	public ClassType place() {
		if (placeType_ == null)
			placeType_ = load("x10.lang.place"); // java file
		return placeType_;
	}

	protected ClassType regionType_;
	public ClassType region() {
		if (regionType_ == null)
			regionType_ = load("x10.lang.region"); // java file
		return regionType_;
	}
	
	protected ClassType pointType_;
	public ClassType point() {
		if (pointType_ == null)
			pointType_ = load("x10.lang.point"); // java file
		return pointType_;
	}
	
	protected ClassType valueType_;
	public ClassType value() {
		if (valueType_ == null)
			valueType_ = load("x10.lang.ValueType"); // java file
		return valueType_;
	}
	
	protected ClassType distributionType_;
	public ClassType distribution() {
		if (distributionType_ == null)
			distributionType_ = load("x10.lang.dist"); // java file
		return distributionType_;
	}
	
	protected ClassType activityType_;
	public ClassType Activity() {
		if (activityType_ == null)
			activityType_ = load("x10.lang.Activity"); // java file
		return activityType_;
	}
	
	protected ClassType futureActivityType_;
	public ClassType FutureActivity() {
		if (futureActivityType_ == null)
			futureActivityType_ = load("x10.lang.Activity$Expr"); // java file
		return futureActivityType_;
	}
	
	protected ClassType x10ArrayType_;
	public ClassType array() {
		if (x10ArrayType_ == null)
			x10ArrayType_ = load("x10.lang.x10Array"); // java file
		return x10ArrayType_;
	}
	
	protected ClassType clockType_;
	public ClassType clock() {
		if (clockType_ == null)
			clockType_ = load("x10.lang.clock"); // java file
		return clockType_;
	}
	
	protected ClassType runtimeType_;
	public ClassType Runtime() {
		if (runtimeType_ == null)
			runtimeType_ = load("x10.lang.Runtime"); // java file
		return runtimeType_;
	}
	
	protected ClassType operatorPointwiseType_;
	public ClassType OperatorPointwise() {
		if (operatorPointwiseType_ == null)
			operatorPointwiseType_ = load("x10.array.Operator.Pointwise"); // java file
		return operatorPointwiseType_;
	}
	
	protected ClassType operatorBinaryType_;
	public ClassType OperatorBinary() {
		if (operatorBinaryType_ == null)
			operatorBinaryType_ = load("x10.array.Operator.Binary"); // java file
		return operatorBinaryType_;
	}
	
	protected ClassType operatorUnaryType_;
	public ClassType OperatorUnary() {
		if (operatorUnaryType_ == null)
			operatorUnaryType_ = load("x10.array.Operator.Unary"); // java file
		return operatorUnaryType_;
	}
	
	protected ClassType arrayOperationsType_;
	public ClassType ArrayOperations() {
		if (arrayOperationsType_ == null)
			arrayOperationsType_ = load("x10.lang.ArrayOperations"); // java file
		return arrayOperationsType_;
	}
	
	/*protected ClassType booleanArrayPointwiseOpType_;
	 public ClassType BooleanArrayPointwiseOp() {
	 if (booleanArrayPointwiseOpType_ == null)
	 booleanArrayPointwiseOpType_ = load("x10.lang.booleanArray$pointwiseOp"); // java file
	 return booleanArrayPointwiseOpType_;
	 }
	 
	 protected ClassType charArrayPointwiseOpType_;
	 public ClassType CharArrayPointwiseOp() {
	 if (charArrayPointwiseOpType_ == null)
	 charArrayPointwiseOpType_ = load("x10.lang.charArray$pointwiseOp"); // java file
	 return charArrayPointwiseOpType_;
	 }
	 
	 protected ClassType byteArrayPointwiseOpType_;
	 public ClassType ByteArrayPointwiseOp() {
	 if (byteArrayPointwiseOpType_ == null)
	 byteArrayPointwiseOpType_ = load("x10.lang.byteArray$pointwiseOp"); // java file
	 return byteArrayPointwiseOpType_;
	 }
	 
	 protected ClassType shortArrayPointwiseOpType_;
	 public ClassType ShortArrayPointwiseOp() {
	 if (shortArrayPointwiseOpType_ == null)
	 shortArrayPointwiseOpType_ = load("x10.lang.shortArray$pointwiseOp"); // java file
	 return shortArrayPointwiseOpType_;
	 }
	 
	 protected ClassType intArrayPointwiseOpType_;
	 public ClassType IntArrayPointwiseOp() {
	 if (intArrayPointwiseOpType_ == null) {
	 intArray(); // ensure that intArray is loaded.
	 intArrayPointwiseOpType_ = load("x10.lang.intArray$pointwiseOp"); // java file
	 }
	 return intArrayPointwiseOpType_;
	 }
	 
	 protected ClassType floatArrayPointwiseOpType_;
	 public ClassType FloatArrayPointwiseOp() {
	 if (floatArrayPointwiseOpType_ == null)
	 floatArrayPointwiseOpType_ = load("x10.lang.floatArray$pointwiseOp"); // java file
	 return floatArrayPointwiseOpType_;
	 }
	 
	 protected ClassType doubleArrayPointwiseOpType_;
	 public ClassType DoubleArrayPointwiseOp() {
	 if (doubleArrayPointwiseOpType_ == null)
	 doubleArrayPointwiseOpType_ = load("x10.lang.doubleArray$pointwiseOp"); // java file
	 return doubleArrayPointwiseOpType_;
	 }
	 */
	
	public Type array(Type type, boolean isValueType, Expr distribution) {
		if (type.isBoolean()) return booleanArray(isValueType, distribution);
		if (type.isChar())    return charArray(isValueType, distribution);
		if (type.isByte())    return byteArray(isValueType, distribution);
		if (type.isShort())   return shortArray(isValueType, distribution);
		if (type.isInt())     return intArray(isValueType, distribution);
		if (type.isFloat())   return floatArray(isValueType, distribution);
		if (type.isDouble())  return doubleArray(isValueType, distribution);
		if (type.isLong())    return longArray(isValueType, distribution);
		List<Ref<? extends Type>> list = new ArrayList<Ref<? extends Type>>();
		list.add(Types.ref(type));
		return genericArray(isValueType, distribution, list);
	}
	
	public Type array(Type type, Expr distribution) {
		return array(type, false, distribution);
	}
	
	public Type array(Type type, boolean isValue) {
		return array(type, isValue, null);
	}
	
	public Type array(Type type) {
		return array(type, false, null);
	}
	
	public Type array(Ref<? extends Type> type, boolean isValueType, Expr distribution) {
	    return array(Types.get(type), isValueType, distribution);
	}

	public Type array(Ref<? extends Type> type, Expr distribution) {
	    return array(type, false, distribution);
	}

	public Type array(Ref<? extends Type> type, boolean isValueType) {
	    return array(type, isValueType, null);
	}

	public Type array(Ref<? extends Type> type) {
	    return array(type, false, null);
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
			genericArrayType_ = (X10ParsedClassType) load("x10.lang.genericArray");
		return fixGenericType(base, genericArrayType_, false /* should be true XXX */);
	}

	static
	TypeProperty getFirstTypeProperty(X10ClassDef classDef) {
		if (classDef.typeProperties().size() == 1)
			return classDef.typeProperties().get(0);
		Type t = classDef.superType().get();
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
	
	public Type booleanArray(boolean isValueType, Expr distribution) {
		return isValueType
		? booleanValueArray(distribution)
				: BooleanReferenceArray(distribution);
	}
	
	public Type booleanArray() {
		return booleanArray(null);
	}
	
	protected Type booleanArrayType_;
	public Type booleanArray(Expr distribution) {
		if (booleanArrayType_ == null)
			booleanArrayType_ = newAndImprovedValueArray(Types.ref(Boolean()));
		return booleanArrayType_;
	}
	
	private Type booleanValueArray() {
		return booleanValueArray(null);
	}
	
	// see the TODO for intValueArray.
	private Type booleanValueArray(Expr distribution) {
		return booleanArray(distribution);
	}
	
	private Type BooleanReferenceArray() {
		return BooleanReferenceArray(null);
	}
	
	protected Type booleanReferenceArrayType_;
	private Type BooleanReferenceArray(Expr distribution) {
		if (booleanReferenceArrayType_ == null)
			booleanReferenceArrayType_ = newAndImprovedArray(Types.ref(Boolean()));
		return booleanReferenceArrayType_;
	}
	
	public Type charArray(boolean isValueType, Expr distribution) {
		return isValueType
		? charValueArray(distribution)
				: CharReferenceArray(distribution);
	}
	
	public Type charArray() {
		return charArray(null);
	}
	
	protected Type charArrayType_;
	public Type charArray(Expr distribution) {
		if (charArrayType_ == null)
			charArrayType_ = newAndImprovedValueArray(Types.ref(Char()));
		
		return charArrayType_;
	}
	
	private Type charValueArray() {
		return charValueArray(null);
	}
	
	// see the TODO for intValueArray.
	private Type charValueArray(Expr distribution) {
		return charArray(distribution);
	}
	
	private Type CharReferenceArray() {
		return CharReferenceArray(null);
	}
	
	protected Type charReferenceArrayType_;
	private Type CharReferenceArray(Expr distribution) {
		if (charReferenceArrayType_ == null)
			charReferenceArrayType_ = newAndImprovedValueArray(Types.ref(Char()));
		
		return charReferenceArrayType_;
	}
	
	public Type byteArray(boolean isValueType, Expr distribution) {
		return isValueType
		? byteValueArray(distribution)
				: ByteReferenceArray(distribution);
	}
	
	public Type byteArray() {
		return byteArray(null);
	}
	
	protected Type byteArrayType_;
	public Type byteArray(Expr distribution) {
		if (byteArrayType_ == null)
		byteArrayType_ = newAndImprovedValueArray(Types.ref(Byte()));
		
		return byteArrayType_;
	}
	
	private Type byteValueArray() {
		return byteValueArray(null);
	}
	
	// see the TODO for intValueArray.
	private Type byteValueArray(Expr distribution) {
		return byteArray(distribution);
	}
	
	private Type ByteReferenceArray() {
		return ByteReferenceArray(null);
	}
	
	protected Type byteReferenceArrayType_;
	private Type ByteReferenceArray(Expr distribution) {
		if (byteReferenceArrayType_ == null)
			byteReferenceArrayType_ = newAndImprovedArray(Types.ref(Byte()));
		return byteReferenceArrayType_;
	}
	
	public Type shortArray(boolean isValueType, Expr distribution) {
		return isValueType
		? shortValueArray(distribution)
				: ShortReferenceArray(distribution);
	}
	
	public Type shortArray() {
		return shortArray(null);
	}
	
	protected Type shortArrayType_;
	public Type shortArray(Expr distribution) {
		if (shortArrayType_ == null)
			shortArrayType_ = newAndImprovedValueArray(Types.ref(Short()));
		
		return shortArrayType_;
	}
	
	private Type shortValueArray() {
		return shortValueArray(null);
	}
	
	// see the TODO for intValueArray.
	private Type shortValueArray(Expr distribution) {
		return shortArray(distribution);
	}
	
	private Type ShortReferenceArray() {
		return ShortReferenceArray(null);
	}
	
	protected Type shortReferenceArrayType_;
	public Type ShortReferenceArray(Expr distribution) {
		if (shortReferenceArrayType_ == null)
			shortReferenceArrayType_ = newAndImprovedArray(Types.ref(Byte()));
		return shortReferenceArrayType_;
	}
	
	public Type intArray(boolean isValueType, Expr distribution) {
		return isValueType
		? intValueArray(distribution)
				: IntReferenceArray(distribution);
	}
	
	protected Type intArrayType_;
	public Type intArray(Expr distribution) {
		if (intArrayType_ == null)
			intArrayType_ = newAndImprovedValueArray(Types.ref(Int()));
		return intArrayType_;
	}
	
	public Type intArray() {
		return intArray(null);
	}
	
	private Type intValueArray() {
		return intValueArray(null);
	}
	
	private Type intValueArray(Expr distribution) {
		// vj: should really load x10.lang.intValueArray, but we are cheating...
		// there is no difference in the public type (= methods) of x10.lang.intValueArray
		// and x10.lang.intArray, so we reuse the implementation of x10.lang.intArray.
		// The backend does not do anything special about value classes right now anyway,
		// so there is no need to tell the implementation that this is a value class.
		// This hack will need to be fixed once the compiler starts checking that
		// value classes can only be extended by value classes.
		// [IP] FIXME
		return intArray(distribution);
	}
	
	private Type IntReferenceArray() {
		return IntReferenceArray(null);
	}
	
	protected Type intReferenceArrayType_;
	private Type IntReferenceArray(Expr distribution) {
		if (intReferenceArrayType_ == null)
			intReferenceArrayType_ = newAndImprovedArray(Types.ref(Int()));
		return intReferenceArrayType_;
	}
	
	protected Type longArrayPointwiseOpType_;
	public Type LongArrayPointwiseOp() {
		if (longArrayPointwiseOpType_ == null)
			longArrayPointwiseOpType_ = (X10ParsedClassType) load("x10.lang.longArray$pointwiseOp"); // java file
		return longArrayPointwiseOpType_;
	}
	
	
	public Type longArray(boolean isValueType, Expr distribution) {
		return isValueType
		? longValueArray(distribution)
				: LongReferenceArray(distribution);
	}
	
	protected Type longArrayType_;
	public Type longArray(Expr distribution) {
		if (longArrayType_ == null)
			longArrayType_ = newAndImprovedValueArray(Types.ref(Long()));
		return longArrayType_;
	}
	
	public Type longArray() {
		return longArray(null);
	}
	
	private Type longValueArray() {
		return longValueArray(null);
	}
	
	private Type longValueArray(Expr distribution) {
		return longArray(distribution);
	}
	
	private Type LongReferenceArray() {
		return LongReferenceArray(null);
	}
	
	protected Type longReferenceArrayType_;
	public Type LongReferenceArray(Expr distribution) {
		if (longReferenceArrayType_ == null)
			longReferenceArrayType_ = newAndImprovedArray(Types.ref(Long()));
		return longReferenceArrayType_;
	}
	
	public Type genericArray(boolean isValueType, Expr distribution, List<Ref<? extends Type>> types) {
		return isValueType
		? genericValueArray(distribution, types)
				: GenericReferenceArray(distribution, types);
	}
	
	public Type genericArray(Expr distribution, List<Ref<? extends Type>> typeParams) {
		return newAndImprovedValueArray(typeParams.size() == 0 ? null : typeParams.get(0));
	}
	
	public X10ParsedClassType genericArray() {
		return (X10ParsedClassType) genericArray(null, Collections.EMPTY_LIST);
	}
	
	private X10ParsedClassType genericValueArray() {
		return (X10ParsedClassType) genericValueArray(null, Collections.EMPTY_LIST);
	}
	
	private Type genericValueArray(Expr distribution, List<Ref<? extends Type>> types) {
		return genericArray(distribution, types);
	}
	
	private X10ParsedClassType GenericReferenceArray() {
		return (X10ParsedClassType) GenericReferenceArray(null, Collections.EMPTY_LIST);
	}
	
	private Type GenericReferenceArray(Expr distribution, List<Ref<? extends Type>> typeParams) {
		return newAndImprovedArray(typeParams.size() == 0 ? null : typeParams.get(0));
	}
	
	public Type floatArray(boolean isValueType, Expr distribution) {
		return isValueType
		? floatValueArray(distribution)
				: FloatReferenceArray(distribution);
	}
	
	public Type floatArray() {
		return floatArray(null);
	}
	
	protected Type floatArrayType_;
	public Type floatArray(Expr distribution) {
		if (floatArrayType_ == null)
			floatArrayType_ = newAndImprovedValueArray(Types.ref(Float()));
		
		// TODO: Also need to pass along distribution.
		
		return floatArrayType_;
	}
	
	public Type floatValueArray() {
		return floatValueArray(null);
	}
	
	// see the TODO for intValueArray.
	private Type floatValueArray(Expr distribution) {
		return floatArray(distribution);
	}
	
	private Type FloatReferenceArray() {
		return FloatReferenceArray(null);
	}
	
	protected Type floatReferenceArrayType_;
	private Type FloatReferenceArray(Expr distribution) {
		if (floatReferenceArrayType_ == null)
			floatReferenceArrayType_ = newAndImprovedArray(Types.ref(Float()));
		// TODO: Also need to pass along distribution.

		return floatReferenceArrayType_;
	}
	
	public Type doubleArray(boolean isValueType, Expr distribution) {
		return isValueType
		? doubleValueArray(distribution)
				: DoubleReferenceArray(distribution);
	}
	
	public Type doubleArray() {
		return doubleArray(null);
	}
	
	protected Type doubleArrayType_;
	public Type doubleArray(Expr distribution) {
		if (doubleArrayType_ == null)
			doubleArrayType_ = newAndImprovedValueArray(Types.ref(Double()));
		
		// TODO: Also need to pass along distribution.
		
		return doubleArrayType_;
	}
	
	private Type doubleValueArray() {
		return doubleValueArray(null);
	}
	
	// see the todo for intValueArray.
	private Type doubleValueArray(Expr distribution) {
		return doubleArray(distribution);
	}
	
	private Type DoubleReferenceArray() {
		return DoubleReferenceArray(null);
	}
	
	protected Type doubleReferenceArrayType_;
	private Type DoubleReferenceArray(Expr distribution) {
		if (doubleReferenceArrayType_ == null)
			doubleReferenceArrayType_ = newAndImprovedArray(Types.ref(Double()));
		// TODO: Also need to pass along distribution.
		
		return doubleReferenceArrayType_;
	}
	
	protected ClassType indexableType_ = null;
	public ClassType Indexable() {
		if (indexableType_ == null)
			indexableType_ = load("x10.lang.Indexable"); // java file
		return indexableType_;
	}
	
	protected ClassType arrayType_ = null;
	public ClassType Array() {
		if (arrayType_ == null)
			arrayType_ = load("x10.lang.Array"); // java file
		return arrayType_;
	}
	
	public MethodInstance primitiveEquals() {
		String name = WRAPPER_PACKAGE + ".BoxedNumber";
		
		try {
			Type ct = (Type) systemResolver().find(name);
			
			List<Type> args = new LinkedList<Type>();
			args.add(X10Object());
			args.add(X10Object());
			
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
		    				ReferenceType container = ci.container();
		    				return ci.container((ReferenceType) X10TypeMixin.xclause(container, c));
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
			Ref<? extends ReferenceType> container, Flags flags,
			Ref<? extends Type> type, String name) {
		assert_(container);
		assert_(type);
		return new X10FieldDef_c(this, pos, container, flags, type, name);
	}
	
	public X10FieldDef propertyInstance(Position pos,
	        Ref<? extends ReferenceType> container, Flags flags,
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
			result &= typeRefListEquals(ct1.argumentTypes(), ct2.argumentTypes());
			result &= typeRefListEquals(ct1.throwTypes(), ct2.throwTypes());           // XXX: ORDER shouldn't matter
			result &= isSubtype(ct1.returnType().get(), ct2.returnType().get());
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
			result &= typeListEquals(pt1.typeParams(), pt2.typeParams());
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
				XConstraint c = base.selfConstraint();
				if (c != null) {
					try {
						// Check if the constraint on the base p implies that p.T <: S
						// Avoid infinite recursion by removing selfConstraint on base.
						base.setSelfConstraint(null);
						c = c.substitute(base, XSelf.Self);
						XConstraint c2 = new XConstraint_c();
						c2.addAtom(xtypeTranslator().transSubtype(t1, t2));
						boolean result = c.entails(c2);
						if (result)
							return true;
					}
					catch (XFailure e) {
					}
					finally {
						base.setSelfConstraint(c);
					}
				}
			}
		}

		if (t1 instanceof PrimitiveType && t2 instanceof NullableType) {
			NullableType nt = (NullableType) t2;
			return isSubtype(t1, nt.base());
		}

		if (t1 instanceof PrimitiveType && t2.typeEquals(X10Object())) {
			return true;
		}
		
		if (t1 instanceof PrimitiveType && t2.typeEquals(Object())) {
			return true;
		}
		
		if (t1 instanceof PrimitiveType && ! t1.isVoid() && t2.typeEquals(boxedType((PrimitiveType) t1))) {
			return true;
		}

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
			result &= typeRefListEquals(ct1.argumentTypes(), ct2.argumentTypes());
			result &= typeRefListEquals(ct1.throwTypes(), ct2.throwTypes());           // XXX: ORDER shouldn't matter
			result &= typeEquals(ct1.returnType().get(), ct2.returnType().get());
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
				if (typeEquals(fromCT, this.Object()) || typeEquals(fromCT, this.X10Object()))
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

			if (typeEquals(toType, X10Object())) {
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
			return descendsFrom(cc.baseType().get(), ancestor);
		}
		
		if (ancestor instanceof ConstrainedType) {
			ConstrainedType ac = (ConstrainedType) ancestor;
			return descendsFrom(child, ac.baseType().get());
		}
		
		if (child instanceof ClosureType) {
			return typeEquals(ancestor, Object());
		}
		
		if (child instanceof PrimitiveType) {
			return typeEquals(ancestor, X10Object()) || typeEquals(ancestor, this.value());
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

	public  boolean isFuture(Type me) {
		Type t = X10TypeMixin.xclause(me, null);
		if (t instanceof ClassType && ((ClassType) t).fullName().equals("x10.lang.Future"))
			return true;
		return false;
	}
	protected boolean isX10Subtype(Type me, Type sup) {
		boolean result = isSubtype(me, sup);        
		return result;
	}
	protected boolean isX10BaseSubtype(Type me, Type sup) {
		Type xme = X10TypeMixin.xclause(me, null);
		Type xsup = X10TypeMixin.xclause(sup, null);
		return isX10Subtype(xme, xsup);
	}
	public  boolean isIndexable(Type me) { 
		return isX10BaseSubtype(me, Indexable()); 
	}
	public  boolean isX10Array(Type me) { 
		return isX10Subtype(me, Array()); 
	}
	
	public boolean isTypeConstrained(Type me) {
		return me instanceof ConstrainedType;
	}
	
	public  boolean isBooleanArray(Type me) {
		return isX10Subtype(me, booleanArray()); 
	}
	public boolean isCharArray(Type me) {
		return isX10Subtype(me, charArray()); 
	}
	public boolean isByteArray(Type me) {
		return isX10Subtype(me, byteArray()); 
	}
	public  boolean isShortArray(Type me) {
		return isX10Subtype(me, shortArray()); 
	}
	public  boolean isIntArray(Type me) {
		return isX10Subtype(me, intArray()); 
	}
	public  boolean isLongArray(Type me) {
		return isX10Subtype(me, longArray()); 
	}
	public boolean isFloatArray(Type me) {
		return isX10Subtype(me, floatArray()); 
	}
	public  boolean isDoubleArray(Type me) {
		return isX10Subtype(me, doubleArray()); 
	}
	public  boolean isClock(Type me) {
		return isX10Subtype(me, clock()); 
	}
	public  boolean isPoint(Type me) {
		return isX10Subtype(me, point()); 
	}
	public  boolean isPlace(Type me) {
		return isX10Subtype(me, place());
	}
	public  boolean isRegion(Type me) {
		return isX10Subtype(me, region());
	}
	public  boolean isDistribution(Type me) {
		return isX10Subtype(me, distribution());
	}
	public  boolean isDistributedArray(Type me) {
		return isX10Array(me);
	}
	public  boolean isValueType( Type me) {
		return isX10BaseSubtype((X10Type) me, value());
	}
	public Type baseType(Type theType) {
		if (theType instanceof X10ClassType) {
			X10ClassType xct = (X10ClassType) theType;
			X10ClassDef def = (X10ClassDef) xct.def();
			if (def == genericArray().def() || def == GenericReferenceArray().def()) {
				return X10TypeMixin.getParameterType(xct, "T");
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
		
	}
	public Flags Local() { return X10Flags.LOCAL;}
	public Flags Sequential() { return X10Flags.SEQUENTIAL;}
	public Flags Safe() { return X10Flags.SAFE;}
	public Flags NonBlocking() { return X10Flags.NON_BLOCKING;}
	
	
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
    
	@Override
	public Type promote(Type t) throws SemanticException {
	    Type pt = super.promote(t);
	    return X10TypeMixin.xclause(pt, null);
	}
	@Override
	public Type promote(Type t1, Type t2) throws SemanticException {
		Type pt = super.promote(t1, t2);
		return X10TypeMixin.xclause(pt, null);
	}
	@Override
	public Type leastCommonAncestor(Type type1, Type type2)
	throws SemanticException
	{
		assert_(type1);
		assert_(type2);
		try { 
		if (typeBaseEquals(type1, type2)) {
			Type base1 = X10TypeMixin.xclause(type1, null);
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
		
		if (type1.isArray() && type2.isArray()) {
			return arrayOf(leastCommonAncestor(
					type1.toArray().base(), type2.toArray().base()));
		}
		
		if (type1.isReference() && type2.isNull()) return type1;
		if (type2.isReference() && type1.isNull()) return type2;
		
		if (type1.isReference() && type2.isReference()) {
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
			
			// Walk up the hierarchy
			Type t1 = leastCommonAncestor(
					type1.toReference().superType(), type2);
			Type t2 = leastCommonAncestor(
					type2.toReference().superType(), type1);
			
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
	        return typeEquals(X10TypeMixin.xclause(type1, null), X10TypeMixin.xclause(type2, null));
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
                 List<Ref<? extends Type>> excTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(excTypes);
		 return constructorDef(pos, container, flags, container, Collections.EMPTY_LIST, argTypes, excTypes);
	 }
	
	 public X10ConstructorDef constructorDef(Position pos,
			 Ref<? extends ClassType> container,
			 Flags flags, Ref<? extends ClassType> returnType, 
			 List<Ref<? extends Type>> typeParams,
			 List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> excTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(excTypes);
		 return new X10ConstructorDef_c(this, pos, container, flags,
				 returnType, typeParams, argTypes, excTypes);
	 }

	public void addAnnotation(X10Def o, X10ClassType annoType, boolean replace) {
	    List<Ref<? extends X10ClassType>> newATs = new ArrayList<Ref<? extends X10ClassType>>();

	    if (replace) {
	        for (Ref<? extends X10ClassType> at : o.defAnnotations()) {
	            if (! at.get().isSubtype(X10TypeMixin.xclause(annoType, null))) {
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

	Type TypeType = null;
	public Type TypeType() {
		if (TypeType == null) {
			TypeType = new Type_c(this, Position.COMPILER_GENERATED) {
				public java.lang.String toString() {
					return "type";
				}

				public java.lang.String translate(Resolver c) {
					return "type";
				}
			};
		}
		return TypeType;
	}

	public X10MethodInstance findMethod(ReferenceType targetType, String name, List<Type> typeArgs, List<Type> argTypes, ClassDef currentClassDef)
	throws SemanticException {
		// EVIL HACK: prepend type arguments T as type{self==T} to the argument types.
		// callValid will separate them out again.
		List<Type> piggybackArgTypes = new ArrayList<Type>(typeArgs.size() + argTypes.size());
		for (Type t : typeArgs) {
			XConstraint c = new XConstraint_c();
			try {
				c.addBinding(XSelf.Self, xtypeTranslator().trans(t));
			}
			catch (XFailure e) {
			}
			Type typeType = X10TypeMixin.xclause(TypeType(), c);
			piggybackArgTypes.add(typeType);
		}
		piggybackArgTypes.addAll(argTypes);
		return (X10MethodInstance) super.findMethod(targetType, name, piggybackArgTypes, currentClassDef);
	}
	
	public X10ConstructorInstance findConstructor(ClassType ct, List<Type> typeArgs, List<Type> argTypes, ClassDef currentClassDef)
			throws SemanticException {
		// EVIL HACK: prepend type arguments T as type{self==T} to the argument types.
		// callValid will separate them out again.
		List<Type> piggybackArgTypes = new ArrayList<Type>(typeArgs.size() + argTypes.size());
		for (Type t : typeArgs) {
			XConstraint c = new XConstraint_c();
			try {
				c.addBinding(XSelf.Self, xtypeTranslator().trans(t));
			}
			catch (XFailure e) {
			}
			Type typeType = X10TypeMixin.xclause(TypeType(), c);
			piggybackArgTypes.add(typeType);
		}
		piggybackArgTypes.addAll(argTypes);
		return (X10ConstructorInstance) super.findConstructor(ct, piggybackArgTypes, currentClassDef);
	}

}
