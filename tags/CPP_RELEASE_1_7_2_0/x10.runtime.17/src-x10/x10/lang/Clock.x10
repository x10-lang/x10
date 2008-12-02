/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * @author tardieu
 */
public abstract value Clock(name: String) {
    public static def make(): Clock = make("");

    public static def make(name: String): Clock = new x10.runtime.Clock_c(name);

 	protected def this(name: String) = property(name);

    public abstract def registered(): boolean;

    public abstract def dropped(): boolean;

    public abstract def resume(): void;

    public abstract def next(): void;    

    public abstract def phase(): int;

    public abstract def drop(): void;
}
