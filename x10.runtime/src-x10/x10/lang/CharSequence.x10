/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * This interface provides uniform, read-only access to different kinds of Char sequences.
 */
@NativeRep("java", "java.lang.CharSequence", null, "x10.rtt.Types.CHAR_SEQUENCE")
public interface CharSequence {

    /**
     * Returns the number of Chars in this sequence.
     * @return the number of Chars in this sequence.
     */
    @Native("java", "(#this).length()")
    def length():Int;

    /**
     * Returns the Char value at the specified index.  An index ranges from zero to length()-1.
     * @param index the index of the Char value to be returned
     * @return the specified Char value
     * @throws IndexOutOfBoundsException if the index argument is negative or not less than length()
     */
    @Native("java", "(#this).charAt(#index)")
    def charAt(index:Int):Char;

    /**
     * Returns a new CharSequence that is a subsequence of this sequence.
     * The subsequence starts with the Char value at fromIndex and ends with the Char value at toIndex-1.
     * The length of the returned sequence is toIndex-fromIndex, so if fromIndex==toIndex then an empty sequence is returned.
     * @param fromIndex the start index, inclusive
     * @param toIndex the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if fromIndex or toIndex are negative, if toIndex is greater than length(),
     * or if fromIndex is greater than toIndex
     */
    @Native("java", "(#this).subSequence(#fromIndex, #toIndex)")
    def subSequence(fromIndex:Int, toIndex:Int):CharSequence;

    /**
     * Returns a String containing the Chars in this sequence in the same order as this sequence.
     * The length of the String will be the length of this sequence.
     * @return a String consisting of exactly this sequence of Chars
     */
    @Native("java", "(#this).toString()")
    def toString():String;
}

// vim:shiftwidth=4:tabstop=4:expandtab
