package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.ext.jl.types.NullType_c;

import polyglot.ext.x10.types.X10PrimitiveType_c;
import polyglot.frontend.Source;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.NullType;
import polyglot.types.ReferenceType;
import polyglot.types.UnknownType;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

import polyglot.main.Report;

/**
 * A TypeSystem implementation for X10.
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 *
 *
 */
public class X10TypeSystem_c 
    extends TypeSystem_c 
    implements X10TypeSystem {
 
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
    public boolean isImplicitCastValid(Type fromType, Type toType) {
        assert_(fromType);
        assert_(toType);
        if (Report.should_report("debug", 5))
	    Report.report(5, "[X10TypeSystem_c] isImplicitCastValid |" + fromType + "| to |" + toType + "|?");
        boolean result = fromType.isImplicitCastValidImpl(toType);
        if (Report.should_report("debug", 5))
	    Report.report(5, "[X10TypeSystem_c] ... " + result);
        return result;
    }


    public NullableType createNullableType( Position pos, ReferenceType type ) {
    	return NullableType_c.makeNullable( this, pos, type );
    }

    public FutureType createFutureType( Position pos, Type type ) {
    	return new FutureType_c( this, pos, type );
    }
    
    
    protected UnknownType createUnknownType() {
     return new X10UnknownType_c( this );
    }
    /**
     * Factory method for ArrayTypes.
     */
    protected ArrayType arrayType(Position pos, Type type) {
        // FIXME: use our array type that support future/nullable here!
        return new X10ArrayType_c(this, pos, type);
    }
    
    protected NullType createNull() {
        return new X10NullType_c(this);
    }

    public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource) {
        return new X10ParsedClassType_c(this, init, fromSource);    
    }
       
    public ParsedClassType getRuntimeType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Runtime");
    }
    
    public ParsedClassType getActivityType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Activity");
    }

    public ParsedClassType getFutureActivityType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Activity.Expr");
    }
    
    public ParsedClassType getFutureType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Future");
    }
    
    public ParsedClassType getX10ObjectType() {
        return (ParsedClassType) forcefulLookup("x10.lang.X10Object");
    }
    
    public ParsedClassType getPlaceType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Place");
    }

    
    private Type forcefulLookup(String name) {
        try {
            Type t = typeForName(name);
            if (t==null) 
                throw new InternalCompilerError(name + " lookup resulted in null");            
            return t;
        } catch (SemanticException e) {
            throw new InternalCompilerError("While looking up "+name, e);
        }
    }
    
    /******************** Primitive types as Objects ******************/
    
    private static final String WRAPPER_PACKAGE = "x10.compilergenerated";
    
    public PrimitiveType createPrimitive(PrimitiveType.Kind kind) {
        return new X10PrimitiveType_c(this, kind);
    }

    /* predefined classes that need not be translated by polyglot */
    protected ClassType X10Object_;
    public ClassType X10Object()  {
        ClassType ret;
        if (X10Object_ != null) 
            ret = X10Object_;
        else
        	ret = X10Object_ = load("x10.lang.Object");
        return ret;
    }
    
    public MethodInstance primitiveEquals() {
        String name = WRAPPER_PACKAGE + ".BoxedNumber";

        try {
            Type ct = (Type) systemResolver().find(name);

            List args = new LinkedList();
            args.add(X10Object());
            args.add(X10Object());

            for (Iterator i = ct.toClass().methods("equals", args).iterator();
                 i.hasNext(); ) {

                MethodInstance mi = (MethodInstance) i.next();
                return mi;
            }
        }
        catch (SemanticException e) {
            throw new InternalCompilerError(e.getMessage());
        }

        throw new InternalCompilerError("Could not find equals method.");
    }

    public MethodInstance getter(PrimitiveType t) {
        String methodName = t.toString() + "Value";
        ConstructorInstance ci = wrapper(t);

        for (Iterator i = ci.container().methods().iterator();
              i.hasNext(); ) {
            MethodInstance mi = (MethodInstance) i.next();
            if (mi.name().equals(methodName) && mi.formalTypes().isEmpty()) {
                return mi;
            }
        }

        throw new InternalCompilerError("Could not find getter for " + t);
    }

    public Type boxedType(PrimitiveType t) {
        return wrapper(t).container();
    }

    public ConstructorInstance wrapper(PrimitiveType t) {
        String name = WRAPPER_PACKAGE + ".Boxed" + wrapperTypeString(t).substring("java.lang.".length());

        try {
            ClassType ct = ((Type) systemResolver().find(name)).toClass();

            for (Iterator i = ct.constructors().iterator(); i.hasNext(); ) {
                ConstructorInstance ci = (ConstructorInstance) i.next();
                if (ci.formalTypes().size() == 1) {
                    Type argType = (Type) ci.formalTypes().get(0);
                    if (equals(argType, t)) {
                        return ci;
                    }
                }
            }
        }
        catch (SemanticException e) {
            throw new InternalCompilerError(e.getMessage());
        }

        throw new InternalCompilerError("Could not find constructor for " + t);
    }
    
    
} // end of X10TypeSystem_c
