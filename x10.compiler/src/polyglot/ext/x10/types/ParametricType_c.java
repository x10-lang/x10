/*
 * Created by vj on Jan 9, 2005
 */
package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.Package;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * Implementation of parametric and generic types.
 * For generics, this currently implements a heterogenous
 * translation strategy.  However, the current translation
 * does not translate types inside of the method body and
 * is thus inherently incomplete (may not always work).
 * 
 * I first want to see how/if this heterogenous strategy works
 * out in general before commiting to a pass on the code of
 * the methods.
 * 
 * @author vj Jan 9, 2005
 * @author Christian Grothoff
 */
public class ParametricType_c 
    extends X10ReferenceType_c
    implements ParametricType {
    
	protected X10ReferenceType base;
	protected DepParameterExpr parameters;
	protected List typeparameters;

	public List getTypeParameters() {
	    return typeparameters;
	}	 
    
	/**
	 * 
	 */
	public ParametricType_c() {
		super();
	}

	/**
	 * @param ts
	 */
	public ParametricType_c(TypeSystem ts) {
		super(ts);
	}
    
	/**
	 * @param ts
	 * @param pos
	 */
	public ParametricType_c(TypeSystem ts,
	        Position pos, 
	        X10ReferenceType base, 
	        List typeparameters,
	        DepParameterExpr parameters) {
		super(ts, pos);
		assert base != null;
		this.base = base;
		this.typeparameters = typeparameters;
		this.parameters=parameters;
	}

        /**
         * Default implementation is pointer equality.
         */
        public boolean equalsImpl(TypeObject t) {
            if (t == this)
                return true;
            if (! (t instanceof ParametricType_c))
                return false;
            ParametricType_c pt = (ParametricType_c) t;
            if (! pt.base.equalsImpl(base))
                return false;
            
            if (pt.typeparameters != typeparameters) {
                if ( (pt.typeparameters == null) ||
                        (typeparameters == null) )
                    return false;
            
                if (pt.typeparameters.size() != typeparameters.size())
                    return false;
                Iterator it1 = typeparameters.iterator();
                Iterator it2 = pt.typeparameters.iterator();
                while (it1.hasNext()) {
                    Type t1 = (Type) it1.next();
                    Type t2 = (Type) it2.next();
                    if (! t1.equals(t2))
                        return false;
                }
            }
            if (pt.parameters == parameters)
                return true;
            if ( (pt.parameters == null) ||
                  (parameters == null) )
                return false;
            if (pt.parameters.args().size() != parameters.args().size())
                return false;

            return pt.parameters.equals(parameters);
        }
    
        public boolean isArray() {
            return base.isArray();
        }
        public boolean isX10Array() {
            return base.isX10Array();
        }
    public boolean isClass() {
        return base.isClass();
    }
    public ClassType toClass() {
        return this;
    }
    public ArrayType toArray() {
        return base.toArray();
    }
    
	public boolean isCanonical() {
	    if (! base.isCanonical())
	        return false;
	    if (typeparameters != null) {
	        Iterator it = typeparameters.iterator();
	        while (it.hasNext()) {
	            Type t = (Type) it.next();
	            if (!t.isCanonical())
	                return false;
	        }
	    }
	    return true;
	}

    private Type translateType(Type t) {
        return t;
    }
    private FieldInstance translateTypes(FieldInstance i) {
        FieldInstance fi = (FieldInstance)i.copy();
        fi.setType(translateType(i.type()));
        fi.container(this);
        return fi;
    }
    private MethodInstance translateTypes(MethodInstance i) {
        MethodInstance mi = (MethodInstance)i.copy();
        List formals = mi.formalTypes();
        List nformals = new LinkedList();
        Iterator it = formals.iterator();
        while (it.hasNext())
            nformals.add(translateType((Type)it.next()));
        mi.formalTypes(nformals);
        mi.returnType(translateType(i.returnType()));
        mi.container(this);
        return mi;
    }
    private ConstructorInstance translateTypes(ConstructorInstance i) {
        ConstructorInstance ci = (ConstructorInstance)i.copy();
        List formals = ci.formalTypes();
        List nformals = new LinkedList();
        Iterator it = formals.iterator();
        while (it.hasNext())
            nformals.add(translateType((Type)it.next()));
        ci.formalTypes(nformals);
        ci.container(this);
        return ci;
    }

    /* (non-Javadoc)
     * @see polyglot.types.ReferenceType#fields()
     */
    public List fields() {
        List il = base.toClass().fields();
        List lo = new LinkedList();
        Iterator it = il.iterator();
        while (it.hasNext()) {
            FieldInstance ci = (FieldInstance) it.next();
            lo.add(translateTypes(ci));
        }
        return lo;
    }

        /**
         * The class's constructors.
         * A list of <code>ConstructorInstance</code>.
         * @see polyglot.types.ConstructorInstance
         */
	public List constructors() {
	    List il = base.toClass().constructors();
	    List lo = new LinkedList();
	    Iterator it = il.iterator();
	    while (it.hasNext()) {
	        ConstructorInstance ci = (ConstructorInstance) it.next();
	        lo.add(translateTypes(ci));
	    }
	    return lo;
	}

        /** Get a field by name, or null. */
        public FieldInstance fieldNamed(String name) {
            FieldInstance fi = base.toClass().fieldNamed(name);
            if (fi == null)
                return null;
            return translateTypes(fi);
        }

    
    /* (non-Javadoc)
     * @see polyglot.types.ReferenceType#methods()
     */
    public List methods() {
        List methods = base.methods();
        List lo = new LinkedList();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            MethodInstance ci = (MethodInstance) it.next();
            lo.add(translateTypes(ci));
        }
        return lo;
    }

    
        /**
         * The class's member classes.
         * A list of <code>ClassType</code>.
         * @see polyglot.types.ClassType
         */
        public List memberClasses() {
            List bl = base.toClass().memberClasses();
            List bo = new LinkedList();
            Iterator it = bl.iterator();
            while (it.hasNext()) {
                ClassType ct = (ClassType) it.next();
                bo.add(new ParametricType_c(this.ts,
                            ct.position(),
                            (X10ReferenceType) ct,
                            typeparameters,
                            parameters));
            }
            return bo;
        }

        /** Returns the member class with the given name, or null. 
         * 
         * Note that I'm not sure that this is what is supposed to 
         * happen in all cases (does a static inner class of a parametric
         * type inherit the parameters of the outer class? always?
         * should we check? how to check? what does the parser do?
         * what about non-static?).  However, this may also just work
         * perfectly as-is.
         */
        public ClassType memberClassNamed(String name) {
            ClassType ct = base.toClass().memberClassNamed(name);
            if (ct == null)
                return null;
            return new ParametricType_c(this.ts,
                    ct.position(),
                    (X10ReferenceType) ct,
                    typeparameters,
                    parameters);
        }

	/**
         * This method probably does not currently work correctly if
         * the we do something like
         * 
         * class ParametricType<T> extends ParentType<T> {};
         * 
         * However, that would require some additional support
         * by the parser, too.
         *  
	 * @see polyglot.types.ReferenceType#superType()
	 */
	public Type superType() {
	    // FIXME: parent class may use parameters (extends Base<T>)
	    // that we need to instantiate (Base<Foo>)
		return base.superType();
	}

	/** 
         * This method probably does not currently work correctly if
         * the we do something like
         * 
         * class ParametricType<T> implements MyInterface<T> {};
         * 
         * However, that would require some additional support
         * by the parser, too.
         *  
	 * @see polyglot.types.ReferenceType#interfaces()
	 */
	public List interfaces() {
	    // FIXME: interfaces may use parameters (implements List<T>)
	    // that we need to instantiate (List<Foo>)
		return base.interfaces();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.Type#translate(polyglot.types.Resolver)
	 */
	public String translate(Resolver c) {
	    return base.translate(c);
	}

	/**
	 * Some code-generator uses toString() where it should not.
	 * As a result, this toString() cannot print the full dependent
	 * type signature since javac would not parse this.  FIXME 
	 * (by finding & fixing caller!).
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return base.toString(); /* 
		+ (typeparameters==null ? "" : typeparameters.toString()) 
		+ (parameters == null ? "" : parameters.toString());*/
	}

    
    /** Get the class's kind. */
        public Kind kind() {
            return base.toClass().kind();
        }

        /**
         * Return true if the class is top-level (i.e., not inner).
         * Equivalent to kind() == TOP_LEVEL.
         */
        public boolean isTopLevel() {
            return base.toClass().isTopLevel();
        }

        /**
         * Return true if the class is an inner class.
         * Equivalent to kind() == MEMBER || kind() == LOCAL || kind() == ANONYMOUS.
         * @deprecated Was incorrectly defined. Use isNested for nested classes, 
         *          and isInnerClass for inner classes.
         */
        public boolean isInner() {
            return base.toClass().isInner();
        }

        /**
         * Return true if the class is a nested.
         * Equivalent to kind() == MEMBER || kind() == LOCAL || kind() == ANONYMOUS.
         */
        public boolean isNested() {
            return base.toClass().isNested();
        }

        /**
         * Return true if the class is an inner class, that is, it is a nested
         * class that is not explicitly or implicitly declared static; an interface
         * is never an inner class.
         */
        public boolean isInnerClass() {
            return base.toClass().isInnerClass();
        }

        /**
         * Return true if the class is a member class.
         * Equivalent to kind() == MEMBER.
         */
        public boolean isMember() {
            return base.toClass().isMember();
        }

        /**
         * Return true if the class is a local class.
         * Equivalent to kind() == LOCAL.
         */
        public boolean isLocal() {
            return base.toClass().isLocal();
        }

        /**
         * Return true if the class is an anonymous class.
         * Equivalent to kind() == ANONYMOUS.
         */
        public boolean isAnonymous() {
            return base.toClass().isAnonymous();
        }

        /**
         * Return true if the class declaration occurs in a static context.
         * Is used to determine if a nested class is implicitly static.
         */
        public boolean inStaticContext() {
            return base.toClass().inStaticContext();
        }

        /** Return true if the class is strictly contained in <code>outer</code>. */
        public boolean isEnclosed(ClassType outer) {
            return base.toClass().isEnclosed(outer);
        }

        /**
         * Implementation of <code>isEnclosed</code>.
         * This method should only be called by the <code>TypeSystem</code>
         * or by a subclass.
         */
        public boolean isEnclosedImpl(ClassType outer) {
            return base.toClass().isEnclosedImpl(outer);
        }

        /** Return true if an object of the class has
         * an enclosing instance of <code>encl</code>. */
        public boolean hasEnclosingInstance(ClassType encl) {
            return base.toClass().hasEnclosingInstanceImpl(encl);
        }

        /**
         * Implementation of <code>hasEnclosingInstance</code>.
         * This method should only be called by the <code>TypeSystem</code>
         * or by a subclass.
         */
        public boolean hasEnclosingInstanceImpl(ClassType encl) {
            return base.toClass().hasEnclosingInstanceImpl(encl);
        }

        /** The class's outer class if this is a nested class, or null. */
        public ClassType outer() {
            return base.toClass().outer();
        }

        public Package package_() {
            return base.toClass().package_();
        }

        /**
         * Return the member's flags.
         */
        public Flags flags() {
            return base.toClass().flags();
        }

        /**
         * Return the member's containing type.
         */
        public ReferenceType container() {
            return base.toClass().container();
        }

        /**
         * Simple name of the type object. Anonymous classes do not have names.
         * 
         * Q: does this have any semantics? Should (must??) we add the
         * type parameters and dependent expressions here?
         */
        public String name() {
            return base.toClass().name() + "_GENERIC";
        }

        /**
         * Full dotted-name of the type object. For a package, top level class, 
         * top level interface, or primitive type, this is
         * the fully qualified name. For a member class or interface that is
         * directly enclosed in a class or interface with a fully qualified name,
         * then this is the fully qualified name of the member class or interface. 
         * For local and anonymous classes, this method returns a string that is
         * not the fully qualified name (as these classes do not have fully 
         * qualified names), but that may be suitable for debugging or error 
         * messages. 
         * 
         * Q: does this have any semantics? Should (must??) we add the
         * type parameters and dependent expressions here?
         */
        public String fullName() {
            return base.toClass().fullName() + "_GENERIC";
        }

        
}
