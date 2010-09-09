/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.array;

public class RankMismatchException extends RuntimeException {
    public RankMismatchException(Object o, int rank) {
        super(o + " should have rank " + rank);
    }
}
