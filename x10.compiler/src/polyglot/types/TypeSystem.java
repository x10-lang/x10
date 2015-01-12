/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.util.*;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Source;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.types.reflect.ClassFile;
import polyglot.types.reflect.ClassFileLazyClassInitializer;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.AsyncDef;
import x10.types.AtDef;
import x10.types.ClosureDef;
import x10.types.ClosureInstance;
import x10.types.ClosureType;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.ThisDef;
import x10.types.ThisInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassDef_c;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import polyglot.types.Context;
import x10.types.X10Def;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeEnv;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

/**
 * The <code>TypeSystem</code> defines the types of the language and
 * how they are related.
 */
public interface TypeSystem {
    public static final boolean SERIALIZE_MEMBERS_WITH_CONTAINER = false;
    public static final String CONSTRUCTOR_NAME = "this";

    /**
     * Initialize the type system with the compiler.  This method must be
     * called before any other type system method is called.
     *
     * @param resolver The resolver to use for loading types from class files
     *                 or other source files.
     */
    void initialize(TopLevelResolver resolver);

    /** Return the language extension this type system is for. */
    ExtensionInfo extensionInfo();

    /**
     * Returns the system resolver.  This resolver can load top-level classes
     * with fully qualified names from the class path and the source path.
     */
    SystemResolver systemResolver();

    /**
     * Return the type system's loaded resolver.
     * This resolver contains types loaded from class files.
     */
    TopLevelResolver loadedResolver();

    /** Create an import table for the source file.
     * @param sourceName Name of the source file to import into.  This is used
     * mainly for error messages and for debugging.
     * @param pkg The package of the source file in which to import.
     */
    ImportTable importTable(String sourceName, Ref<? extends Package> pkg);

    /** Create an import table for the source file.
     * @param pkg The package of the source file in which to import.
     */
    ImportTable importTable(Ref<? extends Package> pkg);

    /**
     * Return a list of the packages names that will be imported by
     * default.  A list of Strings is returned, not a list of Packages.
     */
    List<QName> defaultOnDemandImports();

    /**
     * Returns true if the package named <code>name</code> exists.
     */
    boolean packageExists(QName name);

    /** Return class def of a type, or null. */
    ClassDef classDefOf(Type t);

    /** Get the type with the following name.
     * @param name The name of the type to look for.
     * @exception SemanticException when type is not found.
     */
    Type forName(QName name) throws SemanticException;

    /** Get the  type with the following name.
     * @param name The name to create the type for.
     * @exception SemanticException when type is not found.
     * @deprecated Use {@link #forName(QName)}
     */
    Type typeForName(QName name) throws SemanticException;

    /** Create an initailizer instance.
     * @param pos Position of the initializer.
     * @param container Containing class of the initializer.
     * @param flags The initializer's flags.
     */
    InitializerDef initializerDef(Position pos, Ref<? extends ClassType> container,
                                            Flags flags);

    /** Create a constructor instance.
     * @param pos Position of the constructor.
     * @param container Containing class of the constructor.
     * @param flags The constructor's flags.
     * @param argTypes The constructor's formal parameter types.
     * @param throwsTypes The constructor's exception throw types.
     */
    ConstructorDef constructorDef(Position pos, Position errorPos, Ref<? extends ClassType> container,
                                            Flags flags, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwsTypes);

    /** Create a method instance.
     * @param pos Position of the method.
     * @param container Containing type of the method.
     * @param flags The method's flags.
     * @param returnType The method's return type.
     * @param name The method's name.
     * @param argTypes The method's formal parameter types.
     * @param throwsTypes The method's exception throw types.
     */
    MethodDef methodDef(Position pos, Position errorPos, Ref<? extends ContainerType> container,
                                  Flags flags, Ref<? extends Type> returnType, Name name,
                                  List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwsTypes);

    /** Create a default constructor instance.
     * @param pos Position of the constructor.
     * @param container Containing class of the constructor.
     */
    ConstructorDef defaultConstructor(Position pos, Position errorPos, Ref<? extends ClassType> container);

    /** Get an unknown class def. */
    X10ClassDef unknownClassDef();

    /** Get an unknown type. */
    UnknownType unknownType(Position pos);

    /** Get an unknown package. */
    UnknownPackage unknownPackage(Position pos);

    /** Get an unknown type qualifier. */
    UnknownQualifier unknownQualifier(Position pos);

    /**
     * Returns true iff child is not ancestor, but child descends from ancestor.     */
    boolean descendsFrom(ClassDef child, ClassDef ancestor);

    /**
     * Returns true iff a cast from fromType to toType is valid; in other
     * words, some non-null members of fromType are also members of toType.
     * @param context TODO
     */
    boolean isCastValid(Type fromType, Type toType, Context context);

    /**
     * Returns true iff an implicit cast from fromType to toType is valid;
     * in other words, every member of fromType is member of toType.
     * @param context TODO
     */
    boolean isImplicitCastValid(Type fromType, Type toType, Context context);

    /**
     * Returns true iff type1 and type2 represent the same type object.
     */
    boolean equals(TypeObject type1, TypeObject type2);
    void equals(Type type1, Type type2);
    void equals(Type type1, TypeObject type2);
    void equals(TypeObject type1, Type type2);

    /**
     * Returns true iff type1 and type2 are equivalent.
     * This is usually the same as equals(type1, type2), but may
     * differ in the presence of, say, type aliases.
     * @param context TODO
     */
    boolean typeEquals(Type type1, Type type2, Context context);

    /**
     * Returns true iff type1 and type2 are equivalent.
     * This is usually the same as equals(type1, type2), but may
     * differ in the presence of, say, type aliases.
     */
    boolean packageEquals(Package type1, Package type2);

    /**
     * Returns true if <code>value</code> can be implicitly cast to type
     * <code>t</code>.  This method should be removed.  It is kept for backward
     * compatibility.
     */
    boolean numericConversionValid(Type t, long value, Context context);

    /**
     * Returns true if <code>value</code> can be implicitly cast to
     * type <code>t</code>.
     * @param context TODO
     */
    boolean numericConversionValid(Type t, Object value, Context context);

    /**
     * Returns the least common ancestor of type1 and type2
     * @param context TODO
     * @exception SemanticException if the LCA does not exist
     */
    Type leastCommonAncestor(Type type1, Type type2, Context context) throws SemanticException;

    /**
     * Checks whether a class member can be accessed from <code>context</code>.
     */
    boolean isAccessible(MemberInstance<?> mi, Context context);

    /**
     * Checks whether a class can be accessed from Context context.
     */
    boolean classAccessible(ClassDef ct, Context context);

    /**
     * Checks whether a top-level or member class can be accessed from the
     * package pkg.  Returns false for local and anonymous classes.
     */
    boolean classAccessibleFromPackage(ClassDef ct, Package pkg);

    /**
     * Returns whether inner is enclosed within outer
     */
    boolean isEnclosed(ClassDef inner, ClassDef outer);

    /**
     * Returns whether an object of the inner class <code>inner</code> has an
     * enclosing instance of class <code>encl</code>.
     */
    boolean hasEnclosingInstance(ClassDef inner, ClassDef encl);

    ////
    // Various one-type predicates.
    ////

    /**
     * Returns true iff the type t can be coerced to a String in the given
     * Context. If a type can be coerced to a String then it can be
     * concatenated with Strings, e.g. if o is of type T, then the code snippet
     *         "" + o
     * would be allowed.
     */
    boolean canCoerceToString(Type t, Context c);

    /**
     * Returns true iff an object of type <code>type</code> may be thrown.
     */
    boolean isThrowable(Type type);

    /**
     * Returns a true iff the type or a supertype is in the list
     * returned by uncheckedExceptions().
     */
    boolean isUncheckedException(Type type);


    /**
     * Returns a collection of the Throwable types that need not be declared
     * in method and constructor signatures.
     */
    Collection<Type> uncheckedExceptions();

    /** Unary promotion for numeric types.
     * @exception SemanticException if the type cannot be promoted.
     */
    Type promote(Type t) throws SemanticException;

    /** Binary promotion for numeric types.
     * @exception SemanticException if the types cannot be promoted.
     */
    Type promote(Type t1, Type t2) throws SemanticException;

    ////
    // Functions for type membership.
    ////

    Matcher<Type> TypeMatcher(Name name);
    Matcher<Type> MemberTypeMatcher(Type container, Name name, Context context);

    /**
     * Find a member class.
     * @exception SemanticException if the class cannot be found or is
     * inaccessible.
     */
    Type findMemberType(Type container, Name name, Context context)
	throws SemanticException;

    /**
     * Returns the immediate supertype of type, or null if type has no
     * supertype.
     **/
    Type superClass(Type type);

    /**
     * Returns an immutable list of all the interface types which type
     * implements.
     **/
    List<Type> interfaces(Type type);

    ////
    // Functions for method testing.
    ////


    /**
     * Returns true iff <code>t</code> has the method <code>mi</code>.
     */
    boolean hasMethod(Type t, MethodInstance mi, Context context);

    /**
     * Returns true iff <code>t</code> has a method with name <code>name</code>
     * either defined in <code>t</code> or inherited into it.
     */
    boolean hasMethodNamed(Type t, Name name);

    /**
     * Returns true iff <code>m1</code> is the same method as <code>m2</code>.
     * @param context TODO
     */
    boolean isSameMethod(MethodInstance m1, MethodInstance m2, Context context);

    /**
     * Returns true iff <code>m1</code> is more specific than <code>m2</code>.
     */
    <T extends ProcedureDef> boolean moreSpecific(ProcedureInstance<T> p1, ProcedureInstance<T> p2, Context context);

    /**
     * Returns true iff <code>p</code> has exactly the formal arguments
     * <code>formalTypes</code>.
     * @param context TODO
     */
    boolean hasFormals(ProcedureInstance<? extends ProcedureDef> p, List<Type> formalTypes, Context context);

    ////
    // Functions which yield particular types.
    ////

    /**
     * The type of <code>null</code>.
     */
    NullType Null();

    /**
     * <code>void</code>
     */
    Type Void();

    /**
     * <code>boolean</code>
     */
    Type Boolean();

    /**
     * <code>char</code>
     */
    Type Char();

    /**
     * <code>byte</code>
     */
    Type Byte();

    /**
     * <code>short</code>
     */
    Type Short();

    /**
     * <code>int</code>
     */
    Type Int();

    /**
     * <code>long</code>
     */
    Type Long();

    /**
     * <code>float</code>
     */
    Type Float();

    /**
     * <code>double</code>
     */
    Type Double();

    /**
     * <code>java.lang.String</code>
     */
    Type String();

    /**
     * <code>java.lang.Class</code>
     */
    Type Class();

    /**
     * <code>java.lang.Throwable</code>
     */
    Type CheckedThrowable();

    /**
     * <code>java.lang.Error</code>
     */
    Type Error();

    /**
     * <code>java.lang.Exception</code>
     */
    Type Exception();

    /**
     * <code>java.lang.NullPointerException</code>
     */
    Type NullPointerException();

    /**
     * <code>java.lang.ClassCastException</code>
     */
    Type ClassCastException();

    /**
     * <code>java.lang.ArrayIndexOutOfBoundsException</code>
     */
    Type OutOfBoundsException();

    /**
     * <code>java.lang.ArrayStoreException</code>
     */
    Type ArrayStoreException();

    /**
     * <code>java.lang.ArithmeticException</code>
     */
    Type ArithmeticException();

    /**
     * Return an array of <code>type</code>
     */
    Type arrayOf(Ref<? extends Type> type);
    Type arrayOf(Type type);

    /**
     * Return an array of <code>type</code>
     */
    Type arrayOf(Position pos, Ref<? extends Type> type);
    Type arrayOf(Position pos, Type type);

    /**
     * Return a <code>dims</code>-array of <code>type</code>
     */
    Type arrayOf(Ref<? extends Type> type, int dims);
    Type arrayOf(Type type, int dims);

    /**
     * Return a <code>dims</code>-array of <code>type</code>
     */
    Type arrayOf(Position pos, Ref<? extends Type> type, int dims);
    Type arrayOf(Position pos, Type type, int dims);

    /**
     * Return a package by name.
     * Fail if the package does not exists.
     */
    Package packageForName(QName name) throws SemanticException;

    /**
     * Return a package by name with the given outer package.
     * Fail if the package does not exists.
     */
    Package packageForName(Ref<? extends Package> prefix, Name name) throws SemanticException;
    Package packageForName(Package prefix, Name name) throws SemanticException;

    /**
     * Return a package by name.
     */
    Package createPackage(QName name);

    /**
     * Return a package by name with the given outer package.
     */
    Package createPackage(Ref<? extends Package> prefix, Name name);
    Package createPackage(Package prefix, Name name);

    /** Get a resolver for looking up a type in a package. */
    Resolver packageContextResolver(Package pkg, ClassDef accessor, Context context);
    Resolver packageContextResolver(Package pkg);
    AccessControlResolver createPackageContextResolver(Package pkg);

    /** Get a resolver for looking up a type in a class context. */
    Resolver classContextResolver(Type ct, Context context);
    Resolver classContextResolver(Type ct);
    AccessControlResolver createClassContextResolver(Type ct);

    /**
     * Create a new empty class.
     */
    X10ClassDef createClassDef();

    InitializerInstance createInitializerInstance(Position pos, Ref<? extends InitializerDef> def);

    /**
     * Return the set of objects that should be serialized into the
     * type information for the given TypeObject.
     * Usually only the object itself should get encoded, and references
     * to other classes should just have their name written out.
     * If it makes sense for additional types to be fully encoded,
     * (i.e., they're necessary to correctly reconstruct the given object,
     * and the usual class resolvers can't otherwise find them) they
     * should be returned in the set in addition to object.
     */
    Set<TypeObject> getTypeEncoderRootSet(TypeObject o);

    /**
     * Get the transformed class name of a class.
     * This utility method returns the "mangled" name of the given class,
     * whereby all periods ('.') following the toplevel class name
     * are replaced with dollar signs ('$'). If any of the containing
     * classes is not a member class or a top level class, then null is
     * returned.
     */
    public QName getTransformedClassName(ClassDef ct);

    /** Get a place-holder for serializing a type object.
     * @param o The object to get the place-holder for.
     * @param roots The root objects for the serialization.  Place holders
     * are not created for these.
     */
    Object placeHolder(TypeObject o, Set<TypeObject> roots);

    /** Get a place-holder for serializing a type object.
     * @param o The object to get the place-holder for.
     */
    Object placeHolder(TypeObject o);

    /**
     * Translate a package.
     */
    String translatePackage(Resolver c, Package p);

    /**
     * Translate a primitive type.
     */
    String translatePrimitive(Resolver c, JavaPrimitiveType t);

    /**
     * Translate an array type.
     */
    String translateArray(Resolver c, JavaArrayType t);

    /**
     * Translate a top-level class type.
     */
    String translateClass(Resolver c, ClassType t);

    /**
     * Return the boxed version of <code>t</code>.
     */
    String wrapperTypeString(Type t);

    /**
     * Return true if <code>pi</code> can be called with
     * actual parameters of types <code>actualTypes</code>.
     * @param thisType TODO
     * @param context TODO
     */
    boolean callValid(ProcedureInstance<? extends ProcedureDef> mi, Type thisType, List<Type> argTypes, Context context);

    /**
     * Get the list of methods <code>mi</code> (potentially) overrides, in
     * order from this class (that is, including <code>this</code>) to super
     * classes.
     * @param context TODO
     */
    List<MethodInstance> overrides(MethodInstance mi, Context context);

    /**
     * Return true if <code>mi</code> can override <code>mj</code>.
     * @param context TODO
     */
    boolean canOverride(MethodInstance mi, MethodInstance mj, Context context);

    /**
     * Throw a SemanticException if <code>mi</code> cannot override
     * <code>mj</code>.
     * @param context TODO
     */
    void checkOverride(MethodInstance mi, MethodInstance mj, Context context) throws SemanticException;

    /**
     * Get the list of methods <code>mi</code> implements, in no
     * specified order.
     * @param context TODO
     */
    List<MethodInstance> implemented(MethodInstance mi, Context context);

    /**
     * Return the primitive with the given name.
     */
    Type primitiveForName(Name name) throws SemanticException;

    /** All possible <i>access</i> flags. */
    public abstract Flags legalAccessFlags();

    /** All flags allowed for a local variable. */
    public abstract Flags legalLocalFlags();

    /** All flags allowed for a field. */
    public abstract Flags legalFieldFlags();

    /** All flags allowed for a constructor. */
    public abstract Flags legalConstructorFlags();

    /** All flags allowed for an initializer block. */
    public abstract Flags legalInitializerFlags();

    /** All flags allowed for a method. */
    public abstract Flags legalMethodFlags();

    /** All flags allowed for an abstract method. */
    public abstract Flags legalAbstractMethodFlags();

    /** All flags allowed for an interface. */
    public abstract Flags legalInterfaceFlags();

    /** All flags allowed for a top-level class. */
    public abstract Flags legalTopLevelClassFlags();

    /** All flags allowed for a member class. */
    public abstract Flags legalMemberClassFlags();

    /** All flags allowed for a local class. */
    public abstract Flags legalLocalClassFlags();

    /**
     * Assert if the flags <code>f</code> are legal method flags.
     */
    void checkMethodFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal local variable flags.
     */
    void checkLocalFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal field flags.
     */
    void checkFieldFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal constructor flags.
     */
    void checkConstructorFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal initializer flags.
     */
    void checkInitializerFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal top-level class flags.
     */
    void checkTopLevelClassFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal member class flags.
     */
    void checkMemberClassFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal local class flags.
     */
    void checkLocalClassFlags(Flags f) throws SemanticException;

    /**
     * Assert if the flags <code>f</code> are legal access flags.
     */
    void checkAccessFlags(Flags f) throws SemanticException;

    /**
     * Assert that <code>t</code> has no cycles in the super type+nested class
     * graph starting at <code>t</code>.
     */
    void checkCycles(Type t) throws SemanticException;

    /**
     * Assert that <code>ct</code> implements all abstract methods that it
     * has to; that is, if it is a concrete class, then it must implement all
     * interfaces and abstract methods that it or its superclasses declare.
     * @param context TODO
     */
    public void checkClassConformance(ClassType ct, Context context) throws SemanticException;

    /**
     * Find a potentially suitable implementation of the method <code>mi</code>
     * in the class <code>ct</code> or a supertype thereof. Since we are
     * looking for implementations, <code>ct</code> cannot be an interface.
     * The first potentially satisfying method is returned, that is, the method
     * that is visible from <code>ct</code>, with the correct signature, in
     * the most precise class in the class hierarchy starting from
     * <code>ct</code>.
     * @param context TODO
     *
     * @return a suitable implementation of the method mi in the class
     *         <code>ct</code> or a supertype thereof, null if none exists.
     */
    public MethodInstance findImplementingMethod(ClassType ct, MethodInstance mi, Context context);

    /**
     * Given the JVM encoding of a set of flags, returns the Flags object
     * for that encoding.
     */
    public Flags flagsForBits(int bits);

    public Flags NoFlags();
    public Flags Public();
    public Flags Protected();
    public Flags Private();
    public Flags Static();
    public Flags Final();
    public Flags Transient();
    public Flags Native();
    public Flags Interface();
    public Flags Abstract();

    boolean isNumeric(Type t);
    boolean isSignedNumeric(Type t);
    boolean isUnsignedNumeric(Type t);
    boolean isIntOrLess(Type t);
    boolean isLongOrLess(Type t);
    boolean isByte(Type t);
    boolean isShort(Type t);
    boolean isChar(Type t);
    boolean isInt(Type t);
    boolean isLong(Type t);
    boolean isFloat(Type t);
    boolean isDouble(Type t);
    boolean isBoolean(Type t);
    boolean isException(Type t);
    boolean isVoid(Type t);

    public <S extends ProcedureDef, T extends ProcedureInstance<S>> Collection<T> findMostSpecificProcedures(List<T> acceptable, Matcher<T> matcher, Context context) throws SemanticException;

    /**
     * Populates the list acceptable with those MethodInstances which are
     * Applicable and Accessible as defined by JLS 15.11.2.1
     */
    public List<MethodInstance> findAcceptableMethods(Type container, MethodMatcher matcher) throws SemanticException;

    /**
     * Populates the list acceptable with those MethodInstances which are
     * Applicable and Accessible as defined by JLS 15.11.2.1
     */
    public List<ConstructorInstance> findAcceptableConstructors(Type container, ConstructorMatcher matcher) throws SemanticException;

    List<Type> abstractSuperInterfaces(Type t);

    public Name DUMMY_PACKAGE_CLASS_NAME = Name.make("_");

    boolean isSubtype(Type t1, Type t2, Context context);

    // empty context
    boolean isSubtype(Type t1, Type t2);
    /**
     * Add an annotation to a type object, optionally replacing existing
     * annotations that are subtypes of annoType.
     */
    void addAnnotation(X10Def o, Type annoType, boolean replace);

    Type AnnotatedType(Position pos, Type baseType, List<Type> annotations);

    MethodInstance findImplementingMethod(ClassType ct, MethodInstance mi, boolean includeAbstract, Context context);

    Type boxOf(Position p, Ref<? extends Type> t);

    Type futureOf(Position p, Ref<? extends Type> t);

    MethodMatcher MethodMatcher(Type container, Name name, List<Type> argTypes, Context context);
    MethodMatcher MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context);
    MethodMatcher MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context, boolean isDumbMatcher);

    ConstructorMatcher ConstructorMatcher(Type container, List<Type> argTypes, Context context);
    ConstructorMatcher ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes, Context context);

    /**
     * Returns the field named 'name' defined on 'type'.
     * @exception SemanticException if the field cannot be found or is
     * inaccessible.
     */
    X10FieldInstance findField(Type container, Type receiver, Name name, Context context) throws SemanticException;
    X10FieldInstance findField(Type container, Type receiver, Name name, Context context, boolean receiverInContext) throws SemanticException;

    /**
     * Find matching fields.
     *
     * @exception SemanticException if no matching field can be found.
     */
    Set<FieldInstance> findFields(Type container, Type receiver, Name name, Context context);

    /**
     * Find a method. We need to pass the class from which the method is being
     * found because the method we find depends on whether the method is
     * accessible from that class. We also check if the field is accessible from
     * the context 'c'.
     *
     * @exception SemanticException
     *                    if the method cannot be found or is inaccessible.
     */
    MethodInstance findMethod(Type container, MethodMatcher matcher) throws SemanticException;

    /**
     * Find matching methods.
     *
     * @exception SemanticException if no matching method can be found.
     */
    Collection<MethodInstance> findMethods(Type container, MethodMatcher matcher) throws SemanticException;

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
     * Find matching constructors.
     *
     * @exception SemanticException if no matching constructor can be found.
     */
    Collection<X10ConstructorInstance> findConstructors(Type container, ConstructorMatcher matcher) throws SemanticException;

    X10ClassDef createClassDef(Source fromSource);

    X10ParsedClassType createClassType(Position pos, Position errorPos, Ref<? extends X10ClassDef> def);
    X10ConstructorInstance createConstructorInstance(Position pos, Position errorPos, Ref<? extends ConstructorDef> def);
    MethodInstance createMethodInstance(Position pos, Position errorPos, Ref<? extends MethodDef> def);
    X10FieldInstance createFieldInstance(Position pos, Ref<? extends FieldDef> def);
    X10LocalInstance createLocalInstance(Position pos, Ref<? extends LocalDef> def);

    /**
     * Create a <code>ClosureType</code> with the given signature.
     */
    ClosureInstance createClosureInstance(Position pos, Position errorPos, Ref<? extends ClosureDef> def);

    ThisInstance createThisInstance(Position pos, Ref<? extends ThisDef> def);
    
    /**
     * Returns an immutable list of all the interfaces
     * which the type implements excluding itself.
     * This is different from {@link #Interface()} in that this method
     * traverses the class hierarchy to collect all implemented interfaces
     * instead of shallowly returning just the interfaces directly implemented
     * by the type.
     */
    List<X10ClassType> allImplementedInterfaces(X10ClassType type);
    List<X10ClassType> allImplementedInterfaces(X10ClassType c, boolean checkSuperClasses);
    
    X10ClassType Place(); // needed for here, async (p) S, future (p) e, etc

    X10ClassType Point(); // needed for destructuring assignment

    X10ClassType Dist();

    X10ClassType Clock(); // needed for clocked loops

    X10ClassType FinishState();

    X10ClassType Runtime(); // used by asyncCodeInstance
    
    X10ClassType Unsafe();

    /**
     * <code>x10.lang.FailedDynamicCheckException</code>
     */
    X10ClassType FailedDynamicCheckException();

    // types used in WS codegen
    X10ClassType Frame();
    X10ClassType FinishFrame();
    X10ClassType RootFinish();
    X10ClassType MainFrame();
    X10ClassType RemoteFinish();
    X10ClassType AtFrame();
    X10ClassType RegularFrame();
    X10ClassType AsyncFrame();
    X10ClassType CollectingFinish();
    X10ClassType TryFrame();
    X10ClassType Worker();
    X10ClassType Abort();
    
    // annotation types used in codegen
    X10ClassType StackAllocate();
    X10ClassType StackAllocateUninitialized();
    X10ClassType Inline();
    X10ClassType InlineOnly();
    X10ClassType NoInline();
    X10ClassType Ephemeral();
    X10ClassType Header();
    X10ClassType Uninitialized();
    X10ClassType SuppressTransientError();
    X10ClassType TransientInitExpr();
    X10ClassType Embed();
    X10ClassType RemoteInvocation();
    X10ClassType Synthetic();

    //Type Value();

    X10ClassType GlobalRef();
    X10ClassType Any();

    X10ClassType NativeType();
    X10ClassType NativeRep();
    X10ClassType NativeClass();
    X10ClassType CompileTimeConstant();

    X10ClassType Endpoint();
    X10ClassType RuntimeProfile();

    XLit FALSE();

    XLit TRUE();

    XLit NEG_ONE();

    XLit ZERO();

    XLit ONE();

    XLit TWO();

    XLit THREE();

    XLit NULL();


    AsyncDef asyncCodeInstance(Position pos, ThisDef thisDef,
            List<ParameterType> typeParameters,
            Ref<? extends CodeInstance<?>> methodContainer,
            Ref<? extends ClassType> typeContainer, boolean isStatic);

    AtDef atCodeInstance(Position pos, ThisDef thisDef,
            List<ParameterType> typeParameters,
            Ref<? extends CodeInstance<?>> methodContainer,
            Ref<? extends ClassType> typeContainer, boolean isStatic);

    ThisDef thisDef(Position pos, Ref<? extends ClassType> type);
    /**
     * To be called to generate qType.this, where this:baseType.
     * @param pos
     * @param qType
     * @param baseType
     * @return
     */
    ThisDef thisDef(Position pos, Ref<? extends ClassType> qType, Ref<? extends ClassType> baseType);

    /**
     * Create a closure instance.
     * @param returnType
     *                The closure's return type.
     * @param argTypes
     *                The closure's formal parameter types.
     * @param thisDef TODO
     * @param typeGuard TODO
     * @param pos
     *                Position of the closure.
     * @param container
     *                Containing type of the closure.
     * @param excTypes
     *                The closure's exception throw types.
     */
    ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer,
            Ref<? extends CodeInstance<?>> methodContainer,
                    Ref<? extends Type> returnType,
                    List<Ref<? extends Type>> argTypes,
                    ThisDef thisDef,
                    List<LocalDef> formalNames,
                    Ref<CConstraint> guard,

                    Ref<? extends Type> offerType);


    X10ConstructorDef constructorDef(Position pos, Position errorPos, Ref<? extends ClassType> container,
            Flags flags, List<Ref<? extends Type>> argTypes, 
            List<Ref<? extends Type>> throwsTypes, Ref<? extends Type> offerType);

    X10ConstructorDef constructorDef(Position pos, Position errorPos, Ref<? extends ContainerType> container, Flags flags, Ref<? extends Type> returnType,
            List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwsTypes, ThisDef thisDef, List<LocalDef> formalNames, Ref<CConstraint> guard,
            Ref<TypeConstraint> typeGuard, Ref<? extends Type> offerType);

    X10MethodDef methodDef(Position pos, Position errorPos, Ref<? extends ContainerType> container,
            Flags flags, Ref<? extends Type> returnType, Name name,
            List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwsTypes,  Ref<? extends Type> offerType);

    X10MethodDef methodDef(Position pos, Position errorPos, Ref<? extends ContainerType> container, Flags flags, Ref<? extends Type> returnType, Name name,
            List<ParameterType> typeParams, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwsTypes, ThisDef thisDef, List<LocalDef> formalNames,
            Ref<CConstraint> guard, Ref<TypeConstraint> typeGuard, Ref<? extends Type> offerType, Ref<XTerm> body);

    X10FieldDef fieldDef(Position pos, Ref<? extends ContainerType> container, Flags flags, Ref<? extends Type> type, Name name);

    X10FieldDef fieldDef(Position pos, Ref<? extends ContainerType> container, Flags flags, Ref<? extends Type> type, Name name,
            ThisDef thisDef);

    X10LocalDef localDef(Position pos, Flags flags, Ref<? extends Type> type, Name name);

    /**
     * Return the ClassType object for the x10.regionarray.Array class.
     */
    X10ClassType RegionArray();

    /**
     * Return the ClassType object for the x10.lang.Rail class.
     */
    X10ClassType Rail();

    /**
     * Return the ClassType object for the x10.array.Array class.
     */
    X10ClassType Array();

    /**
     * Return the ClassType object for the x10.array.DistArray class.
     */
    X10ClassType DistArray();

    /**
     * Return the ClassType object for the x10.regionarray.DistArray class.
     */
    X10ClassType RegionDistArray();

    /**
     * Return the ClassType object for the x10.lang.Runtime.Mortal interface.
     */
    X10ClassType Mortal();

    boolean isRegionArray(Type t);

    boolean isRail(Type t);
    
    boolean isArray(Type t);
    
    boolean isDistArray(Type t);

    public boolean isRegionArrayOf(Type t, Type p);

    public boolean isRailOf(Type t, Type p);

    X10ClassType RegionArray(Type arg);

    X10ClassType Rail(Type arg);

    X10ClassType Array(Type arg);

    X10ClassType Settable();

    X10ClassType Settable(Type domain, Type range);

    X10ClassType Iterable();
    X10ClassType Iterable(Type index);

    X10ClassType Unserializable();
    X10ClassType CustomSerialization();
    X10ClassType Serializer();
    X10ClassType Deserializer();

    boolean isAny(Type me);

    boolean isStruct(Type me);

    boolean isString(Type me);

    boolean isRuntime(Type me);

    boolean isClock(Type me);

    boolean isPoint(Type me);

    boolean isPlace(Type me);

    boolean isStructType(Type me);

    boolean isObjectType(Type me, Context context);

    boolean isUByte(Type t);
    boolean isUShort(Type t);
    boolean isUInt(Type t);
    boolean isULong(Type t);

    boolean hasSameClassDef(Type t1, Type t2);

    X10TypeEnv env(Context c);

    /**
     * Is a type constrained (i.e. its depClause is != null) If me is a
     * nullable, then the basetype is checked.
     *
     * @param me
     *                Type to check
     * @return true if type has a depClause.
     */
    public boolean isTypeConstrained(Type me);

    XTypeTranslator xtypeTranslator();

   // boolean entailsClause(Type me, Type other, Context context);
   // boolean entailsClause(CConstraint me, CConstraint other, Context context, Type selfType);

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

    Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op);

    Type performUnaryOperation(Type t, Type l, Unary.Operator op);

    MacroType findTypeDef(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) throws SemanticException;

    List<MacroType> findTypeDefs(Type container, Name name, ClassDef currClass) throws SemanticException;

    X10ClassType UByte();

    X10ClassType UShort();

    X10ClassType UInt();

    X10ClassType ULong();

    /** x10.lang.Box *
    X10ClassType Box();

    Type boxOf(Ref<? extends Type> base);

    boolean isBox(Type type);
*/
    boolean isFunctionType(Type type);



  //  List<ClosureType> getFunctionSupertypes(Type type, X10Context context);

    boolean isInterfaceType(Type toType);

    FunctionType functionType(Position position, Ref<? extends Type> returnType,
            List<ParameterType> typeParameters,
            List<Ref<? extends Type>> formalTypes, List<LocalDef> formalNames,
            Ref<CConstraint> guard
            //Ref<TypeConstraint> typeGuard,
    );

    ClosureType closureType(ClosureDef cd);


    Type expandMacros(Type arg);

//    /** Run fromType thorugh a coercion function to toType, if possible, returning the return type of the coercion function, or return null. */
//    Type coerceType(Type fromType, Type toType);

    boolean clausesConsistent(CConstraint c1, CConstraint c2, Context context);

    /** Return true if the constraint is consistent. */
    boolean consistent(CConstraint c);
    boolean consistent(TypeConstraint c, Context context);

    /** Return true if constraints in the type are all consistent.
     * @param context TODO*/
    boolean consistent(Type t, Context context);

    boolean isObjectOrInterfaceType(Type t, Context context);

    boolean isInterfaceType(Type t, Context context);
    
    public boolean isHandOptimizedInterface(Type t);

    boolean isParameterType(Type toType);

    X10ClassType Region();
    
    X10ClassType IterationSpace();

    X10ClassType IntRange();

    X10ClassType Iterator(Type formalType);

    boolean numericConversionValid(Type toType, Type fromType, Object constantValue, Context context);

    public Long size(Type t);

    boolean isX10RegionArray(Type me);

    boolean isX10RegionDistArray(Type me);
    
    boolean isIntRange(Type me);

    boolean isLongRange(Type me);

    Context emptyContext();
    boolean isExactlyFunctionType(Type t);

    Name homeName();

    LazyRef<Type> lazyAny();

    ClassType load(String name);

    boolean isRegion(Type me);
    
    boolean isIterationSpace(Type me);

    boolean isDistribution(Type me);

    boolean isComparable(Type me);

    boolean isIterable(Type me);

    boolean isIterator(Type me);
    boolean isReducible(Type me);
    X10ClassType Reducible();

    boolean isUnknown(Type t);
    boolean hasUnknown(Type t);
    X10LocalInstance createFakeLocal(Name name, SemanticException error);
    X10ClassType createFakeClass(QName fullName, SemanticException error);
    
    Context createContext();
    
    X10ConstructorInstance createFakeConstructor(ClassType container, Flags flags, List<Type> argTypes, 
                                                 SemanticException error);
    X10ConstructorInstance createFakeConstructor(QName containerName, Flags flags, List<Type> typeArgs, 
                                                 List<Type> argTypes, SemanticException error);
    X10FieldInstance createFakeField(ClassType container, Flags flags, Name name, SemanticException error);
    MethodInstance createFakeMethod(Name name, List<Type> typeArgs, List<Type> argTypes, SemanticException error);
    MethodInstance createFakeMethod(ClassType container, Flags flags, Name name, List<Type> typeArgs, List<Type> argTypes, 
                                    SemanticException error);
    List<LocalDef> dummyLocalDefs(List<Ref<? extends Type>> types);
    List<MethodInstance> methods(ContainerType t, Name name, List<Type> typeParams, List<LocalInstance> formalNames, 
                                 XVar thisVar, XVar placeTerm, Context context);
    boolean equalsStruct(Type a, Type b);
    X10ClassType AtomicInteger();
    boolean isRemoteArray(Type t);
    Boolean structHaszero(X10ClassDef z);
    Map<X10ClassDef_c, Boolean> structHaszero();
    X10ClassType AtomicBoolean();

    /**
     * Constructs a new ClassFileLazyClassInitializer for the given class file.
     */
    ClassFileLazyClassInitializer classFileLazyClassInitializer(ClassFile clazz);

    X10ClassType JavaInterop();
    X10ClassType JavaArray();
    boolean isJavaArray(Type me);
    boolean isPrimitiveJavaArray(Type type);
    //boolean isJavaThrowable(Type me);

    public <T extends ProcedureDef> boolean throwsSubset(ProcedureInstance<T> p1, ProcedureInstance<T> p2);

	X10ClassType System();

	Type Profile();

	boolean typeIsJLIterable(Type classType);

	public boolean isGlobalRail(Type typ);
}
