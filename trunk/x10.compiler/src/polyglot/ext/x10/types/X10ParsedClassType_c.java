/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ext.jl.types.ParsedClassType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;

/** 6/2006 Modified so that every type is now potentially generic and dependent.
 * @author vj
 */
public class X10ParsedClassType_c extends ParsedClassType_c
	implements X10ParsedClassType
{
	
	public X10ParsedClassType_c() { super();}

	/**
	 * @param ts
	 * @param init
	 * @param fromSource
	 */
	public X10ParsedClassType_c(TypeSystem ts,
								LazyClassInitializer init,
								Source fromSource)
	{
		super(ts, init, fromSource);
	}

    /** An instance of X10ParsedClassType_c obtained by reading a code
     ** file defining a class C is said to tbe base representation of
     ** C. X10 supports the creation of variants of the type obtained
     ** by parsing C; these variants are obtained by instantiating
     ** type parameters or by supplying dependent clauses. Each
     ** variant is represented in the type system by an instance of
     ** X10ClassType_c (with the appropriate fields set). In the
     ** field baseClass we record the instance of X10ParsedClassType_c
     ** obtained by reading the code from a file. This instance is
     ** guaranteed to have a null depClaus and an empty list in the
     ** typeParameters field.

     ** Now we can determine if two instances of X10ParsedClassType_c
     ** correspond to the same base Class by simply == checking their
     ** baseClass fields.

     */  
	protected DepParameterExpr depClause;
	protected List/*<GenParameterExpr>*/ typeParameters;
	protected X10Type baseType = this;
	public X10Type baseType() { return baseType;}
	public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public X10Type makeVariant(DepParameterExpr d, List/*<GenParameterExpr>*/ l) { 
        if (  Report.should_report("debug", 5)) {
           // new Exception().printStackTrace();
            Report.report(5,"X10ParsedClassType_c. " + this + "(#" + hashCode() + ").makeVariant(|" + d + "|, |" + l + "|");
        }
	    if (d == null && (l == null || l.isEmpty()))
	        return this;
	    X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
	    // n.baseType = baseType; // this may not be needed.
	    n.typeParameters = l;
	    n.depClause = d;
        if ( Report.should_report("debug", 5))
            Report.report(5,"X10ParsedClassType_c.makeVariant: " + n + "|(#" + n.hashCode() 
                    + ") created with l=|" + l + "|");
	    return n;
	}

   public boolean equalsImpl(TypeObject o) {
        if ( Report.should_report("debug", 5))
            Report.report(5,"X10ParsedClassType_c: equals |" + this + "| and |" + o+"|?");
       
        boolean result = false;
        try {
            if (o == this) return result = true;
            // So these are two distinct type objects. Now they represent the
            // same type only if they have the same non-null baseType (so 
            // they are variants of the same type).
            
            if (! (o instanceof X10ParsedClassType_c)) return false;
            
            X10ParsedClassType_c other = (X10ParsedClassType_c) o;
            
            if ( baseType != other.baseType) return result = false;
            
            // TODO: Implement depClause typechecking!!!
            // if (depClause == null && other.depClause != null) return result = false;
            // if (depClause != null && ! depClause.equals(other.depClause)) return result = false;
            if ( Report.should_report("debug", 5))
                Report.report(5,"X10ParsedClassType_c: this=|" + this 
                        + "| and other =|" + o + "|");
            
            if (typeParameters == null || typeParameters.isEmpty()) 
                return result=(other.typeParameters == null || other.typeParameters.isEmpty());
            if (other.typeParameters == null || other.typeParameters.isEmpty())
                return result=false;
            if (typeParameters.size() != other.typeParameters.size()) return result = false;
            Iterator it1 = typeParameters.iterator();
            Iterator it2 = other.typeParameters.iterator();
            while (it1.hasNext()) {
                Type t1 = (Type) it1.next();
                Type t2 = (Type) it2.next();
                if (!t1.equals(t2)) return result=false;
            }
            return result=true;
        } finally {
            if ( Report.should_report("debug", 5))
            Report.report(5,"X10ParsedClassType_c: (in finally) returns ... |" + result+"|");
        }
    }
    
   public boolean isCanonical() {
       if (typeParameters != null) {
           Iterator it = typeParameters.iterator();
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
    /**
     * TODO: vj Fix major bug. Cannot make copies of FieldInstances. These are destructively modified in place
     * to record whether they have constant values or not.
     * 6/21/06. Hmm. This code should be triggered with what we have in CVS now if we have an array class
     * with a field of type Parameter1.
     * @param i
     * @return
     */
    private FieldInstance translateTypes(FieldInstance i) {
        return i;
       /* FieldInstance fi = (FieldInstance)i.copy();
        fi.setType(translateType(i.type()));
        fi.container(this);
        return fi;*/
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
   /*public List fields() {
        List il = super.fields();
        List lo = new LinkedList();
        Iterator it = il.iterator();
        while (it.hasNext()) {
            FieldInstance ci = (FieldInstance) it.next();
            lo.add(translateTypes(ci));
        }
        return lo;
    }*/
    

    /**
     * The class's constructors.
     * A list of <code>ConstructorInstance</code>.
     * @see polyglot.types.ConstructorInstance
     */
   /* public List constructors() {
        List il=super.constructors();
        Iterator it = il.iterator();
        while (it.hasNext()) {
            Report.report(2, "[X10ParsedClassType] "  + it.next());
            
        }
        return il;
        /*List il = super.constructors();
        List lo = new LinkedList();
        Iterator it = il.iterator();
        while (it.hasNext()) {
            ConstructorInstance ci = (ConstructorInstance) it.next();
            lo.add(translateTypes(ci));
        }
        return lo;}*/
   

    /** Get a field by name, or null. */
    public FieldInstance fieldNamed(String name) {
        FieldInstance fi = super.fieldNamed(name);
        if (fi == null)
            return null;
        return translateTypes(fi);
    }

    /* (non-Javadoc)
     * @see polyglot.types.ReferenceType#methods()
     */
    public List methods() {
        //Report.report(5, "X10ParsedClassTypes_c: methods in | (#" + this + ")|:");
    
        List methods = super.methods();
        List lo = new LinkedList();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            MethodInstance ci = (MethodInstance) it.next();
            MethodInstance m = translateTypes(ci);
            lo.add(m);
           // Report.report(5, "X10ParsedClassTypes_c: ... |" + m + "|:");
        }
       
        return lo;
    }

    /**
     * The class's member classes.
     * A list of <code>ClassType</code>.
     * @see polyglot.types.ClassType
     */
   public List memberClasses() {
        List bl = super.memberClasses();
        List bo = new LinkedList();
        Iterator it = bl.iterator();
        while (it.hasNext()) {
            X10ClassType ct = 
                (X10ClassType) ((X10ClassType) it.next()).makeVariant(null, typeParameters);
          
           // TODO: Figure out how to set the dependentClause;.
           bo.add(ct);
        }
        return bo;
    }

    /**
     * Returns the member class with the given name, or null.
     *
     * Note that I'm not sure that this is what is supposed to
     * happen in all cases (does a static inner class of a parametric
     * type inherit the parameters of the outer class? always?
     * should we check? how to check? what does the parser do?
     * what about non-static?).  However, this may also just work
     * perfectly as-is.
     */
  /*  public ClassType memberClassNamed(String name) {
        ClassType ct = super.memberClassNamed(name);
        if (ct == null) return null;
        X10ClassType result = (X10ClassType) ct.copy();
        result.setTypeParameters(typeParameters);
        result.setDepClause(depClause);
       return result;
    }*/

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
      //  if (toString().startsWith("x10.lang.GenericReferenceArray"))
    //    Report.report(3, "X10ParsedClassType.superType " + this + "(#" + this.hashCode() + ") " + this.getClass() + " is |" + super.superType() + "|");
        return (baseType == this ? super.superType() : ((X10ParsedClassType_c) baseType).superType());
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
        return super.interfaces();
    }

    /**
     * Simple name of the type object. Anonymous classes do not have names.
     *
     * Q: does this have any semantics? Should (must??) we add the
     * type parameters and dependent expressions here?
     */
 /*  public String name() {
        return super.name() + (typeParameters == null || typeParameters.isEmpty() ? "" : "_GENERIC");
    }*/
    
  // Uncomment the method below for debugging only. The output will confuse the post compiler (javac).
    
  /*  public String toString() { 
        //Report.report(5,"X10ParsedClassType: toString |" + super.toString() + "|(#" 
        //        + this.hashCode() + this.getClass() + ") typeParameters=|" + typeParameters+"|");
        return  
        ((baseType == this) ? super.toString() : ((X10ParsedClassType_c) baseType).toString())
        + (isParametric() ? typeParameters.toString()  : "") 
        + (depClause == null ? "" :  depClause.toString());
      //  + "(#" + hashCode() + ")";
    }*/
        
//	 ----------------------------- begin manual mixin code from X10Type_c
	public boolean isNullable() { return false; }
	public boolean isFuture() { return false; }
	public FutureType toFuture() { return null; }
	public NullableType toNullable() { return null;}
	public boolean isPrimitiveTypeArray() { return X10Type_c.isPrimitiveTypeArray(this);}
	public boolean isX10Array() { 
        return X10Type_c.isX10Array(this);
        }
	public boolean isDistributedArray() { return X10Type_c.isPrimitiveTypeArray(this);}
	public boolean isBooleanArray() { return X10Type_c.isBooleanArray(this);}
	public boolean isCharArray() { return X10Type_c.isCharArray(this);}
	public boolean isByteArray() { return X10Type_c.isByteArray(this); }
	public boolean isShortArray() { return X10Type_c.isShortArray(this);}
	public boolean isIntArray() { return X10Type_c.isIntArray(this); }
	public boolean isLongArray() { return X10Type_c.isLongArray(this);}
	public boolean isFloatArray() { return X10Type_c.isFloatArray(this);}
	public boolean isDoubleArray() { return X10Type_c.isDoubleArray(this);}
	public boolean isClock() { return X10Type_c.isClock(this);}
	public boolean isPoint() { return X10Type_c.isPoint(this);}
	public boolean isPlace() { return X10Type_c.isPlace(this);}
	public boolean isRegion() { return X10Type_c.isRegion(this);}
	public boolean isDistribution() { return X10Type_c.isDistribution(this);}
	public boolean isSubtypeImpl(  Type other) { return X10Type_c.isSubtypeImpl(this, other);}
    public boolean isValueType() { return X10Type_c.isValueType(this);}
  
   // boolean f = descendsFromImpl(this);
	// ----------------------------- end manual mixin code from X10Type_c

	// ugh... toString() is being used to write out code..!!

	public boolean isImplicitCastValidImpl(Type toType) {
        if (  Report.should_report("debug", 5))
            Report.report(5,"X10ParsedClassType_c: isImplicitCastValidImpl|" 
                    + this + "(#" + hashCode() + ")| and |"
                    + toType+"(#" + toType.hashCode() + "|?");
		if (toType.isArray()) return false;
		X10Type targetType = (X10Type) toType;
         if ( Report.should_report("debug", 5))
                Report.report(5,"X10ParsedClassType_c: ... isClass? "  
                        + targetType.isClass()
                        + " isNullable? " + targetType.isNullable());
		if (!targetType.isClass() && !targetType.isNullable())
			return false;
       
		boolean result = ts.isSubtype( this, targetType);
         if ( Report.should_report("debug", 5))
             Report.report(5,"X10ParsedClassType_c: ...  " +
                     this  + (result ? "is a subtype of " : "is not a subtype of ")
                     + targetType
                     +".");
		return result;
	}

	/** Returns true iff a cast from this to <code>toType</code> is valid. */
	public boolean isCastValidImpl(Type toType) {
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type targetType = (X10Type) toType;

		boolean result = (toType.isPrimitive() && ts.isCastValid(xts.X10Object(), this));
		if (result) return result;
		if (targetType.isNullable()) {
			NullableType type = targetType.toNullable();
			return isCastValidImpl(type.base());
		}
		if (targetType.isFuture()) {
			// If we can cast the Future into this type, we can do the reverse
			return targetType.isCastValidImpl(this);
		}
        result = super.isCastValidImpl(toType);
        //Report.report(5, "X10ParsedClassType_c: isCastValidImpl "  + this + " to " + toType + ":" + result);
        
        for (Iterator i = interfaces.iterator(); i.hasNext();) {
            Report.report(5, "interface " + i.next());
        }
		return result;
	}


}

