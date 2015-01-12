/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.io.Serializable;
import java.util.*;

/**
 * <code>Flags</code> is an immutable set of class, method, or field modifiers.
 * We represent package scope as the absence of private, public and protected
 * scope modifiers.
 */
public class Flags implements Serializable
{
    private static final long serialVersionUID = -539580723506705467L;

    protected Set<String> flags;


    public static final Flags NONE         = new Flags();
    public static final Flags PUBLIC       = createFlag("public", null);
    public static final Flags PRIVATE      = createFlag("private", null);
    public static final Flags PROTECTED    = createFlag("protected", null);
    public static final Flags STATIC       = createFlag("static", null);
    public static final Flags FINAL        = createFlag("final", null);
    public static final Flags TRANSIENT    = createFlag("transient", null);
    public static final Flags NATIVE       = createFlag("native", null);
    public static final Flags INTERFACE    = createFlag("interface", null);
    public static final Flags ABSTRACT     = createFlag("abstract", null);
    public static final Flags JAVA_FLAGS     = PUBLIC.Private().Protected().Static().Final().Transient().Native().Interface().Abstract();

    public static final Flags REFERENCE = createFlag("reference", null);
    public static final Flags ATOMIC = createFlag("atomic", null);
    public static final Flags PURE = createFlag("pure", null);
    public static final Flags MUTABLE = createFlag("mutable", null);
    public static final Flags PROPERTY = createFlag("property", null);
    public static final Flags STRUCT = createFlag("struct", null);
    public static final Flags CLOCKED = createFlag("clocked", null);

    /** All access flags. */
    protected static final Flags ACCESS_FLAGS = PUBLIC.set(PRIVATE).set(PROTECTED);

    /**
     * Return a new Flags object with a new name.  Should be called only once
     * per name.
     *
     * @param name the name of the new flag
     * @param after the flags after which this flag should be printed;
     *        Flags.NONE to print before all other flags, null
     *        if we should print at the end.
     */
    private static Flags createFlag(String name, Flags after) {
        return new Flags(name);
    }


    /**
     * Effects: returns a new accessflags object with no accessflags set.
     */
    protected Flags() {
        this.flags = new TreeSet<String>();
    }
    protected Flags(String name) {
        this();
        flags.add(name);
    }

    public Set<String> flags() {
        return this.flags;
    }

    /**
     * Create new flags with the flags in <code>other</code> also set.
     */
    public Flags set(Flags other) {
        Flags f = new Flags();
        f.flags.addAll(this.flags);
        f.flags.addAll(other.flags);
        return f;
    }

    /**
     * Create new flags with the flags in <code>other</code> cleared.
     */
    public Flags clear(Flags other) {
        Flags f = new Flags();
        f.flags.addAll(this.flags);
        f.flags.removeAll(other.flags);
        return f;
    }

    /**
     * Create new flags with only flags in <code>other</code> set.
     */
    public Flags retain(Flags other) {
        Flags f = new Flags();
        f.flags.addAll(this.flags);
        f.flags.retainAll(other.flags);
        return f;
    }

    public Flags retainJava() {
        return retain(JAVA_FLAGS);
    }

    /**
     * Check if <i>any</i> flags in <code>other</code> are set.
     */
    public boolean intersects(Flags other) {
        for (String name : this.flags) {
            if (other.flags.contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if <i>all</i> flags in <code>other</code> are set.
     */
    public boolean contains(Flags other) {
        return this.flags.containsAll(other.flags);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>public</code>
     * flag set.
     */
    public Flags Public() {
	return set(PUBLIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>public</code>
     * flag clear.
     */
    public Flags clearPublic() {
	return clear(PUBLIC);
    }

    /**
     * Return true if <code>this</code> has the <code>public</code> flag set.
     */
    public boolean isPublic() {
	return contains(PUBLIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>private</code>
     * flag set.
     */
    public Flags Private() {
	return set(PRIVATE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>private</code>
     * flag clear.
     */
    public Flags clearPrivate() {
	return clear(PRIVATE);
    }

    /**
     * Return true if <code>this</code> has the <code>private</code> flag set.
     */
    public boolean isPrivate() {
	return contains(PRIVATE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>protected</code>
     * flag set.
     */
    public Flags Protected() {
	return set(PROTECTED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>protected</code>
     * flag clear.
     */
    public Flags clearProtected() {
	return clear(PROTECTED);
    }

    /**
     * Return true if <code>this</code> has the <code>protected</code> flag set.
     */
    public boolean isProtected() {
	return contains(PROTECTED);
    }

    /**
     * Return a copy of this <code>this</code> with no access flags
     * (<code>public</code>, <code>private</code>, <code>protected</code>) set.
     */
    public Flags Package() {
        return clear(ACCESS_FLAGS);
    }

    /**
     * Return true if <code>this</code> has the no access flags
     * (<code>public</code>, <code>private</code>, <code>protected</code>) set.
     */
    public boolean isPackage() {
        return ! intersects(ACCESS_FLAGS);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>static</code>
     * flag set.
     */
    public Flags Static() {
	return set(STATIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>static</code>
     * flag clear.
     */
    public Flags clearStatic() {
	return clear(STATIC);
    }

    /**
     * Return true if <code>this</code> has the <code>static</code> flag set.
     */
    public boolean isStatic() {
	return contains(STATIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>final</code>
     * flag set.
     */
    public Flags Final() {
	return set(FINAL);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>final</code>
     * flag clear.
     */
    public Flags clearFinal() {
	return clear(FINAL);
    }

    /**
     * Return true if <code>this</code> has the <code>final</code> flag set.
     */
    public boolean isFinal() {
	return contains(FINAL);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>transient</code>
     * flag set.
     */
    public Flags Transient() {
	return set(TRANSIENT);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>transient</code>
     * flag clear.
     */
    public Flags clearTransient() {
	return clear(TRANSIENT);
    }

    /**
     * Return true if <code>this</code> has the <code>transient</code> flag set.
     */
    public boolean isTransient() {
	return contains(TRANSIENT);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>native</code>
     * flag set.
     */
    public Flags Native() {
	return set(NATIVE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>native</code>
     * flag clear.
     */
    public Flags clearNative() {
	return clear(NATIVE);
    }

    /**
     * Return true if <code>this</code> has the <code>native</code> flag set.
     */
    public boolean isNative() {
	return contains(NATIVE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>interface</code>
     * flag set.
     */
    public Flags Interface() {
	return set(INTERFACE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>interface</code>
     * flag clear.
     */
    public Flags clearInterface() {
	return clear(INTERFACE);
    }

    /**
     * Return true if <code>this</code> has the <code>interface</code> flag set.
     */
    public boolean isInterface() {
	return contains(INTERFACE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>abstract</code>
     * flag set.
     */
    public Flags Abstract() {
	return set(ABSTRACT);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>abstract</code>
     * flag clear.
     */
    public Flags clearAbstract() {
	return clear(ABSTRACT);
    }

    /**
     * Return true if <code>this</code> has the <code>abstract</code> flag set.
     */
    public boolean isAbstract() {
	return contains(ABSTRACT);
    }


    /**
     * Return a copy of this <code>this</code> with the <code>atomic</code> flag
     * set.
     */
    public Flags Atomic() {
        return set(ATOMIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>atomic</code> flag
     * clear.
     */
    public Flags clearAtomic() {
        return clear(ATOMIC);
    }

    /**
     * Return true if <code>this</code> has the <code>atomic</code> flag set.
     */
    public boolean isAtomic() {
        return contains(ATOMIC);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pure</code> flag
     * set.
     */
    public Flags Pure() {
        return set(PURE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pure</code> flag
     * clear.
     */
    public Flags clearPure() {
        return clear(PURE);
    }

    /**
     * Return true if <code>this</code> has the <code>pure</code> flag set.
     */
    public boolean isPure() {
        return contains(PURE);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>struct</code> flag
     * set.
     */
    public Flags Struct() {
        return set(STRUCT);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>struct</code> flag
     * clear.
     */
    public Flags clearStruct() {
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
     */
    public Flags Property() {
        return set(PROPERTY);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>property</code>
     * flag clear.
     */
    public Flags clearProperty() {
        return clear(PROPERTY);
    }

    /**
     * Return true if <code>this</code> has the <code>property</code> flag set.
     */
    public boolean isProperty() {
        return contains(PROPERTY);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag set.
     */
    public Flags Clocked() {
        return set(CLOCKED);
    }

    /**
     * Return a copy of this <code>this</code> with the <code>pinned</code>
     * flag clear.
     */
    public Flags clearClocked() {
        return clear(CLOCKED);
    }

    /**
     * Return true if <code>this</code> has the <code>pinned</code> flag
     * set.
     */
    public boolean isClocked() {
        return contains(CLOCKED);
    }


    /**
     * Return true if <code>this</code> has more restrictive access flags than
     * <code>f</code>.
     */
    public boolean moreRestrictiveThan(Flags f) {
        if (isPrivate() && (f.isProtected() || f.isPackage() || f.isPublic())) {
            return true;
        }

        if (isPackage() && (f.isProtected() || f.isPublic())) {
            return true;
        }

        if (isProtected() && f.isPublic()) {
            return true;
        }

        if (!isProperty() && f.isProperty())
            return true;

        return false;
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

    /**
     * Return "" if no flags set, or toString() + " " if some flags are set.
     */
    public String translateJava() {
        return retainJava().translate();
    }
    public String translate() {
        StringBuffer sb = new StringBuffer();

        for (String s : this.flags) {
            sb.append(s);
            sb.append(" ");
        }

        return sb.toString();
    }

    public int hashCode() {
        return flags.hashCode();
    }

    public boolean equals(Object o) {
	return o instanceof Flags && flags.equals(((Flags) o).flags);
    }



    public String prettyPrint() {
        return translate();
    }

    public boolean hasAllAnnotationsOf(Flags f) {
        boolean result = true;
        // Report.report(1, "Flags: " + this + ".hasAllAnnotationsOf(" + f +
        // ")? " + result);
        return result;

    }
}
