package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.frontend.Source;
import polyglot.types.ArrayType;
import polyglot.types.LazyClassInitializer;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * A TypeSystem implementation for X10.
 * 
 * @author Christian Grothoff
 *
 * TODO Various methods need to be updated for futures; in particular we will
 * probably need to make a 'force()' method be the only thing visible for objects
 * of type 'future'.
 */
public class X10TypeSystem_c 
    extends TypeSystem_c 
    implements X10TypeSystem {
 

    /**
     * Extends Java's type system with Futures, that is, a future
     * must not be cased to a non-future and vice-versa. 
     */
    public boolean isCastValid(Type fromType, Type toType) {
        if (fromType instanceof X10ReferenceType) {
            X10ReferenceType ft = (X10ReferenceType) fromType;
            if (toType instanceof X10ReferenceType) {
                X10ReferenceType tt = (X10ReferenceType) toType;
                if (ft.isFuture() != tt.isFuture())
                    return false;
            }
        } else if (toType instanceof X10ReferenceType) {
            X10ReferenceType tt = (X10ReferenceType) toType;
            if (tt.isFuture())
                return false;
        }
        return super.isCastValid(fromType, toType);
    }
    
    /**
     * Returns true iff child and ancestor are distinct
     * reference types, and child descends from ancestor.
     **/
    public boolean descendsFrom(Type child, Type ancestor) {
           // FIXME: futures!
        return super.descendsFrom(child, ancestor);
    }

    public boolean isImplicitCastValid(Type fromType, Type toType) {
        // FIXME: futures!
        return super.isImplicitCastValid(fromType, toType);
    }

    /**
     * Factory method for ArrayTypes.
     */
    protected ArrayType arrayType(Position pos, Type type) {
        // FIXME: use our array type that support future/nullable here!
        return new ArrayType_c(this, pos, type);
    }

    public ParsedClassType createClassType(int flags) {
        return new ParsedX10ClassType_c(this, defaultClassInitializer(), null, flags);
    }

    public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource) {
        return new ParsedX10ClassType_c(this, init, fromSource, 0);
    }

    public final ParsedClassType createClassType(Source fromSource,
                                                 int flags) {
        return createClassType(defaultClassInitializer(), fromSource, flags);
    }

    public ParsedClassType createClassType(LazyClassInitializer init, 
                                           Source fromSource,
                                           int flags) {
        return new ParsedX10ClassType_c(this, init, fromSource, flags);
    }

    
    public ParsedClassType getRuntimeType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Runtime");
    }
    
    public ParsedClassType getActivityType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Activity");
    }

    public ParsedClassType getFutureActivityType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Activity.FutureActivity");
    }
    
    public ParsedClassType getFutureType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Future");
    }
    
    public ParsedClassType getX10ObjectType() {
        return (ParsedClassType) forcefulLookup("x10.lang.Object");
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
            throw new InternalCompilerError("While looking up "+name,
                                            e);
        }
    }
    
    
} // end of X10TypeSystem_c
