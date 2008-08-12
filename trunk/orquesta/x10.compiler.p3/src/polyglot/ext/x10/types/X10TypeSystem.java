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
import polyglot.ext.x10.types.X10TypeSystem_c.TypeDefMatcher;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.PrimitiveType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.VarDef;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.MethodMatcher;
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

	MethodMatcher MethodMatcher(Type container, String name, List<Type> typeArgs, List<Type> argTypes);
	ConstructorMatcher ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes);

	    /**
	     * Find a method.  We need to pass the class from which the method
	     * is being found because the method
	     * we find depends on whether the method is accessible from that
	     * class.
	     * We also check if the field is accessible from the context 'c'.
	     * @exception SemanticException if the method cannot be found or is
	     * inaccessible.
	     */
	X10MethodInstance findMethod(Type container,
		    TypeSystem_c.MethodMatcher matcher, ClassDef currClass) throws SemanticException;

	    /**
	     * Find a constructor.  We need to pass the class from which the constructor
	     * is being found because the constructor
	     * we find depends on whether the constructor is accessible from that
	     * class.
	     * @exception SemanticException if the constructor cannot be found or is
	     * inaccessible.
	     */
	   X10ConstructorInstance findConstructor(Type container,
		    TypeSystem_c.ConstructorMatcher matcher, ClassDef currClass) throws SemanticException;

	/**
	 * Create a <code>ClosureType</code> with the given signature.
	 */
	ClosureInstance createClosureInstance(Position pos, Ref<? extends ClosureDef> def);

	XTerm here();
	
	Type Place(); // needed for here, async (p) S, future (p) e, etc
//	Type Region();
	Type Point(); // needed for destructuring assignment
	Type Dist();
	Type Clock(); // needed for clocked loops
	Type Value(); // needed for value types
	@Deprecated 
	Type Runtime(); // used by asyncCodeInstance

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
	                                                                              Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams,
	                                                                              List<Ref<? extends Type>> argTypes,
	                                                                              List<LocalDef> formalNames,
	                                                                              Ref<XConstraint> whereClause, List<Ref<? extends Type>> throwTypes);

	X10MethodDef
	methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, String name,
		        List<Ref<? extends Type>> typeParams,
		        List<Ref<? extends Type>> argTypes,
		        List<LocalDef> formalNames,
		        Ref<XConstraint> whereClause,
		        List<Ref<? extends Type>> excTypes, Ref<XTerm> body);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array().
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	Type x10Array(Ref<? extends Type> type, boolean isValueType);

	/**
	 * Return the ClassType object for the x10.lang.Array interface.
	 */
	Type Array();
	
	/**
	 * Return the ClassType object for the x10.lang.Array interface.
	 */
	Type ValArray();
	
	/**
	 * Return the ClassType object for the x10.lang.Rail interface.
	 * @return
	 */
	Type Rail();
	
	/**
	 * Return the ClassType object for the x10.lang.ValRail interface.
	 * @return
	 */
	Type ValRail();
	
	boolean isRail(Type t);
	boolean isValRail(Type t);
	
	Type Rail(Type arg);
	Type ValRail(Type arg);
	
	Type Settable();
	Type Settable(Type domain, Type range);
	boolean isSettable(Type me);
	
	
	      boolean isX10Array(Type me) ;
	      boolean isBooleanArray(Type me);
	     boolean isCharArray(Type me) ;
	     boolean isByteArray(Type me) ;
	      boolean isShortArray(Type me) ;
	      boolean isIntArray(Type me) ;
	      boolean isLongArray(Type me) ;
	     boolean isFloatArray(Type me);
	      boolean isDoubleArray(Type me);
	      
	      boolean isClock(Type me);
	      boolean isPoint(Type me);
	      boolean isPlace(Type me);

	      boolean isValueType( Type me);

	
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
			 List<Ref<? extends Type>> argTypes, 
			 List<LocalDef> formalNames,
			 Ref<XConstraint> whereClause,
			 List<Ref<? extends Type>> excTypes);
   Type boxedTypeToPrimitiveType(Type presumedBoxedType);

   Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op);
   Type performUnaryOperation(Type t, Type l, Unary.Operator op);

   X10TypeSystem_c.TypeDefMatcher TypeDefMatcher(Type container, String name, List<Type> typeArgs, List<Type> argTypes);
   MacroType findTypeDef(Type t, X10TypeSystem_c.TypeDefMatcher matcher, ClassDef currentClassDef) throws SemanticException;
   List<MacroType> findTypeDefs(Type container, String name, ClassDef currClass) throws SemanticException;

   PathType findTypeProperty(Type t, String name, ClassDef currClass) throws SemanticException;

   Type UByte();
   Type UShort();
   Type UInt();
   Type ULong();

   /** x10.lang.Box */
   Type Box();
   Type boxOf(Ref<? extends Type> base);
   boolean isBox(Type type);

   Type Ref();

   TypeDef BoxRefTypeDef();

   boolean isFunction(Type type);

   X10ClassDef closureInterfaceDef(ClosureDef def);

   ClosureType toFunction(Type targetType);

   
//   X10NamedType createBoxFromTemplate(X10ClassDef def);

}

