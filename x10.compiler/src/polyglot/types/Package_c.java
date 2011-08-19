/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.CodeWriter;

/**
 * An <code>PackageType</code> represents a package type. It may or may
 * not be fully qualified. Package types are never canonical and never
 * primitive.
 */
public class Package_c extends TypeObject_c implements Package
{
    private static final long serialVersionUID = -4825470324133363428L;

    protected Ref<? extends Package> prefix;
    protected Name name;

    /**
     * The full name is computed lazily from the prefix and name.
     */
    protected String fullname = null;

    /** Used for deserializing types. */
    protected Package_c() { }
    
    public Package_c(TypeSystem ts) {
        this(ts, null, null);
    }

    public Package_c(TypeSystem ts, Name name) {
        this(ts, null, name);
    }

    public Package_c(TypeSystem ts, Ref<? extends Package> prefix, Name name) {
        super(ts);
        this.prefix = prefix;
        this.name = name;
    }
    
    public boolean isGloballyAccessible() {
        return true;
    }
    
    protected transient Resolver memberCache;
    
    public Resolver resolver() {
        if (memberCache == null) {
            memberCache = new AnotherCachingResolver(ts.createPackageContextResolver(this),
                                                     ts.extensionInfo().getOptions().reporter);
        }
        return memberCache;
    }
    
    public Object copy() {
        Package_c n = (Package_c) super.copy();
        n.memberCache = null;
        return n;
    }
    
    public boolean equalsImpl(TypeObject o) {
        if (o instanceof Package) {
            Package p = (Package) o;
            if (name.equals(p.name())) {
                if (prefix == null)
                    return p.prefix() == null;
                else
                    return prefix.equals(p.prefix());
            }
        }
        return false;
    }
        
    public boolean packageEquals(Package p) {
	    if (p == null)
		    return false;
        if (name.equals(p.name())) {
            if (prefix == null)
                return p.prefix() == null;
            else
                return ts.packageEquals(Types.get(prefix), Types.get(p.prefix()));
        }
        return false;
    }

    public boolean isType() { return false; }
    public boolean isPackage() { return true; }
    public Type toType() { return null; }
    public Package toPackage() { return this; }

    public Ref<? extends Package> prefix() {
	return prefix;
    }

    public Name name() {
	return name;
    }

    public String translate(Resolver c) {
        if (prefix() == null) {
          return name().toString();
        }

        return prefix().get().translate(c) + "." + name();
    }

    public QName fullName() {
	return QName.make(prefix() != null ? prefix().get().fullName() : null, name);
    }

    public String toString() {
	String s = (prefix() != null ? prefix().toString() + "." : "") + name;
	return s;
    }
    
    public void print(CodeWriter w) {
	if (prefix() != null) {
	    prefix().get().print(w);
	    w.write(".");
	    w.allowBreak(2, 3, "", 0);
	}
	w.write(name.toString());
    }

    public int hashCode() {
        return name.hashCode();
    }
}
