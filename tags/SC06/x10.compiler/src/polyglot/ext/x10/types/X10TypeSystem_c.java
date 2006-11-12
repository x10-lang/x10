package polyglot.ext.x10.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ext.jl.types.FieldInstance_c;
import polyglot.ext.jl.types.LocalInstance_c;
import polyglot.ext.jl.types.MethodInstance_c;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyClassInitializer;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.UnknownType;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * A TypeSystem implementation for X10.
 *
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 */
public class X10TypeSystem_c extends TypeSystem_c implements X10TypeSystem, Serializable {
	
	private static X10TypeSystem x10TypeSystem = null;
	public static void setTypeSystem(X10TypeSystem x) { x10TypeSystem = x;}
	public static X10TypeSystem getTypeSystem() { return x10TypeSystem;}
	
	public X10TypeSystem_c() {
		super();
		unknownType = new X10UnknownType_c(this);
	}
	
	private final static Set primitiveTypeNames= new HashSet();
	
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
	
	public MethodInstance methodInstance(Position pos,
			ReferenceType container, Flags flags,
			Type returnType, String name,
			List argTypes, List excTypes) {
		
		assert_(container);
		assert_(returnType);
		assert_(argTypes);
		assert_(excTypes);
		return new X10MethodInstance_c(this, pos, container, flags,
				returnType, name, argTypes, excTypes);
	}
	
	/**
	 * Requires: all type arguments are canonical.
	 *
	 * Returns true iff an implicit cast from fromType to toType is valid;
	 * in other words, every member of fromType is member of toType.
	 *
	 * Returns true iff child and ancestor are non-primitive
	 * types, and a variable of type child may be legally assigned
	 * to a variable of type ancestor.
	 *
	 */
	public boolean isImplicitCastValid(Type fromType, Type toType) {
		assert_(fromType);
		assert_(toType);
		if ( Report.should_report("debug", 5))
			Report.report(5, "[X10TypeSystem_c] isImplicitCastValid |" + fromType.getClass() + "| to |" + toType + "|?");
		boolean result = fromType.isImplicitCastValidImpl(toType);
		if (Report.should_report("debug", 5))
			Report.report(5, "[X10TypeSystem_c] ... " + result);
		return result;
	}
	
	private HashMap nullableMap = new HashMap();
	/**
	 * Return a nullable type based on a given type.
	 * TODO: rename this to nullableType() -- the name is misleading.
	 */
	public NullableType createNullableType(Position pos, X10NamedType type) {
		NullableType t = (NullableType) nullableMap.get(type);
		if (t == null) {
			t = NullableType_c.makeNullable(this, pos, type);
			nullableMap.put(type, t);
		}
		return t;
	}
	
	public FutureType createFutureType(Position pos, X10NamedType type) {
		return new FutureType_c(this, pos, type);
	}
	
	/*public ParametricType createParametricType(Position pos,
	 X10ReferenceType type,
	 List typeparameters,
	 DepParameterExpr expr)
	 {
	 return new ParametricType_c(this, pos, type, typeparameters, expr);
	 }*/
	
	protected UnknownType createUnknownType() {
		return unknownType;
	}
	
	/**
	 * [IP] TODO: this should be a special CodeInstance instead
	 */
	protected CodeInstance asyncCodeInstance_;
	public CodeInstance asyncCodeInstance() {
		if (asyncCodeInstance_ == null)
			asyncCodeInstance_ =
				new MethodInstance_c(this, Position.COMPILER_GENERATED,
						Runtime(), Public(), VOID_, "$dummyAsync$",
						Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		return asyncCodeInstance_;
	}
	
	protected NullType createNull() {
		return new X10NullType_c(this);
	}
	
	public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource) {
		if ( Report.should_report("debug",3))
			Report.report(3, "X10TypeSystem_c: Creating Class fromSource =|" + fromSource + "|");
		return new X10ParsedClassType_c(this, init, fromSource);
	}
	
	/******************** Primitive types as Objects ******************/
	
	private static final String WRAPPER_PACKAGE = "x10.compilergenerated";
	
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
			operatorPointwiseType_ = load("x10.array.Operator$Pointwise"); // java file
		return operatorPointwiseType_;
	}
	
	protected ClassType operatorBinaryType_;
	public ClassType OperatorBinary() {
		if (operatorBinaryType_ == null)
			operatorBinaryType_ = load("x10.array.Operator$Binary"); // java file
		return operatorBinaryType_;
	}
	
	protected ClassType operatorUnaryType_;
	public ClassType OperatorUnary() {
		if (operatorUnaryType_ == null)
			operatorUnaryType_ = load("x10.array.Operator$Unary"); // java file
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
	/**
	 * Factory method for ArrayTypes.
	 * Called only from jl.types.TypeSystem_c.
	 */
	protected ArrayType arrayType(Position pos, Type type) {
		return new X10ArrayType_c(this, pos, type);
	}
	
	public ReferenceType array(Type type, boolean isValueType, Expr distribution) {
		if (type.isBoolean()) return booleanArray(isValueType, distribution);
		if (type.isChar())    return charArray(isValueType, distribution);
		if (type.isByte())    return byteArray(isValueType, distribution);
		if (type.isShort())   return shortArray(isValueType, distribution);
		if (type.isInt())     return intArray(isValueType, distribution);
		if (type.isFloat())   return floatArray(isValueType, distribution);
		if (type.isDouble())  return doubleArray(isValueType, distribution);
		if (type.isLong())    return longArray(isValueType, distribution);
		List list = new LinkedList();
		list.add(type);
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
	
	/*protected X10ReferenceType genericArrayPointwiseOpType_;
	 public X10ReferenceType GenericArrayPointwiseOp() {
	 if (genericArrayPointwiseOpType_ == null)
	 genericArrayPointwiseOpType_
	 = (X10ReferenceType) load("x10.lang.genericArray$pointwiseOp"); // java file
	 X10ReferenceType result = genericArrayPointwiseOpType_;
	 return genericArrayPointwiseOpType_;
	 }
	 
	 public ReferenceType GenericArrayPointwiseOp(Type typeParam) {
	 List l = new LinkedList();
	 l.add(typeParam);
	 
	 X10ReferenceType result = (X10ReferenceType) GenericArrayPointwiseOp().makeVariant(null, l);
	 return result;
	 }*/
	
	public ClassType genericArray(boolean isValueType, Expr distribution, List types) {
		return isValueType
		? genericValueArray(distribution, types)
				: GenericReferenceArray(distribution, types);
	}
	
	protected X10ParsedClassType genericArrayType_;
	public ClassType genericArray(Expr distribution, List typeParams) {
		if (genericArrayType_ == null) {
			genericArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray"); // java file
		}
		// FIXME: was genericArray
		// Also: I'd like to eliminate the plehora of Array classes
		// in the runtime - CG
		//TODO: Convert distribution to a depclause.
		ClassType result = (ClassType) genericArrayType_.makeVariant(null, typeParams);
		return result;
	}
	
	public ClassType genericArray() {
		return genericArray(null, new LinkedList());
	}
	
	private ClassType genericValueArray() {
		return genericValueArray(null, new LinkedList());
	}
	
	private ClassType genericValueArray(Expr distribution, List types) {
		return genericArray(distribution, types);
	}
	
	private ClassType GenericReferenceArray() {
		
		return GenericReferenceArray(null, new LinkedList());
	}
	
	protected X10ParsedClassType genericReferenceArrayType_;
	private ClassType GenericReferenceArray(Expr distribution, List typeParams) {
		
		if (genericReferenceArrayType_ == null) {
			genericReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray"); // java file
		}
		// return a variant of genericReferenceArrayType_.
		// TODO: Also need to pass along distribution.
		X10ClassType result = (X10ClassType) genericReferenceArrayType_.makeVariant(null, typeParams);
		return result;
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
	
	protected ClassType floatValueArrayType_;
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
			
			List args = new LinkedList();
			args.add(X10Object());
			args.add(X10Object());
			
			for (Iterator i = ct.toClass().methods("equals", args).iterator();
			i.hasNext(); )
			{
				MethodInstance mi = (MethodInstance) i.next();
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
		
		for (Iterator i = ci.container().methods().iterator(); i.hasNext(); ) {
			MethodInstance mi = (MethodInstance) i.next();
			if (mi.name().equals(methodName) && mi.formalTypes().isEmpty()) {
				return mi;
			}
		}
		
		throw new InternalCompilerError("Could not find getter for " + t);
	}
	
	public X10NamedType boxedType(X10PrimitiveType t) {
		return (X10NamedType) wrapper(t).container();
	}
	
	public ConstructorInstance wrapper(X10PrimitiveType t) {
		String name = WRAPPER_PACKAGE + ".Boxed" + wrapperTypeString(t).substring("java.lang.".length());
		
		try {
			ClassType ct = ((Type) systemResolver().find(name)).toClass();
			
			for (Iterator i = ct.constructors().iterator(); i.hasNext(); ) {
				ConstructorInstance ci = (ConstructorInstance) i.next();
				if (ci.formalTypes().size() == 1) {
					Type argType = (Type) ci.formalTypes().get(0);
					if (equals(argType, t)) {
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
	
	// RMF 11/1/2005 - Not having the "static" qualifier on interfaces causes problems,
	// e.g. for New_c.disambiguate(AmbiguityRemover), which assumes that instantiating
	// non-static types requires a "this" qualifier expression.
	// [IP] FIXME: why does the above matter when we supply the bits?
	public Flags flagsForBits(int bits) {
		Flags sf = super.flagsForBits(bits);
		if (sf.isInterface()) return sf.Static();
		return sf;
	}
	
	public FieldInstance fieldInstance(Position pos,
			ReferenceType container, Flags flags,
			Type type, String name) {
		assert_(container);
		assert_(type);
		return new X10FieldInstance_c(this, pos, container, flags, type, name);
	}
	
	public X10FieldInstance propertyInstance(Position pos,
			ReferenceType container, Flags flags,
			Type type, String name) {
		assert_(container);
		assert_(type);
		return new X10FieldInstance_c(this, pos, container, flags, type, name);
	}
	
	public boolean isCastValid(Type fromType, Type toType) {
		return super.isCastValid(fromType, toType);
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
	public  boolean isIndexable(Type me) { 
		return isX10Subtype(me, Indexable()); 
	}
	public  boolean isX10Array(Type me) { 
		return isX10Subtype(me, Array()); 
	}
	
	Constraint rect;
	
	
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
		return isX10Subtype(me,intArray()); 
	}
	public  boolean isLongArray(Type me) {
		return isX10Subtype(me, longArray()); 
	}
	public boolean isFloatArray(Type me) {
		return isX10Subtype(me,floatArray()); 
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
		return isX10Subtype((X10Type) me,value());
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
		return ((X10Type)me).baseType();
	}
	
	public VarInstance createSelf(X10Type t) {
		VarInstance v = new X10LocalInstance_c(this,Position.COMPILER_GENERATED, Flags.PUBLIC, t, "self");
		return v;
	}
	protected TypeTranslator eval = new TypeTranslator();
	public TypeTranslator typeTranslator() {
		return eval;
	}
	public boolean equivClause(X10Type me, X10Type other) {
		//Report.report(1, "X10TypeSystem_c.equivClause" + me + " " + other);
		boolean result = true;
		try {
			
			X10Type bt1 = me.baseType(), bt2 = other.baseType();
			result &= bt1 == bt2;
			if (!result) return result;
			List tp1 = me.typeParameters(), tp2 = other.typeParameters();
			
			if (tp1 == null || tp1.isEmpty()) {
				if (tp2 != null && ! tp2.isEmpty())
					return result=false;
			} else {
				int n = tp1.size();
				if (n > 0) {
					if (tp2==null ||  n != tp2.size()) return result=false;
					Iterator it1 = tp1.iterator();
					Iterator it2 = tp2.iterator();
					while (it1.hasNext()) {
						Type t1 = (Type) it1.next();
						Type t2 = (Type) it2.next();
						result &= t1.equals(t2);
						if (!result) return result;
					}
					if (! result) return result;
				}
			}
			result = entailsClause(me, other) && entailsClause(other, me);
			return result;
		} finally {
			//Report.report(1, "X10TypeSystem_c.equivClause" +result);
		}
		
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
		Constraint c1 = me.realClause(), c2=other.depClause();
		return entailsClause(c1,c2);
		
	}
	public Flags createNewFlag(String name, Flags after) {
		Flags f = X10Flags.createFlag(name, after);
		flagsForName.put(name, f);
		return f;
	}
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
	
	public void checkMemberClassFlags(Flags f) throws SemanticException {
		if (! f.clear(X10_MEMBER_CLASS_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
					"Cannot declare a member class with flag(s) " +
					f.clear(X10_MEMBER_CLASS_FLAGS) + ".");
		}
		
		checkAccessFlags(f);
	}
	
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
	public FieldInstance fieldInstance(Position pos,
			ReferenceType container, Flags flags,
			String name, String initValue) {
		assert_(container);
		
		return new X10FieldInstance_c(this, pos, container, flags,  name, initValue);
	}
	public boolean equalsWithoutClause(X10Type type1, X10Type type2) {
		assert_(type1);
		assert_(type2);
		if (type1 == type2) return true;
		if (type1 == null || type2 == null) return false;
		return type1.equalsWithoutClauseImpl(type2);
	}
	public Type leastCommonAncestor(Type type1, Type type2)
	throws SemanticException
	{
		assert_(type1);
		assert_(type2);
		Type result = null;
		try { 
		if (typeBaseEquals(type1, type2)) return result = ((X10Type)type1).baseType();
		
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
	 public LocalInstance localInstance(Position pos,
			 Flags flags, Type type, String name) {
		 assert_(type);
		 return new X10LocalInstance_c(this, pos, flags, type, name);
	 }

	 /**
	  * Populates the list acceptable with those MethodInstances which are
	  * Applicable and Accessible as defined by JLS 15.11.2.1
	  */
	 protected List findAcceptableMethods(ReferenceType container, String name,
			 List argTypes, ClassType currClass)
	 throws SemanticException {
		 
		 assert_(container);
		 assert_(argTypes);
		 
		 SemanticException error = null;
		 
		 // The list of acceptable methods. These methods are accessible from
		 // currClass, the method call is valid, and they are not overridden
		 // by an unacceptable method (which can occur with protected methods
		 // only).
		 List acceptable = new ArrayList();
		 
		 // A list of unacceptable methods, where the method call is valid, but
		 // the method is not accessible. This list is needed to make sure that
		 // the acceptable methods are not overridden by an unacceptable method.
		 List unacceptable = new ArrayList();
		 
		 Set visitedTypes = new HashSet();
		 
		 LinkedList typeQueue = new LinkedList();
		 typeQueue.addLast(container);
		 
		 while (! typeQueue.isEmpty()) {
			 Type type = (Type) typeQueue.removeFirst();
			 
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
			 
			 for (Iterator i = type.toReference().methods().iterator(); i.hasNext(); ) {
				 MethodInstance mi = (MethodInstance) i.next();
				 
				 if (Report.should_report(Report.types, 3))
					 Report.report(3, "Trying " + mi);
				 
				 if (! mi.name().equals(name)) {
					 continue;
				 }
				 // vj: This is the only change for X10.
				 mi = ((X10MethodInstance) mi).instantiateForThis((X10Type)container);
				 
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
		 for (Iterator i = unacceptable.iterator(); i.hasNext();) {
			 MethodInstance mi = (MethodInstance)i.next();
			 acceptable.removeAll(mi.overrides());
		 }
		 
		 if (acceptable.size() == 0) {
			 throw error;
		 }
		 
		 return acceptable;
	 }

} // end of X10TypeSystem_c

