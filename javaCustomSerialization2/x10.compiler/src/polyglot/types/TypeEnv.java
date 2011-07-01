package polyglot.types;

import java.util.List;

import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.util.Copy;
import polyglot.types.Package;
import x10.types.MethodInstance;

public interface TypeEnv  {

//    Context context();
//    TypeSystem typeSystem();


    void checkClassConformance(ClassType ct) throws SemanticException;

    /**
     * Returns true iff the type t can be coerced to a String in the given
     * Context. If a type can be coerced to a String then it can be concatenated
     * with Strings, e.g. if o is of type T, then the code snippet "" + o would
     * be allowed.
     */
    public boolean canCoerceToString(Type t);

    /**
     * Returns true iff type1 and type2 are equivalent.
     */
    public boolean typeEquals(Type type1, Type type2);

    /**
     * Returns true iff type1 and type2 are equivalent.
     */
    public boolean packageEquals(Package pkg1, Package pkg2);

    /**
     * Requires: all type arguments are canonical. ToType is not a NullType.
     * 
     * Returns true iff a cast from fromType to toType is valid; in other words,
     * some non-null members of fromType are also members of toType.
     **/
    public boolean isCastValid(Type fromType, Type toType);

    /**
     * Requires: all type arguments are canonical.
     * 
     * Returns true iff an implicit cast from fromType to toType is valid; in
     * other words, every member of fromType is member of toType.
     * 
     * Returns true iff child and ancestor are non-primitive types, and a
     * variable of type child may be legally assigned to a variable of type
     * ancestor.
     * 
     */
    public boolean isImplicitCastValid(Type fromType, Type toType);

    /**
     * Returns true if <code>value</code> can be implicitly cast to Primitive
     * type <code>t</code>.
     */
    public boolean numericConversionValid(Type t, Object value);

    // //
    // Functions for one-type checking and resolution.
    // //

    /**
     * Checks whether the member mi can be accessed from Context "context".
     */
    public boolean isAccessible(MemberInstance<?> mi);

    /** True if the class targetClass accessible from the context. */
    public boolean classAccessible(ClassDef targetClass);

    /** True if the class targetClass accessible from the package pkg. */
    public boolean classAccessibleFromPackage(ClassDef targetClass, Package pkg);

    /**
     * Return true if a member (in an accessible container) or a top-level class
     * with access flags <code>flags</code> in package <code>pkg1</code> is
     * accessible from package <code>pkg2</code>.
     */
    public boolean accessibleFromPackage(Flags flags, Package pkg1, Package pkg2);
    
    public boolean isSubtype(Type t1, Type t2);

    /**
     * Returns whether method 1 is <i>more specific</i> than method 2, where
     * <i>more specific</i> is defined as JLS 15.11.2.2
     */
    public <T extends ProcedureDef> boolean moreSpecific(ProcedureInstance<T> p1, ProcedureInstance<T> p2);

    /**
     * Requires: all type arguments are canonical. Returns the least common
     * ancestor of Type1 and Type2
     **/
    public Type leastCommonAncestor(Type type1, Type type2);

    /** Return true if t overrides mi */
    public boolean hasMethod(Type t, MethodInstance mi);

    /** Return true if t overrides mi */
    public boolean hasFormals(ProcedureInstance<? extends ProcedureDef> pi, List<Type> formalTypes);

    public List<MethodInstance> overrides(MethodInstance mi);
    public List<MethodInstance> implemented(MethodInstance mi);
    public List<MethodInstance> implemented(MethodInstance mi, ContainerType st);

    public boolean canOverride(MethodInstance mi, MethodInstance mj);

    public void checkOverride(MethodInstance mi, MethodInstance mj) throws SemanticException;

    public void checkOverride(MethodInstance mi, MethodInstance mj, boolean allowCovariantReturn) throws SemanticException;

    /**
     * Returns true iff <m1> is the same method as <m2>
     */
    public boolean isSameMethod(MethodInstance m1, MethodInstance m2);

    public boolean callValid(ProcedureInstance<? extends ProcedureDef> prototype, Type thisType, List<Type> argTypes);

    public Type findMemberType(Type container, Name name) throws SemanticException;

    /**
     * Populates the list acceptable with those MethodInstances which are
     * Applicable and Accessible as defined by JLS 15.11.2.1
     * 
     * @param container
     *            TODO
     * @param matcher
     *            TODO
     */
    public List<ConstructorInstance> findAcceptableConstructors(Type container, ConstructorMatcher matcher) throws SemanticException;
}
