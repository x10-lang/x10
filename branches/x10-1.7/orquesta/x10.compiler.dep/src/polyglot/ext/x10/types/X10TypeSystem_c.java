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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ext.x10.types.constr.BindingConstraintSystem;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.ConstraintSystem;
import polyglot.ext.x10.types.constr.CvcConstraintSystem;
import polyglot.ext.x10.types.constr.CvcSetConstraintSystem;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.ext.x10.types.constr.TypeTranslator;
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
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

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
	
	private final static Set<String> primitiveTypeNames= new HashSet<String>();
	
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
	public MethodDef methodDef(Position pos, Ref<? extends ReferenceType> container, Flags flags, Ref<? extends Type> returnType, java.lang.String name,
	        List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> excTypes) {
	    assert_(container);
	    assert_(returnType);
	    assert_(argTypes);
	    assert_(excTypes);
	    return new X10MethodDef_c(this, pos, container, flags,
	                              returnType, name, argTypes, excTypes);
	}
	
	/**
	 * Return a nullable type based on a given type.
	 * TODO: rename this to nullableType() -- the name is misleading.
	 */
	public NullableType createNullableType(Position pos, Ref<? extends X10NamedType> type) {
	    return NullableType_c.makeNullable(this, pos, type);
	}

	public FutureType createFutureType(Position pos, Ref<? extends X10NamedType> type) {
	    return new FutureType_c(this, pos, type);
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

	public ClosureType closure(Position p, Ref<? extends Type> returnType, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwTypes) {
	    return new ClosureType_c(this, p, returnType, argTypes, throwTypes);
	}
	
	public ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer, Ref<? extends Type> returnType, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwTypes) {
	    return new ClosureDef_c(this, p, typeContainer, methodContainer, returnType, argTypes, throwTypes);
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
	
	public ReferenceType array(Type type, boolean isValueType, Expr distribution) {
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
		//Report.report(1, "X10TypeSystem_c: GOLDEN " + type );
		ReferenceType result = genericArray(isValueType, distribution, list);
		return result;
	}
	
	public ReferenceType array(Type type, Expr distribution) {
		return array(type, false, distribution);
	}
	
	public ReferenceType array(Type type, boolean isValue) {
		return array(type, isValue, null);
	}
	
	public ReferenceType array(Type type) {
		return array(type, false, null);
	}
	
	public ReferenceType array(Ref<? extends Type> type, boolean isValueType, Expr distribution) {
	    return array(Types.get(type), isValueType, distribution);
	}

	public ReferenceType array(Ref<? extends Type> type, Expr distribution) {
	    return array(type, false, distribution);
	}

	public ReferenceType array(Ref<? extends Type> type, boolean isValueType) {
	    return array(type, isValueType, null);
	}

	public ReferenceType array(Ref<? extends Type> type) {
	    return array(type, false, null);
	}
	
	public ClassType booleanArray(boolean isValueType, Expr distribution) {
		return isValueType
		? booleanValueArray(distribution)
				: BooleanReferenceArray(distribution);
	}
	
	public ClassType booleanArray() {
		return booleanArray(null);
	}
	
	protected X10ParsedClassType booleanArrayType_;
	public ClassType booleanArray(Expr distribution) {
		if (booleanArrayType_ == null)
			booleanArrayType_ = (X10ParsedClassType) load("x10.lang.booleanArray"); // java file
		
		
		return booleanArrayType_;
	}
	
	private ClassType booleanValueArray() {
		return booleanValueArray(null);
	}
	
	protected ClassType booleanValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType booleanValueArray(Expr distribution) {
		return booleanArray(distribution);
	}
	
	private ClassType BooleanReferenceArray() {
		return BooleanReferenceArray(null);
	}
	
	protected X10ParsedClassType booleanReferenceArrayType_;
	private ClassType BooleanReferenceArray(Expr distribution) {
		if (booleanReferenceArrayType_ == null)
			booleanReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.BooleanReferenceArray"); // java file
		return booleanReferenceArrayType_;
	}
	
	public ClassType charArray(boolean isValueType, Expr distribution) {
		return isValueType
		? charValueArray(distribution)
				: CharReferenceArray(distribution);
	}
	
	public ClassType charArray() {
		return charArray(null);
	}
	
	protected X10ParsedClassType charArrayType_;
	public ClassType charArray(Expr distribution) {
		if (charArrayType_ == null)
			charArrayType_ = (X10ParsedClassType) load("x10.lang.charArray"); // java file
		
		return charArrayType_;
	}
	
	private ClassType charValueArray() {
		return charValueArray(null);
	}
	
	protected ClassType charValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType charValueArray(Expr distribution) {
		return charArray(distribution);
	}
	
	private ClassType CharReferenceArray() {
		return CharReferenceArray(null);
	}
	
	protected X10ParsedClassType charReferenceArrayType_;
	private ClassType CharReferenceArray(Expr distribution) {
		if (charReferenceArrayType_ == null)
			charReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.CharReferenceArray"); // java file
		
		
		return charReferenceArrayType_;
	}
	
	public ClassType byteArray(boolean isValueType, Expr distribution) {
		return isValueType
		? byteValueArray(distribution)
				: ByteReferenceArray(distribution);
	}
	
	public ClassType byteArray() {
		return byteArray(null);
	}
	
	protected X10ParsedClassType byteArrayType_;
	public ClassType byteArray(Expr distribution) {
		if (byteArrayType_ == null)
			byteArrayType_ = (X10ParsedClassType) load("x10.lang.byteArray"); // java file
		
		return byteArrayType_;
	}
	
	private ClassType byteValueArray() {
		return byteValueArray(null);
	}
	
	protected ClassType byteValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType byteValueArray(Expr distribution) {
		return byteArray(distribution);
	}
	
	private ClassType ByteReferenceArray() {
		return ByteReferenceArray(null);
	}
	
	protected X10ParsedClassType byteReferenceArrayType_;
	private ClassType ByteReferenceArray(Expr distribution) {
		if (byteReferenceArrayType_ == null)
			byteReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.ByteReferenceArray"); // java file
		return byteReferenceArrayType_;
	}
	
	public ClassType shortArray(boolean isValueType, Expr distribution) {
		return isValueType
		? shortValueArray(distribution)
				: ShortReferenceArray(distribution);
	}
	
	public ClassType shortArray() {
		return shortArray(null);
	}
	
	protected X10ParsedClassType shortArrayType_;
	public ClassType shortArray(Expr distribution) {
		if (shortArrayType_ == null)
			shortArrayType_ = (X10ParsedClassType) load("x10.lang.shortArray"); // java file
		
		
		
		return shortArrayType_;
	}
	
	private ClassType shortValueArray() {
		return shortValueArray(null);
	}
	
	protected ClassType shortValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType shortValueArray(Expr distribution) {
		return shortArray(distribution);
	}
	
	private ClassType ShortReferenceArray() {
		return ShortReferenceArray(null);
	}
	
	protected X10ParsedClassType shortReferenceArrayType_;
	public ClassType ShortReferenceArray(Expr distribution) {
		if (shortReferenceArrayType_ == null)
			shortReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.ShortReferenceArray"); // java file
		return shortReferenceArrayType_;
	}
	
	public ClassType intArray(boolean isValueType, Expr distribution) {
		return isValueType
		? intValueArray(distribution)
				: IntReferenceArray(distribution);
	}
	
	protected X10ParsedClassType intArrayType_;
	public ClassType intArray(Expr distribution) {
		if (intArrayType_ == null)
			intArrayType_ = (X10ParsedClassType) load("x10.lang.intArray"); // java file
		return intArrayType_;
	}
	
	public ClassType intArray() {
		return intArray(null);
	}
	
	private ClassType intValueArray() {
		return intValueArray(null);
	}
	
	protected ClassType intValueArrayType_;
	private ClassType intValueArray(Expr distribution) {
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
	
	private ClassType IntReferenceArray() {
		return IntReferenceArray(null);
	}
	
	protected X10ParsedClassType intReferenceArrayType_;
	private ClassType IntReferenceArray(Expr distribution) {
		if (intReferenceArrayType_ == null)
			intReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.IntReferenceArray"); // java file
		// return intReferenceArrayType_.setParameter("distribution", distribution);
		X10ClassType result = intReferenceArrayType_;
		return result;
	}
	
	protected X10ParsedClassType longArrayPointwiseOpType_;
	public ClassType LongArrayPointwiseOp() {
		if (longArrayPointwiseOpType_ == null)
			longArrayPointwiseOpType_ = (X10ParsedClassType) load("x10.lang.longArray$pointwiseOp"); // java file
		X10ClassType result = longArrayPointwiseOpType_;
		
		return result;
	}
	
	
	public ClassType longArray(boolean isValueType, Expr distribution) {
		return isValueType
		? longValueArray(distribution)
				: LongReferenceArray(distribution);
	}
	
	protected X10ParsedClassType longArrayType_;
	public ClassType longArray(Expr distribution) {
		if (longArrayType_ == null)
			longArrayType_ = (X10ParsedClassType) load("x10.lang.longArray"); // java file
		X10ClassType result = longArrayType_;
		return result;
	}
	
	public ClassType longArray() {
		return longArray(null);
	}
	
	private ClassType longValueArray() {
		return longValueArray(null);
	}
	
	private ClassType longValueArray(Expr distribution) {
		return longArray(distribution);
	}
	
	private ClassType LongReferenceArray() {
		return LongReferenceArray(null);
	}
	
	protected X10ParsedClassType longReferenceArrayType_;
	public ClassType LongReferenceArray(Expr distribution) {
		if (longReferenceArrayType_ == null)
			longReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.LongReferenceArray"); // java file
		// return longReferenceArrayType_.setParameter("distribution", distribution);
		X10ClassType result = longReferenceArrayType_;
		return result;
	}
	
	public ClassType genericArray(boolean isValueType, Expr distribution, List<Ref<? extends Type>> types) {
		return isValueType
		? genericValueArray(distribution, types)
				: GenericReferenceArray(distribution, types);
	}
	
	protected X10ParsedClassType genericArrayType_;
	public ClassType genericArray(Expr distribution, List<Ref<? extends Type>> typeParams) {
		if (genericArrayType_ == null) {
			genericArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray"); // java file
		}
		// FIXME: was genericArray
		// Also: I'd like to eliminate the plethora of Array classes
		// in the runtime - CG
		//TODO: Convert distribution to a depclause.
	        return (ClassType) X10TypeMixin.typeParams(genericArrayType_, typeParams);
	}
	
	public ClassType genericArray() {
		return genericArray(null, Collections.EMPTY_LIST);
	}
	
	private ClassType genericValueArray() {
		return genericValueArray(null, Collections.EMPTY_LIST);
	}
	
	private ClassType genericValueArray(Expr distribution, List<Ref<? extends Type>> types) {
		return genericArray(distribution, types);
	}
	
	private ClassType GenericReferenceArray() {
		
		return GenericReferenceArray(null, Collections.EMPTY_LIST);
	}
	
	protected X10ParsedClassType genericReferenceArrayType_;
	private ClassType GenericReferenceArray(Expr distribution, List<Ref<? extends Type>> typeParams) {
		
		if (genericReferenceArrayType_ == null) {
			genericReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray"); // java file
		}
		// return a variant of genericReferenceArrayType_.
		// TODO: Also need to pass along distribution.

		return (ClassType) X10TypeMixin.typeParams(genericReferenceArrayType_, typeParams);
	}
	
	public ClassType floatArray(boolean isValueType, Expr distribution) {
		return isValueType
		? floatValueArray(distribution)
				: FloatReferenceArray(distribution);
	}
	
	public ClassType floatArray() {
		return floatArray(null);
	}
	
	protected X10ParsedClassType floatArrayType_;
	public ClassType floatArray(Expr distribution) {
		if (floatArrayType_ == null)
			floatArrayType_ = (X10ParsedClassType) load("x10.lang.floatArray"); // java file
		
		// TODO: Also need to pass along distribution.
		
		X10ClassType result = floatArrayType_;
		return result;
	}
	
	public ClassType floatValueArray() {
		return floatValueArray(null);
	}
	
	protected Type floatValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType floatValueArray(Expr distribution) {
		return floatArray(distribution);
	}
	
	private ClassType FloatReferenceArray() {
		return FloatReferenceArray(null);
	}
	
	protected X10ParsedClassType floatReferenceArrayType_;
	private ClassType FloatReferenceArray(Expr distribution) {
		if (floatReferenceArrayType_ == null)
			floatReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.FloatReferenceArray"); // java file
		X10ClassType result = floatReferenceArrayType_;
		// TODO: Also need to pass along distribution.
		// result.setDepClause(depClauseFromDist(distribution));
		return result;
	}
	
	public ClassType doubleArray(boolean isValueType, Expr distribution) {
		return isValueType
		? doubleValueArray(distribution)
				: DoubleReferenceArray(distribution);
	}
	
	public ClassType doubleArray() {
		return doubleArray(null);
	}
	
	protected X10ParsedClassType doubleArrayType_;
	public ClassType doubleArray(Expr distribution) {
		if (doubleArrayType_ == null)
			doubleArrayType_ = (X10ParsedClassType) load("x10.lang.doubleArray"); // java file
		
		X10ClassType result = doubleArrayType_;
		// TODO: Also need to pass along distribution.
		
		// result.setDepClause(depClauseFromDist(distribution));
		return result;
	}
	
	private ClassType doubleValueArray() {
		return doubleValueArray(null);
	}
	
	protected X10ClassType doubleValueArrayType_;
	// see the todo for intValueArray.
	private ClassType doubleValueArray(Expr distribution) {
		return doubleArray(distribution);
	}
	
	private ClassType DoubleReferenceArray() {
		return DoubleReferenceArray(null);
	}
	
	protected X10ParsedClassType doubleReferenceArrayType_;
	private ClassType DoubleReferenceArray(Expr distribution) {
		if (doubleReferenceArrayType_ == null)
			doubleReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.DoubleReferenceArray"); // java file
		X10ClassType result = doubleReferenceArrayType_;
		// TODO: Also need to pass along distribution.
		
		// result.setDepClause(depClauseFromDist(distribution));
		return result;
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
	
	public MethodInstance getter(X10PrimitiveType t) {
		String methodName = t.typeName() + "Value";
		ConstructorInstance ci = wrapper(t);
		
		for (MethodInstance mi : ci.container().methods(methodName, Collections.EMPTY_LIST)) {
		    return mi;
		}
		
		throw new InternalCompilerError("Could not find getter for " + t);
	}
	
	public X10NamedType boxedType(X10PrimitiveType t) {
		X10NamedType namedType = (X10NamedType) wrapper(t).container();
		return namedType; 
	}
	
	public boolean isBoxedType(Type t) {
		t = this.isNullable(t) ? ((NullableType) t).base() : t;
		String targetCanonicalName = t.toString();
		return (t instanceof X10ParsedClassType) && 
					targetCanonicalName.startsWith(WRAPPER_PACKAGE + ".Boxed");		
	}

	public String getGetterName(Type t) {
		if (isBoxedType(t)) {
			t = this.isNullable(t) ? ((NullableType) t).base() : t;
			return this.boxedGetterAsString((X10ParsedClassType) t);
		}
		
		throw new InternalCompilerError(t + " is not a primitive type boxed");
	}
	
	
	public ConstructorInstance wrapper(X10PrimitiveType t) {
		String name = WRAPPER_PACKAGE + ".Boxed" + wrapperTypeString(t).substring("java.lang.".length());
		
		try {
			ClassType ct = ((Type) systemResolver().find(name)).toClass();
			
			for (ConstructorInstance ci : ct.constructors()) {
			    if (ci.formalTypes().size() == 1) {
			        Type argType = (Type) ci.formalTypes().get(0);
			        if (typeBaseEquals(argType, t)) {
			            if (t.depClause() != null) {
			                return ci.container( X10TypeMixin.makeDepVariant((X10ClassType) ci.container(), t.depClause()));
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
			return X10TypeMixin.makeVariant(t, ((X10Type)presumedBoxedType).depClause(), null);
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
	
	public  boolean isNullable(Type me) {
		return (me instanceof NullableType);
	}
	public  boolean isFuture(Type me) {
		NullableType r = ((X10Type) me).toNullable();
		if (r != null) me = r.base();
		return (me instanceof FutureType);
		
	}
	protected boolean isX10Subtype(Type me, Type sup) {
		NullableType r = ((X10Type) me).toNullable();
		if (r != null) me = r.base();
		boolean result = isSubtype(me, sup);        
		return result;
	}
	protected boolean isX10BaseSubtype(Type me, Type sup) {
		X10Type xme = (X10Type) me;
		X10Type xsup = (X10Type) sup;
		xme = xme.rootType();
		xsup = xsup.rootType();
		return isX10Subtype(xme, xsup);
	}
	public  boolean isIndexable(Type me) { 
		X10Type mex = (X10Type) me;
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant(mex), Indexable()); 
	}
	public  boolean isX10Array(Type me) { 
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), Array()); 
	}
	
	public boolean isTypeConstrained(Type me) {
		X10Type target = isNullable(me) ?((NullableType) me).base() : 
			(((X10Type) me));
		return target.depClause() != null;   
	}
	Constraint rect;
	
	
	public  boolean isBooleanArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), booleanArray()); 
	}
	public boolean isCharArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), charArray()); 
	}
	public boolean isByteArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), byteArray()); 
	}
	public  boolean isShortArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), shortArray()); 
	}
	public  boolean isIntArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me),intArray()); 
	}
	public  boolean isLongArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), longArray()); 
	}
	public boolean isFloatArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me),floatArray()); 
	}
	public  boolean isDoubleArray(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), doubleArray()); 
	}
	public  boolean isClock(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), clock()); 
	}
	public  boolean isPoint(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), point()); 
	}
	public  boolean isPlace(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), place());
	}
	public  boolean isRegion(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), region());
	}
	public  boolean isDistribution(Type me) {
		return isX10Subtype(X10TypeMixin.makeNoClauseVariant((X10Type) me), distribution());
	}
	public  boolean isDistributedArray(Type me) {
		return isX10Array(me);
	}
	public  boolean isValueType( Type me) {
		return isX10BaseSubtype((X10Type) me,value());
	}
	public Type baseType(Type theType) {
		Type me = theType;
		if (isBooleanArray(me))
			return Boolean();
		if (isByteArray(me))
			return Byte();
		if (isCharArray(me))
			return Char();
		if (isShortArray(me))
			return Short();
		if (isIntArray(me))
			return Int();
		if (isLongArray(me))
			return Long();
		if (isFloatArray(me))
			return Float();
		if (isDoubleArray(me))
			return Double();
		if (!isX10Array(me))
			return null;
		List<Type> typeParams = ((X10Type)me).typeParameters();
		if (typeParams.size() != 1)
			return null;
		return (Type) typeParams.get(0);
	}
	
	public VarDef createSelf(X10Type t) {
	    VarDef v = localDef(Position.COMPILER_GENERATED, Flags.PUBLIC, Types.ref(t), "self");
		return v;
	}
	private List<ConstraintSystem> constraintSystems;
	protected TypeTranslator eval = new TypeTranslator(this);
	public void addConstraintSystem(ConstraintSystem cs) {
	    constraintSystems().add(cs);
	}
	public TypeTranslator typeTranslator() {
		return eval;
	}
	public List<ConstraintSystem> constraintSystems() {
	    if (constraintSystems == null) {
	        constraintSystems = new ArrayList<ConstraintSystem>();
	    }
	    return constraintSystems;
	}
	
	@Override
	public void initialize(TopLevelResolver loadedResolver, ExtensionInfo extInfo) throws SemanticException {
	    super.initialize(loadedResolver, extInfo);
//	    addConstraintSystem(new CvcConstraintSystem(this));
	    addConstraintSystem(new CvcSetConstraintSystem(this));
//	    addConstraintSystem(new BindingConstraintSystem(this));
//	    addConstraintSystem(new OmegaConstraintSystem(this));
	}

	public boolean equivClause(X10Type me, X10Type other) {
	    return CollectionUtil.equals(X10TypeMixin.typeParameters(me), X10TypeMixin.typeParameters(other), new TypeSystem_c.TypeEquals())
	&& entailsClause(me, other) && entailsClause(other, me);
	}
	
	public boolean equivClause(Constraint c1, Constraint c2) {
		boolean result = (c1==null) ? ((c2==null) ? true : c2.valid())
				: c1.equiv(c2);
		return result;
	}
	
	public boolean entailsClause(Constraint c1, Constraint c2) {
		boolean result = c1==null ? (c2==null || c2.valid()) : c1.entails(c2);
		return result;
		
	}
	public boolean entailsClause(X10Type me, X10Type other) {
		try {
			Constraint c1 = me.realClause(), c2=other.depClause();
			return entailsClause(c1,c2);
		}
		catch (InternalCompilerError e) {
			if (e.getCause() instanceof Failure) {
				return false;
			}
			throw e;
		}
	}

	protected C_Here_c hereConstraintLit; // Maybe this should be declared as C_Lit instead of a concrete impl class?
	public C_Here_c here() {
	    if (hereConstraintLit == null)
		hereConstraintLit= new C_Here_c(this);
	    return hereConstraintLit;
	}

	protected C_Lit FALSE;
	public C_Lit FALSE() {
	    if (FALSE == null)
		FALSE= new C_Lit_c(false, this);
	    return FALSE;
	}
	protected C_Lit TRUE;
	public C_Lit TRUE() {
	    if (TRUE == null)
		TRUE= new C_Lit_c(true, this);
	    return TRUE;
	}
	protected C_Lit NEG_ONE;
	public C_Lit NEG_ONE() {
	    if (NEG_ONE == null)
		NEG_ONE= new C_Lit_c(new Integer(-1), this.Int());
	    return NEG_ONE;
	}
	protected C_Lit ZERO;
	public C_Lit ZERO() {
	    if (ZERO == null)
		ZERO= new C_Lit_c(new Integer(0), this.Int());
	    return ZERO;
	}
	protected C_Lit ONE;
	public C_Lit ONE() {
	    if (ONE == null)
		ONE= new C_Lit_c(new Integer(1), this.Int());
	    return ONE;
	}
	protected C_Lit TWO;
	public C_Lit TWO() {
	    if (TWO == null)
		TWO= new C_Lit_c(new Integer(2), this.Int());
	    return TWO;
	}
	protected C_Lit THREE;
	public C_Lit THREE() {
	    if (THREE == null)
		THREE= new C_Lit_c(new Integer(3), this.Int());
	    return THREE;
	}
	protected C_Lit NULL;
	public C_Lit NULL() {
	    if (NULL == null)
		NULL= new C_Lit_c(null, this.Null());
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
    
	public boolean equalsWithoutClause(X10Type type1, X10Type type2) {
		assert_(type1);
		assert_(type2);
		if (type1 == type2) return true;
		if (type1 == null || type2 == null) return false;
		return type1.equalsWithoutClauseImpl(type2);
	}
	
	@Override
	public PrimitiveType promote(Type t) throws SemanticException {
	    PrimitiveType pt = super.promote(t);

	    if (pt instanceof X10PrimitiveType) {
	        return (X10PrimitiveType) ((X10PrimitiveType) pt).rootType();
	    }
	    return pt;
	}
	@Override
	public PrimitiveType promote(Type t1, Type t2) throws SemanticException {
		PrimitiveType pt = super.promote(t1, t2);
		if (pt instanceof X10PrimitiveType) {
		    return (X10PrimitiveType) ((X10PrimitiveType) pt).rootType();
		}
		return pt;
	}
	@Override
	public Type leastCommonAncestor(Type type1, Type type2)
	throws SemanticException
	{
		assert_(type1);
		assert_(type2);
		Type result = null;
		try { 
		if (typeBaseEquals(type1, type2)) return result = ((X10Type)type1).rootType();
		
		if (type1.isNumeric() && type2.isNumeric()) {
			if (isImplicitCastValid(type1, type2)) {
				return result = type2;
			}
			
			if (isImplicitCastValid(type2, type1)) {
				return result = type1;
			}
			
			if (type1.isChar() && type2.isByte() ||
					type1.isByte() && type2.isChar()) {
				return result = Int();
			}
			
			if (type1.isChar() && type2.isShort() ||
					type1.isShort() && type2.isChar()) {
				return result = Int();
			}
		}
		
		if (type1.isArray() && type2.isArray()) {
			return result = arrayOf(leastCommonAncestor(type1.toArray().base(),
					type2.toArray().base()));
		}
		
		if (type1.isReference() && type2.isNull()) return result = type1;
		if (type2.isReference() && type1.isNull()) return result = type2;
		
		if (type1.isReference() && type2.isReference()) {
			// Don't consider interfaces.
			if (type1.isClass() && type1.toClass().flags().isInterface()) {
				return result = Object();
			}
			
			if (type2.isClass() && type2.toClass().flags().isInterface()) {
				return result = Object();
			}
			
			// Check against Object to ensure superType() is not null.
			if (typeEquals(type1, Object())) return result = type1;
			if (typeEquals(type2, Object())) return result = type2;
			
			if (isSubtype(type1, type2)) return result = type2;
			if (isSubtype(type2, type1)) return result = type1;
			
			// Walk up the hierarchy
			Type t1 = leastCommonAncestor(type1.toReference().superType(),
					type2);
			Type t2 = leastCommonAncestor(type2.toReference().superType(),
					type1);
			
			if (typeEquals(t1, t2)) return result = t1;
			
			return result = Object();
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
	        return ((X10Type) type1).equalsWithoutClauseImpl((X10Type) type2);
	    }
	 @Override
	 public LocalDef localDef(Position pos,
			 Flags flags, Ref<? extends Type> type, String name) {
		 assert_(type);
		 return new X10LocalDef_c(this, pos, flags, type, name);
	 }
	  public static String listToString(List l) {
		  return TypeSystem_c.listToString(l);
	  }
	 /**
	  * Populates the list acceptable with those MethodInstances which are
	  * Applicable and Accessible as defined by JLS 15.11.2.1
	  */
	  @Override
	 protected List<MethodInstance> findAcceptableMethods(ReferenceType container, String name,
			 List<Type> argTypes, ClassDef currClass)
	 throws SemanticException {
		 
		 assert_(container);
		 assert_(argTypes);
		 
		 SemanticException error = null;
		 
		 // The list of acceptable methods. These methods are accessible from
		 // currClass, the method call is valid, and they are not overridden
		 // by an unacceptable method (which can occur with protected methods
		 // only).
		 List<MethodInstance> acceptable = new ArrayList<MethodInstance>();
		 
		 // A list of unacceptable methods, where the method call is valid, but
		 // the method is not accessible. This list is needed to make sure that
		 // the acceptable methods are not overridden by an unacceptable method.
		 List<MethodInstance> unacceptable = new ArrayList<MethodInstance>();
		 
		 Set<Type> visitedTypes = new HashSet<Type>();
		 
		 LinkedList<Type> typeQueue = new LinkedList<Type>();
		 typeQueue.addLast(container);
		 
		 while (! typeQueue.isEmpty()) {
			 Type type = typeQueue.removeFirst();
			 
			 if (visitedTypes.contains(type)) {
				 continue;
			 }
			 
			 visitedTypes.add(type);
			 
			 if (Report.should_report(Report.types, 2))
				 Report.report(2, "Searching type " + type + " for method " +
						 name + "(" + listToString(argTypes) + ")");
			 
			 if (! type.isReference()) {
				 throw new SemanticException("Cannot call method in " +
						 " non-reference type " + type + ".");
			 }
			 
			 for (Iterator<MethodInstance> i = type.toReference().methods().iterator(); i.hasNext(); ) {
				 MethodInstance mi =  i.next();
				 
				 if (Report.should_report(Report.types, 3))
					 Report.report(3, "Trying " + mi);
				 
				 if (! mi.name().equals(name)) {
					 continue;
				 }
				 // vj: This is the only change for X10.
				 mi = ((X10MethodDef) mi.def()).instantiateForThis(container);
				 
				 if (methodCallValid(mi, name, argTypes)) {
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
				 }
				 else {
					 if (error == null) {
						 error = new NoMemberException(NoMemberException.METHOD,
								 "Method " + mi.signature() +
								 " in " + container +
								 " cannot be called with arguments " +
								 "(" + listToString(argTypes) + ")."); 
					 }
				 }
			 }
			 if (type.toReference().superType() != null) {
				 typeQueue.addLast(type.toReference().superType());
			 }
			 
			 typeQueue.addAll(type.toReference().interfaces());
		 }
		 
		 if (error == null) {
			 error = new NoMemberException(NoMemberException.METHOD,
					 "No valid method call found for " + name +
					 "(" + listToString(argTypes) + ")" +
					 " in " +
					 container + ".");
		 }
		 
		 if (acceptable.size() == 0) {
			 throw error;
		 }
		 
		 // remove any method in acceptable that are overridden by an
		 // unacceptable
		 // method.
		 for (Iterator<MethodInstance> i = unacceptable.iterator(); i.hasNext();) {
			 MethodInstance mi = (MethodInstance)i.next();
			 acceptable.removeAll(mi.overrides());
		 }
		 
		 if (acceptable.size() == 0) {
			 throw error;
		 }
		 
		 return acceptable;
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
		 return constructorDef(pos, container, flags, container, argTypes, excTypes);
	 }
	
	 public X10ConstructorDef constructorDef(Position pos,
			 Ref<? extends ClassType> container,
			 Flags flags, Ref<? extends ClassType> returnType, List<Ref<? extends Type>> argTypes,
			 List<Ref<? extends Type>> excTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(excTypes);
		 return new X10ConstructorDef_c(this, pos, container, flags,
				 returnType, argTypes, excTypes);
	 }

	public void addAnnotation(X10Def o, X10ClassType annoType, boolean replace) {
	    List<Ref<? extends X10ClassType>> newATs = new ArrayList<Ref<? extends X10ClassType>>();

	    if (replace) {
	        for (Ref<? extends X10ClassType> at : o.defAnnotations()) {
	            if (! at.get().isSubtype(annoType.rootType())) {
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
	
	public boolean clauseImplicitCastValid(Constraint c1, Constraint c2) {
	    return c1.entails(c2);
	}

	public boolean primitiveClausesConsistent(Constraint c1, Constraint c2) {
//	    Promise p1 = c1.lookup(C_Special_c.Self);
//	    Promise p2 = c2.lookup(C_Special_c.Self);
//	    if (p1 != null && p2 != null) {
//	        C_Term t1 = p1.term();
//	        C_Term t2 = p2.term();
//	        return t1 == null || t2 == null || t1.equals(t2);
//	    }
	    return true;
	}

	public boolean clausesConsistent(Constraint c1, Constraint c2) {
	    if (primitiveClausesConsistent(c1, c2)) {
	        Constraint r = c1.copy();
	        try {
	            r.addIn(c2);
	            return r.consistent();
	        }
	        catch (Failure e) {
	            return false;
	        }
	    }
	    return false;
	}

    public X10Type performBinaryOperation(X10Type t, X10Type l, X10Type r, Binary.Operator op) {
        Constraint cl = X10TypeMixin.realClause(l);
        Constraint cr = X10TypeMixin.realClause(r);
        Constraint c = cl.binaryOp(op, cr);
        return X10TypeMixin.depClauseDeref(t, c);
    }

    public X10Type performUnaryOperation(X10Type t, X10Type a, polyglot.ast.Unary.Operator op) {
        Constraint ca = X10TypeMixin.realClause(a);
        Constraint c = ca.unaryOp(op);
        return X10TypeMixin.depClauseDeref(t, c);
    }




} // end of X10TypeSystem_c

