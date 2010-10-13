/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Id;
import polyglot.ast.TypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Source;
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
import polyglot.types.InitializerDef;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.TopLevelResolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.VarDef;
import polyglot.types.TypeSystem_c.MostSpecificComparator;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Predicate2;
import polyglot.util.TransformingList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeBuilder;
import x10.ast.X10NodeFactory;
import x10.ast.X10NodeFactory_c;
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
 * A TypeSystem implementation for X10.
 *
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 */
public class X10TypeSystem_c extends TypeSystem_c implements X10TypeSystem {
	public static final String DUMMY_AT_ASYNC = "$dummyAsync"; // for async/at/ateach
	public static final int EXPAND_MACROS_DEPTH=25;

    public X10TypeSystem_c() {
        super();
    }

    public ClassDef classDefOf(Type t) {
        t = X10TypeMixin.baseType(t);
        if (t instanceof ClassType)
            return ((ClassType) t).def();
        return null;
    }
    
    @Override
    public Collection<Type> uncheckedExceptions() {
    	List<Type> l = new ArrayList<Type>(1);
    	l.add(Throwable());
    	return l;
        }

    @Override
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
//                    new TypeEqualsInEnvironment((X10Context)context, env)))
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
        X10Context context;
        TypeConstraint env;
        public TypeEqualsInEnvironment(X10Context context, TypeConstraint env) {
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
    public boolean consistent(TypeConstraint c, X10Context context) {
        return env(context).consistent(c);
    }

    /** Return true if constraints in the type are all consistent. */
    public boolean consistent(Type t, X10Context context) {
        return env(context).consistent(t);
    }

    enum Bound {
        UPPER, LOWER, EQUAL
    }

    @Override
    public Set<FieldInstance> findFields(Type container, TypeSystem_c.FieldMatcher matcher) {
        assert_(container);

        Set<FieldInstance> candidates = new HashSet<FieldInstance>();

        for (Type t : env(matcher.context()).upperBounds(container, true)) {
            Set<FieldInstance> fs = super.findFields(t, matcher);
            candidates.addAll(fs);
        }

        return candidates;
    }

    public TypeDefMatcher TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        return new TypeDefMatcher(container, name, typeArgs, argTypes, context);
    }

    @Override
    public Type findMemberType(Type container, Name name, Context context) throws SemanticException {
        // FIXME: check for ambiguities
        for (Type t : env(context).upperBounds(container, true)) {
            try {
                return super.findMemberType(t, name, context);
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

    @Override
    public List<QName> defaultOnDemandImports() {
        List<QName> l = new ArrayList<QName>(1);
        l.add(QName.make("x10.lang"));
        l.add(QName.make("x10.lang", X10TypeSystem.DUMMY_PACKAGE_CLASS_NAME.toString()));
        l.add(QName.make("x10.array"));
        return l;
    }

    public List<MacroType> findAcceptableTypeDefs(Type container, TypeDefMatcher matcher, Context context) throws SemanticException {
        assert_(container);
        return env(context).findAcceptableTypeDefs(container, matcher);
    }

    @Override
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

    @Override
    public Matcher<Named> TypeMatcher(Name name) {
        return new X10TypeMatcher(name);
    }

    @Override
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

    @Override
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
        Position pos = X10NodeFactory_c.compilerGenerated(container);
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
        Position pos = X10NodeFactory_c.compilerGenerated(container);
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
        Position pos = X10NodeFactory_c.compilerGenerated(container);
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

    @Override
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

    public List<FunctionType> getFunctionSupertypes(Type t, X10Context context) {
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

    public Kind kind(Type t, X10Context c) {
        return env(c).kind(t);
    }

    public boolean isParameterType(Type t) {
        t = X10TypeMixin.baseType(t);
        return t instanceof ParameterType;
    }

    public boolean isObjectOrInterfaceType(Type t, X10Context c) {
        Kind kind = kind(t, c);
        return kind == Kind.OBJECT || kind == Kind.INTERFACE;
    }

    public boolean isObjectType(Type t, X10Context c) {
        return kind(t, c) == Kind.OBJECT;
    }

    
    public boolean isStructType(Type t) {
        return kind(t, null) == Kind.STRUCT;
    }

    @Override
    public Type arrayOf(Position pos, Ref<? extends Type> type) {
        // Should be called only by the Java class file loader.
        Type r = Rail();
        return X10TypeMixin.instantiate(r, type);
    }

    @Override
    public X10ClassDef createClassDef(Source fromSource) {
        return new X10ClassDef_c(this, fromSource);
    }

    @Override
    public X10ParsedClassType createClassType(Position pos, Ref<? extends ClassDef> def) {
        return new X10ParsedClassType_c(this, pos, def);
    }

    @Override
    public X10ConstructorInstance createConstructorInstance(Position pos, Ref<? extends ConstructorDef> def) {
        return new X10ConstructorInstance_c(this, pos, (Ref<? extends X10ConstructorDef>) def);
    }

    @Override
    public X10MethodInstance createMethodInstance(Position pos, Ref<? extends MethodDef> def) {
        return new X10MethodInstance_c(this, pos, (Ref<? extends X10MethodDef>) def);
    }

    @Override
    public X10FieldInstance createFieldInstance(Position pos, Ref<? extends FieldDef> def) {
        return new X10FieldInstance_c(this, pos, (Ref<? extends X10FieldDef>) def);
    }

    @Override
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
        return new X10Context_c(this);
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
        return X10Flags.toX10Flags(super.legalTopLevelClassFlags()).Clocked().Safe().Struct();
    }

    protected final X10Flags X10_TOP_LEVEL_CLASS_FLAGS = (X10Flags) legalTopLevelClassFlags();

    /** All flags allowed for an interface. */
    public Flags legalInterfaceFlags() {
        return X10Flags.toX10Flags(super.legalInterfaceFlags()).Clocked().Safe().Value();
    }

    protected final X10Flags X10_INTERFACE_FLAGS = (X10Flags) legalInterfaceFlags();

    /** All flags allowed for a member class. */
    public Flags legalMemberClassFlags() {
        return X10Flags.toX10Flags(super.legalMemberClassFlags()).Clocked().Safe().Struct();
    }

    protected final Flags X10_MEMBER_CLASS_FLAGS = (X10Flags) legalMemberClassFlags();

    /** All flags allowed for a local class. */
    public Flags legalLocalClassFlags() {
        return X10Flags.toX10Flags(super.legalLocalClassFlags()).Safe().Struct();
    }

    protected final X10Flags X10_LOCAL_CLASS_FLAGS = (X10Flags) legalLocalClassFlags();

    @Override
    public Flags legalLocalFlags() {
        return X10Flags.toX10Flags(super.legalLocalFlags()).Clocked();
    }

    protected final X10Flags X10_LOCAL_VARIABLE_FLAGS = (X10Flags) legalLocalFlags();

    @Override
    public Flags legalFieldFlags() {
        return X10Flags.toX10Flags(super.legalFieldFlags()).Property().Clocked();
    }

    protected final X10Flags X10_FIELD_VARIABLE_FLAGS = (X10Flags) legalFieldFlags();

    @Override
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

    @Override
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

        public Void(X10TypeSystem ts) {
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
        Flags sf = super.flagsForBits(bits);
        if (sf.isInterface())
            return sf.Static();
        return sf;
    }

    @Override
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
        return super.isNumeric(t) || isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
    }

    public boolean isIntOrLess(Type t) {
        if (isChar(t))
            return false;
        return super.isIntOrLess(t) || isUByte(t) || isUShort(t);
    }

    public boolean isLongOrLess(Type t) {
        if (isChar(t))
            return false;
        return super.isLongOrLess(t) || isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
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

    public boolean entails(CConstraint c1, CConstraint c2, X10Context context, Type selfType) {
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

    @Override
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
        VarDef v = localDef(X10NodeFactory_c.compilerGenerated(t), Flags.PUBLIC, Types.ref(t), Name.make("self"));
        return v;
    }

 
    protected XTypeTranslator xtt = new XTypeTranslator(this);

    public XTypeTranslator xtypeTranslator() {
        return xtt;
    }

    @Override
    public void initialize(TopLevelResolver loadedResolver, ExtensionInfo extInfo) throws SemanticException {
        super.initialize(loadedResolver, extInfo);
    }

    public boolean equivClause(Type me, Type other, X10Context context) {
        return entailsClause(me, other, context) && entailsClause(other, me, context);
    }

    public boolean entailsClause(CConstraint c1, CConstraint c2, X10Context context, Type selfType) {
        return entails(c1, c2, context, selfType);
    }

    public boolean entailsClause(Type me, Type other, X10Context context) {
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

    @Override
    public Flags createNewFlag(String name, Flags after) {
        Flags f = X10Flags.createFlag(name, after);
        flagsForName.put(name, f);
        return f;
    }

    @Override
    protected void initFlags() {
        super.initFlags();
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
    @Override
    public Flags legalConstructorFlags() {
        return legalAccessFlags().Synchronized().Native(); // allow native (but
                                                           // not extern)
    }

    protected final Flags X10_METHOD_FLAGS = legalMethodFlags();

    @Override
    public void checkMethodFlags(Flags f) throws SemanticException {
        // Report.report(1, "X10TypeSystem_c:method_flags are |" +
        // X10_METHOD_FLAGS + "|");
        if (!f.clear(X10_METHOD_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare method with flags " + f.clear(X10_METHOD_FLAGS) + ".");
        }

        if (f.isAbstract() && !f.clear(ABSTRACT_METHOD_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare abstract method with flags " + f.clear(ABSTRACT_METHOD_FLAGS) + ".");
        }

        checkAccessFlags(f);
    }

    @Override
    public void checkTopLevelClassFlags(Flags f) throws SemanticException {
        if (!f.clear(X10_TOP_LEVEL_CLASS_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare a top-level class with flag(s) " + f.clear(X10_TOP_LEVEL_CLASS_FLAGS) + ".");
        }

        if (f.isInterface() && !f.clear(X10_INTERFACE_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare interface with flags " + f.clear(X10_INTERFACE_FLAGS) + ".");
        }

        checkAccessFlags(f);
    }

    @Override
    public void checkMemberClassFlags(Flags f) throws SemanticException {
        if (!f.clear(X10_MEMBER_CLASS_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException("Cannot declare a member class with flag(s) " + f.clear(X10_MEMBER_CLASS_FLAGS) + ".");
        }

        checkAccessFlags(f);
    }

    @Override
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

    @Override
    public Type promote(Type t) throws SemanticException {
        Type pt = promote2(t);
        return X10TypeMixin.baseType(pt);
    }

    @Override
    public Type promote(Type t1, Type t2) throws SemanticException {
        Type pt = promote2(t1, t2);
        return X10TypeMixin.baseType(pt);
    }

    @Override
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

    @Override
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

    @Override
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
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        CConstraint c = xts.xtypeTranslator().binaryOp(op, cl, cr);
        return X10TypeMixin.xclause(X10TypeMixin.baseType(t), c);
    }

    public Type performUnaryOperation(Type t, Type a, polyglot.ast.Unary.Operator op) {
        CConstraint ca = X10TypeMixin.realX(a);
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
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

    @Override
    public boolean descendsFrom(ClassDef child, ClassDef ancestor) {
        ClassDef a = classDefOf(Any());

        if (ancestor == a)
            return true;

        if (child == a)
            return false;

        return super.descendsFrom(child, ancestor);
    }

    @Override
    public X10TypeEnv env(Context context) {
        return new X10TypeEnv_c(context == null ? emptyContext() : context);
    }

    @Override
    public List<MethodInstance> findAcceptableMethods(Type container, MethodMatcher matcher) throws SemanticException {

        X10MethodMatcher m = (X10MethodMatcher) matcher;

        List<MethodInstance> candidates = new ArrayList<MethodInstance>();
 
        List<Type> types = env(matcher.context()).upperBounds(container, true);
        for (Type t : types) {
            List<MethodInstance> ms = super.findAcceptableMethods(t, matcher);
            candidates.addAll(ms);
        }

        return candidates;
    }

    @Override
    public X10MethodInstance findMethod(Type container, MethodMatcher matcher) throws SemanticException {
        return (X10MethodInstance) super.findMethod(container, matcher);
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

    @Override
    public X10ConstructorInstance findConstructor(Type container, polyglot.types.TypeSystem_c.ConstructorMatcher matcher) throws SemanticException {
        return (X10ConstructorInstance) super.findConstructor(container, matcher);
    }

    public Collection<X10ConstructorInstance> findConstructors(Type container, polyglot.types.TypeSystem_c.ConstructorMatcher matcher) throws SemanticException {
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

        return super.hasMethodNamed(container, name);
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
 	  X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
			X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
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
	   List<Type> result = super.abstractSuperInterfaces(t);
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
