/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.types;

import java.lang.reflect.Modifier;
import java.util.*;

import polyglot.frontend.*;
import polyglot.main.Report;
import polyglot.types.reflect.ClassFile;
import polyglot.types.reflect.ClassFileLazyClassInitializer;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeBuilder;
import x10.ast.Binary;
import x10.ast.Id;
import x10.ast.NodeFactory;
import x10.ast.NodeFactory_c;
import x10.ast.TypeNode;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.parser.X10ParsedName;
import x10.types.XTypeTranslator.XTypeLit_c;
import x10.types.constraints.CConstraint;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.matcher.X10ConstructorMatcher;
import x10.types.matcher.X10FieldMatcher;
import x10.types.matcher.X10MemberTypeMatcher;
import x10.types.matcher.X10MethodMatcher;
import x10.types.matcher.X10TypeMatcher;
import x10.util.ClosureSynthesizer;

/**
 * TypeSystem_c
 *
 * Overview:
 *    A TypeSystem_c is a universe of types, including all Java types.
 **/
public class TypeSystem_c implements TypeSystem
{
    private static volatile int counter = 0;

    protected SystemResolver systemResolver;
    protected TopLevelResolver loadedResolver;
    protected Map<String, Flags> flagsForName;
    protected ExtensionInfo extInfo;

    private Throwable creator;
    private int creationTime;
    public TypeSystem_c() {
        creator = new Throwable().fillInStackTrace();
        creationTime = counter++;
        if (Report.should_report("TypeSystem", 1))
            Report.report(1, "Creating " + getClass() + " at " + creationTime);
    }

    /**
     * Initializes the type system and its internal constants (which depend on
     * the resolver).
     */
    private void SUPERinitialize(TopLevelResolver loadedResolver, ExtensionInfo extInfo)
    throws SemanticException {

	if (Report.should_report(Report.types, 1))
	    Report.report(1, "Initializing " + getClass().getName());

	this.extInfo = extInfo;

	// The loaded class resolver.  This resolver automatically loads types
	// from class files and from source files not mentioned on the command
	// line.
	this.loadedResolver = loadedResolver;

	// The system class resolver. The class resolver contains a map from
	// fully qualified names to instances of Named. A pass over a
	// compilation unit looks up classes first in its
	// import table and then in the system resolver.
	this.systemResolver = new SystemResolver(loadedResolver, extInfo);

	initEnums();
	initFlags();
	initTypes();
    }

    protected void initEnums() {
	// Ensure the enums in the type system are initialized and interned
	// before any deserialization occurs.

	// Just force the static initializers of ClassType and PrimitiveType
	// to run.
	Object o;
	o = ClassDef.TOP_LEVEL;
    }

    protected void initTypes() throws SemanticException {
	// FIXME: don't do this when rewriting a type system!

	// Prime the resolver cache so that we don't need to check
	// later if these are loaded.

	// We cache the most commonly used ones in fields.
	/* DISABLED CACHING OF COMMON CLASSES; CAUSES PROBLEMS IF
           COMPILING CORE CLASSES (e.g. java.lang package).
           TODO: Longer term fix. Maybe a flag to tell if we are compiling
                 core classes? XXX
        Object();
        Class();
        String();
        Throwable();

        systemResolver.find("java.lang.Error");
        systemResolver.find("java.lang.Exception");
        systemResolver.find("java.lang.RuntimeException");
        systemResolver.find("java.lang.Cloneable");
        systemResolver.find("java.io.Serializable");
        systemResolver.find("java.lang.NullPointerException");
        systemResolver.find("java.lang.ClassCastException");
        systemResolver.find("java.lang.ArrayIndexOutOfBoundsException");
        systemResolver.find("java.lang.ArrayStoreException");
        systemResolver.find("java.lang.ArithmeticException");
	 */
    }

    /** Return the language extension this type system is for. */
    public ExtensionInfo extensionInfo() {
	return extInfo;
    }

    public SystemResolver systemResolver() {
	return systemResolver;
    }

    /**
     * Return the system resolver.  This used to return a different resolver.
     * enclosed in the system resolver.
     * @deprecated
     */
    public CachingResolver parsedResolver() {
	return systemResolver;
    }

    public TopLevelResolver loadedResolver() {
	return loadedResolver;
    }

    public ClassFileLazyClassInitializer classFileLazyClassInitializer(ClassFile clazz) {
	return new ClassFileLazyClassInitializer(clazz, this);
    }

    public ImportTable importTable(String sourceName, Ref<? extends Package> pkg) {
	assert_(pkg);
	return new ImportTable(this, pkg, sourceName);
    }

    public ImportTable importTable(Ref<? extends Package> pkg) {
	assert_(pkg);
	return new ImportTable(this, pkg);
    }

    /**
     * Returns true if the package named <code>name</code> exists.
     */
    public boolean packageExists(QName name) {
	return systemResolver.packageExists(name);
    }

    protected void assert_(Collection<?> l) {
	for (Iterator<?> i = l.iterator(); i.hasNext(); ) {
	    Object o = i.next();
	    if (o instanceof TypeObject) {
		assert_((TypeObject) o);
	    }
	    else if (o instanceof Ref<?>) {
		assert_((Ref<?>) o);
	    }
	}
    }

    protected void assert_(Ref<?> ref) { }

    protected void assert_(TypeObject o) {
	if (o != null && o.typeSystem() != this) {
            TypeSystem_c ots = (TypeSystem_c) o.typeSystem();
            System.err.print("we are " + this + "(" + creationTime + "): ");
            this.creator.printStackTrace(System.err);
            System.err.print(" but " + o + " ("+o.getClass()+")" + " is from " + ots + "(" + ots.creationTime + "): ");
            ots.creator.printStackTrace(System.err);
            throw new InternalCompilerError("we are " + this + " but " + o + " ("+o.getClass()+")" +
                                            " is from " + ots);
	}
    }

    public String wrapperTypeString(Type t) {
	assert_(t);

	if (t.isBoolean())
	    return "java.lang.Boolean";
	if (t.isChar())
	    return "java.lang.Character";
	if (t.isByte())
	    return "java.lang.Byte";
	if (t.isShort())
	    return "java.lang.Short";
	if (t.isInt())
	    return "java.lang.Integer";
	if (t.isLong())
	    return "java.lang.Long";
	if (t.isFloat())
	    return "java.lang.Float";
	if (t.isDouble())
	    return "java.lang.Double";
	if (t.isVoid())
	    return "java.lang.Void";

	throw new InternalCompilerError("Unrecognized primitive type " + t);
    }

    private Context SUPERemptyContext() {
	return new Context_c(this);
    }
    
    /** @deprecated */
    public Resolver packageContextResolver(Resolver cr, Package p) {
	return packageContextResolver(p);
    }

    public AccessControlResolver createPackageContextResolver(Package p) {
	assert_(p);
	return new PackageContextResolver(this, p);
    }

    public Resolver packageContextResolver(Package p, ClassDef accessor, Context context) {
	if (accessor == null) {
	    return p.resolver();
	}
	else {
	    return new AccessControlWrapperResolver(createPackageContextResolver(p), context);
	}
    }

    public Resolver packageContextResolver(Package p) {
	assert_(p);
	return packageContextResolver(p, null, emptyContext());
    }

    public Resolver classContextResolver(final Type type, Context context) {
	assert_(type);
	if (context == null) {
	    if (type instanceof ClassType) {
		return ((ClassType) type).resolver();
	    }
	    return new Resolver() {
		public Named find(Matcher<Named> matcher) throws SemanticException {
		    throw new NoClassException(matcher.name().toString(), type);
		}
	    };
	}
	else {
	    return new AccessControlWrapperResolver(createClassContextResolver(type), context);
	}
    }

    public Resolver classContextResolver(Type type) {
	return classContextResolver(type, emptyContext());
    }

    public AccessControlResolver createClassContextResolver(Type type) {
	assert_(type);
	return new ClassContextResolver(this, type);
    }

    private FieldDef SUPERfieldDef(Position pos,
	    Ref<? extends StructType> container, Flags flags,
	    Ref<? extends Type> type, Name name) {
	assert_(container);
	assert_(type);
	return new FieldDef_c(this, pos, container, flags, type, name);
    }

    private LocalDef SUPERlocalDef(Position pos,
	    Flags flags, Ref<? extends Type> type, Name name) {
	assert_(type);
	return new LocalDef_c(this, pos, flags, type, name);
    }

    private ConstructorDef SUPERdefaultConstructor(Position pos,
	    Ref<? extends ClassType> container) {
	assert_(container);

	// access for the default constructor is determined by the 
	// access of the containing class. See the JLS, 2nd Ed., 8.8.7.
	Flags access = Flags.NONE;
	Flags flags = container.get().flags();
	if (flags.isPrivate()) {
	    access = access.Private();
	}
	if (flags.isProtected()) {
	    access = access.Protected();            
	}
	if (flags.isPublic()) {
	    access = access.Public();            
	}
	return constructorDef(pos, container,
	                      access, Collections.<Ref<? extends Type>>emptyList()
	                      );
    }

    private ConstructorDef SUPERconstructorDef(Position pos,
	    Ref<? extends ClassType> container,
	    Flags flags, List<Ref<? extends Type>> argTypes
	    ) {
	assert_(container);
	assert_(argTypes);

	return new ConstructorDef_c(this, pos, container, flags,
	                            argTypes);
    }

    private InitializerDef SUPERinitializerDef(Position pos,
	    Ref<? extends ClassType> container,
	    Flags flags) {
	assert_(container);
	return new InitializerDef_c(this, pos, container, flags);
    }

    private MethodDef SUPERmethodDef(Position pos,
	    Ref<? extends StructType> container, Flags flags,
	    Ref<? extends Type> returnType, Name name,
	    List<Ref<? extends Type>> argTypes) {

	assert_(container);
	assert_(returnType);
	assert_(argTypes);
	return new MethodDef_c(this, pos, container, flags,
	                       returnType, name, argTypes);
    }
    
    private ClassDef SUPERclassDefOf(Type t) {
	if (t instanceof ClassType)
	    return ((ClassType) t).def();
	return null;
    }

    /**
     * Returns true iff child and ancestor are object types and child descends
     * from or equals ancestor.
     **/
    private boolean SUPERdescendsFrom(ClassDef child, ClassDef ancestor) {
	if (child == ancestor)
	    return true;

	ClassDef o = classDefOf(Object());

	if (ancestor == o)
	    return true;

	if (child == o)
	    return false;

	Type sup = Types.get(child.superType());

	if (sup != null && classDefOf(sup) != null) {
	    if (descendsFrom(classDefOf(sup), ancestor)) {
		return true;
	    }
	}

	// Next check interfaces.
	for (Ref<? extends Type> parentType : child.interfaces()) {
	    Type t = Types.get(parentType);
	    if (t != null && classDefOf(t) != null) {
		if (descendsFrom(classDefOf(t), ancestor)) {
		    return true;
		}
	    }
	}

	return false;
    }

    /**
     * Requires: all type arguments are canonical.  ToType is not a NullType.
     *
     * Returns true iff a cast from fromType to toType is valid; in other
     * words, some non-null members of fromType are also members of toType.
     **/
    public boolean isCastValid(Type fromType, Type toType, Context context) {
	assert_(fromType);
	assert_(toType);
	return env(context).isCastValid(fromType, toType);
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
    public boolean isImplicitCastValid(Type fromType, Type toType, Context context) {
	assert_(fromType);
	assert_(toType);
	return env(context).isImplicitCastValid(fromType, toType);
    }

    /**
     * Returns true iff type1 and type2 represent the same type object.
     */
    public boolean equals(TypeObject type1, TypeObject type2) {
	assert_(type1);
	assert_(type2);
	if (type1 == type2) return true;
	if (type1 == null || type2 == null) return false;
	return type1.equalsImpl(type2);
    }

    public final void equals(Type type1, Type type2) {
	assert false;
    }

    public final void equals(Type type1, TypeObject type2) {
	assert false;
    }

    public final void equals(TypeObject type1, Type type2) {
	assert false;
    }

    /**
     * Returns true iff type1 and type2 are equivalent.
     */
    public boolean typeEquals(Type type1, Type type2, Context context) {
	assert_(type1);
	assert_(type2);
	return env(context).typeEquals(type1, type2);
    }

    /**
     * Returns true iff type1 and type2 are equivalent.
     */
    public boolean packageEquals(Package type1, Package type2) {
	assert_(type1);
	assert_(type2);
	return env(emptyContext()).packageEquals(type1, type2);
    }

    /**
     * Returns true if <code>value</code> can be implicitly cast to Primitive
     * type <code>t</code>.
     */
    private boolean SUPERnumericConversionValid(Type t, Object value, Context context) {
	assert_(t);
	return env(context).numericConversionValid(t, value);
    }

    /**
     * Returns true if <code>value</code> can be implicitly cast to Primitive
     * type <code>t</code>.  This method should be removed.  It is kept for
     * backward compatibility.
     */
    public boolean numericConversionValid(Type t, long value, Context context) {
	assert_(t);
	return env(context).numericConversionValid(t, value);
    }

    ////
    // Functions for one-type checking and resolution.
    ////

    /**
     * Checks whether the member mi can be accessed from Context "context".
     */
    public boolean isAccessible(MemberInstance<?> mi, Context context) {
	assert_(mi);
	return env(context).isAccessible(mi);
    }

    /** True if the class targetClass accessible from the context. */
    public boolean classAccessible(ClassDef targetClass, Context context) {
	assert_(targetClass);
	return env(context).classAccessible(targetClass);
    }

    /** True if the class targetClass accessible from the package pkg. */
    public boolean classAccessibleFromPackage(ClassDef targetClass, Package pkg) {
	assert_(targetClass);
	return env(emptyContext()).classAccessibleFromPackage(targetClass, pkg);
    }

    /**
     * Return true if a member (in an accessible container) or a
     * top-level class with access flags <code>flags</code>
     * in package <code>pkg1</code> is accessible from package
     * <code>pkg2</code>.
     */
    protected boolean accessibleFromPackage(Flags flags, Package pkg1, Package pkg2) {
	return env(emptyContext()).accessibleFromPackage(flags, pkg1, pkg2);
    }

    public boolean isEnclosed(ClassDef inner, ClassDef outer) {
	return inner.asType().isEnclosed(outer.asType());
    }

    public boolean hasEnclosingInstance(ClassDef inner, ClassDef encl) {
	return inner.asType().hasEnclosingInstance(encl.asType());
    }

    public void checkCycles(Type goal) throws SemanticException {
	checkCycles(goal, goal);
    }

    protected void checkCycles(Type curr, Type goal) throws SemanticException {
	assert_(curr);
	assert_(goal);

	if (curr == null) {
	    return;
	}

	if (curr instanceof ObjectType) {
	    ObjectType ot = (ObjectType) curr;
	    Type superType = null;

	    if (ot.superClass() != null) {
		superType = ot.superClass();
	    }

	    if (goal == superType) {
		throw new SemanticException("Circular inheritance involving " + goal,curr.position());
	    }

	    checkCycles(superType, goal);

	    for (Type si : ot.interfaces()) {
		if (si == goal) {
		    throw new SemanticException("Circular inheritance involving " + goal,curr.position());
		}

		checkCycles(si, goal);
	    }    
	}

	if (curr.isClass()) {
	    checkCycles(curr.toClass().outer(), goal);
	}
    }

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
    public boolean canCoerceToString(Type t, Context c) {
	return env(c).canCoerceToString(t);
    }

    private boolean SUPERisNumeric(Type t) {
	return t.isLongOrLess() || t.isFloat() || t.isDouble();
    }

    private boolean SUPERisIntOrLess(Type t) {
	return t.isByte() || t.isShort() || t.isChar() || t.isInt();
    }

    private boolean SUPERisLongOrLess(Type t) {
	return t.isByte() || t.isShort() || t.isChar() || t.isInt() || t.isLong();
    }

    private boolean SUPERisVoid(Type t) {
	Context context = emptyContext();
	return typeEquals(t, Void(), context);
    }

    public boolean isBoolean(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Boolean(), context);
    }

    public boolean isChar(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Char(), context);
    }

    public boolean isByte(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Byte(), context);
    }

    public boolean isShort(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Short(), context);
    }

    public boolean isInt(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Int(), context);
    }

    public boolean isLong(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Long(), context);
    }

    public boolean isFloat(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Float(), context);
    }

    public boolean isDouble(Type t) {
	Context context = emptyContext();
	return isSubtype(t, Double(), context);
    }

    /**
     * Returns true iff an object of type <type> may be thrown.
     **/
    private boolean SUPERisThrowable(Type type) {
	assert_(type);
	Context context = emptyContext();
	return isSubtype(type, Throwable(), context);
    }

    /**
     * Returns a true iff the type or a supertype is in the list
     * returned by uncheckedExceptions().
     */
    private boolean SUPERisUncheckedException(Type type) {
	assert_(type);
	Context context = emptyContext();

	if (type.isThrowable()) {
	    for (Type t : uncheckedExceptions()) {
		if (isSubtype(type, t, context)) {
		    return true;
		}
	    }
	}

	return false;
    }

    /**
     * Returns a list of the Throwable types that need not be declared
     * in method and constructor signatures.
     */
    private Collection<Type> SUPERuncheckedExceptions() {
	List<Type> l = new ArrayList<Type>(2);
	l.add(Error());
	l.add(RuntimeException());
	return l;
    }

    private boolean SUPERisSubtype(Type t1, Type t2, Context context) {
	assert_(t1);
	assert_(t2);
	return env(context).isSubtype(t1, t2);
    }

    ////
    // Functions for type membership.
    ////

    /**
     * Returns the FieldInstance for the field <code>name</code> defined
     * in type <code>container</code> or a supertype, and visible from
     * <code>currClass</code>.  If no such field is found, a SemanticException
     * is thrown.  <code>currClass</code> may be null.
     **/
    private FieldInstance SUPERfindField(Type container, TypeSystem_c.FieldMatcher matcher) throws SemanticException {

	Context context = matcher.context();
	
	Collection<FieldInstance> fields = findFields(container, matcher);

	if (fields.size() == 0) {
	    throw new NoMemberException(NoMemberException.FIELD,
	                                "Field " + matcher.signature() +
	                                " not found in type \"" +
	                                container + "\".");
	}

	Iterator<FieldInstance> i = fields.iterator();
	FieldInstance fi = i.next();

	if (i.hasNext()) {
	    FieldInstance fi2 = i.next();

	    throw new SemanticException("Field " + matcher.signature() +
	                                " is ambiguous; it is defined in both " +
	                                fi.container() + " and " +
	                                fi2.container() + "."); 
	}

	if (context != null && ! isAccessible(fi, context)) {
	    throw new SemanticException("Cannot access " + fi + ".");
	}

	return fi;
    }

    /**
     * Returns a set of fields named <code>name</code> defined
     * in type <code>container</code> or a supertype.  The list
     * returned may be empty.
     */
    private Set<FieldInstance> SUPERfindFields(Type container, TypeSystem_c.FieldMatcher matcher) {
	Name name = matcher.name();

	Context context = matcher.context();
	assert_(container);

	if (container == null) {
	    throw new InternalCompilerError("Cannot access field \"" + name +
	    "\" within a null container type.");
	}

	if (container instanceof StructType) {
	    FieldInstance fi = ((StructType) container).fieldNamed(name);
	    if (fi != null) {
		try {
		    fi = matcher.instantiate(fi);
		    if (fi != null)
			return Collections.singleton(fi);
		}
		catch (SemanticException e) {
		}
		return Collections.<FieldInstance>emptySet();
	    }
	}

	Set<FieldInstance> fields = new HashSet<FieldInstance>();

	if (container instanceof ObjectType) {
	    ObjectType ot = (ObjectType) container;
	    if (ot.superClass() != null && ot.superClass() instanceof StructType) {
		Set<FieldInstance> superFields = findFields((StructType) ot.superClass(), matcher);
		fields.addAll(superFields);
	    }

	    for (Type it : ot.interfaces()) {
		if (it instanceof StructType) {
		    Set<FieldInstance> superFields = findFields((StructType) it, matcher);
		    fields.addAll(superFields);
		}
	    }
	}

	return fields;
    }
    
    

    private Type SUPERfindMemberType(Type container, Name name,
	    Context context) throws SemanticException
	    {
	return env(context).findMemberType(container, name);
	    }
    
    /**
     * Returns the list of methods with the given name defined or inherited
     * into container, checking if the methods are accessible from the
     * body of currClass
     */
    private boolean SUPERhasMethodNamed(Type container, Name name) {
	assert_(container);

	if (container == null) {
	    throw new InternalCompilerError("Cannot access method \"" + name +
	    "\" within a null container type.");
	}

	if (container instanceof StructType) {
	    if (! ((StructType) container).methodsNamed(name).isEmpty()) {
		return true;
	    }
	}

	if (container instanceof ObjectType) {
	    ObjectType ot = (ObjectType) container;
	    if (ot.superClass() != null && ot.superClass() instanceof StructType) {
		if (hasMethodNamed((StructType) ot.superClass(), name)) {
		    return true;
		}
	    }

	    for (Type it : ot.interfaces()) {
		if (it instanceof StructType) {
		    if (hasMethodNamed((StructType) it, name)) {
			return true;
		    }
		}
	    }
	}

	return false;
    }

    public static class ConstructorMatcher implements Matcher<ConstructorInstance> {
	protected Type container;
	protected List<Type> argTypes;
	protected Context context;

	protected ConstructorMatcher(Type receiverType, List<Type> argTypes, Context context) {
	    super();
	    this.container = receiverType;
	    this.argTypes = argTypes;
	    this.context = context;
	}
	
	public Context context() {
	    return context;
	}
	public Type container() {
		return container;
	}

	public Name name() {
	    return Name.make("this");
	}

	public String signature() {
	    return container + argumentString();
	}

	public ConstructorInstance instantiate(ConstructorInstance ci) throws SemanticException {
	    TypeSystem ts = ci.typeSystem();
	    if (ci.formalTypes().size() != argTypes.size()) {
		return null;
	    }
	    Context context = ts.emptyContext();
	    if (! ts.callValid(ci, container, argTypes, context)) {
		throw new SemanticException("Cannot invoke " + ci + " on " + signature() + ".");
	    }
	    return ci;
	}

	public String argumentString() {
	    return  "(" + CollectionUtil.listToString(argTypes) + ")";
	}

	public String toString() {
	    return signature();
	}

	public Object key() {
	    return null;
	}
    }

    public static class MethodMatcher implements Copy, Matcher<MethodInstance> {
	protected Type container;
	protected Name name;
	protected List<Type> argTypes;
	protected Context context;

	protected MethodMatcher(Type container, Name name, List<Type> argTypes, Context context) {
	    super();
	    this.container = container;
	    this.name = name;
	    this.argTypes = argTypes;
	    this.context = context;
	}

	public MethodMatcher container(Type container) {
	    MethodMatcher n = copy();
	    n.container = container;
	    return n;
	}
	public Type container() {
		return container;
	}

	public MethodMatcher copy() {
	    try {
		return (MethodMatcher) super.clone();
	    }
	    catch (CloneNotSupportedException e) {
		throw new InternalCompilerError(e);
	    }
	}

	public String signature() {
	    return name + argumentString();
	}

	public Name name() {
	    return name;
	}

	public MethodInstance instantiate(MethodInstance mi) throws SemanticException {
	    if (! mi.name().equals(name)) {
		return null;
	    }
	    if (mi.formalTypes().size() != argTypes.size()) {
		return null;
	    }
	    TypeSystem ts = mi.typeSystem();
	    if (! ts.callValid(mi, container, argTypes, context)) {
		throw new SemanticException("Cannot invoke " + mi + " on " + signature() + ".");
	    }
	    return mi;
	}

	public String argumentString() {
	    return  "(" + CollectionUtil.listToString(argTypes) + ")";
	}

	public String toString() {
	    return signature();
	}

	public Object key() {
	    return null;
	}

	public Context context() {
	    return context;
	}
    }

    public static class FieldMatcher implements Copy, Matcher<FieldInstance> {
	protected Type container;
	protected Name name;
	protected Context context;
	
	protected FieldMatcher(Type container, Name name, Context context) {
	    super();
	    this.container = container;
	    this.name = name;
	    this.context = context;
	}
	
	public Context context() {
	    return context;
	}

	public Type container() {
		return container;
	}
	public FieldMatcher container(Type container) {
	    FieldMatcher n = copy();
	    n.container = container;
	    return n;
	}

	public FieldMatcher copy() {
	    try {
		return (FieldMatcher) super.clone();
	    }
	    catch (CloneNotSupportedException e) {
		throw new InternalCompilerError(e);
	    }
	}

	public String signature() {
	    return name.toString();
	}

	public Name name() {
	    return name;
	}

	public FieldInstance instantiate(FieldInstance mi) throws SemanticException {
	    if (! mi.name().equals(name)) {
		return null;
	    }
	    return mi;
	}

	public String toString() {
	    return signature();
	}

	public Object key() {
	    return null;
	}
    }

    public static class MemberTypeMatcher implements Matcher<Named> {
	protected Type container;
	protected Name name;
	protected Context context;

	protected MemberTypeMatcher(Type container, Name name, Context context) {
	    super();
	    this.container = container;
	    this.name = name;
	    this.context = context;
	}

	public Type container() {
		return container;
	}
	public String signature() {
	    return name.toString();
	}

	public Name name() {
	    return name;
	}

	public Named instantiate(Named t) throws SemanticException {
	    if (! t.name().equals(name)) {
		return null;
	    }
	    return t;
	}

	public String toString() {
	    return signature();
	}

	public Object key() {
	    return name;
	}
    }

    private Matcher<Named> SUPERMemberTypeMatcher(Type container, Name name, Context context) {
	return new MemberTypeMatcher(container, name, context);
    }

    private Matcher<Named> SUPERTypeMatcher(Name name) {
	return new TypeMatcher(name);
    }

    public static class TypeMatcher implements Matcher<Named> {
	protected Name name;

	protected TypeMatcher(Name name) {
	    super();
	    this.name = name;
	}

	public String signature() {
	    return name.toString();
	}

	public Name name() {
	    return name;
	}

	
	public Named instantiate(Named t) throws SemanticException {
	    if (! t.name().equals(name)) {
		return null;
	    }
	    return t;
	}

	public String toString() {
	    return signature();
	}

	public Object key() {
	    return name;
	}
    }

    private MethodMatcher SUPERMethodMatcher(Type container, Name name, List<Type> argTypes, Context context) {
	return new MethodMatcher(container, name, argTypes, context);
    }

    private ConstructorMatcher SUPERConstructorMatcher(Type container, List<Type> argTypes, Context context) {
	return new ConstructorMatcher(container, argTypes, context);
    }

    private FieldMatcher SUPERFieldMatcher(Type container, Name name, Context context) {
	return new FieldMatcher(container, name, context);
    }

    private MethodInstance SUPERfindMethod(Type container, MethodMatcher matcher) 
    throws SemanticException {

	assert_(container);
	
	Context context = matcher.context();

	List<MethodInstance> acceptable = findAcceptableMethods(container, matcher);

	if (acceptable.size() == 0) {
	    throw new NoMemberException(NoMemberException.METHOD,
	                                "No valid method call found for call in given type."
	    		+ "\n\t Call: " + matcher.signature() 
	    		+ "\n\t Type: " + container);
	                             
	}

	Collection<MethodInstance> maximal =
	    findMostSpecificProcedures(container, acceptable, (Matcher<MethodInstance>) matcher, context);

	if (maximal.size() > 1) {
	    StringBuffer sb = new StringBuffer();
	    for (Iterator<MethodInstance> i = maximal.iterator(); i.hasNext();) {
		MethodInstance ma = (MethodInstance) i.next();
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
	                                " is ambiguous, multiple methods match: "
	                                + sb.toString());
	}

	MethodInstance mi = maximal.iterator().next();
	return mi;
    }

    private ConstructorInstance SUPERfindConstructor(Type container, ConstructorMatcher matcher)
    throws SemanticException {

	assert_(container);
	Context context = matcher.context();

	List<ConstructorInstance> acceptable = findAcceptableConstructors(container, matcher);

	if (acceptable.size() == 0) {
	    throw new NoMemberException(NoMemberException.CONSTRUCTOR,
	                                "No valid constructor found for " + matcher.signature() + ").");
	}

	Collection<ConstructorInstance> maximal = findMostSpecificProcedures(acceptable, matcher, context);

	if (maximal.size() > 1) {
	    throw new NoMemberException(NoMemberException.CONSTRUCTOR,
	                                "Reference to " + container + " is ambiguous, multiple " +
	                                "constructors match: " + maximal);
	}

	ConstructorInstance ci = maximal.iterator().next();
	return ci;
    }

    public <S extends ProcedureDef, T extends ProcedureInstance<S>> Collection<T>
    findMostSpecificProcedures(List<T> acceptable, Matcher<T> matcher, Context context)
    throws SemanticException {
    	return findMostSpecificProcedures(null, acceptable, matcher, context);
    }
    public <S extends ProcedureDef, T extends ProcedureInstance<S>> Collection<T>
    findMostSpecificProcedures(Type container, List<T> acceptable, Matcher<T> matcher, Context context)
    throws SemanticException {
	
	// now, use JLS 15.11.2.2
	// First sort from most- to least-specific.
	Comparator<T> msc = mostSpecificComparator(container, matcher, context);
	ArrayList<T> acceptable2 = new ArrayList<T>(acceptable); // make into array list to sort

	Collections.<T>sort(acceptable, msc);

	List<T> maximal = new ArrayList<T>(acceptable.size());

	Iterator<T> i = acceptable.iterator();

	T first = i.next();
	maximal.add(first);

	// Now check to make sure that we have a maximal most-specific method.
	while (i.hasNext()) {
	    T p = i.next();

	    if (msc.compare(first, p) == 0) {
		maximal.add(p);
	    }
	}

	if (maximal.size() > 1) {
	    // If exactly one method is not abstract, it is the most specific.
	    List<T> notAbstract = new ArrayList<T>(maximal.size());
	    for (Iterator<T> j = maximal.iterator(); j.hasNext(); ) {
		T p = j.next();
		if (! (p instanceof MemberInstance<?>) || ! ((MemberInstance<?>) p).flags().isAbstract()) {
		    notAbstract.add(p);
		}
	    }

	    if (notAbstract.size() == 1) {
		maximal = notAbstract;
	    }
	    else if (notAbstract.size() == 0) {
		// all are abstract; if all signatures match, any will do.
		Iterator<T> j = maximal.iterator();
		first = j.next();
		S firstDecl = first.def();
		List<Type> firstFormals = new TransformingList<Ref<? extends Type>,Type>(firstDecl.formalTypes(), new DerefTransform<Type>());
		while (j.hasNext()) {
		    T p = j.next();

		    // Use the declarations to compare formals.
		    S pDecl = p.def();

		    List<Type> pFormals = new TransformingList<Ref<? extends Type>,Type>(pDecl.formalTypes(), new DerefTransform<Type>());

		    if (! CollectionUtil.allElementwise(firstFormals, pFormals, new TypeEquals(context))) {
			// not all signatures match; must be ambiguous
			return maximal;
		    }
		}

		// all signatures match, just take the first
		maximal = Collections.<T>singletonList(first);
	    }
	}

	return maximal;
    }

    private <S extends ProcedureDef, T extends ProcedureInstance<S>> Comparator<T> SUPERmostSpecificComparator(Type container, Matcher<T> matcher, Context context) {
	return new MostSpecificComparator<S,T>(context);
    }

    public static class TypeEquals implements Predicate2<Type> {
	Context context;
	public TypeEquals(Context context) {
	    this.context = context;
	}
	public boolean isTrue(Type o, Type p) {
	    TypeSystem ts = context.typeSystem();
	    return ts.typeEquals(o, p, context);
	}
    }

    public static class ImplicitCastValid implements Predicate2<Type> {
	Context context;
	public ImplicitCastValid(Context context) {
	    this.context = context;
	}
	public boolean isTrue(Type o, Type p) {
	    TypeSystem ts = context.typeSystem();
	    return ts.isImplicitCastValid(o, p, context);
	}
    }

    public static class Subtype implements Predicate2<Type> {
	Context context;
	public Subtype(Context context) {
	    this.context = context;
	}
	public boolean isTrue(Type o, Type p) {
	    TypeSystem ts = context.typeSystem();
	    return ts.isSubtype(o, p, context);
	}
    }

    /**
     * Class to handle the comparisons; dispatches to moreSpecific method.
     */
    public static class MostSpecificComparator<S extends ProcedureDef, T extends ProcedureInstance<S>> implements Comparator<T> {
	protected Context context;

	public MostSpecificComparator(Context context) {
	    this.context = context;
	    
	}
	public int compare(T p1, T p2) {
	    if (p1.moreSpecific(null, p2, context))
		return -1;
	    if (p2.moreSpecific(null, p1, context))
		return 1;
	    return 0;
	}
    }

    /**
     * Populates the list acceptable with those MethodInstances which are
     * Applicable and Accessible as defined by JLS 15.11.2.1
     */
    private List<MethodInstance> SUPERfindAcceptableMethods(Type container, MethodMatcher matcher)
    throws SemanticException {

	assert_(container);
	
	Context context = matcher.context();
	
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

	Q:
	    while (! typeQueue.isEmpty()) {
		Type t = typeQueue.removeFirst();

		if (t instanceof StructType) {
		    StructType type = (StructType) t;

		    for (Type s : visitedTypes) {
			if (typeEquals(type, s, context))
			    continue Q;
		    }

		    if (visitedTypes.contains(type)) {
			continue;
		    }

		    visitedTypes.add(type);

		    if (Report.should_report(Report.types, 2))
			Report.report(2, "Searching type " + type + " for method " + matcher.signature());

		    for (Iterator<MethodInstance> i = type.methodsNamed(matcher.name()).iterator(); i.hasNext(); ) {
			MethodInstance mi = i.next();

			if (Report.should_report(Report.types, 3))
			    Report.report(3, "Trying " + mi);

			try {
				MethodInstance oldmi = mi;
			    mi = matcher.instantiate(mi);

			    if (mi == null) {
				continue;
			    }
			    mi.setOrigMI(oldmi);
			    if (isAccessible(mi, context)) {
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
			    // Treat any instantiation errors as call invalid errors.
			    if (error == null)
				error = new NoMemberException(NoMemberException.METHOD,
				                              "Method " + mi.signature() +
				                              " in " + container +
				                              " cannot be called with arguments " +
				                              matcher.argumentString() + "; " + e.getMessage()); 
			}

			if (error == null) {
			    error = new NoMemberException(NoMemberException.METHOD,
			                                  "Method " + mi.signature() +
			                                  " in " + container +
			                                  " cannot be called with arguments " +
			                                  matcher.argumentString() + "."); 
			}
		    }
		}

		if (t instanceof ObjectType) {
		    ObjectType ot = (ObjectType) t;

		    if (ot.superClass() != null) {
			typeQueue.addLast(ot.superClass());
		    }

		    typeQueue.addAll(ot.interfaces());
		}
	    }
	
	if (acceptable.size() > 0) {
		// remove any method in acceptable that are overridden by an
		// unacceptable
		// method.
		for (Iterator<MethodInstance> i = unacceptable.iterator(); i.hasNext();) {
		    MethodInstance mi = i.next();
		    acceptable.removeAll(mi.overrides(context));
		}
	}
	
	if (acceptable.size() == 0) {
	    if (error == null) {
	    	  throw new NoMemberException(NoMemberException.METHOD,
                      "No valid method call found for call in given type."
	+ "\n\t Call: " + matcher.signature() 
	+ "\n\t Type: " + container);
	    }
	    throw error;
	}

	return acceptable;
    }
    
    /**
     * Populates the list acceptable with those MethodInstances which are
     * Applicable and Accessible as defined by JLS 15.11.2.1
     */
    public List<ConstructorInstance> findAcceptableConstructors(Type container, ConstructorMatcher matcher) throws SemanticException {
	return env(matcher.context()).findAcceptableConstructors(container, matcher);
    }
    
    /**
     * Returns whether method 1 is <i>more specific</i> than method 2,
     * where <i>more specific</i> is defined as JLS 15.11.2.2
     */
    public <T extends ProcedureDef> boolean moreSpecific(ProcedureInstance<T> p1, ProcedureInstance<T> p2, Context context) {
	return env(context).moreSpecific(p1, p2);
    }

    /**
     * Returns the supertype of type, or null if type has no supertype.
     **/
    public Type superClass(Type type) {
	assert_(type);
	if (type instanceof ObjectType)
	    return ((ObjectType) type).superClass();
	return null;
    }

    /**
     * Returns an immutable list of all the interface types which type
     * implements.
     **/
    public List<Type> interfaces(Type type) {
	assert_(type);
	if (type instanceof ObjectType)
	    return ((ObjectType) type).interfaces();
	return Collections.<Type>emptyList();
    }

    /**
     * Requires: all type arguments are canonical.
     * Returns the least common ancestor of Type1 and Type2
     **/
    private Type SUPERleastCommonAncestor(Type type1, Type type2, Context context)
    throws SemanticException
    {
	assert_(type1);
	assert_(type2);
	return env(context).leastCommonAncestor(type1, type2);
    }

    ////
    // Functions for method testing.
    ////
    
    /** Return true if t overrides mi */
    public boolean hasMethod(Type t, MethodInstance mi, Context context) {
	assert_(t);
	assert_(mi);
	return env(context).hasMethod(t, mi);
    }

    

    /** Return true if t overrides mi */
    public boolean hasFormals(ProcedureInstance<? extends ProcedureDef> pi, List<Type> formalTypes, Context context) {
	assert_(pi);
	assert_(formalTypes);
	return env(context).hasFormals(pi, formalTypes);
    }

    public List<MethodInstance> overrides(MethodInstance mi, Context context) {
	return env(context).overrides(mi);
    }

    public List<MethodInstance> implemented(MethodInstance mi, Context context) {
	return env(context).implemented(mi);
    }

    protected List<MethodInstance> implemented(MethodInstance mi, StructType st, Context context) {
	return env(context).implemented(mi, st);
    }


    public boolean canOverride(MethodInstance mi, MethodInstance mj, Context context) {
	return env(context).canOverride(mi, mj);
    }

    public void checkOverride(MethodInstance mi, MethodInstance mj, Context context) throws SemanticException {
	env(context).checkOverride(mi, mj);
    }

    public void checkOverride(MethodInstance mi, MethodInstance mj, boolean allowCovariantReturn, Context context) throws SemanticException {
	env(context).checkOverride(mi, mj, allowCovariantReturn);
    }

    /**
     * Returns true iff <m1> is the same method as <m2>
     */
    public boolean isSameMethod(MethodInstance m1, MethodInstance m2, Context context) {
	assert_(m1);
	assert_(m2);
	return env(context).isSameMethod(m1, m2);
    }

    public boolean callValid(ProcedureInstance<? extends ProcedureDef> prototype, Type thisType, List<Type> argTypes, Context context) {
	assert_(prototype);
	assert_(argTypes);
	return env(context).callValid(prototype, thisType, argTypes);
    }

    ////
    // Functions which yield particular types.
    ////
    public NullType Null()         { return NULL_; }
    private Type SUPERVoid()    { return VOID_; }
    private Type SUPERBoolean() { return BOOLEAN_; }
    private Type SUPERChar()    { return CHAR_; }
    private Type SUPERByte()    { return BYTE_; }
    private Type SUPERShort()   { return SHORT_; }
    private Type SUPERInt()     { return INT_; }
    private Type SUPERLong()    { return LONG_; }
    private Type SUPERFloat()   { return FLOAT_; }
    private Type SUPERDouble()  { return DOUBLE_; }

    private ClassType SUPERload(String name) {
	try {
	    return (ClassType) typeForName(QName.make(name));
	}
	catch (SemanticException e) {
	    throw new InternalCompilerError("Cannot find class \"" +
	                                    name + "\"; " + e.getMessage(),
	                                    e);
	}
    }

    public Named forName(QName name) throws SemanticException {
	try {
	    return systemResolver.find(name);
	}
	catch (SemanticException e) {
	    if (name.qualifier() != null) {
		try {
		    Named container = forName(name.qualifier());
		    if (container instanceof ClassType) {
			return classContextResolver((ClassType) container).find(MemberTypeMatcher((ClassType) container, name.name(), emptyContext()));
		    }
		}
		catch (SemanticException e2) {
		}
	    }

	    // throw the original exception
	    throw e;
	}
    }

    public Type typeForName(QName name) throws SemanticException {
	return (Type) forName(name);
    }

    protected Type OBJECT_;
    protected Type CLASS_;
    protected Type STRING_;
    protected Type THROWABLE_;

    private Type SUPERObject()  { if (OBJECT_ != null) return OBJECT_;
    return OBJECT_ = load("java.lang.Object"); }
    private Type SUPERClass()   { if (CLASS_ != null) return CLASS_;
    return CLASS_ = load("java.lang.Class"); }
    private Type SUPERString()  { if (STRING_ != null) return STRING_;
    return STRING_ = load("java.lang.String"); }
    private Type SUPERThrowable() { if (THROWABLE_ != null) return THROWABLE_;
    return THROWABLE_ = load("java.lang.Throwable"); }
    private Type SUPERError() { return load("java.lang.Error"); }
    private Type SUPERException() { return load("java.lang.Exception"); }
    private Type SUPERRuntimeException() { return load("java.lang.RuntimeException"); }
    private Type SUPERCloneable() { return load("java.lang.Cloneable"); }
    private Type SUPERSerializable() { return load("java.io.Serializable"); }
    private Type SUPERNullPointerException() { return load("java.lang.NullPointerException"); }
    private Type SUPERClassCastException()   { return load("java.lang.ClassCastException"); }
    private Type SUPEROutOfBoundsException() { return load("java.lang.ArrayIndexOutOfBoundsException"); }
    private Type SUPERArrayStoreException()  { return load("java.lang.ArrayStoreException"); }
    private Type SUPERArithmeticException()  { return load("java.lang.ArithmeticException"); }

    private NullType SUPERcreateNull() {
	return new NullType_c(this);
    }

    private PrimitiveType SUPERcreatePrimitive(Name name) {
	return new PrimitiveType_c(this, name);
    }

    protected final NullType NULL_         = createNull();
    private final PrimitiveType SUPERVOID_    = createPrimitive(Name.make("void"));
    protected final PrimitiveType BOOLEAN_ = createPrimitive(Name.make("boolean"));
    protected final PrimitiveType CHAR_    = createPrimitive(Name.make("char"));
    protected final PrimitiveType BYTE_    = createPrimitive(Name.make("byte"));
    protected final PrimitiveType SHORT_   = createPrimitive(Name.make("short"));
    protected final PrimitiveType INT_     = createPrimitive(Name.make("int"));
    protected final PrimitiveType LONG_    = createPrimitive(Name.make("long"));
    protected final PrimitiveType FLOAT_   = createPrimitive(Name.make("float"));
    protected final PrimitiveType DOUBLE_  = createPrimitive(Name.make("double"));

    public Object placeHolder(TypeObject o) {
	return placeHolder(o, Collections.<TypeObject>emptySet());
    }

    public Object placeHolder(TypeObject o, Set<TypeObject> roots) {
	assert_(o);

	if (o instanceof Ref_c<?>) {
	    Ref_c<?> ref = (Ref_c<?>) o;

	    if (ref.get() instanceof ClassDef) {
		ClassDef ct = (ClassDef) ref.get();

		// This should never happen: anonymous and local types cannot
		// appear in signatures.
		if (ct.isLocal() || ct.isAnonymous()) {
		    throw new InternalCompilerError("Cannot serialize " + o + ".");
		}

		// Use the transformed name so that member classes will
		// be sought in the correct class file.
		QName name = getTransformedClassName(ct);

		TypeSystem_c ts = this;
		LazyRef<ClassDef> sym = Types.lazyRef(unknownClassDef(), null);
		Goal resolver = extInfo.scheduler().LookupGlobalTypeDef(sym, name);
		resolver.update(Goal.Status.SUCCESS);
		sym.setResolver(resolver);
		return sym;
	    }
	}

	return o;
    }

    private ClassDef SUPERunknownClassDef() {
	if (unknownClassDef == null) {
	    unknownClassDef = new ClassDef_c(this, null);
	    unknownClassDef.name(Name.make("<unknown class>"));
	    unknownClassDef.kind(ClassDef.TOP_LEVEL);
	}
	return unknownClassDef;
    }

    protected ClassDef unknownClassDef = null;

    private UnknownType SUPERunknownType = new UnknownType_c(this);
    protected UnknownPackage unknownPackage = new UnknownPackage_c(this);
    protected UnknownQualifier unknownQualifier = new UnknownQualifier_c(this);

    private UnknownType SUPERunknownType(Position pos) {
	return unknownType;
    }

    public UnknownPackage unknownPackage(Position pos) {
	return unknownPackage;
    }

    public UnknownQualifier unknownQualifier(Position pos) {
	return unknownQualifier;
    }

    public Package packageForName(Package prefix, Name name) throws SemanticException {
	return createPackage(prefix, name);
    }

    public Package packageForName(Ref<? extends Package> prefix, Name name) throws SemanticException {
	return createPackage(prefix, name);
    }

    public Package packageForName(QName name) throws SemanticException {
	if (name == null) {
	    return null;
	}

	return packageForName(packageForName(name.qualifier()), name.name());
    }

    /** @deprecated */
    public Package createPackage(Package prefix, Name name) {
	return createPackage(prefix != null ? Types.ref(prefix) : null, name);
    }

    /** @deprecated */
    public Package createPackage(Ref<? extends Package> prefix, Name name) {
	assert_(prefix);
	return new Package_c(this, prefix, name);
    }

    /** @deprecated */
    public Package createPackage(QName name) {
	if (name == null) {
	    return null;
	}

	return createPackage(createPackage(name.qualifier()), name.name());
    }

    /**
     * Returns a type identical to <type>, but with <dims> more array
     * dimensions.
     */
    public Type arrayOf(Type type) {
	assert_(type);
	return arrayOf(type.position(), Types.ref(type));
    }

    public Type arrayOf(Ref<? extends Type> type) {
	assert_(type);
	return arrayOf(null, type);
    }

    private Type SUPERarrayOf(Position pos, Ref<? extends Type> type) {
	return arrayType(pos, type);
    }

    public Type arrayOf(Position pos, Type type) {
	assert_(type);
	return arrayOf(pos, Types.ref(type));
    }

    public Type arrayOf(Type type, int dims) {
	return arrayOf(Types.ref(type), dims);
    }

    public Type arrayOf(Position pos, Type type, int dims) {
	return arrayOf(pos, Types.ref(type), dims);
    }

    Map<Ref<? extends Type>,Type> arrayTypeCache = new HashMap<Ref<? extends Type>,Type>();

    /**
     * Factory method for ArrayTypes.
     */
    protected ArrayType arrayType(Position pos, Ref<? extends Type> type) {
	ArrayType t = (ArrayType) arrayTypeCache.get(type);
	if (t == null) {
	    t = createArrayType(pos, type);
	    arrayTypeCache.put(type, t);
	}
	return t;
    }

    protected ArrayType createArrayType(Position pos, Ref<? extends Type> type) {
	return new ArrayType_c(this, pos, type);
    }

    public Type arrayOf(Ref<? extends Type> type, int dims) {
	return arrayOf(null, type, dims);
    }

    public Type arrayOf(Position pos, Ref<? extends Type> type, int dims) {
	if (dims > 1) {
	    return arrayOf(pos, arrayOf(pos, type, dims-1));
	}
	else if (dims == 1) {
	    return arrayOf(pos, type);
	}
	else {
	    throw new InternalCompilerError(
	                                    "Must call arrayOf(type, dims) with dims > 0");
	}
    }

    /**
     * Returns a canonical type corresponding to the Java Class object
     * theClass.  Does not require that <theClass> have a JavaClass
     * registered in this typeSystem.  Does not register the type in
     * this TypeSystem.  For use only by JavaClass implementations.
     **/
    public Type typeForClass(Class<?> clazz) throws SemanticException
    {
	if (clazz == java.lang.Void.TYPE)      return VOID_;
	if (clazz == Boolean.TYPE)   return BOOLEAN_;
	if (clazz == Byte.TYPE)      return BYTE_;
	if (clazz == Character.TYPE) return CHAR_;
	if (clazz == Short.TYPE)     return SHORT_;
	if (clazz == Integer.TYPE)   return INT_;
	if (clazz == Long.TYPE)      return LONG_;
	if (clazz == Float.TYPE)     return FLOAT_;
	if (clazz == Double.TYPE)    return DOUBLE_;

	if (clazz.isArray()) {
	    return arrayOf(typeForClass(clazz.getComponentType()));
	}

	return (Type) systemResolver.find(QName.make(clazz.getName()));
    }

    /**
     * Return the set of objects that should be serialized into the
     * type information for the given TypeObject.
     * Usually only the object itself should get encoded, and references
     * to other classes should just have their name written out.
     * If it makes sense for additional types to be fully encoded,
     * (i.e., they're necessary to correctly reconstruct the given clazz,
     * and the usual class resolvers can't otherwise find them) they
     * should be returned in the set in addition to clazz.
     */
    public Set<TypeObject> getTypeEncoderRootSet(TypeObject t) {
	return Collections.singleton(t);
    }

    /**
     * Get the transformed class name of a class.
     * This utility method returns the "mangled" name of the given class,
     * whereby all periods ('.') following the toplevel class name
     * are replaced with dollar signs ('$'). If any of the containing
     * classes is not a member class or a top level class, then null is
     * returned.
     * 
     * Return null if the class is not globally accessible.
     */
    public QName getTransformedClassName(ClassDef ct) {
	assert ct != null;
	assert ct.fullName() != null;

	if (!ct.isMember() && !ct.isTopLevel()) {
	    assert ! ct.asType().isGloballyAccessible();
	    return null;
	}

	StringBuilder sb = new StringBuilder();

	while (ct.isMember()) {
	    sb.insert(0, ct.name());
	    sb.insert(0, '$');
	    ct = ct.outer().get();
	    if (!ct.isMember() && !ct.isTopLevel()) {
		assert ! ct.asType().isGloballyAccessible();
		return null;
	    }
	}

	assert ct.asType().isGloballyAccessible();

	if (sb.length() > 0)
	    return QName.make(ct.fullName(), Name.make(sb.toString()));
	else
	    return QName.make(ct.fullName());
    }

    public String translatePackage(Resolver c, Package p) {
	return p.translate(c);
    }

    public String translateArray(Resolver c, ArrayType t) {
	return t.translate(c);
    }

    public String translateClass(Resolver c, ClassType t) {
	return t.translate(c);
    }

    public String translatePrimitive(Resolver c, PrimitiveType t) {
	return t.translate(c);
    }

    public Type primitiveForName(Name sname)
    throws SemanticException {
	String name = sname.toString();
	if (name.equals("void")) return Void();
	if (name.equals("boolean")) return Boolean();
	if (name.equals("char")) return Char();
	if (name.equals("byte")) return Byte();
	if (name.equals("short")) return Short();
	if (name.equals("int")) return Int();
	if (name.equals("long")) return Long();
	if (name.equals("float")) return Float();
	if (name.equals("double")) return Double();

	throw new SemanticException("Unrecognized primitive type \"" +
	                            name + "\".");
    }

    public final ClassDef createClassDef() {
	return createClassDef((Source) null);
    }

    private ClassDef SUPERcreateClassDef(Source fromSource) {
	return new ClassDef_c(this, fromSource);
    }

    private ParsedClassType SUPERcreateClassType(Position pos, Ref<? extends ClassDef> def) {
	return new ParsedClassType_c(this, pos, def);
    }

    private ConstructorInstance SUPERcreateConstructorInstance(Position pos, Ref<? extends ConstructorDef> def) {
	return new ConstructorInstance_c(this, pos, def);
    }

    private MethodInstance SUPERcreateMethodInstance(Position pos, Ref<? extends MethodDef> def) {
	return new MethodInstance_c(this, pos, def);
    }

    private FieldInstance SUPERcreateFieldInstance(Position pos, Ref<? extends FieldDef> def) {
	return new FieldInstance_c(this, pos, def);
    }

    private LocalInstance SUPERcreateLocalInstance(Position pos, Ref<? extends LocalDef> def) {
	return new LocalInstance_c(this, pos, def);
    }

    public InitializerInstance createInitializerInstance(Position pos, Ref<? extends InitializerDef> def) {
	return new InitializerInstance_c(this, pos, def);
    }

    private List<QName> SUPERdefaultOnDemandImports() {
	List<QName> l = new ArrayList<QName>(1);
	l.add(QName.make("java.lang"));
	return l;
    }

    private Type SUPERpromote(Type t1, Type t2) throws SemanticException {
	if (! t1.isNumeric()) {
	    throw new SemanticException(
	                                "Cannot promote non-numeric type " + t1);
	}

	if (! t2.isNumeric()) {
	    throw new SemanticException(
	                                "Cannot promote non-numeric type " + t2);
	}

	if (t1.isDouble() || t2.isDouble()) {
	    return Double();
	}

	if (t1.isFloat() || t2.isFloat()) {
	    return Float();
	}

	if (t1.isLong() || t2.isLong()) {
	    return Long();
	}

	return Int();
    }

    private Type SUPERpromote(Type t) throws SemanticException {
	if (! t.isNumeric()) {
	    throw new SemanticException(
	                                "Cannot promote non-numeric type " + t);
	}

	if (t.isIntOrLess()) {
	    return Int();
	}

	return t;
    }

    /** All possible <i>access</i> flags. */
    public Flags legalAccessFlags() {
	return Public().Protected().Private();
    }

    protected final Flags ACCESS_FLAGS = legalAccessFlags();

    /** All flags allowed for a local variable. */
    private Flags SUPERlegalLocalFlags() {
	return Final();
    }

    protected final Flags LOCAL_FLAGS = legalLocalFlags();

    /** All flags allowed for a field. */
    private Flags SUPERlegalFieldFlags() {
	return legalAccessFlags().Static().Final().Transient().Volatile();
    }

    protected final Flags FIELD_FLAGS = legalFieldFlags();

    /** All flags allowed for a constructor. */
    private Flags SUPERlegalConstructorFlags() {
	return legalAccessFlags().Synchronized();
    }

    protected final Flags CONSTRUCTOR_FLAGS = legalConstructorFlags();

    /** All flags allowed for an initializer block. */
    public Flags legalInitializerFlags() {
	return Static();
    }

    protected final Flags INITIALIZER_FLAGS = legalInitializerFlags();

    /** All flags allowed for a method. */
    private Flags SUPERlegalMethodFlags() {
	return legalAccessFlags().Abstract().Static().Final().Native().Synchronized().StrictFP();
    }

    protected final Flags METHOD_FLAGS = legalMethodFlags();

    private Flags SUPERlegalAbstractMethodFlags() {
	return legalAccessFlags().clear(Private()).Abstract();
    }

    protected final Flags ABSTRACT_METHOD_FLAGS = legalAbstractMethodFlags();

    /** All flags allowed for a top-level class. */
    private Flags SUPERlegalTopLevelClassFlags() {
	return legalAccessFlags().clear(Private()).Abstract().Final().StrictFP().Interface();
    }

    protected final Flags TOP_LEVEL_CLASS_FLAGS = legalTopLevelClassFlags();

    /** All flags allowed for an interface. */
    private Flags SUPERlegalInterfaceFlags() {
	return legalAccessFlags().Abstract().Interface().Static();
    }

    protected final Flags INTERFACE_FLAGS = legalInterfaceFlags();

    /** All flags allowed for a member class. */
    private Flags SUPERlegalMemberClassFlags() {
	return legalAccessFlags().Static().Abstract().Final().StrictFP().Interface();
    }

    protected final Flags MEMBER_CLASS_FLAGS = legalMemberClassFlags();

    /** All flags allowed for a local class. */
    private Flags SUPERlegalLocalClassFlags() {
	return Abstract().Final().StrictFP().Interface();
    }

    protected final Flags LOCAL_CLASS_FLAGS = legalLocalClassFlags();

    private void SUPERcheckMethodFlags(Flags f) throws SemanticException {
	if (! f.clear(METHOD_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare method with flags " +
	                                f.clear(METHOD_FLAGS) + ".");
	}

	if (f.isAbstract() && ! f.clear(ABSTRACT_METHOD_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare abstract method with flags " +
	                                f.clear(ABSTRACT_METHOD_FLAGS) + ".");
	}

	checkAccessFlags(f);
    }

    public void checkLocalFlags(Flags f) throws SemanticException {
	if (! f.clear(LOCAL_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare local variable with flags " +
	                                f.clear(LOCAL_FLAGS) + ".");
	}
    }

    public void checkFieldFlags(Flags f) throws SemanticException {
	if (! f.clear(FIELD_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare field with flags " +
	                                f.clear(FIELD_FLAGS) + ".");
	}

	checkAccessFlags(f);
    }

    public void checkConstructorFlags(Flags f) throws SemanticException {
	if (! f.clear(CONSTRUCTOR_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare constructor with flags " +
	                                f.clear(CONSTRUCTOR_FLAGS) + ".");
	}

	checkAccessFlags(f);
    }

    public void checkInitializerFlags(Flags f) throws SemanticException {
	if (! f.clear(INITIALIZER_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare initializer with flags " +
	                                f.clear(INITIALIZER_FLAGS) + ".");
	}
    }

    private void SUPERcheckTopLevelClassFlags(Flags f) throws SemanticException {
	if (! f.clear(TOP_LEVEL_CLASS_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare a top-level class with flag(s) " +
	                                f.clear(TOP_LEVEL_CLASS_FLAGS) + ".");
	}


	if (f.isInterface() && ! f.clear(INTERFACE_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException("Cannot declare interface with flags " +
	                                f.clear(INTERFACE_FLAGS) + ".");
	}

	checkAccessFlags(f);
    }

    private void SUPERcheckMemberClassFlags(Flags f) throws SemanticException {
	if (! f.clear(MEMBER_CLASS_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare a member class with flag(s) " +
	                                f.clear(MEMBER_CLASS_FLAGS) + ".");
	}

	checkAccessFlags(f);
    }

    private void SUPERcheckLocalClassFlags(Flags f) throws SemanticException {
	if (f.isInterface()) {
	    throw new SemanticException("Cannot declare a local interface.");
	}

	if (! f.clear(LOCAL_CLASS_FLAGS).equals(Flags.NONE)) {
	    throw new SemanticException(
	                                "Cannot declare a local class with flag(s) " +
	                                f.clear(LOCAL_CLASS_FLAGS) + ".");
	}

	checkAccessFlags(f);
    }

    public void checkAccessFlags(Flags f) throws SemanticException {
	int count = 0;
	if (f.isPublic()) count++;
	if (f.isProtected()) count++;
	if (f.isPrivate()) count++;

	if (count > 1) {
	    throw new SemanticException(
	                                "Invalid access flags: " + f.retain(ACCESS_FLAGS) + ".");
	}
    }

    /**
     * Utility method to gather all the superclasses and interfaces of
     * <code>ct</code> that may contain abstract methods that must be
     * implemented by <code>ct</code>. The list returned also contains
     * <code>rt</code>.
     */
    private List<Type> SUPERabstractSuperInterfaces(Type t) {
	if (t instanceof ObjectType) {
	    ObjectType rt = (ObjectType) t;

	    List<Type> superInterfaces = new LinkedList<Type>();
	    superInterfaces.add(rt);

	    for (Type interf : rt.interfaces()) {
		superInterfaces.addAll(abstractSuperInterfaces(interf));
	    }

	    if (rt.superClass() instanceof ClassType) {
		ClassType c = (ClassType) rt.superClass();
		if (c.flags().isAbstract()) {
		    // the superclass is abstract, so it may contain methods
		    // that must be implemented.
		    superInterfaces.addAll(abstractSuperInterfaces(c));
		}
		else {
		    // the superclass is not abstract, so it must implement
		    // all abstract methods of any interfaces it implements, and
		    // any superclasses it may have.
		}
	    }
	    
	    return superInterfaces;
	}
	return Collections.<Type>emptyList();
    }

    /**
     * Assert that <code>ct</code> implements all abstract methods required;
     * that is, if it is a concrete class, then it must implement all
     * interfaces and abstract methods that it or it's superclasses declare, and if 
     * it is an abstract class then any methods that it overrides are overridden 
     * correctly.
     */
    private void SUPERcheckClassConformance(ClassType ct, Context context) throws SemanticException {
	env(context).checkClassConformance(ct);
    }
    
    public MethodInstance findImplementingMethod(ClassType ct, MethodInstance mi, Context context) {
	return findImplementingMethod(ct, mi, false, context);
    }

    private MethodInstance SUPERfindImplementingMethod(ClassType ct, MethodInstance mi, boolean includeAbstract, Context context) {
	StructType curr = ct;
	while (curr != null) {
	    List<MethodInstance> possible = curr.methods(mi.name(), mi.formalTypes(), context);
	    for (Iterator<MethodInstance> k = possible.iterator(); k.hasNext(); ) {
		MethodInstance mj = k.next();
		if ((includeAbstract || !mj.flags().isAbstract()) && 
			((isAccessible(mi, context) && isAccessible(mj, context)) || 
				isAccessible(mi, context))) {
		    // The method mj may be a suitable implementation of mi.
		    // mj is not abstract, and either mj's container 
		    // can access mi (thus mj can really override mi), or
		    // mi and mj are both accessible from ct (e.g.,
		    // mi is declared in an interface that ct implements,
		    // and mj is defined in a superclass of ct).
		    return mj;                    
		}
	    }
	    if (curr.typeEquals(mi.container(), context)) {
		// we've reached the definition of the abstract 
		// method. We don't want to look higher in the 
		// hierarchy; this is not an optimization, but is 
		// required for correctness. 
		break;
	    }

	    if (curr instanceof ObjectType) {
		ObjectType ot = (ObjectType) curr;
		if (ot.superClass() instanceof StructType) {
		    curr = (StructType) ot.superClass();
		}
		else {
		    curr = null;
		}
	    }
	    else {
		curr = null;
	    }
	}
	return null;
    }

    private void SUPERinitFlags() {
	flagsForName = new HashMap<String, Flags>();
	flagsForName.put("public", Flags.PUBLIC);
	flagsForName.put("private", Flags.PRIVATE);
	flagsForName.put("protected", Flags.PROTECTED);
	flagsForName.put("static", Flags.STATIC);
	flagsForName.put("final", Flags.FINAL);
	flagsForName.put("synchronized", Flags.SYNCHRONIZED);
	flagsForName.put("transient", Flags.TRANSIENT);
	flagsForName.put("native", Flags.NATIVE);
	flagsForName.put("interface", Flags.INTERFACE);
	flagsForName.put("abstract", Flags.ABSTRACT);
	flagsForName.put("volatile", Flags.VOLATILE);
	flagsForName.put("strictfp", Flags.STRICTFP);
    }

    private Flags SUPERcreateNewFlag(String name, Flags after) {
	Flags f = Flags.createFlag(name, after);
	flagsForName.put(name, f);
	return f;
    }

    public Flags NoFlags()      { return Flags.NONE; }
    public Flags Public()       { return Flags.PUBLIC; }
    public Flags Private()      { return Flags.PRIVATE; }
    public Flags Protected()    { return Flags.PROTECTED; }
    public Flags Static()       { return Flags.STATIC; }
    public Flags Final()        { return Flags.FINAL; }
    public Flags Synchronized() { return Flags.SYNCHRONIZED; }
    public Flags Transient()    { return Flags.TRANSIENT; }
    public Flags Native()       { return Flags.NATIVE; }
    public Flags Interface()    { return Flags.INTERFACE; }
    public Flags Abstract()     { return Flags.ABSTRACT; }
    public Flags Volatile()     { return Flags.VOLATILE; }
    public Flags StrictFP()     { return Flags.STRICTFP; }

    private Flags SUPERflagsForBits(int bits) {
	Flags f = Flags.NONE;

	if ((bits & Modifier.PUBLIC) != 0)       f = f.Public();
	if ((bits & Modifier.PRIVATE) != 0)      f = f.Private();
	if ((bits & Modifier.PROTECTED) != 0)    f = f.Protected();
	if ((bits & Modifier.STATIC) != 0)       f = f.Static();
	if ((bits & Modifier.FINAL) != 0)        f = f.Final();
	if ((bits & Modifier.SYNCHRONIZED) != 0) f = f.Synchronized();
	if ((bits & Modifier.TRANSIENT) != 0)    f = f.Transient();
	if ((bits & Modifier.NATIVE) != 0)       f = f.Native();
	if ((bits & Modifier.INTERFACE) != 0)    f = f.Interface();
	if ((bits & Modifier.ABSTRACT) != 0)     f = f.Abstract();
	if ((bits & Modifier.VOLATILE) != 0)     f = f.Volatile();
	if ((bits & Modifier.STRICT) != 0)       f = f.StrictFP();

	return f;
    }

    protected String getCreatorStack(int limit) {
        StackTraceElement[] trace = creator.getStackTrace();
        // The first 3 elements will be the factory methods and the constructor
        int size = trace.length-3 < limit ? trace.length-3 : limit;
        StackTraceElement[] res = new StackTraceElement[size];
        for (int i = 0; i < res.length; i++)
            res[i] = trace[i+3];
        return Arrays.toString(res);
    }

    public String toString() {
	return StringUtil.getShortNameComponent(getClass().getName()) + " created at " + creationTime;
    }

	public static final String DUMMY_AT_ASYNC = "$dummyAsync"; // for async/at/ateach
	public static final int EXPAND_MACROS_DEPTH=25;

    public ClassDef classDefOf(Type t) {
        t = X10TypeMixin.baseType(t);
        if (t instanceof ClassType)
            return ((ClassType) t).def();
        return null;
    }
    
    
    public Collection<Type> uncheckedExceptions() {
    	List<Type> l = new ArrayList<Type>(1);
    	l.add(Throwable());
    	return l;
        }

    
    public InitializerDef initializerDef(Position pos, Ref<? extends ClassType> container, Flags flags) {
        String fullNameWithThis = "<init>#this";
        XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
        XVar thisVar = XTerms.makeLocal(thisName);

        return initializerDef(pos, container, flags, thisVar);
    }

    public InitializerDef initializerDef(Position pos, Ref<? extends ClassType> container, Flags flags, XVar thisVar) {
        assert_(container);
        return new X10InitializerDef_c(this, pos, container, flags, thisVar);
    }

    public List<MethodInstance> methods(StructType t, Name name, List<Type> typeParams, List<Type> argTypes, XVar thisVar, Context context) {
        List<MethodInstance> l = new ArrayList<MethodInstance>();
        for (Iterator<MethodInstance> i = t.methodsNamed(name).iterator(); i.hasNext();) {
            X10MethodInstance mi = (X10MethodInstance) i.next();

            List<XVar> ys = new ArrayList<XVar>(2);
            List<XVar> xs = new ArrayList<XVar>(2);

            X10MethodInstance_c.buildSubst((X10MethodInstance) mi, ys, xs, thisVar);
            final XVar[] y = ys.toArray(new XVar[ys.size()]);
            final XVar[] x = xs.toArray(new XVar[ys.size()]);

            mi = new X10TypeEnv_c(context).fixThis((X10MethodInstance) mi, y, x);

            if (mi.typeParameters().size() != typeParams.size()) {
                continue;
            }

//            TypeConstraint env = new TypeConstraint();
//            for (int j = 0; j < mi.typeParameters().size(); j++) {
//                Type p1 = mi.typeParameters().get(j);
//                Type p2 = typeParams.get(j);
//                env.addTerm(new SubtypeConstraint(p1, p2, true));
//            }
//
//            if (CollectionUtil.allElementwise(argTypes, mi.formalTypes(),
//                    new TypeEqualsInEnvironment((Context)context, env)))
//            {
//                l.add(mi);
//            }

            TypeParamSubst tps = new TypeParamSubst(this, typeParams, mi.x10Def().typeParameters());
            if (CollectionUtil.allElementwise(argTypes, tps.reinstantiate(mi.formalTypes()), new TypeEquals(context))) {
                l.add(mi);
            }
        }

        return l;
    }

    public static class TypeEqualsInEnvironment implements Predicate2<Type> {
        Context context;
        TypeConstraint env;
        public TypeEqualsInEnvironment(Context context, TypeConstraint env) {
            this.context = context;
            this.env = env;
        }
        public boolean isTrue(Type o, Type p) {
            TypeConstraint newEnv = new TypeConstraint();
            newEnv.addTerm(new SubtypeConstraint(o, p, true));
            // FIXME: Vijay, why doesn't this work?
            return env.entails(newEnv, context);
        }
    }

    public static class BaseTypeEquals implements Predicate2<Type> {
        Context context;
        public BaseTypeEquals(Context context) {
            this.context = context;
        }
        public boolean isTrue(Type o, Type p) {
            TypeSystem ts = context.typeSystem();
            return ts.typeEquals(X10TypeMixin.baseType(o), X10TypeMixin.baseType(p), context);
        }
    }

    /**
     * Assert that <code>ct</code> implements all abstract methods required;
     * that is, if it is a concrete class, then it must implement all interfaces
     * and abstract methods that it or it's superclasses declare, and if it is
     * an abstract class then any methods that it overrides are overridden
     * correctly.
     */
    public void checkClassConformance(ClassType ct, Context context) throws SemanticException {
        env(context).checkClassConformance(ct);
    }

    public void checkOverride(ClassType ct, MethodInstance mi0, MethodInstance mj0, Context context) throws SemanticException {
        env(context).checkOverride(ct, mi0, mj0);
    }

    public X10MethodInstance findImplementingMethod(ClassType ct, MethodInstance jmi, boolean includeAbstract, Context context) {
        X10MethodInstance mi = (X10MethodInstance) jmi;

        XVar thisVar = ((X10ClassDef) ct.def()).thisVar(); // XTerms.makeLocal(XTerms.makeFreshName("this"));

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XVar> xs = new ArrayList<XVar>(2);
        X10MethodInstance_c.buildSubst((X10MethodInstance) mi, ys, xs, thisVar);
        X10MethodInstance_c.buildSubst(ct, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XVar[] x = xs.toArray(new XVar[ys.size()]);

        mi = new X10TypeEnv_c(context).fixThis((X10MethodInstance) mi, y, x);

        StructType curr = ct;
        while (curr != null) {
            List<MethodInstance> possible = methods(curr, mi.name(), mi.typeParameters(), mi.formalTypes(), thisVar, context);
            for (Iterator<MethodInstance> k = possible.iterator(); k.hasNext();) {
                MethodInstance mj = k.next();
                if ((includeAbstract || !mj.flags().isAbstract()) && ((isAccessible(mi, context) && isAccessible(mj, context)) || isAccessible(mi, context))) {
                    // The method mj may be a suitable implementation of mi.
                    // mj is not abstract, and either mj's container
                    // can access mi (thus mj can really override mi), or
                    // mi and mj are both accessible from ct (e.g.,
                    // mi is declared in an interface that ct implements,
                    // and mj is defined in a superclass of ct).
                    return (X10MethodInstance) mj;
                }
            }
            if (curr.typeEquals(mi.container(), context)) {
                // we've reached the definition of the abstract
                // method. We don't want to look higher in the
                // hierarchy; this is not an optimization, but is
                // required for correctness.
                break;
            }

            if (curr instanceof ObjectType) {
                ObjectType ot = (ObjectType) curr;
                if (ot.superClass() instanceof StructType) {
                    curr = (StructType) ot.superClass();
                }
                else {
                    curr = null;
                }
            }
            else {
                curr = null;
            }
        }
        return null;
    }

    public AnnotatedType AnnotatedType(Position pos, Type baseType, List<Type> annotations) {
        return new AnnotatedType_c(this, pos, baseType, annotations);
    }

    public boolean equalsStruct(Type l, Type r) {
        // if (l instanceof ParameterType && r instanceof ParameterType) {
        // return TypeParamSubst.isSameParameter((ParameterType) l,
        // (ParameterType) r);
        // }
        return equals((TypeObject) l, (TypeObject) r);
    }

    /** Return true if the constraint is consistent. */
    public boolean consistent(CConstraint c) {
        return env(null).consistent(c);
    }

    /** Return true if the constraint is consistent. */
    public boolean consistent(TypeConstraint c, Context context) {
        return env(context).consistent(c);
    }

    /** Return true if constraints in the type are all consistent. */
    public boolean consistent(Type t, Context context) {
        return env(context).consistent(t);
    }

    enum Bound {
        UPPER, LOWER, EQUAL
    }

    
    public Set<FieldInstance> findFields(Type container, TypeSystem_c.FieldMatcher matcher) {
        assert_(container);

        Set<FieldInstance> candidates = new HashSet<FieldInstance>();

        for (Type t : env(matcher.context()).upperBounds(container, true)) {
            Set<FieldInstance> fs = SUPERfindFields(t, matcher);
            candidates.addAll(fs);
        }

        return candidates;
    }

    public TypeDefMatcher TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        return new TypeDefMatcher(container, name, typeArgs, argTypes, context);
    }

    
    public Type findMemberType(Type container, Name name, Context context) throws SemanticException {
        // FIXME: check for ambiguities
        for (Type t : env(context).upperBounds(container, true)) {
            try {
                return SUPERfindMemberType(t, name, context);
            }
            catch (SemanticException e) {
            }
            try {
                return this.findTypeDef(t, this.TypeDefMatcher(t, name, Collections.<Type>emptyList(), Collections.<Type>emptyList(), context), context);
            }
            catch (SemanticException e) {
            }
        }

        throw new NoClassException(name.toString(), container);
    }

    
    
    public List<LocalDef> dummyLocalDefs(List<Ref<? extends Type>> types) {
        List<LocalDef> list = new ArrayList<LocalDef>();
        for (int i = 0; i < types.size(); i++) {
            LocalDef ld = localDef(Position.COMPILER_GENERATED, Flags.FINAL, types.get(i), Name.make("a" + (i + 1)));
            ld.setNotConstant();
            list.add(ld);
        }
        return list;
    }

    
    public List<QName> defaultOnDemandImports() {
        List<QName> l = new ArrayList<QName>(1);
        l.add(QName.make("x10.lang"));
        l.add(QName.make("x10.lang", TypeSystem.DUMMY_PACKAGE_CLASS_NAME.toString()));
        l.add(QName.make("x10.array"));
        return l;
    }

    public List<MacroType> findAcceptableTypeDefs(Type container, TypeDefMatcher matcher, Context context) throws SemanticException {
        assert_(container);
        return env(context).findAcceptableTypeDefs(container, matcher);
    }

    
    protected <S extends ProcedureDef, T extends ProcedureInstance<S>> Comparator<T> mostSpecificComparator(Type ct, Matcher<T> matcher, Context context) {
    	return new X10MostSpecificComparator<S,T>(ct, matcher, context);
        }
    protected static class X10MostSpecificComparator<S extends ProcedureDef, T extends ProcedureInstance<S>> extends MostSpecificComparator<S, T> {
        private Matcher<T> matcher;
        Type container;

        protected X10MostSpecificComparator(Type container, Matcher<T> matcher, Context context) {
            super(context);
            this.matcher = matcher;
            this.container=container;
        }
        
        public int compare(T p1, T p2) {
    	    if (p1.moreSpecific(container, p2, context))
    		return -1;
    	    if (p2.moreSpecific(container, p1, context))
    		return 1;
    	    return 0;
    	}

        public Type container() {
        	return container;
        }
       
    }

    private boolean contains(Collection<Type> c, Type x) {
    	Context cxt = emptyContext();
    	for (Type t : c) {
    		if (typeEquals(t, x, cxt)) {
    			return true;
    		}
    	}
    	return false;
    }

    public static class FilteringMatcher<T, U extends T> implements Matcher<U> {
        private Matcher<T> matcher;
        private Class<U> filter;
        public FilteringMatcher(Matcher<T> matcher, Class<U> filter) {
            this.matcher = matcher;
            this.filter = filter;
        }
        @SuppressWarnings("unchecked") // Casting to a generic type argument
        public U instantiate(U matched) throws SemanticException {
            if (filter.isInstance(matched)) {
                T result = matcher.instantiate(matched);
                if (filter.isInstance(result)) {
                    return (U) result;
                }
            }
            return null;
        }
        public java.lang.Object key() { return matcher.key(); }
        public Name name() { return matcher.name(); }
        public java.lang.String signature() { return matcher.signature(); }
    }
    public MacroType findTypeDef(Type container, TypeDefMatcher matcher, Context context) throws SemanticException {
        List<MacroType> acceptable = findAcceptableTypeDefs(container, matcher, context);

        if (acceptable.size() == 0) {
            throw new SemanticException("No valid type definition found for " + matcher.signature() + " in " + container + ".");
        }

        Collection<MacroType> maximal = findMostSpecificProcedures(acceptable,
                new FilteringMatcher<Named, MacroType>(matcher, MacroType.class),
                context);

        if (maximal.size() > 1) { // remove references that resolve to the same type.
        	Collection<Type> reduced = Collections.<Type>emptyList();
        	Collection<MacroType> max2 = Collections.<MacroType>emptyList();
        	for (MacroType mt : maximal) {
        		Type expanded = X10TypeMixin.baseType(mt);
        		if (! reduced.contains(expanded)) {
        			reduced.add(expanded);
        			max2.add(mt);
        		}
        	}
        	 maximal = max2;
        }
       
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

            throw new SemanticException("Reference to " + matcher.name() + " is ambiguous, multiple type defintions match: " + sb.toString());
        }

        MacroType mi = maximal.iterator().next();

        return mi;
    }

    public List<MacroType> findTypeDefs(Type container, Name name, ClassDef currClass) throws SemanticException {
        assert_(container);

        // Named n = classContextResolver(container, currClass).find(name);
        //
        // if (n instanceof MacroType) {
        // return (MacroType) n;
        // }

        throw new NoClassException(name.toString(), container);
    }

    
    public Matcher<Named> TypeMatcher(Name name) {
        return new X10TypeMatcher(name);
    }

    
    public Matcher<Named> MemberTypeMatcher(Type container, Name name, Context context) {
        return new X10MemberTypeMatcher(container, name, context);
    }

    public ClassDef unknownClassDef() {
        if (unknownClassDef == null) {
            unknownClassDef = createFakeClass(QName.make("<unknown class>"), new SemanticException("Unknown class")).def();
        }
        return unknownClassDef;
    }

    X10UnknownType_c unknownType;

    
    public ClassType load(String name) {
        QName qualName = QName.make(name);
        try {
            return (ClassType) typeForName(qualName);
        }
        catch (SemanticException e) {
            extensionInfo().compiler().errorQueue().enqueue(
                                                    ErrorInfo.INTERNAL_ERROR,
                                                    "Cannot load X10 runtime class \"" + name
                                                            + "\".  Is the X10 runtime library in your classpath or sourcepath?");
            Goal goal = extensionInfo().scheduler().currentGoal();
            if (goal != null)
                goal.fail();
            return createFakeClass(qualName, e);
        }
    }

    protected X10ClassType typeForNameSilent(QName fullName) {
        try {
            if (fullName == null) {
                return (X10ClassType) unknownClassDef().asType();
            }
            return (X10ClassType) typeForName(fullName);
        }
        catch (SemanticException e) {
            return createFakeClass(fullName, e);
        }
    }

    public boolean isUnknown(Type t) {
        return X10TypeMixin.baseType(t) instanceof UnknownType;
    }

    // Temporary hack:
    //   use cache to break cycles checking for unknown type
    //   WARNING: this code is NOT reentrant
    //   FIXME: resolve cycles and remove this cache
    private Map<Type, Boolean> unknownTypeMap = new HashMap<Type, Boolean>();
    public boolean hasUnknown(Type t) {
        unknownTypeMap = new HashMap<Type, Boolean>();
        return hasUnknownType(t);
    }
    
    private boolean hasUnknownType(Type t) {
        Boolean unknown = unknownTypeMap.get(t);
        if (null == unknown) {
            unknownTypeMap.put(t, false); // break circular check for unknown type (this value may get reset to true below)
        } else {
            return unknown.booleanValue();
        }
        
        if (isUnknown(t)) {
            unknownTypeMap.put(t, true);
            return true;
        }
        if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            for (Type a : ct.typeArguments()) {
                if (hasUnknownType(a)) {
                    unknownTypeMap.put(t, true);
                    return true;
                }
            }
            if (ct.x10Def().isFunction()) {
                // Look at the superclass and interfaces (if any)
                if (hasUnknownType(ct.superClass())) {
                    unknownTypeMap.put(t, true);
                    return true;
                }
                for (Type i : ct.interfaces()) {
                    if (hasUnknownType(i)) {
                        unknownTypeMap.put(t, true);
                        return true;
                    }
                }
            }
        }
        if (t instanceof AnnotatedType) {
            if (hasUnknownType(X10TypeMixin.baseType(t))) {
                unknownTypeMap.put(t, true);
                return true;
            }
            AnnotatedType at = (AnnotatedType) t;
            List<Type> ann = at.annotations();
            for (Type a : ann) {
                if (hasUnknownType(a)) {
                    unknownTypeMap.put(t, true);
                    return true;
                }
            }
        }
        if (t instanceof ConstrainedType) {
            if (hasUnknownType(X10TypeMixin.baseType(t))) {
                unknownTypeMap.put(t, true);
                return true;
            }
            ConstrainedType ct = (ConstrainedType) t;
            for (XTerm x : X10TypeMixin.xclause(ct).constraints()) {
                if (hasUnknown(x)) {
                    unknownTypeMap.put(t, true);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasUnknown(XTerm x) {
        if (x instanceof XFormula) {
            for (XTerm a : ((XFormula) x).arguments()) {
                if (hasUnknown(a))
                    return true;
            }
        } else if (x instanceof XField) {
            XField f = (XField) x;
            if (hasUnknown(f.receiver()))
                return true;
            return hasUnknown(f.field());
        } else if (x instanceof XTypeLit_c) {
            return hasUnknownType(((XTypeLit_c) x).type());
        } else if (x instanceof XEQV) {
            return false;
        } else if (x instanceof XLocal) {
            return hasUnknown(((XLocal) x).name());
        }
        return false;
    }

    private boolean hasUnknown(XName n) {
        if (n instanceof XNameWrapper<?>) {
            Object v = ((XNameWrapper<?>) n).val();
            if (v instanceof LocalDef) {
                LocalDef ld = (LocalDef) v;
                return hasUnknownType(Types.get(ld.type()));
            } else if (v instanceof FieldDef) {
                FieldDef fd = (FieldDef) v;
                return hasUnknownType(Types.get(fd.type())) || hasUnknownType(Types.get(fd.container()));
            }
        }
        return false;
    }

    public X10ClassType createFakeClass(QName fullName, SemanticException error) {
        X10ClassDef cd = (X10ClassDef) createClassDef();
        cd.name(fullName.name());
        cd.position(Position.COMPILER_GENERATED);
        cd.kind(ClassDef.TOP_LEVEL);
        cd.flags(Flags.PUBLIC);
        cd.superType(null);

        try {
            cd.setPackage(Types.ref(packageForName(fullName.qualifier())));
        }
        catch (SemanticException e) {
        }

        return ((X10ParsedClassType) cd.asType()).error(error);
    }

    public X10FieldInstance createFakeField(QName containerName, Flags flags, Name name, SemanticException error) {
        return createFakeField(typeForNameSilent(containerName), flags, name, error);
    }
    public X10FieldInstance createFakeField(Name name, SemanticException error) {
        return createFakeField(unknownClassDef().asType(), Flags.PUBLIC.Static(), name, error);
    }
    public X10FieldInstance createFakeField(ClassType container, Flags flags, Name name, SemanticException error) {
        Position pos = NodeFactory_c.compilerGenerated(container);
        Type type = unknownType(pos);
        XVar thisVar = XTerms.makeEQV();
        List<Ref<? extends Type>> excTypes = Collections.emptyList();
        X10FieldDef fd = (X10FieldDef) fieldDef(pos, Types.ref(container), flags,
                                                Types.ref(type), name, thisVar);
        return ((X10FieldInstance) fd.asInstance()).error(error);
    }

    public X10MethodInstance createFakeMethod(QName containerName, Flags flags, Name name, List<Type> typeArgs, List<Type> argTypes, SemanticException error) {
        return createFakeMethod(typeForNameSilent(containerName), flags, name, typeArgs, argTypes, error);
    }
    public X10MethodInstance createFakeMethod(Name name, List<Type> typeArgs, List<Type> argTypes, SemanticException error) {
        return createFakeMethod(unknownClassDef().asType(), Flags.PUBLIC.Static(), name, typeArgs, argTypes, error);
    }
    public X10MethodInstance createFakeMethod(ClassType container, Flags flags, Name name, List<Type> typeArgs, List<Type> argTypes, SemanticException error) {
        Position pos = NodeFactory_c.compilerGenerated(container);
        Type returnType = unknownType(pos);
        List<Ref<? extends Type>> args = new ArrayList<Ref<? extends Type>>();
        List<LocalDef> formalNames = new ArrayList<LocalDef>();
        int i = 0;
        for (Type t : argTypes) {
            args.add(Types.ref(t));
            formalNames.add(localDef(pos, Flags.FINAL, Types.ref(t), Name.make("p"+(++i))));
        }
        XVar thisVar = XTerms.makeEQV();
        X10MethodDef md = (X10MethodDef) methodDef(pos, Types.ref(container), flags,
                                                   Types.ref(returnType), name, Collections.<ParameterType>emptyList(),
                                                   args, thisVar, formalNames, null, null,  null, null);
        List<ParameterType> typeParams = new ArrayList<ParameterType>();
        i = 0;
        for (Type r : typeArgs) {
            typeParams.add(new ParameterType_c(this, pos, Name.make("T"+(++i)), Types.ref(md)));
        }
        md.setTypeParameters(typeParams);
        return ((X10MethodInstance) md.asInstance()).error(error);
    }

    public X10ConstructorInstance createFakeConstructor(QName containerName, Flags flags, List<Type> typeArgs, List<Type> argTypes, SemanticException error) {
        return createFakeConstructor(typeForNameSilent(containerName).typeArguments(typeArgs), flags, argTypes, error);
    }
    public X10ConstructorInstance createFakeConstructor(ClassType container, Flags flags, List<Type> argTypes, SemanticException error) {
        Position pos = NodeFactory_c.compilerGenerated(container);
        List<Ref<? extends Type>> args = new ArrayList<Ref<? extends Type>>();
        List<LocalDef> formalNames = new ArrayList<LocalDef>();
        int i = 0;
        for (Type t : argTypes) {
            args.add(Types.ref(t));
            formalNames.add(localDef(pos, Flags.FINAL, Types.ref(t), Name.make("p"+(++i))));
        }
        XVar thisVar = XTerms.makeEQV();
        X10ConstructorDef cd = (X10ConstructorDef) constructorDef(pos, Types.ref(container), flags,
                Types.ref(container), args,
                thisVar, formalNames, null, null,  null);
//        List<Ref<? extends Type>> typeParams = new ArrayList<Ref<? extends Type>>();
//        i = 0;
//        for (Type r : typeArgs) {
//            typeParams.add(Types.ref(new ParameterType_c(this, pos, Name.make("T"+(++i)), Types.ref(cd))));
//        }
//        cd.setTypeParameters(typeParams);
        return ((X10ConstructorInstance) cd.asInstance()).error(error);
    }
    
    public X10LocalInstance createFakeLocal(Name name, SemanticException error) {
        Position pos = Position.COMPILER_GENERATED;
        Type type = unknownType(pos);
        List<Ref<? extends Type>> excTypes = Collections.emptyList();
        X10LocalDef ld = (X10LocalDef) localDef(pos, Flags.FINAL, Types.ref(type), name);
        return ((X10LocalInstance) ld.asInstance()).error(error);
    }

    
    public UnknownType unknownType(Position pos) {
        if (unknownType == null)
            unknownType = new X10UnknownType_c(this);
        return unknownType;
    }

  /*  private X10ParsedClassType boxType_;

    public Type Box() {
        if (boxType_ == null)
            boxType_ = (X10ParsedClassType) load("x10.lang.Box");
        return boxType_;
    }

    public Type boxOf(Ref<? extends Type> base) {
        return boxOf(Position.COMPILER_GENERATED, base);
    }
*/
    public List<Type> superTypes(ObjectType t) {
        Type sup = t.superClass();
        if (sup == null)
            return t.interfaces();
        List<Type> ts = new ArrayList<Type>();
        ts.add(sup);
        ts.addAll(t.interfaces());
        return ts;
    }

    public List<FunctionType> getFunctionSupertypes(Type t, Context context) {
        if (t == null)
            return Collections.<FunctionType>emptyList();

        List<FunctionType> l = new ArrayList<FunctionType>();

        for (Type bound : env(context).upperBounds(t, false)) {
            if (bound instanceof FunctionType)
                l.add((FunctionType) bound);

            if (bound instanceof ObjectType) {
                ObjectType ot = (ObjectType) bound;
                for (Type ti : superTypes(ot)) {
                    List<FunctionType> supFunctions = getFunctionSupertypes(ti, context);
                    l.addAll(supFunctions);
                }
            }
        }

        return l;
    }

    public boolean isFunctionType(Type t) {
    	t = X10TypeMixin.baseType(t);
    	if (! (t instanceof X10ClassType)) {
    		return false;
    	}
    	X10ClassType xt = (X10ClassType) t;
    	return declaredFunctionType(xt) || ((X10ClassDef) xt.def()).isFunction();
    }
    public boolean declaredFunctionType(X10ClassType t) {
    	for (Type i : t.interfaces()) {
    		if (i instanceof FunctionType)
    			return true;
    	}
    	return false;
    }
    public boolean isExactlyFunctionType(Type t) {
    	t = X10TypeMixin.baseType(t);
    	if (! (t instanceof X10ClassType)) {
    		return false;
    	}
    	X10ClassType xt = (X10ClassType) t;
    	return (xt instanceof FunctionType) || ((X10ClassDef) xt.def()).isFunction();
    }

   /* public boolean isBox(Type t) {
        return hasSameClassDef(t, this.Box());
    }*/

    public boolean isInterfaceType(Type t) {
        t = X10TypeMixin.baseType(t);
        if (t instanceof ClassType)
            if (((ClassType) t).flags().isInterface())
                return true;
        return false;
    }

    static enum Kind {
        NEITHER, EITHER, OBJECT, STRUCT, INTERFACE
    }

    public Kind kind(Type t, Context c) {
        return env(c).kind(t);
    }

    public boolean isParameterType(Type t) {
        t = X10TypeMixin.baseType(t);
        return t instanceof ParameterType;
    }

    public boolean isObjectOrInterfaceType(Type t, Context c) {
        Kind kind = kind(t, c);
        return kind == Kind.OBJECT || kind == Kind.INTERFACE;
    }

    public boolean isObjectType(Type t, Context c) {
        return kind(t, c) == Kind.OBJECT;
    }

    
    public boolean isStructType(Type t) {
        return kind(t, null) == Kind.STRUCT;
    }

    
    public Type arrayOf(Position pos, Ref<? extends Type> type) {
        // Should be called only by the Java class file loader.
        Type r = Rail();
        return X10TypeMixin.instantiate(r, type);
    }

    
    public X10ClassDef createClassDef(Source fromSource) {
        return new X10ClassDef_c(this, fromSource);
    }

    
    public X10ParsedClassType createClassType(Position pos, Ref<? extends ClassDef> def) {
        return new X10ParsedClassType_c(this, pos, def);
    }

    
    public X10ConstructorInstance createConstructorInstance(Position pos, Ref<? extends ConstructorDef> def) {
        return new X10ConstructorInstance_c(this, pos, (Ref<? extends X10ConstructorDef>) def);
    }

    
    public X10MethodInstance createMethodInstance(Position pos, Ref<? extends MethodDef> def) {
        return new X10MethodInstance_c(this, pos, (Ref<? extends X10MethodDef>) def);
    }

    
    public X10FieldInstance createFieldInstance(Position pos, Ref<? extends FieldDef> def) {
        return new X10FieldInstance_c(this, pos, (Ref<? extends X10FieldDef>) def);
    }

    
    public X10LocalInstance createLocalInstance(Position pos, Ref<? extends LocalDef> def) {
        return new X10LocalInstance_c(this, pos, (Ref<? extends X10LocalDef>) def);
    }

    public ClosureInstance createClosureInstance(Position pos, Ref<? extends ClosureDef> def) {
        return new ClosureInstance_c(this, pos, def);
    }

    public List<X10ClassType> allImplementedInterfaces(X10ClassType c) {
    	return allImplementedInterfaces(c, true);
    }
    
	public List<X10ClassType> allImplementedInterfaces(X10ClassType c, boolean checkSuperClasses) {
		List<X10ClassType> ans =  new ArrayList<X10ClassType>();
		allImplementedInterfaces(c, checkSuperClasses, ans);
		return ans;
	}

	private void allImplementedInterfaces(X10ClassType c, boolean checkSuperClasses, List<X10ClassType> l) {
		Context context = createContext();
		if (c.typeEquals(Object(), context)) {
			return;
		}

		for (Type old : l) {
			if (c.typeEquals(old, context)) {
				return; /* Already been here */
			}
		}

		if (c.flags().isInterface()) {
			l.add(c);
		}

		if (checkSuperClasses && c.superClass() != null) {
			allImplementedInterfaces((X10ClassType)X10TypeMixin.baseType(c.superClass()), 
					checkSuperClasses, l);
		}

		for (Type parent : c.interfaces()) {
			allImplementedInterfaces((X10ClassType)X10TypeMixin.baseType(parent), 
					checkSuperClasses, l);
		}
	}



    public final Context createContext() {
        return emptyContext();
    }

    public Context emptyContext() {
        return new Context_c(this);
    }

    /** All flags allowed for a method. */
    public Flags legalMethodFlags() {
        X10Flags x = X10Flags.toX10Flags(legalAccessFlags().Abstract().Static().Final().Native().StrictFP());
        x = x.Clocked().Safe().NonBlocking().Sequential().Property().Pure().Extern().Atomic(); 
        return x;

    }

    public Flags legalAbstractMethodFlags() {
        X10Flags x = X10Flags.toX10Flags(legalAccessFlags().clear(Private()).Abstract());
        x = x.Clocked().Safe().NonBlocking().Sequential().Property().Pure().Atomic(); 
        return x;
    }

    /** All flags allowed for a top-level class. */
    public Flags legalTopLevelClassFlags() {
        return X10Flags.toX10Flags(SUPERlegalTopLevelClassFlags()).Clocked().Safe().Struct();
    }

    protected final X10Flags X10_TOP_LEVEL_CLASS_FLAGS = (X10Flags) legalTopLevelClassFlags();

    /** All flags allowed for an interface. */
    public Flags legalInterfaceFlags() {
        return X10Flags.toX10Flags(SUPERlegalInterfaceFlags()).Clocked().Safe().Value();
    }

    protected final X10Flags X10_INTERFACE_FLAGS = (X10Flags) legalInterfaceFlags();

    /** All flags allowed for a member class. */
    public Flags legalMemberClassFlags() {
        return X10Flags.toX10Flags(SUPERlegalMemberClassFlags()).Clocked().Safe().Struct();
    }

    protected final Flags X10_MEMBER_CLASS_FLAGS = (X10Flags) legalMemberClassFlags();

    /** All flags allowed for a local class. */
    public Flags legalLocalClassFlags() {
        return X10Flags.toX10Flags(SUPERlegalLocalClassFlags()).Safe().Struct();
    }

    protected final X10Flags X10_LOCAL_CLASS_FLAGS = (X10Flags) legalLocalClassFlags();

    
    public Flags legalLocalFlags() {
        return X10Flags.toX10Flags(SUPERlegalLocalFlags()).Clocked();
    }

    protected final X10Flags X10_LOCAL_VARIABLE_FLAGS = (X10Flags) legalLocalFlags();

    
    public Flags legalFieldFlags() {
        return X10Flags.toX10Flags(SUPERlegalFieldFlags()).Property().Clocked();
    }

    protected final X10Flags X10_FIELD_VARIABLE_FLAGS = (X10Flags) legalFieldFlags();

    
    public X10MethodDef methodDef(Position pos, Ref<? extends StructType> container, Flags flags, 
    		Ref<? extends Type> returnType, Name name,
            List<Ref<? extends Type>> argTypes) {
    	return methodDef(pos, container, flags, returnType, name, argTypes, null);
    }

    public X10MethodDef methodDef(Position pos, Ref<? extends StructType> container,
            Flags flags, Ref<? extends Type> returnType, Name name,
            List<Ref<? extends Type>> argTypes,  Ref<? extends Type> offerType)
    {
    	String fullNameWithThis = name + "#this";
    	XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
    	XVar thisVar = XTerms.makeLocal(thisName);

    	// set up null thisVar for method def's, so the outer contexts are searched for thisVar.
    	return methodDef(pos, container, flags, returnType, name, Collections.<ParameterType>emptyList(), argTypes, 
    	        name.toString().contains(DUMMY_AT_ASYNC) ? null : thisVar, dummyLocalDefs(argTypes), null, null,  offerType,
    	                null);
    }
    
    public X10MethodDef methodDef(Position pos, Ref<? extends StructType> container, 
    		Flags flags, Ref<? extends Type> returnType, Name name,
            List<ParameterType> typeParams, List<Ref<? extends Type>> argTypes, 
            XVar thisVar, List<LocalDef> formalNames, 
            Ref<CConstraint> guard,
            Ref<TypeConstraint> typeGuard, 
            Ref<? extends Type> offerType,
            Ref<XTerm> body)
    {
        assert_(container);
        assert_(returnType);
        assert_(typeParams);
        assert_(argTypes);
        return new X10MethodDef_c(this, pos, container, flags, returnType, name, typeParams, argTypes, thisVar, formalNames, guard, typeGuard, offerType, body);
    }

    /**
     * Return a nullable type based on a given type. TODO: rename this to
     * nullableType() -- the name is misleading.
     */
    public Type boxOf(Position pos, Ref<? extends Type> type) {
       return type.get();
      //  X10ParsedClassType box = (X10ParsedClassType) Box();
      //  return X10TypeMixin.instantiate(box, type);
    }

    X10ParsedClassType futureType_;

    public Type futureOf(Position pos, Ref<? extends Type> base) {
        if (futureType_ == null)
            futureType_ = (X10ParsedClassType) load("x10.lang.Future");
        return X10TypeMixin.instantiate(futureType_, base);
    }

    /**
     * [IP] TODO: this should be a special CodeInstance instead
     */
    protected CodeDef asyncStaticCodeInstance_;
    protected CodeDef asyncCodeInstance_;

    public CodeDef asyncCodeInstance(boolean isStatic) {
    	// Need to create a new one on each call. Portions of this methodDef, such as thisVar may be destructively modified later.
                return methodDef(Position.COMPILER_GENERATED, Types.ref((StructType) Runtime()), isStatic ? Public().Static() : Public(),
                		Types.ref(VOID_),
                		Name.make(DUMMY_AT_ASYNC), Collections.<Ref<? extends Type>>emptyList());
    }

    public ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer,
            Ref<? extends Type> returnType, List<Ref<? extends Type>> argTypes, XVar thisVar,
            List<LocalDef> formalNames, Ref<CConstraint> guard,
            Ref<? extends Type> offerType) {
        return new ClosureDef_c(this, p, typeContainer, methodContainer, returnType, 
        		argTypes, thisVar, formalNames, guard,  offerType);
    }

    public FunctionType closureType(Position p, Ref<? extends Type> returnType, 
    		// List<Ref<? extends Type>> typeParams, 
    		List<Ref<? extends Type>> argTypes,
            List<LocalDef> formalNames, Ref<CConstraint> guard
          //  Ref<TypeConstraint> typeGuard, 
            ) {
        Type rt = Types.get(returnType);
        X10ClassDef def = ClosureSynthesizer.closureBaseInterfaceDef(this, 0 /*typeParams.size()*/, argTypes.size(), rt.isVoid(), formalNames, guard);
        FunctionType ct = (FunctionType) def.asType();
        List<Type> typeArgs = new ArrayList<Type>();
        for (Ref<? extends Type> ref : argTypes) {
            typeArgs.add(Types.get(ref));
        }
        if (!rt.isVoid())
            typeArgs.add(rt);
        return (FunctionType) ct.typeArguments(typeArgs);
    }

    protected NullType createNull() {
        return new X10NullType_c(this);
    }

    /******************** Primitive types as Objects ******************/

    private static final String WRAPPER_PACKAGE = "x10.compilergenerated";

    
    public PrimitiveType createPrimitive(Name name) {
        return new X10PrimitiveType_c(this, name);
    }

    // protected final PrimitiveType UBYTE_ = createPrimitive("ubyte");
    // protected final PrimitiveType USHORT_ = createPrimitive("ushort");
    // protected final PrimitiveType UINT_ = createPrimitive("uint");
    // protected final PrimitiveType ULONG_ = createPrimitive("ulong");
    //
    // public Type UByte() { return UBYTE_; }
    // public Type UShort() { return USHORT_; }
    // public Type UInt() { return UINT_; }
    // public Type ULong() { return ULONG_; }

    static class Void extends X10PrimitiveType_c {
        private static final long serialVersionUID = -1026975473924276266L;

        public Void(TypeSystem ts) {
            super(ts, Name.make("Void"));
        }

        public QName fullName() {
            return QName.make("x10.lang.Void");
        }

        public String toString() {
            return fullName().toString();
        }
    }

    // The only primitive left.
    Type VOID_;

    public Type Void() {
        if (VOID_ == null)
            VOID_ = new Void(this);
        return VOID_;
    }

    public boolean isVoid(Type t) {
        return t != null && expandMacros(t).equals((Object) Void());
    } // do not use typeEquals

    protected ClassType Boolean_;

    public Type Boolean() {
        if (Boolean_ == null)
            Boolean_ = load("x10.lang.Boolean");
        return Boolean_;
    }

    protected ClassType Byte_;

    public Type Byte() {
        if (Byte_ == null)
            Byte_ = load("x10.lang.Byte");
        return Byte_;
    }

    protected ClassType Short_;

    public Type Short() {
        if (Short_ == null)
            Short_ = load("x10.lang.Short");
        return Short_;
    }

    protected ClassType Char_;

    public Type Char() {
        if (Char_ == null)
            Char_ = load("x10.lang.Char");
        return Char_;
    }

    protected ClassType Int_;

    public Type Int() {
        if (Int_ == null)
            Int_ = load("x10.lang.Int");
        return Int_;
    }

    protected ClassType Long_;

    public Type Long() {
        if (Long_ == null)
            Long_ = load("x10.lang.Long");
        return Long_;
    }

    protected ClassType Float_;

    public Type Float() {
        if (Float_ == null)
            Float_ = load("x10.lang.Float");
        return Float_;
    }

    protected ClassType Double_;

    public Type Double() {
        if (Double_ == null)
            Double_ = load("x10.lang.Double");
        return Double_;
    }

    // Unsigned integers
    protected ClassType UByte_;

    public Type UByte() {
        if (UByte_ == null)
            UByte_ = load("x10.lang.UByte");
        return UByte_;
    }

    protected ClassType UShort_;

    public Type UShort() {
        if (UShort_ == null)
            UShort_ = load("x10.lang.UShort");
        return UShort_;
    }

    protected ClassType UInt_;

    public Type UInt() {
        if (UInt_ == null)
            UInt_ = load("x10.lang.UInt");
        return UInt_;
    }

    protected ClassType ULong_;

    public Type ULong() {
        if (ULong_ == null)
            ULong_ = load("x10.lang.ULong");
        return ULong_;
    }

    protected ClassType nativeValRail_;

    public Type ValRail() {
        if (nativeValRail_ == null)
            nativeValRail_ = load("x10.lang.ValRail");
        return nativeValRail_;
    }

    protected ClassType nativeRail_;

    public Type Rail() {
        if (nativeRail_ == null)
            nativeRail_ = load("x10.lang.Rail");
        return nativeRail_;
    }

    // protected ClassType XOBJECT_;
    // public Type X10Object() {
    // if (XOBJECT_ == null)
    // XOBJECT_ = load("x10.lang.Object");
    // return XOBJECT_;
    // }

    protected Type GLOBAL_REF_;
    public Type GlobalRef() {
        if (GLOBAL_REF_ == null)
            GLOBAL_REF_ = load("x10.lang.GlobalRef");
        return GLOBAL_REF_;
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
    
    Type ANY_ = null;
    public Type Any() {
        if (ANY_ != null)
            return ANY_;
    	return ANY_ = load("x10.lang.Any"); // x10.util.Any.makeDef(this).asType();
    }

    public LazyRef<Type> lazyAny() {
		final LazyRef<Type> ANY = Types.lazyRef(null);
		ANY.setResolver(new Runnable() {
			public void run() {
				ANY.update(Any());
			}
		});
		return ANY;
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
  
    protected ClassType customSerializationType_;

    public Type CustomSerialization() {
        if (customSerializationType_ == null)
            customSerializationType_ = load("x10.io.CustomSerialization"); // java file
        return customSerializationType_;
    }
 
    protected ClassType reducibleType_;

    public Type Reducible() {
        if (reducibleType_ == null)
            reducibleType_ = load("x10.lang.Reducible"); // java file
        return reducibleType_;
    }

    protected ClassType nativeRepType_;
    public Type NativeRep() {
    	if (nativeRepType_ == null)
    		nativeRepType_ = load("x10.compiler.NativeRep");
    	return nativeRepType_;
    }
    protected ClassType nativeType_;
    public Type NativeType() {
    	if (nativeType_ == null)
    		nativeType_ = load("x10.compiler.Native");
    	return nativeType_;
    }
    public Type Iterable(Type index) {
        return X10TypeMixin.instantiate(Iterable(), index);
    }

    public Type Iterator(Type index) {
        return X10TypeMixin.instantiate(Iterator(), index);
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
            regionType_ = load("x10.array.Region"); // java file
        return regionType_;
    }

    protected Type pointType_;

    public Type Point() {
        if (pointType_ == null)
            pointType_ = load("x10.array.Point");
        return pointType_;
    }

    protected ClassType distributionType_;

    public Type Dist() {
        if (distributionType_ == null)
            distributionType_ = load("x10.array.Dist"); // java file
        return distributionType_;
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

    protected ClassType arrayType_ = null;

    public Type Array() {
        if (arrayType_ == null)
            arrayType_ = load("x10.array.Array");
        return arrayType_;
    }

    protected ClassType remoteArrayType_ = null;

    public Type RemoteArray() {
        if (remoteArrayType_ == null)
            remoteArrayType_ = load("x10.array.RemoteArray");
        return remoteArrayType_;
    }

    protected ClassType distArrayType_ = null;

    public Type DistArray() {
        if (distArrayType_ == null)
            distArrayType_ = load("x10.array.DistArray");
        return distArrayType_;
    }
 
    protected ClassType mortalType_ = null;

    public Type Mortal() {
        if (mortalType_ == null)
            mortalType_ = load("x10.lang.Runtime.Mortal");
        return mortalType_;
    }

    
    // RMF 11/1/2005 - Not having the "static" qualifier on interfaces causes
    // problems,
    // e.g. for New_c.disambiguate(AmbiguityRemover), which assumes that
    // instantiating
    // non-static types requires a "this" qualifier expression.
    // [IP] FIXME: why does the above matter when we supply the bits?
    public Flags flagsForBits(int bits) {
        Flags sf = SUPERflagsForBits(bits);
        if (sf.isInterface())
            return sf.Static();
        return sf;
    }

    
    public X10FieldDef fieldDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> type, Name name) {
        assert_(container);
        assert_(type);

        String fullNameWithThis = name + "#this";
        XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
        XVar thisVar = XTerms.makeLocal(thisName);

        return fieldDef(pos, container, flags, type, name, thisVar);
    }

    public X10FieldDef fieldDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> type, Name name, XVar thisVar) {
        assert_(container);
        assert_(type);
        return new X10FieldDef_c(this, pos, container, flags, type, name, thisVar);
    }

    public boolean isUByte(Type t) {
        return isSubtype(t, UByte(), emptyContext());
    }

    public boolean isUShort(Type t) {
        return isSubtype(t, UShort(), emptyContext());
    }

    public boolean isUInt(Type t) {
        return isSubtype(t, UInt(), emptyContext());
    }

    public boolean isULong(Type t) {
        return isSubtype(t, ULong(), emptyContext());
    }

    public boolean isNumeric(Type t) {
        if (isChar(t))
            return false;
        return SUPERisNumeric(t) || isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
    }

    public boolean isIntOrLess(Type t) {
        if (isChar(t))
            return false;
        return SUPERisIntOrLess(t) || isUByte(t) || isUShort(t);
    }

    public boolean isLongOrLess(Type t) {
        if (isChar(t))
            return false;
        return SUPERisLongOrLess(t) || isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
    }

    public Type expandMacros(Type t) {
    	return expandMacros(t, 0);
    }
    private Type expandMacros(Type t, int depth) {
    	if (depth > EXPAND_MACROS_DEPTH) {
    		System.err.println("Reached max macro expansion depth with " + t + " (at " + t.position());
    		return unknownType(Position.COMPILER_GENERATED); // bottom
    	}
        if (t instanceof AnnotatedType)
            return expandMacros(((AnnotatedType) t).baseType(), depth+1);
        if (t instanceof MacroType)
            return expandMacros(((MacroType) t).definedType(), depth+1);
        if (t instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) t;
            Type base = ct.baseType().get();
            Type ebase = expandMacros(base, depth+1);
            if (base == ebase)
                return t;
            CConstraint c = ct.constraint().get();
            return X10TypeMixin.xclause(ebase, c);
        }
        return t;
    }

    public boolean entails(CConstraint c1, CConstraint c2, Context context, Type selfType) {
        if (c1 != null || c2 != null) {
            boolean result = true;

            if (c1 != null && c2 != null) {
                try {
                    result = c1.entails(c2, context.constraintProjection(c1, c2));
                }
                catch (XFailure e) {
                    result = false;
                }
            }
            else if (c2 != null) {
                result = c2.valid();
            }

            return result;
        }

        return true;
    }

    
    public boolean numericConversionValid(Type t, java.lang.Object value, Context context) {
        return env(context).numericConversionValid(t, value);
    }

    public boolean numericConversionValid(Type t, Type fromType, java.lang.Object value, Context context) {
        return env(context).numericConversionValid(t, fromType, value);
    }

    protected boolean typeRefListEquals(List<Ref<? extends Type>> l1, List<Ref<? extends Type>> l2, Context context) {
        return CollectionUtil.<Type> allElementwise(new TransformingList<Ref<? extends Type>, Type>(l1, new DerefTransform<Type>()),
                                                    new TransformingList<Ref<? extends Type>, Type>(l2, new DerefTransform<Type>()),
                                                    new TypeSystem_c.TypeEquals(context));
    }

    protected boolean typeListEquals(List<Type> l1, List<Type> l2, Context context) {
        return CollectionUtil.<Type> allElementwise(l1, l2, new TypeSystem_c.TypeEquals(context));
    }

    protected boolean listEquals(List<XVar> l1, List<XVar> l2) {
        return CollectionUtil.<XVar> allEqual(l1, l2);
    }

    protected boolean isX10BaseSubtype(Type me, Type sup, Context context) {
        Type xme = X10TypeMixin.baseType(me);
        Type xsup = X10TypeMixin.baseType(sup);
        return isSubtype(xme, xsup, context);
    }

    public boolean isRail(Type t) {
        return hasSameClassDef(t, Rail());
    }

    public boolean isRailOf(Type t, Type p) {
        if (!isRail(t)) return false;
        List<Type> ta = ((X10ClassType)X10TypeMixin.baseType(t)).typeArguments();
        assert (ta.size() == 1);
        return ta.get(0).typeEquals(p, createContext());
    }

    public boolean isValRail(Type t) {
        return hasSameClassDef(t, ValRail());
    }
  
    public boolean isValRailOf(Type t, Type p) {
        if (!isValRail(t)) return false;
        List<Type> ta = ((X10ClassType)X10TypeMixin.baseType(t)).typeArguments();
        assert (ta.size() == 1);
        return ta.get(0).typeEquals(p, createContext());
    }

    public boolean isArray(Type t) {
        return hasSameClassDef(t, Array());
    }

    public boolean isArrayOf(Type t, Type p) {
        if (!isArray(t)) return false;
        List<Type> ta = ((X10ClassType)X10TypeMixin.baseType(t)).typeArguments();
        assert (ta.size() == 1);
        return ta.get(0).typeEquals(p, createContext());
    }

    public boolean isRemoteArray(Type t) {
        return hasSameClassDef(t, RemoteArray());
    }

    public boolean isRemoteArrayOf(Type t, Type p) {
        if (!isRemoteArray(t)) return false;
        List<Type> ta = ((X10ClassType)X10TypeMixin.baseType(t)).typeArguments();
        assert (ta.size() == 1);
        Type array_type = ta.get(0);
        List<Type> ta2 = ((X10ClassType)X10TypeMixin.baseType(array_type)).typeArguments();
        assert (ta2.size() == 1);
        return ta2.get(0).typeEquals(p, createContext());
    }

    public boolean hasSameClassDef(Type t1, Type t2) {
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

    public Type ValRail(Type arg) {
        return X10TypeMixin.instantiate(ValRail(), arg);
    }

    public Type Array(Type arg) {
        return X10TypeMixin.instantiate(Array(), arg);
    }

    public Type Settable(Type domain, Type range) {
        return X10TypeMixin.instantiate(Settable(), domain, range);
    }

    public boolean isSettable(Type me) {
        return hasSameClassDef(me, Settable());
    }

    public boolean isX10Array(Type me) {
        if (hasSameClassDef(me, Array())) {
            return true;
        } else if (me.isClass()) {
            Type parent = me.toClass().superClass();
            return parent != null && isX10Array(parent);
        } else {
            return false;
        }
    }

    public boolean isX10DistArray(Type me) {
        if (hasSameClassDef(me, DistArray())) {
            return true;
        } else if (me.isClass()) {
            Type parent = me.toClass().superClass();
            return parent != null && isX10DistArray(parent);
        } else {
            return false;
        }
    }

    public boolean isTypeConstrained(Type me) {
        return me instanceof ConstrainedType;
    }

    public boolean isAny(Type me) {
        return typeEquals(me, Any(), emptyContext());
    }
    
    public boolean isStruct(Type me) {
        return X10TypeMixin.isX10Struct(me);
            //typeEquals(me, Struct(), emptyContext());
    }
    
    public boolean isClock(Type me) {
        return isSubtype(me, Clock(), emptyContext());
    }

    public boolean isPoint(Type me) {
        return isSubtype(me, Point(), emptyContext());
    }

    public boolean isPlace(Type me) {
        return isSubtype(me, Place(), emptyContext());
    }

    public boolean isRegion(Type me) {
        return isSubtype(me, Region(), emptyContext());
    }

    public boolean isDistribution(Type me) {
        return isSubtype(me, Dist(), emptyContext());
    }

    public boolean isDistributedArray(Type me) {
        return isX10DistArray(me);
    }

    public boolean isComparable(Type me) {
        return isSubtype(me, Comparable(), emptyContext());
    }

    public boolean isIterable(Type me) {
        return isSubtype(me, Iterable(), emptyContext());
    }

    public boolean isIterator(Type me) {
        return isSubtype(me, Iterator(), emptyContext());
    }
    public boolean isReducible(Type me) {
        return isSubtype(me, Reducible(), emptyContext());
    }

    public boolean isContains(Type me) {
        return isSubtype(me, Contains(), emptyContext());
    }

    public boolean isContainsAll(Type me) {
        return isSubtype(me, ContainsAll(), emptyContext());
    }

    public VarDef createSelf(Type t) {
        VarDef v = localDef(NodeFactory_c.compilerGenerated(t), Flags.PUBLIC, Types.ref(t), Name.make("self"));
        return v;
    }

 
    protected XTypeTranslator xtt = new XTypeTranslator(this);

    public XTypeTranslator xtypeTranslator() {
        return xtt;
    }

    
    public void initialize(TopLevelResolver loadedResolver, ExtensionInfo extInfo) throws SemanticException {
        SUPERinitialize(loadedResolver, extInfo);
    }

    public boolean equivClause(Type me, Type other, Context context) {
        return entailsClause(me, other, context) && entailsClause(other, me, context);
    }

    public boolean entailsClause(CConstraint c1, CConstraint c2, Context context, Type selfType) {
        return entails(c1, c2, context, selfType);
    }

    public boolean entailsClause(Type me, Type other, Context context) {
        try {
            CConstraint c1 = X10TypeMixin.realX(me);
            CConstraint c2 = X10TypeMixin.xclause(other);
            return entailsClause(c1, c2, context, null);
        }
        catch (InternalCompilerError e) {
            if (e.getCause() instanceof XFailure) {
                return false;
            }
            throw e;
        }
    }
/*
    protected XLit hereConstraintLit; // Maybe this should be declared as C_Lit
                                      // instead of a concrete impl class?

    public XLit here() {
        if (hereConstraintLit == null)
            hereConstraintLit = xtypeTranslator().transHere();
        return hereConstraintLit;
    }
*/
    protected XLit FALSE;

    public XLit FALSE() {
        if (FALSE == null)
            FALSE = xtypeTranslator().trans(false);
        return FALSE;
    }

    protected XLit TRUE;

    public XLit TRUE() {
        if (TRUE == null)
            TRUE = xtypeTranslator().trans(true);
        return TRUE;
    }

    protected XLit NEG_ONE;

    public XLit NEG_ONE() {
        if (NEG_ONE == null)
            NEG_ONE = xtypeTranslator().trans(-1);
        return NEG_ONE;
    }

    protected XLit ZERO;

    public XLit ZERO() {
        if (ZERO == null)
            ZERO = xtypeTranslator().trans(0);
        return ZERO;
    }

    protected XLit ONE;

    public XLit ONE() {
        if (ONE == null)
            ONE = xtypeTranslator().trans(1);
        return ONE;
    }

    protected XLit TWO;

    public XLit TWO() {
        if (TWO == null)
            TWO = xtypeTranslator().trans(2);
        return TWO;
    }

    protected XLit THREE;

    public XLit THREE() {
        if (THREE == null)
            THREE = xtypeTranslator().trans(3);
        return THREE;
    }

    protected XLit NULL;

    public XLit NULL() {
        if (NULL == null)
            NULL = xtypeTranslator().transNull();
        return NULL;
    }

    
    public Flags createNewFlag(String name, Flags after) {
        Flags f = X10Flags.createFlag(name, after);
        flagsForName.put(name, f);
        return f;
    }

    
    protected void initFlags() {
        SUPERinitFlags();
  //      flagsForName.put("local", X10Flags.LOCAL);
        flagsForName.put("nonblocking", X10Flags.NON_BLOCKING);
        flagsForName.put("safe", X10Flags.SAFE);
        flagsForName.put("sequential", X10Flags.SEQUENTIAL);
        flagsForName.put("incomplete", X10Flags.INCOMPLETE);
        flagsForName.put("property", X10Flags.PROPERTY);
        flagsForName.put("pure", X10Flags.PURE);
        flagsForName.put("atomic", X10Flags.ATOMIC);
        flagsForName.put("global", X10Flags.GLOBAL);
        flagsForName.put("extern", X10Flags.EXTERN);
        flagsForName.put("value", X10Flags.VALUE);
        flagsForName.put("reference", X10Flags.REFERENCE);
        flagsForName.put("mutable", X10Flags.MUTABLE);
        flagsForName.put("shared", X10Flags.SHARED);
        flagsForName.put("struct", X10Flags.STRUCT);
     //   flagsForName.put("rooted", X10Flags.ROOTED);
    }

    /** All flags allowed for a constructor. */
    
    public Flags legalConstructorFlags() {
        return legalAccessFlags().Synchronized().Native(); // allow native (but
                                                           // not extern)
    }

    protected final Flags X10_METHOD_FLAGS = legalMethodFlags();

    
    public void checkMethodFlags(Flags f) throws SemanticException {
        // Report.report(1, "TypeSystem_c:method_flags are |" +
        // X10_METHOD_FLAGS + "|");
        if (!f.clear(X10_METHOD_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare method with flags " + f.clear(X10_METHOD_FLAGS) + ".");
        }

        if (f.isAbstract() && !f.clear(ABSTRACT_METHOD_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare abstract method with flags " + f.clear(ABSTRACT_METHOD_FLAGS) + ".");
        }

        checkAccessFlags(f);
    }

    
    public void checkTopLevelClassFlags(Flags f) throws SemanticException {
        if (!f.clear(X10_TOP_LEVEL_CLASS_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare a top-level class with flag(s) " + f.clear(X10_TOP_LEVEL_CLASS_FLAGS) + ".");
        }

        if (f.isInterface() && !f.clear(X10_INTERFACE_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare interface with flags " + f.clear(X10_INTERFACE_FLAGS) + ".");
        }

        checkAccessFlags(f);
    }

    
    public void checkMemberClassFlags(Flags f) throws SemanticException {
        if (!f.clear(X10_MEMBER_CLASS_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare a member class with flag(s) " + f.clear(X10_MEMBER_CLASS_FLAGS) + ".");
        }

        checkAccessFlags(f);
    }

    
    public void checkLocalClassFlags(Flags f) throws SemanticException {
        if (f.isInterface()) {
            throw new SemanticException("Cannot declare a local interface.");
        }

        if (!f.clear(X10_LOCAL_CLASS_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare a local class with flag(s) " + f.clear(X10_LOCAL_CLASS_FLAGS) + ".");
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
        if (isUByte(t) || isUShort(t) || isUInt(t))
            return UInt();

        if (isULong(t))
            return ULong();

        if (isByte(t) || isShort(t) || isInt(t))
            return Int();

        if (isLong(t))
            return Long();

        if (isFloat(t))
            return Float();

        if (isDouble(t))
            return Double();

        throw new SemanticException("Cannot promote non-numeric type " + t);
    }

    
    public Type promote(Type t) throws SemanticException {
        Type pt = promote2(t);
        return X10TypeMixin.baseType(pt);
    }

    
    public Type promote(Type t1, Type t2) throws SemanticException {
        Type pt = promote2(t1, t2);
        return X10TypeMixin.baseType(pt);
    }

    
    public Type leastCommonAncestor(Type type1, Type type2, Context context) throws SemanticException {
        assert_(type1);
        assert_(type2);
        return env(context).leastCommonAncestor(type1, type2);
    }

    public boolean typeBaseEquals(Type type1, Type type2, Context context) {
        assert_(type1);
        assert_(type2);
        if (type1 == type2)
            return true;
        if (type1 == null || type2 == null)
            return false;
        return typeEquals(X10TypeMixin.baseType(type1), X10TypeMixin.baseType(type2), context);
    }

    public boolean typeDeepBaseEquals(Type type1, Type type2, Context context) {
        assert_(type1);
        assert_(type2);
        if (type1 == type2)
            return true;
        if (type1 == null || type2 == null)
            return false;
        return typeEquals(X10TypeMixin.stripConstraints(type1), X10TypeMixin.stripConstraints(type2), context);
    }

    
    public X10LocalDef localDef(Position pos, Flags flags, Ref<? extends Type> type, Name name) {
        assert_(type);
        return new X10LocalDef_c(this, pos, flags, type, name);
    }


    public boolean equalTypeParameters(List<Type> a, List<Type> b, Context context) {
        if (a == null || a.isEmpty())
            return b == null || b.isEmpty();
        if (b == null || b.isEmpty())
            return false;
        int i = a.size(), j = b.size();
        if (i != j)
            return false;
        boolean result = true;
        for (int k = 0; result && k < i; k++) {
            result = typeEquals(a.get(k), b.get(k), context);
        }
        return result;
    }

    public ConstructorDef defaultConstructor(Position pos,
    	    Ref<? extends ClassType> container) {
    	assert_(container);

    	// access for the default constructor is determined by the 
    	// access of the containing class. See the JLS, 2nd Ed., 8.8.7.
    	Flags access = Flags.NONE;
    	Flags flags = container.get().flags();
    	if (flags.isPrivate()) {
    	    access = access.Private();
    	}
    	if (flags.isProtected()) {
    	    access = access.Protected();            
    	}
    	if (flags.isPublic()) {
    	    access = access.Public();            
    	}
    	return constructorDef(pos, container,
    	                      access, Collections.<Ref<? extends Type>>emptyList()
    	                      );
        }

    
    public X10ConstructorDef constructorDef(Position pos, Ref<? extends ClassType> container, Flags flags, List<Ref<? extends Type>> argTypes) {
    	return constructorDef(pos, container, flags, argTypes,  null);
    }
    public X10ConstructorDef constructorDef(Position pos, Ref<? extends ClassType> container, Flags flags, List<Ref<? extends Type>> argTypes,
           Ref<? extends Type> offerType)
    {
        assert_(container);
        assert_(argTypes);

        String fullNameWithThis = "this#this";
        XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
        XVar thisVar = XTerms.makeLocal(thisName);
		
        return constructorDef(pos, container, flags, Types.ref(Types.get(container)), argTypes, thisVar, dummyLocalDefs(argTypes), 
        		null, null,  offerType);
    }

    public X10ConstructorDef constructorDef(Position pos, Ref<? extends ClassType> container, Flags flags, Ref<? extends ClassType> returnType,
            List<Ref<? extends Type>> argTypes, XVar thisVar, List<LocalDef> formalNames, Ref<CConstraint> guard, Ref<TypeConstraint> typeGuard,
             Ref<? extends Type> offerType)
    {
        assert_(container);
        assert_(argTypes);
        
        X10ClassType t = (X10ClassType) Types.get(returnType);
		assert t != null : "Cannot set return type of constructor to " + t;
		if (t==null)
			throw new InternalCompilerError("Cannot set return type of constructor to " + t);
		//t = (X10ClassType) t.setFlags(X10Flags.ROOTED);
		((Ref<X10ClassType>)returnType).update(t);
		//returnType = new Ref_c<X10ClassType>(t);
        return new X10ConstructorDef_c(this, pos, container, flags, returnType, argTypes, thisVar, formalNames, guard, typeGuard,  offerType);
    }

    public void addAnnotation(X10Def o, Type annoType, boolean replace) {
        List<Ref<? extends Type>> newATs = new ArrayList<Ref<? extends Type>>();

        if (replace) {
            for (Ref<? extends Type> at : o.defAnnotations()) {
                if (!at.get().isSubtype(X10TypeMixin.baseType(annoType), emptyContext())) {
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
    

    public boolean clausesConsistent(CConstraint c1, CConstraint c2, Context context) {
        X10TypeEnv env = env(context);
        return env.clausesConsistent(c1, c2);
    }

    public Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op) {
        CConstraint cl = X10TypeMixin.realX(l);
        CConstraint cr = X10TypeMixin.realX(r);
        TypeSystem xts = (TypeSystem) t.typeSystem();
        CConstraint c = xts.xtypeTranslator().binaryOp(op, cl, cr);
        return X10TypeMixin.xclause(X10TypeMixin.baseType(t), c);
    }

    public Type performUnaryOperation(Type t, Type a, x10.ast.Unary.Operator op) {
        CConstraint ca = X10TypeMixin.realX(a);
        TypeSystem xts = (TypeSystem) t.typeSystem();
        CConstraint c = xts.xtypeTranslator().unaryOp(op, ca);
        if (c == null)
            return t;
        return X10TypeMixin.xclause(X10TypeMixin.baseType(t), c);
    }

    /**
     * Returns true iff an object of type <type> may be thrown.
     **/
    public boolean isThrowable(Type type) {
        assert_(type);
        return isSubtype(type, Throwable(), emptyContext());
    }

    /**
     * Returns a true iff the type or a supertype is in the list returned by
     * uncheckedExceptions().
     */
    public boolean isUncheckedException(Type type) {
        assert_(type);

        if (type.isThrowable()) {
            for (Type t : uncheckedExceptions()) {
                if (isSubtype(type, t, emptyContext())) {
                    return true;
                }
            }
        }

        return false;
    }

    
    public boolean descendsFrom(ClassDef child, ClassDef ancestor) {
        ClassDef a = classDefOf(Any());

        if (ancestor == a)
            return true;

        if (child == a)
            return false;

        return SUPERdescendsFrom(child, ancestor);
    }

    
    public X10TypeEnv env(Context context) {
        return new X10TypeEnv_c(context == null ? emptyContext() : context);
    }

    
    public List<MethodInstance> findAcceptableMethods(Type container, MethodMatcher matcher) throws SemanticException {

        X10MethodMatcher m = (X10MethodMatcher) matcher;

        List<MethodInstance> candidates = new ArrayList<MethodInstance>();
 
        List<Type> types = env(matcher.context()).upperBounds(container, true);
        for (Type t : types) {
            List<MethodInstance> ms = SUPERfindAcceptableMethods(t, matcher);
            candidates.addAll(ms);
        }

        return candidates;
    }

    
    public X10MethodInstance findMethod(Type container, MethodMatcher matcher) throws SemanticException {
        return (X10MethodInstance) SUPERfindMethod(container, matcher);
    }

    public Collection<X10MethodInstance> findMethods(Type container, MethodMatcher matcher) throws SemanticException {
        assert_(container);
        Context context = matcher.context();
        List<MethodInstance> acceptable = findAcceptableMethods(container, matcher);
        if (acceptable.size() == 0) {
        	  throw new NoMemberException(NoMemberException.METHOD,
                      "No valid method call found for call in given type."
	+ "\n\t Call: " + matcher.signature() 
	+ "\n\t Type: " + container);
        }
        Collection<MethodInstance> maximal =
            findMostSpecificProcedures(acceptable, (Matcher<MethodInstance>) matcher, context);
        Collection<X10MethodInstance> result = new ArrayList<X10MethodInstance>();
        for (MethodInstance mi : maximal) {
            result.add((X10MethodInstance) mi);
        }
        return result;
    }

    
    public X10ConstructorInstance findConstructor(Type container, x10.types.TypeSystem_c.ConstructorMatcher matcher) throws SemanticException {
        return (X10ConstructorInstance) SUPERfindConstructor(container, matcher);
    }

    public Collection<X10ConstructorInstance> findConstructors(Type container, x10.types.TypeSystem_c.ConstructorMatcher matcher) throws SemanticException {
        assert_(container);
        Context context = matcher.context();
        List<ConstructorInstance> acceptable = findAcceptableConstructors(container, matcher);
        if (acceptable.size() == 0) {
            throw new NoMemberException(NoMemberException.CONSTRUCTOR,
                                        "No valid constructor found for " + matcher.signature() + ").");
        }
        Collection<ConstructorInstance> maximal = findMostSpecificProcedures(acceptable, matcher, context);
        Collection<X10ConstructorInstance> result = new ArrayList<X10ConstructorInstance>();
        for (ConstructorInstance mi : maximal) {
            result.add((X10ConstructorInstance) mi);
        }
        return result;
    }

    public X10MethodMatcher MethodMatcher(Type container, Name name, List<Type> argTypes, Context context) {
        return new X10MethodMatcher(container, name, argTypes, context);
    }

    public X10MethodMatcher MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        return new X10MethodMatcher(container, name, typeArgs, argTypes, context);
    }

    public X10ConstructorMatcher ConstructorMatcher(Type container, List<Type> argTypes, Context context) {
        return new X10ConstructorMatcher(container, argTypes, context);
    }

    public X10FieldMatcher FieldMatcher(Type container, Name name, Context context) {
    	//container = X10TypeMixin.ensureSelfBound( container);
        return new X10FieldMatcher(container, name, context);
    }
    public X10FieldMatcher FieldMatcher(Type container, boolean contextKnowsReceiver, Name name, Context context) {
    	//container = X10TypeMixin.ensureSelfBound( container);
        return new X10FieldMatcher(container, contextKnowsReceiver, name, context);
    }

    public boolean hasMethodNamed(Type container, Name name) {
        if (container != null)
            container = X10TypeMixin.baseType(container);

        // HACK: use the def rather than the type to avoid gratuitous
        // reinstantiation of methods.
        if (container instanceof ClassType) {
            ClassType ct = (ClassType) container;
            for (MethodDef md : ct.def().methods()) {
                if (md.name().equals(name))
                    return true;
            }
            Type superType = Types.get(ct.def().superType());
            if (superType != null && hasMethodNamed(superType, name))
                return true;
            for (Ref<? extends Type> tref : ct.def().interfaces()) {
                Type ti = Types.get(tref);
                if (ti != null && hasMethodNamed(ti, name))
                    return true;
            }
        }

        return SUPERhasMethodNamed(container, name);
    }

    public boolean isSubtype(Type t1, Type t2, Context context) {
        return env(context).isSubtype(t1, t2);
    }
    public boolean isSubtype(Type t1, Type t2) {
        return isSubtype(t1, t2, emptyContext());
    }

    
    // Returns the number of bytes required to represent the type, or null if unknown (e.g. involves an address somehow)
    // Note for rails and valrails this returns the size of 1 element, this will have to be scaled
    // by the number of elements to get the true figure.
    public Long size(Type t) {
        if (t.isFloat()) return 4l;
        if (t.isDouble()) return 8l;
        if (t.isChar()) return 2l;
        if (t.isByte()) return 1l;
        if (t.isShort()) return 2l;
        if (t.isInt()) return 4l;
        if (t.isLong()) return 8l;
        if (isRail(t) || isValRail(t)) {
            X10ClassType ctyp = (X10ClassType)t;
            assert ctyp.typeArguments().size() == 1;
            return size(ctyp.typeArguments().get(0));
        }
        return null;
    }
 
 
 
	
   private boolean isIn(Collection<FieldInstance> newFields, FieldInstance fi) {
        for (FieldInstance fi2 : newFields)
            if (fi.def()==fi2.def()) return true;
        return false;
   }
   public FieldInstance findField(Type container, TypeSystem_c.FieldMatcher matcher)
	throws SemanticException {
	   

		Context context = matcher.context();
		
		Collection<FieldInstance> fields = findFields(container, matcher);
		
		if (fields.size() >= 2) {
            // if the field is defined in a class, then it will appear only once in "fields".
            // if it is defined in an interface (then it is either a "static val" or a property such as home), then it may appear multiple times in "fields", so we need to filter duplicates.
            // e.g.,
//            interface I1 { static val a = 1;}
//            interface I2 extends I1 {}
//            interface I3 extends I1 {}
//            interface I4 extends I2,I3 {}
//            class Example implements I4 {
//              def example() = a;
//              def m(a:Example{self.home.home.home==here}) = 1;            
//            }
			Collection<FieldInstance> newFields = new HashSet<FieldInstance>();
			for (FieldInstance fi : fields) {
				if ((fi.flags().isStatic())){
                    if (!isIn(newFields,fi))
                            newFields.add(fi);
					continue;
				}
				
				if (! (fi.container().toClass().flags().isInterface())){
					newFields.add(fi);
				}
				
					
			}
			fields = newFields;
		}
		if (fields.size() == 0) {
		    throw new NoMemberException(NoMemberException.FIELD,
		                                "Field " + matcher.signature() +
		                                " not found in type \"" +
		                                container + "\".");
		}

		Iterator<FieldInstance> i = fields.iterator();
		FieldInstance fi = i.next();

		if (i.hasNext()) {
		    FieldInstance fi2 = i.next();

		    throw new SemanticException("Field " + matcher.signature() +
		                                " is ambiguous; it is defined in both " +
		                                fi.container() + " and " +
		                                fi2.container() + "."); 
		}

		if (context != null && ! isAccessible(fi, context)) {
		    throw new SemanticException("Cannot access " + fi + ".");
		}

		return fi;
	   
   }
   public void existsStructWithName(Id name, ContextVisitor tc) throws SemanticException {
 	  NodeFactory nf = (NodeFactory) tc.nodeFactory();
			TypeSystem ts = (TypeSystem) tc.typeSystem();
			Context c = tc.context();
 	  TypeBuilder tb = new TypeBuilder(tc.job(),  ts, nf);
			// First, try to determine if there in fact a struct in scope with the given name.
			TypeNode otn = new X10ParsedName(nf, ts, Position.COMPILER_GENERATED, name).toType();//
			//	nf.AmbDepTypeNode(position(), null, name(), typeArguments, Collections.EMPTY_LIST, null);
		
			TypeNode tn = (TypeNode) otn.visit(tb);
			
			// First ensure that there is a type associated with tn.
			tn = (TypeNode) tn.disambiguate(tc);
			
			// ok, if we made it this far, then there is a type. Check that it is a struct.
			Type t = tn.type();
			t = ts.expandMacros(t);

			CConstraint xc = X10TypeMixin.xclause(t);
			t = X10TypeMixin.baseType(t);

			if (!(ts.isStructType(t))) { // bail
				throw new SemanticException();
			}
     }
   public  List<Type> abstractSuperInterfaces(Type t) {
	   List<Type> result = SUPERabstractSuperInterfaces(t);
	   // A work-around for the current transient state of the system in which
	   // Object is an interface. 
	   if (isStructType(t)) {
		   result.remove(Object());
	   }
	   return result;
   }
   
   Name homeName = Name.make("home");
   public Name homeName() { return homeName;}
}
