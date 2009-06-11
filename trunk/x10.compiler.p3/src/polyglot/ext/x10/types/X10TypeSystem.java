/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
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
import x10.constraint.XRoot;
import x10.constraint.XTerm;

/**
 * Parts of this code are taken from the pao extension in the polyglot
 * framework.
 * 
 * @author Christoph von Praun
 * @author vj
 */
public interface X10TypeSystem extends TypeSystem {
	public Name DUMMY_PACKAGE_CLASS_NAME = Name.make("_");
	
    boolean isSubtype(Type t1, Type t2, Context context);
    
    /**
     * Add an annotation to a type object, optionally replacing existing
     * annotations that are subtypes of annoType.
     */
    void addAnnotation(X10Def o, Type annoType, boolean replace);
    
    AnnotatedType AnnotatedType(Position pos, Type baseType, List<Type> annotations);

    Type boxOf(Position p, Ref<? extends Type> t);

    Type futureOf(Position p, Ref<? extends Type> t);

    MethodMatcher MethodMatcher(Type container, Name name, List<Type> argTypes, Context context);
    MethodMatcher MethodMatcher(Type container, Name name, List<Type> typeArgs,  List<Type> argTypes, Context context);

    ConstructorMatcher ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes, Context context);

    /**
     * Find a method. We need to pass the class from which the method is being
     * found because the method we find depends on whether the method is
     * accessible from that class. We also check if the field is accessible from
     * the context 'c'.
     * 
     * @exception SemanticException
     *                    if the method cannot be found or is inaccessible.
     */
    X10MethodInstance findMethod(Type container, MethodMatcher matcher) throws SemanticException;

    /**
     * Find a constructor. We need to pass the class from which the constructor
     * is being found because the constructor we find depends on whether the
     * constructor is accessible from that class.
     * 
     * @exception SemanticException
     *                    if the constructor cannot be found or is inaccessible.
     */
    X10ConstructorInstance findConstructor(Type container, TypeSystem_c.ConstructorMatcher matcher) throws SemanticException;

    /**
     * Create a <code>ClosureType</code> with the given signature.
     */
    ClosureInstance createClosureInstance(Position pos, Ref<? extends ClosureDef> def);

    XTerm here();

    Type Place(); // needed for here, async (p) S, future (p) e, etc
    // Type Region();

    Type Point(); // needed for destructuring assignment

    Type Dist();

    Type Clock(); // needed for clocked loops

    Type Runtime(); // used by asyncCodeInstance

    Type Value();

    Type Ref();

    XLit FALSE();

    XLit TRUE();

    XLit NEG_ONE();

    XLit ZERO();

    XLit ONE();

    XLit TWO();

    XLit THREE();

    XLit NULL();

    CodeDef asyncCodeInstance(boolean isStatic);

    /**
     * Create a closure instance.
     * @param returnType
     *                The closure's return type.
     * @param argTypes
     *                The closure's formal parameter types.
     * @param thisVar TODO
     * @param typeGuard TODO
     * @param pos
     *                Position of the closure.
     * @param container
     *                Containing type of the closure.
     * @param excTypes
     *                The closure's exception throw types.
     */
    ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer, Ref<? extends Type> returnType,
	    List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, XRoot thisVar, List<LocalDef> formalNames,
	    Ref<XConstraint> guard, Ref<TypeConstraint> typeGuard, List<Ref<? extends Type>> throwTypes);

    X10MethodDef methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, Name name,
	    List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, XRoot thisVar, List<LocalDef> formalNames,
	    Ref<XConstraint> guard, Ref<TypeConstraint> typeGuard, List<Ref<? extends Type>> excTypes, Ref<XTerm> body);

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
     * 
     * @return
     */
    Type Rail();

    /**
     * Return the ClassType object for the x10.lang.ValRail interface.
     * 
     * @return
     */
    Type ValRail();

    boolean isRail(Type t);

    boolean isValRail(Type t);

    Type Rail(Type arg);

    Type ValRail(Type arg);

    Type Settable();

    Type Settable(Type domain, Type range);

    Type Iterable();
    Type Iterable(Type index);

    boolean isSettable(Type me);

    boolean isClock(Type me);

    boolean isPoint(Type me);

    boolean isPlace(Type me);

    boolean isValueType(Type me, X10Context context);

    boolean isReferenceType(Type me, X10Context context);
    
    boolean isUByte(Type t);
    boolean isUShort(Type t);
    boolean isUInt(Type t);
    boolean isULong(Type t);

    // RMF 7/11/2006 - Added so that the parser can create a canonical type node
    // for "primitive types", which otherwise will cause disambiguation to fail.
    //
    // Technically, there are no primitive types in X10, but the compiler can't
    // quite treat types like "int" as ordinary object types - there are no
    // source
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
    public boolean isPrimitiveTypeName(Name name);

    boolean hasSameClassDef(Type t1, Type t2);

    /**
     * Is a type constrained (i.e. its depClause is != null) If me is a
     * nullable, then the basetype is checked.
     * 
     * @param me
     *                Type to check
     * @return true if type has a depClause.
     */
    public boolean isTypeConstrained(Type me);

    VarDef createSelf(X10Type t);

    XTypeTranslator xtypeTranslator();

    boolean entailsClause(Type me, Type other, X10Context context);
    boolean entailsClause(XConstraint me, XConstraint other, X10Context context, Type selfType);

    /**
     * True if the two types are equal, ignoring their dep clauses.
     * @param other
     * @param context TODO
     * 
     * @return
     */

    boolean typeBaseEquals(Type me, Type other, Context context);
    /**
     * True if the two types are equal, ignoring their dep clauses and the dep clauses of their type arguments recursively.
     * 
     * @param other
     * @return
     */
    boolean typeDeepBaseEquals(Type me, Type other, Context context);

    boolean equalTypeParameters(List<Type> a, List<Type> b, Context context);

    X10ConstructorDef constructorDef(Position pos, Ref<? extends ClassType> container, Flags flags, Ref<? extends ClassType> returnType,
	    List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, XRoot thisVar, List<LocalDef> formalNames,
	    Ref<XConstraint> guard, Ref<TypeConstraint> typeGuard, List<Ref<? extends Type>> excTypes);

    Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op);

    Type performUnaryOperation(Type t, Type l, Unary.Operator op);

    X10TypeSystem_c.TypeDefMatcher TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context);

    MacroType findTypeDef(Type t, X10TypeSystem_c.TypeDefMatcher matcher, Context context) throws SemanticException;

    List<MacroType> findTypeDefs(Type container, Name name, ClassDef currClass) throws SemanticException;

    Type UByte();

    Type UShort();

    Type UInt();

    Type ULong();

    /** x10.lang.Box */
    Type Box();

    Type boxOf(Ref<? extends Type> base);

    boolean isBox(Type type);

    boolean isFunction(Type type, X10Context context);

    X10ClassDef closureAnonymousClassDef(ClosureDef def);

    List<ClosureType> getFunctionSupertypes(Type type, X10Context context);

    boolean isInterfaceType(Type toType);

    ClosureType closureType(Position position, Ref<? extends Type> typeRef, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> formalTypes,
            List<LocalDef> formalNames, Ref<XConstraint> guard, Ref<TypeConstraint> typeGuard, List<Ref<? extends Type>> throwTypes);

    
    Type expandMacros(Type arg);

    /** Return true if fromType and toType are primitive types and there is a conversion from one to the other. */
    boolean isPrimitiveConversionValid(Type fromType, Type toType, Context context);

//    /** Run fromType thorugh a coercion function to toType, if possible, returning the return type of the coercion function, or return null. */
//    Type coerceType(Type fromType, Type toType);

    boolean clausesConsistent(XConstraint c1, XConstraint c2, Context context);

    /** Return true if the constraint is consistent. */
    boolean consistent(XConstraint c);
    boolean consistent(TypeConstraint c, X10Context context);

    /** Return true if constraints in the type are all consistent. 
     * @param context TODO*/
    boolean consistent(Type t, X10Context context);

    boolean isReferenceOrInterfaceType(Type t, X10Context context);

    boolean isSubtypeWithValueInterfaces(Type t1, Type t2, Context context);

    boolean isParameterType(Type toType);

    boolean isImplicitNumericCastValid(Type fromType, Type toType, Context context);

    Type Region();

    Type Iterator(Type formalType);
    
    X10FieldDef fieldDef(Position pos,
            Ref<? extends StructType> container, Flags flags,
            Ref<? extends Type> type, Name name, XRoot thisVar);

}
