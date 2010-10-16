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
    public static final X10Flags VALUE = createFlag("value", null);
    public static final X10Flags REFERENCE = createFlag("reference", null);
    public static final X10Flags ATOMIC = createFlag("atomic", null);
    public static final X10Flags PURE = createFlag("pure", null);
    public static final X10Flags MUTABLE = createFlag("mutable", null);
    public static final X10Flags PROPERTY = createFlag("property", null);
    public static final X10Flags STRUCT = createFlag("struct", null);
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
    	return new X10Flags().set(f);
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
    @Override
    public X10Flags set(Flags other) {
        X10Flags f = new X10Flags();
        f.flags.addAll(this.flags);
        f.flags.addAll(other.flags());
        return f;
    }

    /**
     * Create new flags with the flags in <code>other</code> cleared.
     */
    @Override
    public X10Flags clear(Flags other) {
        X10Flags f = new X10Flags();
        f.flags.addAll(this.flags);
        f.flags.removeAll(other.flags());
        return f;
    }

    /**
     * Create new flags with only flags in <code>other</code> set.
     */
    @Override
    public X10Flags retain(Flags other) {
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
    public X10Flags Value() {
        return set(VALUE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>value</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearValue() {
        return clear(VALUE);
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
        return set(REFERENCE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>reference</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearReference() {
        return clear(REFERENCE);
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
        return set(ATOMIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>atomic</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearAtomic() {
        return clear(ATOMIC);
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
     * Return a copy of this <code>this</code> with the <code>pure</code> flag
     * set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Pure() {
        return set(PURE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pure</code> flag
     * clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearPure() {
        return clear(PURE);
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
     * Return a copy of this <code>this</code> with the <code>struct</code> flag
     * set.
     */
    public X10Flags Struct() {
        return set(STRUCT);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>struct</code> flag
     * clear.
     */
    public X10Flags clearStruct() {
        return clear(STRUCT);
    }

    /**
     * Return true if <code>this</code> has the <code>struct</code> flag set.
     */
    public boolean isStruct() {
        return contains(STRUCT);
    }
    
    /**
     * Return a copy of this <code>this</code> with the <code>property</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Property() {
        return set(PROPERTY);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>property</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearProperty() {
        return clear(PROPERTY);
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
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags Clocked() {
        return set(CLOCKED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearClocked() {
        return clear(CLOCKED);
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
     * Return a copy of this <code>this</code> with the <code>hasType</code>
     * flag set.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags HasType() {
        return set(HASTYPE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>hasType</code>
     * flag clear.
     * 
     * @param flags
     *            TODO
     */
    public X10Flags clearHasType() {
        return clear(HASTYPE);
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
        boolean result = true;
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
