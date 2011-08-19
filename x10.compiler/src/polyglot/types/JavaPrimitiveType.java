/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

/**
 * A <code>PrimitiveType</code> represents a type which may not be directly 
 * coerced to java.lang.Object (under the standard Java type system).    
 * <p>
 * 
 */

import polyglot.util.CodeWriter;



/**
 * An <code>JavaPrimitiveType</code> represents a primitive type in Java.This class should never be instantiated directly. Instead, you should
 * use the <code>TypeSystem.get*</code> methods.
 */
public class JavaPrimitiveType extends Type_c 
{
    private static final long serialVersionUID = -7376343049945580290L;

    protected Name name;

    /** Used for deserializing types. */
    protected JavaPrimitiveType() { }

    public JavaPrimitiveType(TypeSystem ts, Name name) {
            super(ts);
            this.name = name;
    }

    public boolean isGloballyAccessible() {
	    return true;
    }

    public String typeToString() {
	return name.toString();
    }

    public String translate(Resolver c) {
	return name.toString();
    }

    public boolean isJavaPrimitive() { return true; }
    public JavaPrimitiveType toPrimitive() { return this; }

    public int hashCode() {
	return name.hashCode();
    }

    public boolean equalsImpl(TypeObject t) {
        if (t instanceof JavaPrimitiveType) {
            JavaPrimitiveType p = (JavaPrimitiveType) t;
            return name.equals(p.name());
        }
        return false;
    }

    public String wrapperTypeString(TypeSystem ts) {
            return ts.wrapperTypeString(this);
    }
    
    public Name name() {
	return name;
    }
    
    public QName fullName() {
            return QName.make(null, name());
    }
	public void print(CodeWriter w) {
		w.write(name().toString());
	}
	public String typeName() { 
	    return toString();
	}
	
	/* All primitive types are structs. */

	/*public boolean isX10Struct() { 
		return true;
		}
	
	public Type makeX10Struct() {
		return this;
	}
	
	Flags flags = Flags.NONE;
	public Flags flags() {
		return flags;
	}
	*/
	/*public Type setFlags(Flags flags) {
		return this;
	}
	public Type clearFlags(Flags flags) {
		PrimitiveType_c c = (PrimitiveType_c) copy();
		if (c.flags != null) {
			c.flags = c.flags.clear(flags);
		}
		return c;
	}
	 public boolean equalsNoFlag(Type t2) {
			return this == t2;
		}*/
}

