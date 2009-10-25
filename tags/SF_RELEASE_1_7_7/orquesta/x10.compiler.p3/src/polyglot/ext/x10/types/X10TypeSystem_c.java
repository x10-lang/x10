/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.types.SubtypeSolver.XSubtype_c;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
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
import polyglot.types.Def;
import polyglot.types.DerefTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.TopLevelResolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.VarDef;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.util.TransformingList;
import polyglot.util.WorkList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XRef_c;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.Equality;

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
	
	public AnnotatedType AnnotatedType(Position pos, Type baseType, List<Type> annotations) {
	    return new AnnotatedType_c(this, pos, baseType, annotations);
	}
	
	public boolean equalsStruct(Type l, Type r) {
//	    if (l instanceof ParameterType && r instanceof ParameterType) {
//	        return TypeParamSubst.isSameParameter((ParameterType) l, (ParameterType) r);
//	    }
	    return equals((TypeObject) l, (TypeObject) r);
	}
	
    public List<Type> getBoundsFromConstraint(Type pt, XConstraint c, Bound dir) {
	    if (c == null)
	        return Collections.EMPTY_LIST;

	    List<Type> upper = new ArrayList<Type>();
	    List<Type> lower = new ArrayList<Type>();
	    
	    for (XTerm term : c.constraints()) {
	        if (term instanceof XEquals) {
	            XEquals eq = (XEquals) term;
	            Type l = SubtypeSolver.getType(eq.left());
	            Type r = SubtypeSolver.getType(eq.right());
	            if (l != null && r != null) {
	                if (equalsStruct(l, pt)) {
	                    upper.add(r);
	                    lower.add(r);
	                }
	                if (equalsStruct(r, pt)) {
	                    upper.add(l);
	                    lower.add(l);
	                }
	            }
	        }
	        if (term instanceof XSubtype_c) {
	            XSubtype_c s = (XSubtype_c) term;
	            Type l = s.subtype();
	            Type r = s.supertype();
	            if (l != null && r != null) {
	                if (equalsStruct(l, pt))
	                    upper.add(r);
	                if (equalsStruct(r, pt))
	                    lower.add(l);
	            }
	        }
	    }
	    
	    switch (dir) {
	    case UPPER:
	        return upper;
	    case LOWER:
	        return lower;
	    case EQUAL:
	        Set<Type> equals = new HashSet<Type>();
	        equals.addAll(upper);
	        equals.retainAll(lower);
	        return new ArrayList<Type>(equals);
	    }
	    
	    return Collections.EMPTY_LIST;
	}
    
    enum Bound {
        UPPER, LOWER, EQUAL
    }
    
    public List<Type> upperBounds(Type t) {
        return bounds(t, Bound.UPPER);
    }
    public List<Type> lowerBounds(Type t) {
        return bounds(t, Bound.LOWER);
    }
    public List<Type> equalBounds(Type t) {
        return bounds(t, Bound.EQUAL);
    }

	/** List the statically known supertypes or subtypes. */
	protected List<Type> bounds(Type t, Bound dir) {
	    List<Type> result = new ArrayList<Type>();
	    Set<Type> visited = new HashSet<Type>();
	    
	    LinkedList<Type> worklist = new LinkedList<Type>();
	    worklist.add(t);

	    while (! worklist.isEmpty()) {
	        Type w = worklist.removeFirst();
	        
	        // Expand macros, remove constraints
	        Type expanded = X10TypeMixin.baseType(w);
	        
                if (visited.contains(expanded)) {
                    continue;
                }
                
                visited.add(expanded);
                
                if (expanded instanceof PathType) {
                    // p: C{self.T<:S}  implies  p.T <: S
                    PathType pt1 = (PathType) expanded;
                    TypeProperty px = pt1.property();
                    XVar base = PathType_c.pathBase(pt1);
                    if (base != null) {
                        final XConstraint c = base.selfConstraint();
                        if (c != null) {
                            try {
                                // Check if the constraint on the base p implies that p.T <: S
                                // Avoid infinite recursion by removing selfConstraint on base.
                                XConstraint c1 = c.substitute(base, XSelf.Self);
                                List<Type> b = getBoundsFromConstraint(pt1, c1, dir);
                                worklist.addAll(b);
                            }
                            catch (XFailure e) {
                            }
                        }
                    }
                    
                    continue;
                }
                
	        if (expanded instanceof ParameterType) {
	            ParameterType pt = (ParameterType) expanded;
	            Def def = Types.get(pt.def());
	            if (def instanceof X10ClassDef) {
	                X10ClassDef cd = (X10ClassDef) def;
	                XConstraint c = X10TypeMixin.realX(cd.asType());
	                List<Type> b = getBoundsFromConstraint(pt, c, dir);
	                worklist.addAll(b);
	            }
	            if (def instanceof X10MethodDef) {
	                X10MethodDef md = (X10MethodDef) def;
	                XConstraint c = Types.get(md.guard());
	                List<Type> b = getBoundsFromConstraint(pt, c, dir);
	                worklist.addAll(b);
	            }
	            if (def instanceof X10ConstructorDef) {
	                X10ConstructorDef cd = (X10ConstructorDef) def;
	                XConstraint c = Types.get(cd.guard());
	                List<Type> b = getBoundsFromConstraint(pt, c, dir);
	                worklist.addAll(b);
	            }
	            continue;
	        }
	        
                result.add(expanded);
	    }
	    
	    if (dir == Bound.UPPER && result.isEmpty())
	        return Collections.singletonList(Object());
	    
	    return new ArrayList<Type>(result);
	}
	
	@Override
	protected Set<FieldInstance> findFields(Type container, TypeSystem_c.FieldMatcher matcher) {
	    assert_(container);

	    Set<FieldInstance> candidates = new HashSet<FieldInstance>();

	    for (Type t : upperBounds(container)) {
	        Set<FieldInstance> fs = super.findFields(t, matcher);
	        candidates.addAll(fs);
	    }

	    return candidates;
	}
	
	public TypeDefMatcher TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes) {
	    return new TypeDefMatcher(container, name, typeArgs, argTypes);
	}
	
	@Override
	public Type findMemberType(Type container, Name name, ClassDef currClass) throws SemanticException {
	    // FIXME: check for ambiguities
	    for (Type t : upperBounds(container)) {
	        try {
	            return super.findMemberType(t, name, currClass);
	        }
	        catch (SemanticException e) {
	        }
	        try {
	            return this.findTypeDef(t, this.TypeDefMatcher(t, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST), currClass);
	        }
	        catch (SemanticException e) {
	        }
	        try {
	            return this.findTypeProperty(t, name, currClass);
	        }
	        catch (SemanticException e) {
	        }
	    }

	    throw new NoClassException(name.toString(), container);
	}

	public X10ClassDef closureAnonymousClassDef(final ClosureDef def) {
	    final X10TypeSystem xts = this;
	    final Position pos = def.position();

	    X10ClassDef cd = new X10ClassDef_c(this, null);
	    /*
	     * {
	        @Override
	        public ClassType asType() {
	            if (asType == null) {
	                // Initialize asType to ClosureType so we don't return a X10ParsedClassType instead.
	                asType = new ClosureType_c(xts, pos, this, (ClosureInstance) def.asInstance());
	            }
	            return asType;
		}
	    };
*/
	    
	    cd.position(pos);
	    cd.name(null);
	    cd.setPackage(null);
	    cd.kind(ClassDef.ANONYMOUS);
	    cd.flags(Flags.NONE);

	    int numTypeParams = def.typeParameters().size();
	    int numValueParams = def.formalTypes().size();

	    // Add type parameters.
	    List<Ref<? extends Type>> typeParams = new ArrayList<Ref<? extends Type>>();
	    List<Type> typeArgs = new ArrayList<Type>();
	    
	    ClosureInstance ci = (ClosureInstance) def.asInstance();
	    typeArgs.addAll(ci.formalTypes());
	    typeArgs.add(ci.returnType());

//	    for (int i = 0; i < numValueParams; i++) {
//		ParameterType t = new ParameterType_c(this, pos, Name.make("Z" + (i+1)), Types.ref(cd));
//		cd.addTypeParameter(t, TypeProperty.Variance.CONTRAVARIANT);
//		typeArgs.add(t);
//	    }
//
//	    ParameterType returnType = new ParameterType_c(this, pos, Name.make("U"), Types.ref(cd));
//	    cd.addTypeParameter(returnType, TypeProperty.Variance.COVARIANT);
//	    typeArgs.add(returnType);
	    
	    // Instantiate the super type on the new parameters.
	    X10ClassType sup = (X10ClassType) closureBaseInterfaceDef(numTypeParams, numValueParams).asType();
	    assert sup.x10Def().typeParameters().size() == typeArgs.size();
	    sup = sup.typeArguments(typeArgs);
	    
	    cd.superType(Types.ref(Value())); // Closures are values.
	    cd.addInterface(Types.ref(sup));
	    
	    return cd;
	}
	
	public X10ClassDef closureBaseInterfaceDef(final int numTypeParams, final int numValueParams) {
	    final X10TypeSystem xts = this;
	    final Position pos = Position.COMPILER_GENERATED;
	    String name = "Fun_" + numTypeParams + "_" + numValueParams;
	    
	    // Check if the class has already been defined.
	    QName fullName = QName.make("x10.lang", name);
	    Named n = xts.systemResolver().check(fullName);
	    
	    if (n instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) n;
	        return ct.x10Def();
	    }

	    X10ClassDef cd = (X10ClassDef) new X10ClassDef_c(this, null) {
	        @Override
	        public ClassType asType() {
	            if (asType == null) {
	                X10ClassDef cd = this;
	                asType = new ClosureType_c(xts, pos, this);
	            }
	            return asType;
	        }
	    };
	        
	    cd.position(pos);
	    cd.name(Name.make(name));
	    try {
	        cd.setPackage(Types.ref(xts.packageForName(fullName.qualifier())));
	    }
	    catch (SemanticException e) {
	        assert false;
	    }
	    
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.superType(null); // interfaces have no superclass
	    cd.flags(Flags.PUBLIC.Abstract().Interface());

	    final List<Ref<? extends Type>> typeParams = new ArrayList<Ref<? extends Type>>();
	    final List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
            
            for (int i = 0; i < numTypeParams; i++) {
                Type t = new ParameterType_c(xts, pos, Name.make("X" + i), Types.ref(cd));
                typeParams.add(Types.ref(t));
            }

            for (int i = 0; i < numValueParams; i++) {
                ParameterType t = new ParameterType_c(xts, pos, Name.make("Z" + (i+1)), Types.ref(cd));
                argTypes.add(Types.ref(t));
                cd.addTypeParameter(t, TypeProperty.Variance.CONTRAVARIANT);
            }

            ParameterType returnType = new ParameterType_c(xts, pos, Name.make("U"), Types.ref(cd));
            cd.addTypeParameter(returnType, TypeProperty.Variance.COVARIANT);

            // NOTE: don't call cd.asType() until after the type parameters are added.
            ClosureType ct = (ClosureType) cd.asType();
            xts.systemResolver().install(fullName, ct);
	    
	    X10MethodDef mi = methodDef(pos, Types.ref(ct), Flags.PUBLIC.Abstract(),
	                                Types.ref(returnType), Name.make("apply"), typeParams, argTypes,
	                                dummyLocalDefs(argTypes), null, Collections.EMPTY_LIST, null);
	    cd.addMethod(mi);
	    
	    return cd;
	}
	
	public List<LocalDef> dummyLocalDefs(List<Ref<? extends Type>> types) {
	    List<LocalDef> list = new ArrayList<LocalDef>();
	    for (int i = 0; i < types.size(); i++) {
		LocalDef ld = localDef(Position.COMPILER_GENERATED, Flags.FINAL, types.get(i), Name.make("a" + (i+1)));
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
	    return l;
	}
	
	static class TypeDefMatcher implements Matcher<Named> {
	    Type container;
	    Name name;
	    List<Type> typeArgs;
	    List<Type> argTypes;
	    
	    public TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes) {
		this.container = container;
		this.name = name;
		this.typeArgs = typeArgs;
		this.argTypes = argTypes;
	    }

	    public Name name() {
		return name;
	    }
	    public String argumentString() {
		return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]")
		+ (argTypes.isEmpty() ? "" : "(" + CollectionUtil.listToString(argTypes) + ")");
	    }
	    public String signature() {
		return name + argumentString();
	    }
	    public String toString() {
		return signature();
	    }

	    public MacroType instantiate(Named n) throws SemanticException {
	        if (n instanceof MacroType) {
	            MacroType mi = (MacroType) n;
	            if (! mi.name().equals(name))
	                return null;
	            if (mi.formalTypes().size() != argTypes.size())
	                return null;
	            if (mi instanceof MacroType) {
	                MacroType xmi = (MacroType) mi;
	                Type c = container != null ? container : xmi.container();
	                if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
	                    return X10MethodInstance_c.instantiate(xmi, c, typeArgs, argTypes, true);
	            }
	        }
	        return null;
	    }

	    public Object key() {
	        return null;
	    }
	}
	
	protected List<MacroType> findAcceptableTypeDefs(Type container, TypeDefMatcher matcher, ClassDef currClass)
	    throws SemanticException {
	    assert_(container);
	    
	    SemanticException error = null;
	    
	    // The list of acceptable methods. These methods are accessible from
	    // currClass, the method call is valid, and they are not overridden
	    // by an unacceptable method (which can occur with protected methods
	    // only).
	    List<MacroType> acceptable = new ArrayList<MacroType>();
	    
	    // A list of unacceptable methods, where the method call is valid, but
	    // the method is not accessible. This list is needed to make sure that
	    // the acceptable methods are not overridden by an unacceptable method.
	    List<MacroType> unacceptable = new ArrayList<MacroType>();
	    
	    Set<Type> visitedTypes = new HashSet<Type>();
	    
	    LinkedList<Type> typeQueue = new LinkedList<Type>();
	    
	    // Get the upper bound of the container.
	    typeQueue.addAll(upperBounds(container));

	    while (! typeQueue.isEmpty()) {
	        Type t = typeQueue.removeFirst();

	        if (t instanceof X10ParsedClassType) {
	            X10ParsedClassType type = (X10ParsedClassType) t;

	            if (visitedTypes.contains(type)) {
	                continue;
	            }

	            visitedTypes.add(type);

	            if (Report.should_report(Report.types, 2))
	                Report.report(2, "Searching type " + type + " for method " +
	                              matcher.signature());

	            for (Iterator<Type> i = type.typeMembers().iterator(); i.hasNext(); ) {
	                Type ti = i.next();

	                if (!(ti instanceof MacroType)) {
	                    continue;	    		
	                }

	                MacroType mi = (MacroType) ti;

	                if (Report.should_report(Report.types, 3))
	                    Report.report(3, "Trying " + mi);

	                try {
	                    mi = matcher.instantiate(mi);

	                    if (mi == null) {
	                        continue;
	                    }

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

	                    continue;
	                }
	                catch (SemanticException e) {
	                }

	                if (error == null) {
	                    error = new SemanticException("Type definition " + mi.name() +
	                                                  " in " + container +
	                                                  " cannot be instantiated with arguments " + matcher.argumentString() + ".");
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
	    
	    if (error == null) {
		error = new SemanticException("No type defintion found in "
		                            + container +
		                              " for " + matcher.signature() + ".");
	    }
	    
	    if (acceptable.size() == 0) {
	    	throw error;
	    }
	    
	    // remove any types in acceptable that are overridden by an
	    // unacceptable
	    // type.
	    // TODO
//	    for (Iterator<MacroType> i = unacceptable.iterator(); i.hasNext();) {
//		MacroType mi = i.next();
//	    	acceptable.removeAll(mi.overrides());
//	    }
	    
	    if (acceptable.size() == 0) {
	    	throw error;
	    }
	    
	    return acceptable;
	}
	
	public MacroType findTypeDef(Type container, TypeDefMatcher matcher, ClassDef currClass) throws SemanticException {
		List<MacroType> acceptable = findAcceptableTypeDefs(container, matcher, currClass);
		
		if (acceptable.size() == 0) {
			throw new SemanticException(
					"No valid type definition found for " + matcher.signature() +
					" in " +
					container + ".");
		}
		
		Collection<MacroType> maximal =
			findMostSpecificProcedures(acceptable);
		
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
		
			throw new SemanticException("Reference to " + matcher.name() +
					" is ambiguous, multiple type defintions match: "
					+ sb.toString());
		}
		
		MacroType mi = maximal.iterator().next();
		
		return mi;
	}
	
	public List<MacroType> findTypeDefs(Type container, Name name, ClassDef currClass) throws SemanticException {
	    assert_(container);
	    
//	    Named n = classContextResolver(container, currClass).find(name);
//	    
//	    if (n instanceof MacroType) {
//		return (MacroType) n;
//	    }
	    
	    throw new NoClassException(name.toString(), container);
	}
	
	
	public PathType findTypeProperty(Type container, Name name, ClassDef currClass) throws SemanticException {
	    assert_(container);

	    // FIXME: look for ambiguities.
	    for (Type bound : upperBounds(container)) {
	        Named n = classContextResolver(bound, currClass).find(MemberTypeMatcher(container, name));

	        if (n instanceof PathType) {
	            return (PathType) n;
	        }
	    }

	    throw new NoClassException(name.toString(), container);
	}
	
	private final static Set<String> primitiveTypeNames = new HashSet<String>();
	
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

	public boolean isPrimitiveTypeName(Name name) {
		return primitiveTypeNames.contains(name.toString());
	}

	public ClassDef unknownClassDef() {
	    if (unknownClassDef == null) {
	        unknownClassDef = new X10ClassDef_c(this, null);
	        unknownClassDef.name(Name.make("<unknown class>"));
	        unknownClassDef.kind(ClassDef.TOP_LEVEL);
	    }
	    return unknownClassDef;
	}
	
	X10UnknownType_c unknownType;
	
	@Override
	protected ClassType load(String name) {
	    QName qualName = QName.make(name);
	    try {
	        return (ClassType) typeForName(qualName);
		}
		catch (SemanticException e) {
			Globals.Compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR, "Cannot load X10 runtime class \"" + name + "\".  Is the X10 runtime library in your classpath or sourcepath?");
			Goal goal = Globals.currentGoal();
			if (goal == null)
				goal.fail();
			return createFakeClass(qualName);
		}
	}
	
	private ClassType createFakeClass(QName fullName) {
	    X10ClassDef cd = (X10ClassDef) createClassDef();
	    cd.name(fullName.name());
	    cd.position(Position.COMPILER_GENERATED);
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.flags(Flags.PUBLIC);
	    cd.superType(null);

	    try {
		cd.setPackage(Types.ref(packageForName(fullName.qualifier())));
	    }
	    catch (SemanticException e) { }
	    
	    systemResolver().install(fullName, cd.asType());

	    return (X10ParsedClassType) cd.asType();

	}
	
	@Override
	public UnknownType unknownType(Position pos) {
		if (unknownType == null)
			unknownType = new X10UnknownType_c(this);
		return unknownType;
	}

	private X10ParsedClassType refType_;
	public Type Ref() {
		if (refType_ == null)
		    refType_ = (X10ParsedClassType) load("x10.lang.Ref"); 
		return refType_;
	}

	private X10ParsedClassType boxType_;
	public Type Box() {
		if (boxType_ == null)
			boxType_ = createBoxType();
		return boxType_;
	}
	
	public X10ParsedClassType createBoxType() {
	    X10ClassDef cd = new X10ClassDef_c(this, null) {
		{
		    // Initialize asType to BoxType so we don't return a X10ParsedClassType instead.
		    asType = new BoxType_c(this);
		}
	    };

	    cd.name(Name.make("Box"));
	    cd.position(Position.COMPILER_GENERATED);
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.flags(Flags.PUBLIC.Final());
	    try {
		cd.setPackage(Types.ref(packageForName(QName.make("x10.lang"))));
	    }
	    catch (SemanticException e) {
		throw new InternalCompilerError(e);
	    }
	    
	    Type pt;
	    if (X10ClassDecl_c.CLASS_TYPE_PARAMETERS) {
		ParameterType param = new ParameterType_c(this, Position.COMPILER_GENERATED, Name.make("T"), Types.ref(cd));
		cd.addTypeParameter(param, TypeProperty.Variance.COVARIANT);
		pt = param;
	    }
	    else {
		TypeProperty prop = new TypeProperty_c(this, Position.COMPILER_GENERATED, Types.ref((X10ClassType) cd.asType()), Name.make("T"), TypeProperty.Variance.INVARIANT);
		cd.addTypeProperty(prop);
		pt = prop.asType();
	    }

//	    X10FieldDef fi = (X10FieldDef) fieldDef(Position.COMPILER_GENERATED, Types.ref(cd.asType()), Flags.FINAL.Public(), Types.ref(pt), "value");
//	    fi.setProperty();
//	    cd.addField(fi);

//	    ParameterType pt = new ParameterType_c(this, Position.COMPILER_GENERATED, "T", null);
//	    XConstraint c = new XConstraint_c();
//	    c.addBinding(prop.asVar(), param);
//	    c.addBinding(xtypeTranslator().trans(XSelf.Self, fi), value);
//	    Type returnType = X10TypeMixin.xclause(Types.ref(cd.asType()), c);
//	    X10ConstructorDef ci = constructorDef(Position.COMPILER_GENERATED, Types.ref(cd.asType()), Flags.PUBLIC, Types.ref(returnType), params, formals, Collections.EMPTY_LIST);
//	    cd.addConstructor(ci);
	    
	    systemResolver().install(QName.make(cd.fullName()), cd.asType());

	    return (BoxType_c) cd.asType();
	}
	

	protected ClassType valueType_;
	public Type Value() {
		if (valueType_ == null)
		    valueType_ = load("x10.lang.Value");
		return valueType_;
	}

	@Deprecated
	private X10ParsedClassType createRefType() {
	    X10ClassDef cd = (X10ClassDef) createClassDef();
	    
	    cd.name(Name.make("Ref"));
	    cd.position(Position.COMPILER_GENERATED);
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.flags(Flags.PUBLIC);
	    
	    cd.superType(null);
	    
	    X10ParsedClassType O = (X10ParsedClassType) Object();
	    cd.addInterface(Types.ref(O));
	    
	    cd.addConstructor(defaultConstructor(Position.COMPILER_GENERATED, Types.ref(cd.asType())));

	    // Copy the methods from x10.lang.Object, making them native
	    for (MethodInstance mi : O.methods()) {
		MethodDef md = mi.def();
		md = (MethodDef) md.copy();
		md.setFlags(md.flags().clearAbstract().Native());
		md.setContainer(Types.ref(cd.asType()));
		cd.addMethod(md);
	    }

	    try {
		cd.setPackage(Types.ref(packageForName(QName.make("x10.lang"))));
	    }
	    catch (SemanticException e) {
	    }
	    
	    systemResolver().install(QName.make(cd.fullName()), cd.asType());

	    return (X10ParsedClassType) cd.asType();
	}
	
	@Deprecated
	private X10ParsedClassType createValueType() {
	    X10ClassDef cd = (X10ClassDef) createClassDef();
	    
	    cd.name(Name.make("Value"));
	    cd.position(Position.COMPILER_GENERATED);
	    cd.kind(ClassDef.TOP_LEVEL);
	    cd.flags(X10Flags.VALUE.Public());
	    
	    cd.superType(null);
	    
	    X10ParsedClassType O = (X10ParsedClassType) Object();
	    cd.addInterface(Types.ref(O));
	    
	    cd.addConstructor(defaultConstructor(Position.COMPILER_GENERATED, Types.ref(cd.asType())));
	    
	    // Copy the methods from x10.lang.Object, making them native
	    for (MethodInstance mi : O.methods()) {
		MethodDef md = mi.def();
		md = (MethodDef) md.copy();
		md.setFlags(md.flags().clearAbstract().Native());
		md.setContainer(Types.ref(cd.asType()));
		cd.addMethod(md);
	    }

	    try {
		cd.setPackage(Types.ref(packageForName(QName.make("x10.lang"))));
	    }
	    catch (SemanticException e) {
		throw new InternalCompilerError(e);
	    }
	    
	    systemResolver().install(QName.make(cd.fullName()), cd.asType());
	    
	    return (X10ParsedClassType) cd.asType();
	}
	
	public Type boxOf(Ref<? extends Type> base) {
		return boxOf(Position.COMPILER_GENERATED, base);
	}
	
	public List<ClosureType> getFunctionSupertypes(Type t) {
	    if (t == null)
		return Collections.EMPTY_LIST;
	    t = X10TypeMixin.baseType(t);
	    t = expandMacros(t);
	    if (t instanceof ClosureType)
		return Collections.singletonList((ClosureType) t);
	    if (t instanceof ObjectType) {
		List<ClosureType> l = Collections.EMPTY_LIST;
		ObjectType ot = (ObjectType) t;
		Type sup = ot.superClass();
		if (sup != null) {
		    List<ClosureType> supFunctions = getFunctionSupertypes(sup);
		    if (! supFunctions.isEmpty())
			l = supFunctions;
		}
		for (Type ti : ot.interfaces()) {
		    List<ClosureType> supFunctions = getFunctionSupertypes(ti);
		    if (! supFunctions.isEmpty()) {
			if (l.isEmpty())
			    l = supFunctions;
			else {
			    l = new ArrayList<ClosureType>(l);
			    l.addAll(supFunctions);
			}
		    }
		}
		return l;
	    }
	    return Collections.EMPTY_LIST;
	}
	
	public boolean isFunction(Type t) {
	    return ! getFunctionSupertypes(t).isEmpty();
	}

	public boolean isBox(Type t) {
		X10TypeSystem ts = this;
		if (ts.descendsFrom(X10TypeMixin.baseType(t), ts.Box())) {
			return true;
		}
		return false;
	}
	
	public boolean isInterfaceType(Type t) {
	    t =  X10TypeMixin.baseType(t);
	    if (t instanceof ClassType)
	        if (((ClassType) t).flags().isInterface())
	            return true;
	    return false;
	}
	
	public boolean isReferenceType(Type t) {
	    return isX10BaseSubtype(t, Ref()) ;
	}
	
	public  boolean isValueType(Type t) {
	    return isX10BaseSubtype(t, Value());
	}
	
//	public TypeDef BoxRefTypeDef() {
//		if (boxRefTypeDef_ == null) {
//			Ref<TypeDef> r = Types.ref(null);
//			Type param = new ParameterType_c(this, Position.COMPILER_GENERATED, "T", r);
//			XConstraint subtypeOfRef = new XConstraint_c();
//			try {
//				subtypeOfRef.addAtom(xtypeTranslator().transSubtype(param, Ref()));
//			}
//			catch (XFailure e) {
//			}
//			boxRefTypeDef_ = new TypeDef_c(this, Position.COMPILER_GENERATED, Flags.PUBLIC, "Box", null, (List) Collections.singletonList(Types.ref(param)), Collections.EMPTY_LIST, Collections.EMPTY_LIST, Types.ref(subtypeOfRef), Types.ref(param));
//			r.update(boxRefTypeDef_);
//		}
//		return boxRefTypeDef_;
//	}

//	public X10NamedType createBoxFromTemplate(X10ClassDef def) {
//		X10ClassDef_c c = def;
//	}
	
//	private X10ParsedClassType createBox() {
//		X10ClassDef cd = (X10ClassDef) createClassDef();
//		try {
//			cd.package_(Types.ref(packageForName(Types.ref(packageForName("x10")), "lang")));
//		}
//		catch (SemanticException e) {
//		}
//		cd.name("Box");
//		cd.flags(Flags.FINAL.Public());
//		cd.position(Position.COMPILER_GENERATED);
//		cd.kind(ClassDef.TOP_LEVEL);
//		Ref<Type> paramRef = Types.ref(null);
//		X10ConstructorDef ci = constructorDef(Position.COMPILER_GENERATED, Types.ref(cd.asType()), Flags.PUBLIC.Native(), Types.ref(cd.asType()),
//						      (List) Collections.singletonList(paramRef), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
//		Type param = new ParameterType_c(this, Position.COMPILER_GENERATED, "X", Types.ref(ci));
//		paramRef.update(param);
//		cd.addConstructor(ci);
//		X10ParsedClassType ct = (X10ParsedClassType) cd.asType();
//		TypeProperty p = new TypeProperty_c(this, Position.COMPILER_GENERATED, Types.<X10ClassType>ref(ct), "T", TypeProperty.Variance.INVARIANT);
//		cd.addTypeProperty(p);
//		return ct;
//	}
	
	@Override
	public Type arrayOf(Position pos, Ref<? extends Type> type) {
	    // Should be called only by the Java class file loader.
	    Type r = Rail();
	    return X10TypeMixin.instantiate(r, type);
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
		x = x.Safe().Local().NonBlocking().Sequential().Incomplete().Property().Pure().Extern();
		return x;
		
	}
	
	 public Flags legalAbstractMethodFlags() {
	     X10Flags x = X10Flags.toX10Flags( legalAccessFlags().clear(Private()).Abstract());
	     x = x.Safe().Local().NonBlocking().Sequential().Property().Pure();
	     return x;
	 }


	/** All flags allowed for a top-level class. */
	public Flags legalTopLevelClassFlags() {
		return X10Flags.toX10Flags(super.legalTopLevelClassFlags()).Safe().Value();
	}
	
	protected final X10Flags X10_TOP_LEVEL_CLASS_FLAGS = (X10Flags) legalTopLevelClassFlags();
	
	/** All flags allowed for an interface. */
	public Flags legalInterfaceFlags() {
		return X10Flags.toX10Flags(super.legalInterfaceFlags()).Safe();
	}
	
	protected final X10Flags X10_INTERFACE_FLAGS = (X10Flags) legalInterfaceFlags();
	
	/** All flags allowed for a member class. */
	public Flags legalMemberClassFlags() {
		return X10Flags.toX10Flags(super.legalMemberClassFlags()).Safe().Value();
	}
	
	protected final Flags X10_MEMBER_CLASS_FLAGS = (X10Flags) legalMemberClassFlags();
	
	/** All flags allowed for a local class. */
	public Flags legalLocalClassFlags() {
		return X10Flags.toX10Flags(super.legalLocalClassFlags()).Safe().Value();
	}
	
	protected final X10Flags X10_LOCAL_CLASS_FLAGS = (X10Flags) legalLocalClassFlags();
	
	
	@Override
	public MethodDef methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, Name name,
			List<Ref<? extends Type>> argTypes,
			List<Ref<? extends Type>> excTypes) {
		return methodDef(pos, container, flags, returnType, name, Collections.EMPTY_LIST, argTypes, dummyLocalDefs(argTypes), null, excTypes, null);
	}
	
	public X10MethodDef methodDef(Position pos, Ref<? extends StructType> container, Flags flags, Ref<? extends Type> returnType, Name name,
	        List<Ref<? extends Type>> typeParams,
	        List<Ref<? extends Type>> argTypes,
	        List<LocalDef> formalNames,
	        Ref<XConstraint> guard,
	        List<Ref<? extends Type>> excTypes, Ref<XTerm> body) {
	    assert_(container);
	    assert_(returnType);
	    assert_(typeParams);
	    assert_(argTypes);
	    assert_(excTypes);
	    return new X10MethodDef_c(this, pos, container, flags,
	                              returnType, name, typeParams, argTypes, formalNames, guard, excTypes, body);
	}
	
	/**
	 * Return a nullable type based on a given type.
	 * TODO: rename this to nullableType() -- the name is misleading.
	 */
	public Type boxOf(Position pos, Ref<? extends Type> type) {
	    X10ParsedClassType box = (X10ParsedClassType) Box();
	    return X10TypeMixin.instantiate(box, type);
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
		if (isStatic) {
			if (asyncStaticCodeInstance_ == null)
				asyncStaticCodeInstance_ = 
				    methodDef(Position.COMPILER_GENERATED, Types.ref((StructType) Runtime()), Public().Static(), Types.ref(VOID_), Name.make("$dummyAsync$"),
                                                     Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			return asyncStaticCodeInstance_;
		} else {
			if (asyncCodeInstance_ == null)
				asyncCodeInstance_ =
				    methodDef(Position.COMPILER_GENERATED, Types.ref((StructType) Runtime()), Public(), Types.ref(VOID_), Name.make("$dummyAsync$"),
				              Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			return asyncCodeInstance_;
		}
	}

	public ClosureDef closureDef(Position p, Ref<? extends ClassType> typeContainer, Ref<? extends CodeInstance<?>> methodContainer, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, List<LocalDef> formalNames, Ref<XConstraint> guard, List<Ref<? extends Type>> throwTypes) {
	    return new ClosureDef_c(this, p, typeContainer, methodContainer, returnType, typeParams, argTypes, formalNames, guard, throwTypes);
	}
	
	public ClosureType closureType(Position p, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, List<LocalDef> formalNames, Ref<XConstraint> guard, List<Ref<? extends Type>> throwTypes) {
	    X10ClassDef def = closureBaseInterfaceDef(typeParams.size(), argTypes.size());
	    ClosureType ct = (ClosureType) def.asType();
	    List<Type> typeArgs = new ArrayList<Type>();
	    for (Ref<? extends Type> ref : argTypes) {
	        typeArgs.add(Types.get(ref));
	    }
	    typeArgs.add(Types.get(returnType));
	    return (ClosureType) ct.typeArguments(typeArgs);
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
	
//	protected final PrimitiveType UBYTE_  = createPrimitive("ubyte");
//	protected final PrimitiveType USHORT_ = createPrimitive("ushort");
//	protected final PrimitiveType UINT_   = createPrimitive("uint");
//	protected final PrimitiveType ULONG_  = createPrimitive("ulong");
//	
//	public Type UByte()  { return UBYTE_; }
//	public Type UShort() { return USHORT_; }
//	public Type UInt()   { return UINT_; }
//	public Type ULong()  { return ULONG_; }
	
	static class Void extends X10PrimitiveType_c {
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
	public Type Void() { if (VOID_ == null) VOID_ = new Void(this); return VOID_; }

	protected ClassType Boolean_;
	public Type Boolean() { if (Boolean_ == null) Boolean_ = load("x10.lang.Boolean"); return Boolean_; }
	protected ClassType Byte_;
	public Type Byte() { if (Byte_ == null) Byte_ = load("x10.lang.Byte"); return Byte_; }
	protected ClassType Short_;
	public Type Short() { if (Short_ == null) Short_ = load("x10.lang.Short"); return Short_; }
	protected ClassType Char_;
	public Type Char() { if (Char_ == null) Char_ = load("x10.lang.Char"); return Char_; }
	protected ClassType Int_;
	public Type Int() { if (Int_ == null) Int_ = load("x10.lang.Int"); return Int_; }
	protected ClassType Long_;
	public Type Long() { if (Long_ == null) Long_ = load("x10.lang.Long"); return Long_; }
	protected ClassType Float_;
	public Type Float() { if (Float_ == null) Float_ = load("x10.lang.Float"); return Float_; }
	protected ClassType Double_;
	public Type Double() { if (Double_ == null) Double_ = load("x10.lang.Double"); return Double_; }

	// Unsigned integers
	protected ClassType UByte_;
	public Type UByte() { if (UByte_ == null) UByte_ = load("x10.lang.UByte"); return UByte_; }
	protected ClassType UShort_;
	public Type UShort() { if (UShort_ == null) UShort_ = load("x10.lang.UShort"); return UShort_; }
	protected ClassType UInt_;
	public Type UInt() { if (UInt_ == null) UInt_ = load("x10.lang.UInt"); return UInt_; }
	protected ClassType ULong_;
	public Type ULong() { if (ULong_ == null) ULong_ = load("x10.lang.ULong"); return ULong_; }

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
	
//	protected ClassType XOBJECT_;
//	public Type X10Object() {
//	    if (XOBJECT_ == null)
//		XOBJECT_ = load("x10.lang.Object");
//	    return XOBJECT_;
//	}
	
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
			regionType_ = load("x10.lang.Region"); // java file
		return regionType_;
	}
	
	protected Type pointType_;
	public Type Point() {
		if (pointType_ == null)
			pointType_ = load("x10.lang.Point");
		return pointType_;
	}
	
	protected ClassType distributionType_;
	public Type Dist() {
		if (distributionType_ == null)
			distributionType_ = load("x10.lang.Dist"); // java file
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
	
	static
	TypeProperty getFirstTypeProperty(X10ClassDef classDef) {
		if (classDef.typeProperties().size() == 1)
			return classDef.typeProperties().get(0);
		Type t = Types.get(classDef.superType());
		if (t != null && t.isClass())
			return getFirstTypeProperty((X10ClassDef) t.toClass().def());
		return null;
	}
	
//	private Type fixGenericType(Ref<? extends Type> base, X10ParsedClassType ct, boolean covariant) {
//		X10ClassDef cd = (X10ClassDef) ct.def();
//		
//		if (base != null) {
//			TypeProperty p = getFirstTypeProperty(cd);
//			assert p != null;
//			
//			XConstraint c = new XConstraint_c();
//			try {
//				X10TypeSystem_c xts = this;
//				if (covariant) {
//					c.addAtom(xts.xtypeTranslator().transSubtype(p.asType(), base.get()));
//				}
//				else {
//					c.addBinding(p.asVar(), xts.xtypeTranslator().trans(base.get()));
//				}
//			}
//			catch (XFailure e) {
//				throw new InternalCompilerError(e);
//			}
//			
//			return X10TypeMixin.xclause(ct, c);
//		}
//		
//		return ct;
//	}
	
	protected ClassType arrayType_ = null;
	public Type Array() {
		if (arrayType_ == null)
			arrayType_ = load("x10.lang.Array");
		return arrayType_;
	}
	
	protected Type valArrayType_ = null;
	public Type ValArray() {
	    if (valArrayType_ == null)
		valArrayType_ = load("x10.lang.ValArray");
	    return valArrayType_;
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
			Ref<? extends StructType> container, Flags flags,
			Ref<? extends Type> type, Name name) {
		assert_(container);
		assert_(type);
		return new X10FieldDef_c(this, pos, container, flags, type, name);
	}
	
	public X10FieldDef propertyInstance(Position pos,
	        Ref<? extends StructType> container, Flags flags,
	        Ref<? extends Type> type, Name name) {
		assert_(container);
		assert_(type);
		X10FieldDef fd = new X10FieldDef_c(this, pos, container, flags, type, name);
		fd.setProperty();
		return fd;
	}
	
	public static final boolean SUPPORT_UNSIGNED = false;
	
	public boolean isUByte(Type t) {
	    if (! SUPPORT_UNSIGNED)
		return false;
	    return isSubtype(t, UByte());
	}
	
	public boolean isUShort(Type t) {
	    if (! SUPPORT_UNSIGNED)
		return false;
	    return isSubtype(t, UShort());
	}
	
	public boolean isUInt(Type t) {
	    if (! SUPPORT_UNSIGNED)
		return false;
	    return isSubtype(t, UInt());
	}
	
	public boolean isULong(Type t) {
	    if (! SUPPORT_UNSIGNED)
		return false;
	    return isSubtype(t, ULong());
	}
	
	public boolean isNumeric(Type t) {
	        return super.isNumeric(t) || isUByte(t) || isUShort(t) || isUInt(t) || isULong(t);
	}

	public boolean isIntOrLess(Type t) {
	    return super.isIntOrLess(t) || isUByte(t) || isUShort(t);
	}

	public boolean isLongOrLess(Type t) {
	    return super.isLongOrLess(t) || isUByte(t) || isUShort(t) || isUInt(t);
	}

	/** Return true if box[t1] is a subtype of t2.  t1 must be a value type */
	public boolean isUnboxedSubtype(Type t1, Type t2) {
	    t1 = expandMacros(t1);
	    t2 = expandMacros(t2);
		
	    if (t1 == t2)
			return true;
		
		if (t1 instanceof ConstrainedType && t2 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			ConstrainedType ct2 = (ConstrainedType) t2;
			XConstraint c1 = ct1.constraint().get();
			XConstraint c2 = ct2.constraint().get();
			Type baseType1 = ct1.baseType().get();
			Type baseType2 = ct2.baseType().get();

			boolean result = true;
			result &= isUnboxedSubtype(baseType1, baseType2);
			try {
				result &= c1.entails(c2);
			}
			catch (XFailure e) {
				result = false;
			}
			
			if (result)
				return true;
		}
		
		if (t1 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			Type baseType1 = ct1.baseType().get();
			
			boolean result = true;
			result &= isUnboxedSubtype(baseType1, t2);
			if (result)
				return true;
		}
		
		if (t2 instanceof ConstrainedType) {
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType2 = ct2.baseType().get();
			XConstraint c2 = ct2.constraint().get();
			
			boolean result = true;
			result &= isUnboxedSubtype(t1, baseType2);
			result &= c2.valid();
			if (result)
				return true;
		}

		if (t1 instanceof ParametrizedType && t2 instanceof ParametrizedType) {
			ParametrizedType pt1 = (ParametrizedType) t1;
			ParametrizedType pt2 = (ParametrizedType) t2;
			
			boolean result = true;
			result &= pt1.def() == pt2.def();
			result &= typeListEquals(pt1.typeParameters(), pt2.typeParameters());
			result &= typeListEquals(pt1.formalTypes(), pt2.formalTypes());
			result &= listEquals(pt1.formals(), pt2.formals()); 
			if (result)
				return true;
		}
		
		// p: C{self.T<:S}  implies  p.T <: S
		if (t1 instanceof PathType) {
			PathType pt1 = (PathType) t1;
			TypeProperty px = pt1.property();
			XVar base = PathType_c.pathBase(pt1);
			if (base != null) {
			    final XConstraint c = base.selfConstraint();
//			    if (c != null) {
//				c = c.copy();
//			    }
//			    else {
//				c = new XConstraint_c();
//			    }
//			    try {
//				c.addIn(X10TypeMixin.realX(pt1.baseType()));
//			    }
//			    catch (XFailure e1) {
//			    }
			    if (c != null) {
					try {
						// Check if the constraint on the base p implies that p.T <: S
						// Avoid infinite recursion by removing selfConstraint on base.
						base.setSelfConstraint(null);
						XConstraint c1 = c.substitute(base, XSelf.Self);
						XConstraint c2 = new XConstraint_c();
						c2.addAtom(xtypeTranslator().transSubtype(boxOf(Types.ref(t1)), t2));
						boolean result = c1.entails(c2);
						if (result)
							return true;
					}
					catch (XFailure e) {
					}
					finally {
						base.setSelfConstraint(new XRef_c<XConstraint>() { public XConstraint compute() { return c; } });
					}
				}
			}
		}

		if (t1 instanceof PrimitiveType && t2.typeEquals(Object())) {
			return true;
		}
		
		if (t1 instanceof PrimitiveType && t2.typeEquals(Value())) {
		    return true;
		}
		
		if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
		    X10ClassType ct1 = (X10ClassType) t1;
		    X10ClassType ct2 = (X10ClassType) t2;
		    if (ct1.def() == ct2.def()) {
			X10ClassDef def = ct1.x10Def();
			if (ct1.typeArguments().size() != def.typeParameters().size())
			    return false;
			if (ct2.typeArguments().size() != def.typeParameters().size())
			    return false;
			if (def.variances().size() != def.typeParameters().size())
			    return false;
			for (int i = 0; i < def.typeParameters().size(); i++) {
			    Type a1 = ct1.typeArguments().get(i);
			    Type a2 = ct2.typeArguments().get(i);
			    TypeProperty.Variance v = def.variances().get(i);
			    switch (v) {
			    case COVARIANT:
				if (! isSubtype(a1, a2)) return false;
				break;
			    case CONTRAVARIANT:
				if (! isSubtype(a2, a1)) return false;
				break;
			    case INVARIANT:
				if (! typeEquals(a1, a2)) return false;
				break;
			    }
			}
			return true;
		    }
		}
		
		return descendsFrom(t1, t2);
	}

	@Override
	public boolean isSubtype(Type t1, Type t2) {
	    t1 = expandMacros(t1);
	    t2 = expandMacros(t2);

		if (t1 == t2)
			return true;
		
		if (t1.isNull() && (t2.isNull() || isReferenceType(t2) || isInterfaceType(t2))) {
		    return true;
		}
		
		if (t2.isNull()) {
		    return false;
		}
		
		if (t1 instanceof ClosureType && t2 instanceof ClosureType) {
			// Permit covariance in the return type, so that a closure that returns a more
			// specific type can be assigned to a closure variable with a less specific
			// return type. Don't permit covariance in the throw types or argument types.
			
			ClosureType ct1 = (ClosureType) t1;
			ClosureType ct2 = (ClosureType) t2;
			
			boolean result = true;
			result &= typeListEquals(ct1.argumentTypes(), ct2.argumentTypes());
			result &= typeListEquals(ct1.throwTypes(), ct2.throwTypes());           // XXX: ORDER shouldn't matter
			result &= isSubtype(ct1.returnType(), ct2.returnType());
			if (result)
				return true;
		}

		if (t1 instanceof ConstrainedType && t2 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			ConstrainedType ct2 = (ConstrainedType) t2;
			XConstraint c1 = ct1.constraint().get();
			XConstraint c2 = ct2.constraint().get();
			Type baseType1 = ct1.baseType().get();
			Type baseType2 = ct2.baseType().get();

			boolean result = true;
			result &= isSubtype(baseType1, baseType2);
			try {
				result &= c1.entails(c2);
			}
			catch (XFailure e) {
				result = false;
			}
			
			if (result)
				return true;
		}
		
		if (t1 instanceof ConstrainedType) {
			ConstrainedType ct1 = (ConstrainedType) t1;
			Type baseType1 = ct1.baseType().get();
			
			boolean result = true;
			result &= isSubtype(baseType1, t2);
			if (result)
				return true;
		}
		
		if (t2 instanceof ConstrainedType) {
			ConstrainedType ct2 = (ConstrainedType) t2;
			Type baseType2 = ct2.baseType().get();
			XConstraint c2 = ct2.constraint().get();
			
			boolean result = true;
			result &= isSubtype(t1, baseType2);
			result &= c2.valid();
			if (result)
				return true;
		}

/*
		if (t1 instanceof ParametrizedType && t2 instanceof ParametrizedType) {
			ParametrizedType pt1 = (ParametrizedType) t1;
			ParametrizedType pt2 = (ParametrizedType) t2;
			
			boolean result = true;
			result &= pt1.def() == pt2.def();
			result &= typeListEquals(pt1.typeParameters(), pt2.typeParameters());
			result &= typeListEquals(pt1.formalTypes(), pt2.formalTypes());
			result &= listEquals(pt1.formals(), pt2.formals()); 
			if (result)
				return true;
		}
		
		// p: C{self.T<:S}  implies  p.T <: S
		if (t1 instanceof PathType) {
			PathType pt1 = (PathType) t1;
			TypeProperty px = pt1.property();
			XVar base = PathType_c.pathBase(pt1);
			if (base != null) {
			    final XConstraint c = base.selfConstraint();
//			    if (c != null) {
//				c = c.copy();
//			    }
//			    else {
//				c = new XConstraint_c();
//			    }
//			    try {
//				c.addIn(X10TypeMixin.realX(pt1.baseType()));
//			    }
//			    catch (XFailure e1) {
//			    }
			    if (c != null) {
					try {
						// Check if the constraint on the base p implies that p.T <: S
						// Avoid infinite recursion by removing selfConstraint on base.
						base.setSelfConstraint(null);
						XConstraint c1 = c.substitute(base, XSelf.Self);
						XConstraint c2 = new XConstraint_c();
						c2.addAtom(xtypeTranslator().transSubtype(t1, t2));
						boolean result = c1.entails(c2);
						if (result)
							return true;
					}
					catch (XFailure e) {
					}
					finally {
						base.setSelfConstraint(new XRef_c<XConstraint>() { public XConstraint compute() { return c; } });
					}
				}
			}
		}
*/
		
		// if T implements I, Box[T] is a subtype of I
		if (isBox(t1)) {
		    Type boxed1 = X10TypeMixin.getParameterType(t1, 0);
		    if (isValueType(boxed1)) {
			if (isUnboxedSubtype(boxed1, t2))
			    return true;
		    }
		    else {
			if (isSubtype(boxed1, t2))
			    return true;
		    }
		}

		if (t1 instanceof PrimitiveType && t2.typeEquals(Object())) {
		    return true;
		}
		
		if (t1 instanceof PrimitiveType && t2.typeEquals(Value())) {
		    return true;
		}
		
		if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
		    X10ClassType ct1 = (X10ClassType) t1;
		    X10ClassType ct2 = (X10ClassType) t2;
		    if (ct1.def() == ct2.def()) {
			X10ClassDef def = ct1.x10Def();
			if (ct1.typeArguments().size() != def.typeParameters().size())
			    return false;
			if (ct2.typeArguments().size() != def.typeParameters().size())
			    return false;
			if (def.variances().size() != def.typeParameters().size())
			    return false;
			for (int i = 0; i < def.typeParameters().size(); i++) {
			    Type a1 = ct1.typeArguments().get(i);
			    Type a2 = ct2.typeArguments().get(i);
			    TypeProperty.Variance v = def.variances().get(i);
			    switch (v) {
			    case COVARIANT:
				if (! isSubtype(a1, a2)) return false;
				break;
			    case CONTRAVARIANT:
				if (! isSubtype(a2, a1)) return false;
				break;
			    case INVARIANT:
				if (! typeEquals(a1, a2)) return false;
				break;
			    }
			}
			return true;
		    }
		}
		
                if (t1 instanceof ParameterType || t1 instanceof PathType) {
                    for (Type s1 : upperBounds(t1)) {
                        if (isSubtype(s1, t2))
                            return true;
                    }
                }
                if (t2 instanceof ParameterType || t2 instanceof PathType) {
                    for (Type s2 : lowerBounds(t2)) {
                        if (isSubtype(t1, s2))
                            return true;
                    }
                }

		return descendsFrom(t1, t2);
	}
	
	@Override
	public boolean typeEquals(Type t1, Type t2) {
	    t1 = expandMacros(t1);
	    t2 = expandMacros(t2);
	    
		if (t1 == t2)
			return true;
		
		Type baseType1 = X10TypeMixin.baseType(t1);
		Type baseType2 = X10TypeMixin.baseType(t2);
		
		// Don't need the real clause here, since will only be true if the base types are equal.
		XConstraint c1 = X10TypeMixin.xclause(t1);
		XConstraint c2 = X10TypeMixin.xclause(t2);
		
		if (c1 != null && c1.valid()) { c1 = null; t1 = baseType1; }
		if (c2 != null && c2.valid()) { c2 = null; t2 = baseType2; }
		
		if (c1 != null && c2 == null) {
		    // one is valid and the other is not
		    return false;
		}
		
		if (c1 == null && c2 != null) {
		    // one is valid and the other is not
		    return false;
		}
		
		if (c1 != null && c2 != null) {
			boolean result = true;
			
			result &= typeEquals(baseType1, baseType2);

			try {
			    result &= c1.equiv(c2);
			}
			catch (XFailure e) {
			    result = false;
			}

			if (result)
				return true;
		}

		if (t1 instanceof ClosureType && t2 instanceof ClosureType) {
			ClosureType ct1 = (ClosureType) t1;
			ClosureType ct2 = (ClosureType) t2;
			
			boolean result = true;
			result &= typeListEquals(ct1.argumentTypes(), ct2.argumentTypes());
			result &= typeListEquals(ct1.throwTypes(), ct2.throwTypes());           // XXX: ORDER shouldn't matter
			result &= typeEquals(ct1.returnType(), ct2.returnType());
			if (result)
				return true;
		}
		
//		if (t1 instanceof ParametrizedType && t2 instanceof ParametrizedType) {
//		    if (t1.equals((TypeObject) t2))
//			return true;
//		}
//		
//		// p: C{self.T==S}  implies  p.T == S
//		if (t1 instanceof PathType) {
//			PathType pt1 = (PathType) t1;
//			TypeProperty px = pt1.property();
//			XVar base = PathType_c.pathBase(pt1);
//			if (base != null) {
//				XConstraint c = base.selfConstraint();
//				if (c != null) {
//					try {
//						// Check if the constraint on the base p implies that T1==T2
//						c = c.substitute(base, XSelf.Self);
//						XConstraint equals = new XConstraint_c();
//						equals.addBinding(xtypeTranslator().trans(t1), xtypeTranslator().trans(t2));
//						boolean result = c.entails(equals);
//						if (result)
//							return true;
//					}
//					catch (XFailure e) {
//					}
//				}
//			}
//		}
//
//		// p: C{self.T==S}  implies  p.T == S
//		if (t2 instanceof PathType) {
//			PathType pt2 = (PathType) t2;
//			TypeProperty px = pt2.property();
//			XVar base = PathType_c.pathBase(pt2);
//			if (base != null) {
//				XConstraint c = base.selfConstraint();
//				if (c != null) {
//					try {
//						// Check if the constraint on the base p implies that T1==T2
//						c = c.substitute(base, XSelf.Self);
//						XConstraint equals = new XConstraint_c();
//						equals.addBinding(xtypeTranslator().trans(t1), xtypeTranslator().trans(t2));
//						boolean result = c.entails(equals);
//						if (result)
//							return true;
//					}
//					catch (XFailure e) {
//					}
//				}
//			}
//		}
		
		
		if (t1 instanceof ParameterType || t1 instanceof PathType) {
		    for (Type s1 : equalBounds(t1)) {
		        if (typeEquals(s1, t2)) {
		            return true;
		        }
		    }
		}
		
		if (t2 instanceof ParameterType || t2 instanceof PathType) {
		    for (Type s2 : equalBounds(t2)) {
		        if (typeEquals(t1, s2)) {
		            return true;
		        }
		    }
		}
		
		if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
		    X10ClassType ct1 = (X10ClassType) t1;
		    X10ClassType ct2 = (X10ClassType) t2;
		    X10ClassDef def1 = ct1.x10Def();
		    X10ClassDef def2 = ct2.x10Def();
		    if (def1 != def2)
			return false;
		    if (! CollectionUtil.allElementwise(ct1.typeArguments(), ct2.typeArguments(), new TypeEquals())) {
			return false;
		    }
		    return true;
		}

		return super.typeEquals(t1, t2);
	}
	
	@Override
	public boolean isCastValid(Type fromType, Type toType) {
	    fromType = expandMacros(fromType);
	    toType = expandMacros(toType);

		if (fromType == toType)
		    return true;

		if (fromType instanceof NullType) {
		    return toType.isNull() || ! isValueType(toType);
		}
		
		// For now, can always cast from one parameter type to another.
		if (fromType instanceof ParameterType || toType instanceof ParameterType) {
		    return true;
		}
		
		Type t1 = X10TypeMixin.baseType(fromType);
		Type t2 = X10TypeMixin.baseType(toType);
		XConstraint c1 = X10TypeMixin.realX(fromType);
		XConstraint c2 = X10TypeMixin.realX(toType);
		
		Type baseType1 = t1;
		Type baseType2 = t2;
		
		if (c1 != null && c1.valid()) { c1 = null; }
		if (c2 != null && c2.valid()) { c2 = null; }

		if (c1 != null && c2 != null && ! clausesConsistent(c1, c2))
		    return false;
		
		if (baseType1 != fromType && baseType2 != toType)
		    return isCastValid(baseType1, baseType2);
		
		if (isValueType(baseType1) && isValueType(baseType2) && ! typeBaseEquals(baseType1, toType))
		    return false;

		if (isValueType(baseType1) && isReferenceType(baseType2))
		    return false;
		
		if (isReferenceType(baseType1) && isValueType(baseType2))
		    return false;
		
		return super.isCastValid(baseType1, baseType2);
	}

	@Override
	public boolean descendsFrom(Type child, Type ancestor) {
		// Strip off the constraints, expand macros.
	        child = X10TypeMixin.baseType(child);
	        ancestor = X10TypeMixin.baseType(ancestor);
		
		if (child instanceof PrimitiveType) {
		    if (typeEquals(ancestor, Object()) || typeEquals(ancestor, Value()))
			return true;
		}
		
		return super.descendsFrom(child, ancestor);
	}
	
	@Override
	public boolean isImplicitCastValid(Type fromType, Type toType) {
	    return isImplicitCastValid(fromType, toType, true);
	}
	
	Type expandMacros(Type t) {
	    if (t instanceof AnnotatedType)
		return expandMacros(((AnnotatedType) t).baseType());
	    if (t instanceof MacroType)
		return expandMacros(((MacroType) t).definedType());
	    return t;
	}
	
	public boolean isImplicitCastValid(Type fromType, Type toType, boolean tryCoercionFunction) {
	    fromType = expandMacros(fromType);
	    toType = expandMacros(toType);
	    
		if (fromType == toType)
		    return true;

		if (! isValueType(fromType) && ! isValueType(toType))
		    if (isSubtype(fromType, toType))
			return true;

		if (tryCoercionFunction) {
		    // Can convert if there is a static method toType.make(fromType)
		    try {
			MethodInstance mi = findMethod(toType, MethodMatcher(toType, Name.make("$convert"), Collections.singletonList(fromType), false), (ClassDef) null);
			if (mi.flags().isStatic() && mi.returnType().isSubtype(toType))
			    return true;
		    }
		    catch (SemanticException e) {
		    }
		
		    try {
			MethodInstance mi = findMethod(toType, MethodMatcher(toType, Name.make("make"), Collections.singletonList(fromType), false), (ClassDef) null);
			if (mi.flags().isStatic() && mi.returnType().isSubtype(toType))
			    return true;
		    }
		    catch (SemanticException e) {
		    }
		}
		
		Type baseType1 = X10TypeMixin.baseType(fromType);
		XConstraint c1 = X10TypeMixin.realX(fromType);
		Type baseType2 = X10TypeMixin.baseType(toType);
		XConstraint c2 = X10TypeMixin.realX(toType);
		
		if (c1 != null && c1.valid()) { c1 = null; }
		if (c2 != null && c2.valid()) { c2 = null; }
		
		// When checking if can assign C{c1, self==x1} to C{c2,
		// self==x2}, we need to unify self in the constraints
		// since self==x1 does not entail self==x2.
		if (c2 != null) {
		    XVar self2 = X10TypeMixin.selfVar(c2);
		    if (self2 != null) {
			try {
			    c1 = c1 == null ? new XConstraint_c() : c1.copy();
			    c1.addSelfBinding(self2);
			}
			catch (XFailure e) { }
		    }
		}
		
		if (c1 != null || c2 != null) {
		    boolean result = true;

		    if (c1 != null && c2 != null) {
			try {
			    result = c1.entails(c2);
			}
			catch (XFailure e) {
			    result = false;
			}
		    }
		    else if (c2 != null) {
			result = c2.valid();
		    }

		    if (! result)
			return false;
		}
		
		if (baseType1 instanceof NullType) {
		    return isReferenceType(baseType2) || isInterfaceType(baseType2);
		}
		
		if (isValueType(baseType1) && isValueType(baseType2)) {
		    if (isVoid(baseType1))
			return false;
		    if (isVoid(baseType2))
			return false;

		    if (isBoolean(baseType1))
			return isBoolean(baseType2);
		    
		    // Allow assignment if the fromType's value can be represented as a toType
		    if (c1 != null && isNumeric(baseType1) && isNumeric(baseType2)) {
			XVar self = X10TypeMixin.selfVar(c1);
			if (self instanceof XLit) {
			    Object val = ((XLit) self).val();
			    if (numericConversionValid(baseType2, val)) {
				return true;
			    }
			}
		    }
		    
		    if (isDouble(baseType1))
			return isDouble(baseType2);
		    if (isFloat(baseType1))
			return isFloat(baseType2) || isDouble(baseType2);
		   
		    // Do not allow conversions to change signedness.
		    if (isLong(baseType1))
			return isLong(baseType2);
		    if (isInt(baseType1))
			return isInt(baseType2) || isLong(baseType2) || isDouble(baseType2);
		    if (isShort(baseType1))
			return isShort(baseType2) || isInt(baseType2) || isLong(baseType2) || isFloat(baseType2) || isDouble(baseType2);
		    if (isByte(baseType1))
			return isByte(baseType2) || isShort(baseType2) || isInt(baseType2) || isLong(baseType2) || isFloat(baseType2) || isDouble(baseType2);

		    if (SUPPORT_UNSIGNED) {
			if (isULong(baseType1))
			    return isULong(baseType2);
			if (isUInt(baseType1))
			    return isUInt(baseType2) || isULong(baseType2) || isDouble(baseType2);
			if (isUShort(baseType1))
			    return isUShort(baseType2) || isUInt(baseType2) || isULong(baseType2) || isFloat(baseType2) || isDouble(baseType2);
			if (isUByte(baseType1))
			    return isUByte(baseType2) || isUShort(baseType2) || isUInt(baseType2) || isULong(baseType2) || isFloat(baseType2) || isDouble(baseType2);
		    }

		    if (typeEquals(baseType1, baseType2))
			return true;
		    
		    // Note: cannot implicitly coerce a value type to a superclass.
		    return false;
		}
		
		if (isValueType(baseType1) && ! isValueType(baseType2)) {
		    Type boxFrom = boxOf(Types.ref(fromType));
		    if (isImplicitCastValid(boxFrom, baseType2))
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean numericConversionValid(Type t, java.lang.Object value) {
		assert_(t);
		
		if (value == null)
		    return false;
		
		if (value instanceof Float || value instanceof Double)
		    return false;

		long v;
	
		if (value instanceof Number) {
		    v = ((Number) value).longValue();
		}
		else if (value instanceof Character) {
		    v = ((Character) value).charValue();
		}
		else {
		    return false;
		}
	
		Type base = X10TypeMixin.baseType(t);
		
		boolean fits = false;

		if (SUPPORT_UNSIGNED) {
		    // For now, all constant values are signed, so conversions are only allowed for half the range.
		    // TODO: add unsigned kinds to IntLit, support unsigned Expr.constantValue()
		    if (isULong(base))
			fits = 0L <= v && v <= Long.MAX_VALUE;
		    if (isUInt(base))
			fits = 0L <= v && v <= Integer.MAX_VALUE;
		    if (isUShort(base))
			fits = 0L <= v && v <= Short.MAX_VALUE;
		    if (isUByte(base))
			fits = 0L <= v && v <= Byte.MAX_VALUE;
		}
		
		if (base.isLong())
		    fits = Long.MIN_VALUE <= v && v <= Long.MAX_VALUE;
		if (base.isInt())
		    fits = Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE;
		if (base.isChar())
		    fits = Character.MIN_VALUE <= v && v <= Character.MAX_VALUE;
		if (base.isShort())
		    fits = Short.MIN_VALUE <= v && v <= Short.MAX_VALUE;
		if (base.isByte())
		    fits = Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE;

		if (! fits)
		    return false;
			
		// Check if adding self==value makes the constraint on t inconsistent.
		
		XLit val = XTerms.makeLit(value);

		try {
		    XConstraint c = new XConstraint_c();
		    c.addSelfBinding(val);
		    return entailsClause(c, X10TypeMixin.realX(t));
		}
		catch (XFailure f) {
		    // Adding binding makes real clause inconsistent.
		    return false;
		}
	}

	protected boolean typeRefListEquals(List<Ref<? extends Type>> l1, List<Ref<? extends Type>> l2) {
		return CollectionUtil.<Type>allElementwise(new TransformingList<Ref<? extends Type>, Type>(l1, new DerefTransform<Type>()),
		                                           new TransformingList<Ref<? extends Type>, Type>(l2, new DerefTransform<Type>()),
		                                           new TypeSystem_c.TypeEquals());
	}
	
	protected boolean typeListEquals(List<Type> l1, List<Type> l2) {
		return CollectionUtil.<Type>allElementwise(l1, l2, new TypeSystem_c.TypeEquals());
	}
	
	protected boolean listEquals(List<XVar> l1, List<XVar> l2) {
		return CollectionUtil.<XVar>allEqual(l1, l2);
	}

	protected boolean isX10BaseSubtype(Type me, Type sup) {
		Type xme = X10TypeMixin.baseType(me);
		Type xsup = X10TypeMixin.baseType(sup);
		return isSubtype(xme, xsup);
	}
	
	
	public boolean isRail(Type t) {
	    return hasSameClassDef(t, Rail());
	}
	
	public boolean isValRail(Type t) {
	    return hasSameClassDef(t, ValRail());
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
	
	public	Type ValRail(Type arg) {
	    return X10TypeMixin.instantiate(Rail(), arg);
	}

	public	Type Settable(Type domain, Type range) {
	    return X10TypeMixin.instantiate(Settable(), domain, range);
	}
	
	public  boolean isSettable(Type me) { 
	    return hasSameClassDef(me, Settable()); 
	}
	public  boolean isX10Array(Type me) { 
		return hasSameClassDef(me, Array()); 
	}
	
	public boolean isTypeConstrained(Type me) {
		return me instanceof ConstrainedType;
	}

	public  boolean isClock(Type me) {
		return isSubtype(me, Clock()); 
	}
	public  boolean isPoint(Type me) {
		return isSubtype(me, Point()); 
	}
	public  boolean isPlace(Type me) {
		return isSubtype(me, Place());
	}
	public  boolean isRegion(Type me) {
		return isSubtype(me, Region());
	}
	public  boolean isDistribution(Type me) {
		return isSubtype(me, Dist());
	}
	public  boolean isDistributedArray(Type me) {
		return isX10Array(me);
	}
	public  boolean isComparable(Type me) {
	    return isSubtype(me, Comparable());
	}
	public  boolean isIterable(Type me) {
	    return isSubtype(me, Iterable());
	}
	public  boolean isIterator(Type me) {
	    return isSubtype(me, Iterator());
	}
	public  boolean isContains(Type me) {
	    return isSubtype(me, Contains());
	}
	public  boolean isContainsAll(Type me) {
	    return isSubtype(me, ContainsAll());
	}
	
	public VarDef createSelf(X10Type t) {
	    VarDef v = localDef(Position.COMPILER_GENERATED, Flags.PUBLIC, Types.ref(t), Name.make("self"));
		return v;
	}
	protected XTypeTranslator xtt = new XTypeTranslator(this);
	public XTypeTranslator xtypeTranslator() {
		return xtt;
	}
	
	@Override
	public void initialize(TopLevelResolver loadedResolver, ExtensionInfo extInfo) throws SemanticException {
	    super.initialize(loadedResolver, extInfo);
	    XTerms.addExternalSolvers(new SubtypeSolver(this));
	}
	
	public boolean equivClause(Type me, Type other) {
	    return entailsClause(me, other) && entailsClause(other, me);
	}
	
	public boolean equivClause(XConstraint c1, XConstraint c2) {
		boolean result;
		try {
			result = (c1==null) ? ((c2==null) ? true : c2.valid())
					: c1.equiv(c2);
		}
		catch (XFailure e) {
			return false;
		}
		return result;
	}
	
	public boolean entailsClause(XConstraint c1, XConstraint c2) {
		boolean result;
		try {
			result = c1==null ? (c2==null || c2.valid()) : c1.entails(c2);
		}
		catch (XFailure e) {
			return false;
		}
		return result;
		
	}
	public boolean entailsClause(Type me, Type other) {
		try {
			XConstraint c1 = X10TypeMixin.realX(me);
			XConstraint c2 = X10TypeMixin.xclause(other);
			return entailsClause(c1,c2);
		}
		catch (InternalCompilerError e) {
			if (e.getCause() instanceof XFailure) {
				return false;
			}
			throw e;
		}
	}

	protected XLit hereConstraintLit; // Maybe this should be declared as C_Lit instead of a concrete impl class?
	public XLit here() {
	    if (hereConstraintLit == null)
		hereConstraintLit= xtypeTranslator().transHere();
	    return hereConstraintLit;
	}

	protected XLit FALSE;
	public XLit FALSE() {
	    if (FALSE == null)
		FALSE= xtypeTranslator().trans(false);
	    return FALSE;
	}
	protected XLit TRUE;
	public XLit TRUE() {
	    if (TRUE == null)
		TRUE= xtypeTranslator().trans(true);
	    return TRUE;
	}
	protected XLit NEG_ONE;
	public XLit NEG_ONE() {
	    if (NEG_ONE == null)
		NEG_ONE=  xtypeTranslator().trans(-1);
	    return NEG_ONE;
	}
	protected XLit ZERO;
	public XLit ZERO() {
	    if (ZERO == null)
		ZERO=  xtypeTranslator().trans(0);
	    return ZERO;
	}
	protected XLit ONE;
	public XLit ONE() {
	    if (ONE == null)
		ONE=  xtypeTranslator().trans(1);
	    return ONE;
	}
	protected XLit TWO;
	public XLit TWO() {
	    if (TWO == null)
		TWO=  xtypeTranslator().trans(2);
	    return TWO;
	}
	protected XLit THREE;
	public XLit THREE() {
	    if (THREE == null)
		THREE=  xtypeTranslator().trans(3);
	    return THREE;
	}
	protected XLit NULL;
	public XLit NULL() {
	    if (NULL == null)
		NULL=  xtypeTranslator().transNull();
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
		flagsForName.put("incomplete", X10Flags.INCOMPLETE);
		flagsForName.put("property", X10Flags.PROPERTY);
		flagsForName.put("pure", X10Flags.PURE);
		flagsForName.put("atomic", X10Flags.ATOMIC);
		
	}
	
        /** All flags allowed for a constructor. */
	@Override
        public Flags legalConstructorFlags() {
                return legalAccessFlags().Synchronized().Native(); // allow native (but not extern)
        }

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
	     
	     if (SUPPORT_UNSIGNED)
		 if (isULong(t1) || isULong(t2))
		     return Long();

	     if (isInt(t1) || isInt(t2))
		 return Int();

	     if (SUPPORT_UNSIGNED)
		 if (isUInt(t1) || isUInt(t2))
		     return Int();
	     
	     if (isShort(t1) || isShort(t2))
		 return Int();
	     
	     if (isChar(t1) || isChar(t2))
		 return Int();
	     
	     if (isByte(t1) || isByte(t2))
		 return Int();
	     
	     if (SUPPORT_UNSIGNED)
		 if (isUShort(t1) || isUShort(t2))
		     return Int();

	     if (SUPPORT_UNSIGNED)
		 if (isUByte(t1) || isUByte(t2))
		     return Int();
	     
	     throw new SemanticException("Cannot promote non-numeric type " + t1);
	 }

	 public Type promote2(Type t) throws SemanticException {
	     if (isUByte(t) || isUShort(t))
		 return UInt();

	     if (isUInt(t))
		 return UInt();

	     if (isULong(t))
		 return ULong();

	     if (isByte(t) || isChar(t) || isShort(t) || isInt(t))
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
	    return X10TypeMixin.xclause(pt, (XConstraint) null);
	}
	@Override
	public Type promote(Type t1, Type t2) throws SemanticException {
		Type pt = promote2(t1, t2);
		return X10TypeMixin.xclause(pt, (XConstraint) null);
	}
	@Override
	public Type leastCommonAncestor(Type type1, Type type2)
	throws SemanticException
	{
		assert_(type1);
		assert_(type2);
		try { 
		if (typeBaseEquals(type1, type2)) {
			Type base1 = X10TypeMixin.xclause(type1, (XConstraint) null);
			return base1;
		}
		
		if (type1.isNumeric() && type2.isNumeric()) {
			if (isImplicitCastValid(type1, type2)) {
				return type2;
			}
			
			if (isImplicitCastValid(type2, type1)) {
				return type1;
			}
			
			if (type1.isChar() && type2.isByte() ||
					type1.isByte() && type2.isChar()) {
				return Int();
			}
			
			if (type1.isChar() && type2.isShort() ||
					type1.isShort() && type2.isChar()) {
				return Int();
			}
		}
		
		// TODO: handle covariant instantiated types.
		// XYZ
		if (type1.isArray() && type2.isArray()) {
			return arrayOf(leastCommonAncestor(
					type1.toArray().base(), type2.toArray().base()));
		}
		
		if (type1.isReference() && type2.isNull()) return type1;
		if (type2.isReference() && type1.isNull()) return type2;
		
			// Don't consider interfaces.
			if (type1.isClass() && type1.toClass().flags().isInterface()) {
				return Object();
			}
			
			if (type2.isClass() && type2.toClass().flags().isInterface()) {
				return Object();
			}
			
			// Check against Object to ensure superType() is not null.
			if (typeEquals(type1, Object())) return type1;
			if (typeEquals(type2, Object())) return type2;
			
			if (isSubtype(type1, type2)) return type2;
			if (isSubtype(type2, type1)) return type1;
			
			if (type1 instanceof ObjectType && type2 instanceof ObjectType) {
			// Walk up the hierarchy
			Type t1 = leastCommonAncestor(((ObjectType) type1).superClass(), type2);
			Type t2 = leastCommonAncestor(((ObjectType) type2).superClass(), type1);
			
			if (typeEquals(t1, t2)) return t1;
			
			return Object();
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
		if (type1 == type2) return true;
		if (type1 == null || type2 == null) return false;
	        return typeEquals(X10TypeMixin.xclause(type1, (XConstraint) null), X10TypeMixin.xclause(type2, (XConstraint) null));
	    }
	 @Override
	 public LocalDef localDef(Position pos,
			 Flags flags, Ref<? extends Type> type, Name name) {
		 assert_(type);
		 return new X10LocalDef_c(this, pos, flags, type, name);
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
                 List<Ref<? extends Type>> throwTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(throwTypes);
		 return constructorDef(pos, container, flags, container, Collections.EMPTY_LIST, argTypes, dummyLocalDefs(argTypes), null, throwTypes);
	 }
	
	 public X10ConstructorDef constructorDef(Position pos,
			 Ref<? extends ClassType> container,
			 Flags flags, Ref<? extends ClassType> returnType, 
			 List<Ref<? extends Type>> typeParams,
			 List<Ref<? extends Type>> argTypes,
			 List<LocalDef> formalNames,
			 Ref<XConstraint> guard, 
			 List<Ref<? extends Type>> excTypes) {
		 assert_(container);
		 assert_(argTypes);
		 assert_(excTypes);
		 return new X10ConstructorDef_c(this, pos, container, flags,
				 returnType, typeParams, argTypes, formalNames, guard, excTypes);
	 }

	public void addAnnotation(X10Def o, Type annoType, boolean replace) {
	    List<Ref<? extends Type>> newATs = new ArrayList<Ref<? extends Type>>();

	    if (replace) {
	        for (Ref<? extends Type> at : o.defAnnotations()) {
	            if (! at.get().isSubtype(X10TypeMixin.baseType(annoType))) {
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
	
	public boolean primitiveClausesConsistent(x10.constraint.XConstraint c1, x10.constraint.XConstraint c2) {
//		try {
//			x10.constraint.Promise p1 = c1.lookup(x10.constraint.C_Self.Self);
//			x10.constraint.Promise p2 = c2.lookup(x10.constraint.C_Self.Self);
//			if (p1 != null && p2 != null) {
//				x10.constraint.C_Term t1 = p1.term();
//				x10.constraint.C_Term t2 = p2.term();
//				return t1 == null || t2 == null || t1.equals(t2);
//			}
//		}
//		catch (x10.constraint.Failure e) {
//			return true;
//		}
		return true;
	}
	
	public boolean clausesConsistent(x10.constraint.XConstraint c1, x10.constraint.XConstraint c2) {
		if (primitiveClausesConsistent(c1, c2)) {
			x10.constraint.XConstraint r = c1.copy();
			try {
				r.addIn(c2);
				return r.consistent();
			}
			catch (x10.constraint.XFailure e) {
				return false;
			}
		}
		return false;
	}

    public Type performBinaryOperation(Type t, Type l, Type r, Binary.Operator op) {
        XConstraint cl = X10TypeMixin.realX(l);
        XConstraint cr = X10TypeMixin.realX(r);
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        XConstraint c = xts.xtypeTranslator().binaryOp(op, cl, cr);
        return X10TypeMixin.xclause(t, c);
    }

    public Type performUnaryOperation(Type t, Type a, polyglot.ast.Unary.Operator op) {
        XConstraint ca = X10TypeMixin.realX(a);
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        XConstraint c = xts.xtypeTranslator().unaryOp(op, ca);
        return X10TypeMixin.xclause(t, c);
    }

	/**
	 * Returns true iff an object of type <type> may be thrown.
	 **/
	public boolean isThrowable(Type type) {
		assert_(type);
		return isSubtype(type, Throwable());
	}

	/**
	 * Returns a true iff the type or a supertype is in the list
	 * returned by uncheckedExceptions().
	 */
	public boolean isUncheckedException(Type type) {
		assert_(type);

		if (type.isThrowable()) {
		    for (Type t : uncheckedExceptions()) {
		        if (isSubtype(type, t)) {
		            return true;
		        }
		    }
		}
		
		return false;
	}
	
	@Override
	protected List<MethodInstance> findAcceptableMethods(Type container, MethodMatcher matcher, ClassDef currClass)
               throws SemanticException {

	    List<MethodInstance> candidates = new ArrayList<MethodInstance>();

	    for (Type t : upperBounds(container)) {
	        List<MethodInstance> ms = super.findAcceptableMethods(t, matcher, currClass);
	        candidates.addAll(ms);
	    }

	    return candidates;
	}

	@Override
	public X10MethodInstance findMethod(Type container, MethodMatcher matcher, ClassDef currClass) throws SemanticException {
	    return (X10MethodInstance) super.findMethod(container, matcher, currClass);
	}

	@Override
	public X10ConstructorInstance findConstructor(Type container, polyglot.types.TypeSystem_c.ConstructorMatcher matcher, ClassDef currClass)
	throws SemanticException {
	    return (X10ConstructorInstance) super.findConstructor(container, matcher, currClass);
	}
	
	    public X10MethodMatcher MethodMatcher(Type container, Name name, List<Type> argTypes) {
		return new X10MethodMatcher(container, name, argTypes, true);
	    }

	    public X10MethodMatcher MethodMatcher(Type container, Name name, List<Type> argTypes, boolean tryCoercionFunction) {
		return new X10MethodMatcher(container, name, argTypes, tryCoercionFunction);
	    }

	    public X10MethodMatcher MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes) {
		return new X10MethodMatcher(container, name, typeArgs, argTypes, true);
	    }

	    public X10MethodMatcher MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, boolean tryCoercionFunction) {
		return new X10MethodMatcher(container, name, typeArgs, argTypes, tryCoercionFunction);
	    }
	    
	    public X10ConstructorMatcher ConstructorMatcher(Type container, List<Type> argTypes) {
		return new X10ConstructorMatcher(container, argTypes);
	    }
	    public X10ConstructorMatcher ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes) {
		return new X10ConstructorMatcher(container, typeArgs, argTypes);
	    }
	    public X10FieldMatcher FieldMatcher(Type container, Name name) {
		return new X10FieldMatcher(container, name);
	    }

	    public static class X10FieldMatcher extends TypeSystem_c.FieldMatcher {
		private X10FieldMatcher(Type container, Name name) {
		    super(container, name);
		}

		@Override
		public FieldInstance instantiate(FieldInstance mi) throws SemanticException {
		    FieldInstance fi =  super.instantiate(mi);
		    if (fi == null)
			return null;
		    Type t = fi.type();
		    Type c = container != null ? container : fi.container();
		    XVar v = X10TypeMixin.selfVar(c);
		    if (v == null) v = new XConstraint_c().genEQV();
		    X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		    XLocal oldThis = ts.xtypeTranslator().transThisWithoutTypeConstraint();
		    Type newT = X10MethodInstance_c.subst(t, new XVar[] { v }, new XRoot[] { oldThis });
		    if (newT != t)
			return fi.type(newT);
		    return fi;
		}
	    }


	public static class X10MethodMatcher extends TypeSystem_c.MethodMatcher {
	    List<Type> typeArgs;
	    boolean tryCoercionFunction;

	    private X10MethodMatcher(Type container, Name name, List<Type> argTypes, boolean tryCoercionFunction) {
		this(container, name, Collections.EMPTY_LIST, argTypes, tryCoercionFunction);
	    }
	    private X10MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, boolean tryCoercionFunction) {
		super(container, name, argTypes);
		this.typeArgs = typeArgs;
		this.tryCoercionFunction = tryCoercionFunction;
	    }
	    
	    @Override
	    public String argumentString() {
		return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
	    }
	    
	    @Override
	    public MethodInstance instantiate(MethodInstance mi) throws SemanticException {
		if (! mi.name().equals(name))
		    return null;
		if (mi.formalTypes().size() != argTypes.size())
		    return null;
		if (mi instanceof X10MethodInstance) {
		    X10MethodInstance xmi = (X10MethodInstance) mi;
		    Type c = container != null ? container : xmi.container();
		    if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
			return X10MethodInstance_c.instantiate(xmi, c, typeArgs, argTypes, tryCoercionFunction);
		}
		return null;
	    }
	}
	
	public boolean hasMethodNamed(Type container, Name name) {
	    if (container != null)
		container = X10TypeMixin.baseType(container);
	    
	    // HACK: use the def rather than the type to avoid gratuitous reinstantiation of methods.
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
	
	public static class X10ConstructorMatcher extends TypeSystem_c.ConstructorMatcher {
	    List<Type> typeArgs;
	    
	    private X10ConstructorMatcher(Type container, List<Type> argTypes) {
		this(container, Collections.EMPTY_LIST, argTypes);
	    }
	    private X10ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes) {
		super(container, argTypes);
		this.typeArgs = typeArgs;
	    }
	    
	    @Override
	    public String argumentString() {
		return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
	    }
	    
	    @Override
	    public ConstructorInstance instantiate(ConstructorInstance ci) throws SemanticException {
		if (ci.formalTypes().size() != argTypes.size())
		    return null;
		if (ci instanceof X10ConstructorInstance) {
		    X10ConstructorInstance xmi = (X10ConstructorInstance) ci;
		    Type c = container != null ? container : xmi.container();
		    if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
			return X10MethodInstance_c.instantiate(xmi, c, typeArgs, argTypes, true);
		}
		return null;
	    }
	}
}
