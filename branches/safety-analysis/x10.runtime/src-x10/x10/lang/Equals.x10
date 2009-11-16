/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * An interface implemented by any container (struct or class) that wishes
 * to support equality.
 */
public interface Equals {
    def equals(Top):boolean;
    def hashCode():int;
}
