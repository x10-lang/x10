/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Source;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.types.reflect.ClassFile;
import polyglot.types.reflect.ClassFileLazyClassInitializer;
import polyglot.util.Position;

/**
 * The <code>TypeSystem</code> defines the types of the language and
 * how they are related.
 */
public interface TypeSystem {
    public static final boolean SERIALIZE_MEMBERS_WITH_CONTAINER = false;

    /**
     * Initialize the type system with the compiler.  This method must be
     * called before any other type system method is called.
     *
     * @param resolver The resolver to use for loading types from class files
     *                 or other source files.
     * @param extInfo The ExtensionInfo the TypeSystem is being created for.
     */
    void initialize(TopLevelResolver resolver, ExtensionInfo extInfo)
                    throws SemanticException;

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

    /**
     * Constructs a new ClassFileLazyClassInitializer for the given class file.
     */
    ClassFileLazyClassInitializer classFileLazyClassInitializer(ClassFile clazz);

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
    
    /** Get the named type object with the following name.
     * @param name The name of the type object to look for.
     * @exception SemanticException when object is not found.    
     */
    Named forName(QName name) throws SemanticException;
    
    /** Get the  type with the following name.
     * @param name The name to create the type for.
     * @exception SemanticException when type is not found.    
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
     * @param excTypes The constructor's exception throw types.
     */
    ConstructorDef constructorDef(Position pos, Ref<? extends ClassType> container,
                                            Flags flags, List<Ref<? extends Type>> argTypes,
                                            List<Ref<? extends Type>> excTypes);

    /** Create a method instance.
     * @param pos Position of the method.
     * @param container Containing type of the method.
     * @param flags The method's flags.
     * @param returnType The method's return type.
     * @param name The method's name.
     * @param argTypes The method's formal parameter types.
     * @param excTypes The method's exception throw types.
     */
    MethodDef methodDef(Position pos, Ref<? extends StructType> container,
                                  Flags flags, Ref<? extends Type> returnType, Name name,
                                  List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> excTypes);

    /** Create a field instance.
     * @param pos Position of the field.
     * @param container Containing type of the field.
     * @param flags The field's flags.
     * @param type The field's type.
     * @param name The field's name.
     */
    FieldDef fieldDef(Position pos, Ref<? extends StructType> container,
                                Flags flags, Ref<? extends Type> type, Name name);

    /** Create a local variable instance.
     * @param pos Position of the local variable.
     * @param flags The local variable's flags.
     * @param type The local variable's type.
     * @param name The local variable's name.
     */
    LocalDef localDef(Position pos, Flags flags, Ref<? extends Type> type,
	    Name name);

    /** Create a default constructor instance.
     * @param pos Position of the constructor.
     * @param container Containing class of the constructor. 
     */
    ConstructorDef defaultConstructor(Position pos, Ref<? extends ClassType> container);

    /** Get an unknown class def. */
    ClassDef unknownClassDef();

    /** Get an unknown type. */
    UnknownType unknownType(Position pos);

    /** Get an unknown package. */
    UnknownPackage unknownPackage(Position pos);

    /** Get an unknown type qualifier. */
    UnknownQualifier unknownQualifier(Position pos);

    /**
     * Returns true iff child descends from ancestor or child == ancestor.
     * This is equivalent to:
     * <pre>
     *    descendsFrom(child, ancestor) || equals(child, ancestor)
     * </pre>
     * @param context TODO
     */
    boolean isSubtype(Type child, Type ancestor, Context context);

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
    boolean isAccessible(MemberInstance<? extends MemberDef> mi, Context context);

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

    /**
     * Returns the field named 'name' defined on 'type'.
     * @exception SemanticException if the field cannot be found or is
     * inaccessible.
     */
    FieldInstance findField(Type container, TypeSystem_c.FieldMatcher matcher)
	throws SemanticException;
    
    Matcher<Named> TypeMatcher(Name name);
    Matcher<Named> MemberTypeMatcher(Type container, Name name, Context context);
    TypeSystem_c.FieldMatcher FieldMatcher(Type container, Name name, Context context);
    TypeSystem_c.MethodMatcher MethodMatcher(Type container, Name name, List<Type> argTypes, Context context);
    TypeSystem_c.ConstructorMatcher ConstructorMatcher(Type container, List<Type> argTypes, Context context);

    /**
     * Find a method.  We need to pass the class from which the method
     * is being found because the method
     * we find depends on whether the method is accessible from that
     * class.
     * We also check if the field is accessible from the context 'c'.
     * @exception SemanticException if the method cannot be found or is
     * inaccessible.
     */
    MethodInstance findMethod(Type container,
	    MethodMatcher matcher) throws SemanticException;

    /**
     * Find a constructor.  We need to pass the class from which the constructor
     * is being found because the constructor
     * we find depends on whether the constructor is accessible from that
     * class.
     * @exception SemanticException if the constructor cannot be found or is
     * inaccessible.
     */
    ConstructorInstance findConstructor(Type container,
	    TypeSystem_c.ConstructorMatcher matcher) throws SemanticException;

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
     * Returns true iff <code>m1</code> throws fewer exceptions than
     * <code>m2</code>.
     */
    <T extends ProcedureDef> boolean throwsSubset(ProcedureInstance<T> p1, ProcedureInstance<T> p2);

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
     * <code>java.lang.Object</code>
     */
    Type Object();

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
    Type Throwable();

    /**
     * <code>java.lang.Error</code>
     */
    Type Error();

    /**
     * <code>java.lang.Exception</code>
     */
    Type Exception();

    /**
     * <code>java.lang.RuntimeException</code>
     */
    Type RuntimeException();

    /**
     * <code>java.lang.Cloneable</code>
     */
    Type Cloneable();

    /**
     * <code>java.io.Serializable</code>
     */
    Type Serializable();

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

    /**
     * Create a new context object for looking up variables, types, etc.
     */
    Context emptyContext();

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
    ClassDef createClassDef();

    /**
     * Create a new empty class.
     */
    ClassDef createClassDef(Source fromSource);

    ParsedClassType createClassType(Position pos, Ref<? extends ClassDef> def);
    ConstructorInstance createConstructorInstance(Position pos, Ref<? extends ConstructorDef> def);
    MethodInstance createMethodInstance(Position pos, Ref<? extends MethodDef> def);
    FieldInstance createFieldInstance(Position pos, Ref<? extends FieldDef> def);
    LocalInstance createLocalInstance(Position pos, Ref<? extends LocalDef> def);
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
    Set<Object> getTypeEncoderRootSet(TypeObject o);

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
    Object placeHolder(TypeObject o, Set roots);

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
    String translatePrimitive(Resolver c, PrimitiveType t);

    /**
     * Translate an array type.
     */
    String translateArray(Resolver c, ArrayType t);

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
     * Find a potentially suitable implementation of the method <code>mi</code>
     * in the class <code>ct</code> or a supertype thereof, or an abstract method
     * that when overridden will implement the method.  Since we are
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
    public MethodInstance findImplementingMethod(ClassType ct, MethodInstance mi, boolean includeAbstract, Context context);

    /**
     * Given the JVM encoding of a set of flags, returns the Flags object
     * for that encoding.
     */
    public Flags flagsForBits(int bits); 

    /**
     * Create a new unique Flags object.
     * @param name the name of the flag
     * @param print_after print the new flag after these flags
     */
    public Flags createNewFlag(String name, Flags print_after);

    public Flags NoFlags();
    public Flags Public();
    public Flags Protected();
    public Flags Private();
    public Flags Static();
    public Flags Final();
    public Flags Synchronized();
    public Flags Transient();
    public Flags Native();
    public Flags Interface();
    public Flags Abstract();
    public Flags Volatile();
    public Flags StrictFP();

    boolean isNumeric(Type t);
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
}
