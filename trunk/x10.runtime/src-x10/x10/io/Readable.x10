/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

public interface Readable {
    def read(r: Reader) : Readable throws IOException;
}
