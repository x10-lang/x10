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
public abstract class Clock(name: String) {
    public static def make(): Clock = make("");

    public static def make(name: String): Clock = RuntimeClock.make(name);

    protected def this(name: String) = property(name);

    public abstract global def registered(): boolean;

    public abstract global def dropped(): boolean;

    public abstract global def resume(): void;

    public abstract global def next(): void;

    public abstract global def phase(): int;

    public abstract global def drop(): void;
}
