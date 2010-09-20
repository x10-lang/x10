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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import polyglot.main.Report;
import polyglot.types.Flags;

/**
 * Representation of X10 flags.
 * 
 * @author pcharles
 * @author vj
 * 
 */
public class X10Flags extends Flags {
    private static final long serialVersionUID = 9052097680529482894L;

    public static final Set<String> X10_FLAGS = new TreeSet<String>();
    public static final X10Flags EXTERN = createFlag("extern", null);
    public static final X10Flags VALUE = createFlag("value", null);
    public static final X10Flags REFERENCE = createFlag("reference", null);
    public static final X10Flags ATOMIC = createFlag("atomic", null);
    public static final X10Flags PURE = createFlag("pure", null);
    public static final X10Flags MUTABLE = createFlag("mutable", null);
    public static final X10Flags SAFE = createFlag("safe", null);
 //   public static final X10Flags LOCAL = createFlag("local", null);
    public static final X10Flags NON_BLOCKING = createFlag("nonblocking", null);
    public static final X10Flags SEQUENTIAL = createFlag("sequential", null);
    public static final X10Flags INCOMPLETE = createFlag("incomplete", null);
    public static final X10Flags PROPERTY = createFlag("property", null);
    public static final X10Flags SHARED = createFlag("shared", null);
    public static final X10Flags GLOBAL = createFlag("global", null);
   // public static final X10Flags ROOTED = createFlag("rooted", null);
    public static final X10Flags STRUCT = createFlag("struct", null);
    public static final X10Flags PINNED = createFlag("pinned", null);
    public static final X10Flags HASTYPE = createFlag("hastype", null); // can only be created through a <: Type declaration.
    public static final X10Flags CLOCKED = createFlag("clocked", null);  

    /**
     * Return a new Flags object with a new name. Should be called only once per
     * name.
     * 
     * @param name
     *            the name of the new flag
     * @param after
     *            the flags after which this flag should be printed; Flags.NONE
     *            to print before all other flags, null if we should print at
     *            the end.
     */
    public static X10Flags createFlag(String name, Flags after) {
        addToOrder(name, after);
        X10_FLAGS.add(name);
        return new X10Flags(name);
    }

    /**
     * Launder Flags as X10Flags. Called so that when translate is called,
     * X10Flags.translate is invoked. This hack is necessary because Polyglot
     * has polyglot.types.Flag baked in all over the place.
     * 
     * @param f
     * @return
     */
    public static X10Flags toX10Flags(Flags f) {
    	if (f instanceof X10Flags)
    		return (X10Flags) f;
    	return new X10Flags().setX(f == null? Flags.NONE : f);
    }

    public static boolean isX10Flag(Flags f) {
        return f instanceof X10Flags;
    }

    private X10Flags() {
        super();
    }

    protected X10Flags(String name) {
        super(name);
    }

    /**
     * Create new flags with the flags in <code>other</code> also set.
     */
    public X10Flags setX(Flags other) {
        X10Flags f = new X10Flags();
        f.flags.addAll(this.flags);
        f.flags.addAll(other.flags());
        return f;
    }

    /**
     * Create new flags with the flags in <code>other</code> cleared.
     */
    public X10Flags clearX(Flags other) {
        X10Flags f = new X10Flags();
        f.flags.addAll(this.flags);
        f.flags.removeAll(other.flags());
        return f;
    }

    /**
     * Create new flags with only flags in <code>other</code> set.
     */
    public X10Flags retainX(Flags other) {
        X10Flags f = new X10Flags();
        f.flags.addAll(this.flags);
        f.flags.retainAll(other.flags());
        return f;
    }

    /**
     * Return a copy of this <code>this</code> with the <code>value</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Extern() {
        return setX(EXTERN);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>value</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearExtern() {
        return clearX(EXTERN);
    }

    /**
     * Return true if <code>this</code> has the <code>value</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isExtern() {
        return contains(EXTERN);
    }

    public static boolean isExtern(Flags flags) {
        return flags.contains(EXTERN);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>value</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Value() {
        return setX(VALUE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>value</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearValue() {
        return clearX(VALUE);
    }

    /**
     * Return true if <code>this</code> has the <code>value</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isValue() {
        return contains(VALUE);
    }

    public static boolean isValue(Flags flags) {
        return flags.contains(VALUE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>reference</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Reference() {
        return setX(REFERENCE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>reference</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearReference() {
        return clearX(REFERENCE);
    }

    /**
     * Return true if <code>this</code> has the <code>reference</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isReference() {
        return contains(REFERENCE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>atomic</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Atomic() {
        return setX(ATOMIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>atomic</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearAtomic() {
        return clearX(ATOMIC);
    }

    /**
     * Return true if <code>this</code> has the <code>atomic</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isAtomic() {
        return contains(ATOMIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>global</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
  //  public X10Flags Global() {
  //      return setX(GLOBAL);
  //  }

    /**
     * Return a copy of this <code>this</code> with the <code>global</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
  //  public X10Flags clearGlobal() {
  //      return clearX(GLOBAL);
  //  }

    /**
     * Return true if <code>this</code> has the <code>global</code> flag set.
     * 
     * @param flags
     *            TODO
     */
  //  public boolean isGlobal() {
  //      return contains(GLOBAL);
 //  }

    /**
     * Return a copy of this <code>this</code> with the <code>pure</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Pure() {
        return setX(PURE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pure</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearPure() {
        return clearX(PURE);
    }

    /**
     * Return true if <code>this</code> has the <code>pure</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isPure() {
        return contains(PURE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>safe</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Safe() {
        return setX(SAFE);
    }
    
    /**
     * Return a copy of this <code>this</code> with the <code>global</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearGlobal() {
        return clearX(GLOBAL);
    }
    
    public X10Flags Global() {
        return setX(GLOBAL);
    }
    
    /**
     * Return true if <code>this</code> has the <code>global</code> flag set.
     */
    public boolean isGlobal() {
        return contains(GLOBAL) || contains(PROPERTY) || contains(STATIC);
    }
    
    /**
     * Return a copy of this <code>this</code> with the <code>struct</code> flag
     * set.
     */
    public X10Flags Struct() {
        return setX(STRUCT);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>struct</code> flag
     * clear.
     */
    public X10Flags clearStruct() {
        return clearX(STRUCT);
    }

    /**
     * Return true if <code>this</code> has the <code>struct</code> flag set.
     */
    public boolean isStruct() {
        return contains(STRUCT);
    }
    
    /**
     * Return true if <code>this</code> has the <code>safe</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isSafe() {
        return contains(SAFE);
    }

   
    /**
     * Return a copy of this <code>this</code> with the <code>nonblocking</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags NonBlocking() {
        return setX(NON_BLOCKING);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>nonblocking</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearNonBlocking() {
        return clearX(NON_BLOCKING);
    }

    /**
     * Return true if <code>this</code> has the <code>nonblocking</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isNonBlocking() {
        return contains(NON_BLOCKING) || contains(SAFE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>incomplete</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Incomplete() {
        return setX(INCOMPLETE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>incomplete</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearIncomplete() {
        return clearX(INCOMPLETE);
    }

    /**
     * Return true if <code>this</code> has the <code>incomplete</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isIncomplete() {
        return contains(INCOMPLETE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>shared</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Shared() {
        return setX(SHARED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>shared</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearShared() {
        return clearX(SHARED);
    }

    /**
     * Return true if <code>this</code> has the <code>shared</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isShared() {
        return contains(SHARED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>property</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Property() {
        return setX(PROPERTY);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>property</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearProperty() {
        return clearX(PROPERTY);
    }

    /**
     * Return true if <code>this</code> has the <code>property</code> flag set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isProperty() {
        return contains(PROPERTY);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>sequential</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Sequential() {
        return setX(SEQUENTIAL);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>sequential</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearSequential() {
        return clearX(SEQUENTIAL);
    }

    /**
     * Return true if <code>this</code> has the <code>sequential</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isSequential() {
        return contains(SEQUENTIAL) || contains(SAFE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Clocked() {
        return setX(CLOCKED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearClocked() {
        return clearX(CLOCKED);
    }

    /**
     * Return true if <code>this</code> has the <code>pinned</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isClocked() {
        return contains(CLOCKED);
    }
    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Pinned() {
        return setX(PINNED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearPinned() {
        return clearX(PINNED);
    }

    /**
     * Return true if <code>this</code> has the <code>pinned</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isPinned() {
        return contains(PINNED);
    }
    /**
     * Return a copy of this <code>this</code> with the <code>hasType</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags HasType() {
        return setX(HASTYPE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>hasType</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearHasType() {
        return clearX(HASTYPE);
    }

    /**
     * Return true if <code>this</code> has the <code>hasType/code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public boolean isHasType() {
        return contains(HASTYPE);
    }
    /**
     * Return "" if no flags set, or toString() + " " if some flags are set.
     */
    public String translate() {
        StringBuffer sb = new StringBuffer();

        for (String s : this.flags) {
            if (X10_FLAGS.contains(s))
                continue;

            sb.append(s);
            sb.append(" ");
        }

        return sb.toString();
    }

    public String prettyPrint() {
        return super.translate();
    }

    public boolean hasAllAnnotationsOf(X10Flags f) {
        boolean result = ((!f.isSequential()) || isSequential() || isSafe()) 
        && ((!f.isNonBlocking()) || isNonBlocking() || isSafe()) && ((!f.isSafe()) || isSafe());
        // Report.report(1, "X10Flags: " + this + ".hasAllAnnotationsOf(" + f +
        // ")? " + result);
        return result;

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (Iterator<String> i = this.flags.iterator(); i.hasNext();) {
            String s = i.next();

            sb.append(s);
            if (i.hasNext())
                sb.append(" ");
        }

        return sb.toString();

    }

}
