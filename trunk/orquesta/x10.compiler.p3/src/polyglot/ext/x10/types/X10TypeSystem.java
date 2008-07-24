/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 1, 2004
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.PrimitiveType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.types.VarDef;
import polyglot.util.Position;
import x10.constraint.XConstraint;
import x10.constraint.XLit;
import x10.constraint.XTerm;

/**
 * Parts of this code are taken from the pao extension in the polyglot framework.
 *
 * @author Christoph von Praun
 * @author vj
 */
public interface X10TypeSystem extends TypeSystem {
	public static final String DIST_FIELD = "dist";
	public static final String REGION_FIELD = "region";
	public static final String RANK_FIELD = "rank";
	public static final String ZERO_BASED_FIELD = "zeroBased";
	public static final String LOCATION_FIELD = "loc";
	public static final String ONE_PLACE_FIELD = "onePlace";

	/** Add an annotation to a type object, optionally replacing existing annotations that are subtypes of annoType. */
	void addAnnotation(X10Def o, X10ClassType annoType, boolean replace);
		
	Type boxOf(Position p, Ref<? extends Type> t);
	Type futureOf(Position p, Ref<? extends Type> t);

	/**
	 * Create a <code>ClosureType</code> with the given signature.
	 */
	ClosureType closure(Position p, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, Ref<? extends XConstraint> whereClause, List<Ref<? extends Type>> throwTypes);
	ClosureInstance createClosureInstance(Position pos, Ref<? extends ClosureDef> def);

	ClassType X10Object();
	ClassType parameter1();

	XTerm here();
	
	ClassType place();
	ClassType region();
	Type point();
	ClassType distribution();
	ClassType Activity();
	ClassType FutureActivity();
	ClassType array();
	ClassType clock();
	ClassType value();
	ClassType Runtime();
	ClassType OperatorPointwise();
	ClassType OperatorBinary();
	ClassType OperatorUnary();
	ClassType ArrayOperations();

	XLit FALSE();
	XLit TRUE();
	XLit NEG_ONE();
	XLit ZERO();
	XLit ONE();
	XLit TWO();
	XLit THREE();
	XLit NULL();
	
	CodeDef asyncCodeInstance(boolean isStatic);

	/** Create a closure instance.
	 * @param pos Position of the closure.
	 * @param container Containing type of the closure.
	 * @param returnType The closure's return type.
	 * @param argTypes The closure's formal parameter types.
	 * @param excTypes The closure's exception throw types.
	 */
	ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer,
	                                                                              Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes,
	                                                                              Ref<? extends XConstraint> whereClause, List<Ref<? extends Type>> throwTypes);

	X10MethodDef
	methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, String name,
		        List<Ref<? extends Type>> typeParams,
		        List<Ref<? extends Type>> argTypes,
		        Ref<? extends XConstraint> whereClause,
		        List<Ref<? extends Type>> excTypes, Ref<XTerm> body);

	/**
	 * Provide a generic type constructor for arrays: Behaves the same as
	 * <Type>Array(isValueType, distribution). May throw an Error if not
	 * implemented for the given type. 1/13/2005 -- implemented for int and
	 * double only. TODO: implement for all native types.
	 */

	Type x10Array(Type type, boolean isValueType, XTerm distribution);
	Type x10Array(Ref<? extends Type> type, boolean isValueType, XTerm distribution);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array(distribution).
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	Type x10Array(Type type, XTerm distribution);
	Type x10Array(Ref<? extends Type> type, XTerm distribution);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array(isValueType).
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	Type x10Array(Type type, boolean isValueType);
	Type x10Array(Ref<? extends Type> type, boolean isValueType);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array().
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	Type x10Array(Type type);
	Type x10Array(Ref<? extends Type> type);

	/**
	 * Return the ClassType object for the x10.lang.Indexable interface.
	 */
	ClassType Indexable();

	/**
	 * Return the ClassType object for the x10.lang.Array interface.
	 */
	ClassType Array();
	
	/**
	 * Return the ClassType object for the x10.lang.Array interface.
	 */
	ClassType ValArray();
	
	/**
	 * Return the ClassType object for the x10.lang.NativeRail interface.
	 * @return
	 */
	ClassType NativeRail();
	
	/**
	 * Return the ClassType object for the x10.lang.NativeRail interface.
	 * @return
	 */
	ClassType NativeValRail();

	// RMF 7/11/2006 - Added so that the parser can create a canonical type node
	// for "primitive types", which otherwise will cause disambiguation to fail.
	//
	// Technically, there are no primitive types in X10, but the compiler can't
	// quite treat types like "int" as ordinary object types - there are no source
	// or class files corresponding to them, and therefore disambiguating such
	// types will always fail. The solution is to prevent the compiler from
	// attempting to disambiguate them by giving the parser enough information
	// to create a CanonicalTypeNode for them. Currently this is only an issue
	// when such types are decorated with place specifiers, in which case the
	// ClassOrInterfaceType production fires, and the left-hand side type could
	// be either an ordinary class/interface or a "primitive type". In the
	// absence of place specifiers, the base Java grammar rule IntegralType
	// fires, which always creates a CanonicalTypeNode.
	/**
	 * @return true iff the given name is that of a "primitive type".
	 */
	public boolean isPrimitiveTypeName(String name);

	/** Return the method instance for runtime.Primitive.equals */
	 MethodInstance primitiveEquals();

	/** Return the method instance for runtime.T.tValue() */
	 MethodInstance getter(PrimitiveType t);

	/** Return the constructor instance for runtime.T.T(t) */
	 ConstructorInstance wrapper(PrimitiveType t);

	/** Return boxed type runtime.T for primitive t. */
	 X10NamedType boxedType(PrimitiveType t);

	 public ClassType Comparable();
    public ClassType Iterable();
    public ClassType Iterator();
    public ClassType Contains();
    public ClassType ContainsAll();
    public ClassType Settable();
  
    boolean isComparable(Type me);
  boolean isIterable(Type me);
  boolean isIterator(Type me);
  boolean isContains(Type me);
  boolean isContainsAll(Type me);
  boolean isSettable(Type me);
  
  boolean isPrimitiveTypeArray(Type me);
  
     boolean isFuture(Type me) ;
     boolean isIndexable(Type me) ;
      boolean isX10Array(Type me) ;
      boolean isBooleanArray(Type me);
     boolean isCharArray(Type me) ;
     boolean isByteArray(Type me) ;
      boolean isShortArray(Type me) ;
      boolean isIntArray(Type me) ;
      boolean isLongArray(Type me) ;
     boolean isFloatArray(Type me);
      boolean isDoubleArray(Type me);
      boolean isClock(Type me) ;
      boolean isPoint(Type me);
      boolean isPlace(Type me);
      boolean isRegion(Type me);
      boolean isDistribution(Type me);
      boolean isDistributedArray(Type me);
     boolean isValueType( Type me);
	Type arrayBaseType(Type theType);
   
	/**
	 * Is a type constrained (i.e. its depClause is != null)
	 * If me is a nullable, then the basetype is checked.
	 * @param me Type to check
	 * @return true if type has a depClause.
	 */
	public boolean isTypeConstrained(Type me);
	
   VarDef createSelf(X10Type t);
   
   XTypeTranslator xtypeTranslator();

   boolean equivClause(Type m, Type o);
   boolean equivClause(XConstraint m, XConstraint o);
   boolean entailsClause(Type me, Type other);
   boolean entailsClause(XConstraint me, XConstraint other);
   
   /**
    * True if the two types are equal, ignoring their dep clauses.
    * @param other
    * @return
    */
   boolean typeBaseEquals(Type me, Type other);
   boolean isBoxedType(Type type);
   String getGetterName(Type type);
   boolean equalTypeParameters(List<Type> a, List<Type> b);
   
   X10ConstructorDef constructorDef(Position pos,
			 Ref<? extends ClassType> container,
			 Flags flags, Ref<? extends ClassType> returnType, List<Ref<? extends Type>> typeParams,
			 List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> excTypes);
   Type boxedTypeToPrimitiveType(Type presumedBoxedType);

   Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op);
   Type performUnaryOperation(Type t, Type l, Unary.Operator op);

   X10MethodInstance findMethod(StructType targetType, String name, List<Type> typeArgs, List<Type> argTypes, ClassDef currentClassDef) throws SemanticException;
   X10ConstructorInstance findConstructor(ClassType ct, List<Type> typeArgs, List<Type> argTypes, ClassDef currentClassDef) throws SemanticException;

   MacroType findTypeDef(ClassType targetType, String name, List<Type> typeArgs, List<Type> argTypes, ClassDef currentClassDef) throws SemanticException;
   List<MacroType> findTypeDefs(ClassType container, String name, ClassDef currClass) throws SemanticException;
   
   PathType findTypeProperty(ClassType container, String name, ClassDef currClass) throws SemanticException;
   
   Type TypeType();

   PrimitiveType UByte();
   PrimitiveType UShort();
   PrimitiveType UInt();
   PrimitiveType ULong();

   /** x10.lang.Box */
   Type Box();
   Type boxOf(Ref<? extends Type> base);
   boolean isBox(Type type);

   Type Ref();

   TypeDef BoxRefTypeDef();

   boolean isFunction(Type type);

   X10ClassDef closureInterfaceDef(int size, int size2);

ClosureType toFunction(Type targetType);

   
//   X10NamedType createBoxFromTemplate(X10ClassDef def);

}

