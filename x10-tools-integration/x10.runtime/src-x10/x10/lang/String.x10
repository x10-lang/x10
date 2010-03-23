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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.util.Ordered;

/**
 * The String class represents character strings.
 * All string literals in X10 programs, such as "Hello", are instances of String.
 * Strings are immutable and cannot be changed after they are created.
 *
 * String provides a concatenation operator '+', methods for converting
 * instances of other types to strings (which invoke the
 * {@link x10.lang.Any#toString()} method), methods for examining individual
 * characters of the sequence, for searching strings, for comparing
 * strings, for extracting substrings, and for creating a copy of a string
 * with all characters translated to uppercase or to lowercase.  Case mapping
 * is defined in {@link x10.lang.Char}.
 */
@NativeRep("java", "java.lang.String", "x10.core.BoxedString", null)
@NativeRep("c++", "x10aux::ref<x10::lang::String>", "x10::lang::String", null)
public final class String implements (Int) => Char/*TODO, (Range) => String*//*TODO, Ordered[String]*/, Comparable[String] {
    /**
     * Default constructor.
     */
    public native def this(): String;

    /**
     * Copy constructor.
     */
    public native def this(String): String;


    /**
     * Return true if the given entity is a String, and this String is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this String is equal to the given entity.
     */
    @Native("java", "(#0).equals(#1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native global safe def equals(Any): boolean;

    // FIXME: Locale sensitivity
    /**
     * Returns true if this String is equal to the given String, ignoring case considerations.
     * @param x the given String
     * @return true if this String is equal to the given String ignoring case.
     */
    @Native("java", "(#0).equalsIgnoreCase(#1)")
    @Native("c++", "(#0)->equalsIgnoreCase(#1)")
    public native global def equalsIgnoreCase(String): boolean;

    /**
     * Returns a hash code for this String.
     * The hash code for a String object is computed as
     * <pre>
     * s(0).ord()*31^(n-1) + s(1).ord()*31^(n-2) + ... + s(n-1).ord()
     * </pre>
     * using integer arithmetic, where s(i) is the ith character of the string,
     * n is the length of the string, and ^ indicates exponentiation.
     * (The hash value of the empty string is zero.)
     * @return a hash code value for this String.
     */
    @Native("java", "(#0).hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    public native global safe def hashCode(): int;


    /**
     * Returns this String.
     * @return the String itself.
     */
    @Native("java", "(#0).toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public native global safe def toString(): String;


    /**
     * Returns the length of this String.
     * @return the length of this String.
     */
    @Native("java", "(#0).length()")
    @Native("c++", "(#0)->length()")
    public native global def length(): Int;

    /**
     * Returns the Char at the specified index in this String.
     * An index ranges from 0 to length()-1.
     * @param index the index of the Char
     * @return the Char at the specified (0-based) index of this String.
     * @see #charAt(Int)
     */
    @Native("java", "(#0).charAt(#1)")
    @Native("c++", "(#0)->charAt(#1)")
    public native global def apply(index: Int): Char;

    /**
     * Returns the Char at the specified index in this String.
     * An index ranges from 0 to length()-1.
     * @param index the index of the Char
     * @return the Char at the specified (0-based) index of this String.
     * @see #apply(Int)
     */
    @Native("java", "(#0).charAt(#1)")
    @Native("c++", "(#0)->charAt(#1)")
    public native global def charAt(index: Int): Char;

    /**
     * Converts this String to a ValRail of Chars.
     * @return a ValRail of Chars whose length is the length of this String and
     *         whose contents are initialized to contain the Chars in this String.
     * @see #bytes()
     */
    @Native("java", "x10.core.RailFactory.<java.lang.Character>makeValRailFromJavaArray((#0).toCharArray())")
    @Native("c++", "(#0)->chars()")
    public native global def chars(): ValRail[Char];

    /**
     * Encodes this String into a sequence of Bytes using the platform's default charset.
     * @return the ValRail of Bytes representing this String in the default charset.
     * @see #chars()
     */
    @Native("java", "x10.core.RailFactory.<java.lang.Byte>makeValRailFromJavaArray((#0).getBytes())")
    @Native("c++", "(#0)->bytes()")
    public native global def bytes(): ValRail[Byte];

    /**
     * Returns a new String that is a substring of this String.
     * The substring begins at the specified fromIndex and extends to the Char at index toIndex-1.
     * Thus the length of the substring is toIndex-fromIndex.
     * @param fromIndex the starting index, inclusive
     * @param toIndex the ending index, exclusive
     * @return the specified substring.
     */
    @Native("java", "(#0).substring(#1, #2)")
    @Native("c++", "(#0)->substring(#1, #2)")
    public native global def substring(fromIndex: Int, toIndex: Int): String;


    /**
     * Returns the index within this String of the first occurrence of the specified Char ch.
     * If the Char ch occurs in this String, then the index of the first such occurrence is returned.
     * This index is the smallest value k such that:
     * <pre>
     * this(k) == ch
     * </pre>
     * is true.
     * If no such Char occurs in this String, then -1 is returned.
     * @param ch the given Char
     * @return the index of the first occurrence of the Char in this String, or -1 if the Char does not occur.
     * @see #indexOf(String)
     * @see #lastIndexOf(Char)
     */
    @Native("java", "(#0).indexOf(#1)")
    @Native("c++", "(#0)->indexOf(#1)")
    public native global def indexOf(ch: Char): Int;

    /**
     * Returns the index within this String of the first occurrence of the specified substring.
     * The Int returned is the smallest value k such that
     * <pre>
     * this.substring(k, k+str.length()).equals(str)
     * </pre>
     * is true.
     * @param str the substring to search for
     * @return if the String argument occurs as a substring within this object,
     *         then the index of the first character of the first such substring
     *         is returned. If it does not occur as a substring, -1 is returned.
     * @see #indexOf(Char)
     * @see #lastIndexOf(String)
     */
    @Native("java", "(#0).indexOf(#1)")
    @Native("c++", "(#0)->indexOf(#1)")
    public native global def indexOf(str: String): Int;

    /**
     * Returns the index within this String of the last occurrence of the specified Char ch.
     * If the Char ch occurs in this String, then the index of the last such occurrence is returned.
     * This index is the largest value k such that:
     * <pre>
     * this(k) == ch
     * </pre>
     * is true.
     * If no such Char occurs in this String, then -1 is returned.
     * The String is searched backwards starting at the last Char.
     * @param ch the given Char
     * @return the index of the last occurrence of the Char in this String, or -1 if the Char does not occur.
     * @see #lastIndexOf(String)
     * @see #indexOf(Char)
     */
    @Native("java", "(#0).lastIndexOf(#1)")
    @Native("c++", "(#0)->lastIndexOf(#1)")
    public native global def lastIndexOf(ch: Char): Int;

    /**
     * Returns the index within this String of the rightmost occurrence of the specified substring.
     * The rightmost empty string "" is considered to occur at the index value this.length().
     * The returned index is the largest value k such that
     * <pre>
     * this.substring(k, k+str.length()).equals(str)
     * </pre>
     * is true.
     * @param str the substring to search for
     * @return if the String argument occurs one or more times as a substring
     *         within this object, then the index of the first character of the
     *         last such substring is returned. If it does not occur as a
     *         substring, -1 is returned.
     * @see #lastIndexOf(Char)
     * @see #indexOf(String)
     */
    @Native("java", "(#0).lastIndexOf(#1)")
    @Native("c++", "(#0)->lastIndexOf(#1)")
    public native global def lastIndexOf(str: String): Int;


    /**
     * Splits this String around matches of the given regular expression.
     * Trailing empty strings are not included in the resulting ValRail.
     * @param regex the delimiting regular expression.
     * @return the ValRail of Strings computed by splitting this String around matches of the given regular expression.
     */
    @Native("java", "x10.core.RailFactory.makeValRailFromJavaArray((#0).split(#1))")
    @Native("c++", "(#0)->split(#1)")
    public native global def split(regex: String): ValRail[String];


    /**
     * Returns the String representation of the given entity.
     * The representation is exactly the one returned by the toString() method of the entity.
     * @param v the given entity
     * @return a String representation of the given entity.
     */
    @Native("java", "java.lang.String.valueOf(#4)")
    @Native("c++", "x10aux::safe_to_string(#4)")
    public native static def valueOf[T](v: T): String;


    /**
     * Returns a formatted String using the specified format String and arguments.
     * The only format specifiers supported at the moment are those common to Java's String.format() and C++'s printf.
     * If there are more arguments than format specifiers, the extra arguments are ignored.
     * The number of arguments is variable and may be zero.
     * The behaviour on a null argument depends on the conversion.
     * @param fmt the format String
     * @param args the arguments referenced by the format specifiers in the format string.
     * @return a formatted string.
     */
    @Native("java", "java.lang.String.format(#1,(#2).getBoxedArray())")
    @Native("c++", "x10::lang::String::format(#1,#2)")
    public native static def format(fmt: String, args: ValRail[Any]): String;


    // FIXME: Locale sensitivity
    /**
     * Converts all of the Chars in this String to lower case.
     * @return this String, converted to lowercase.
     */
    @Native("java", "(#0).toLowerCase()")
    @Native("c++", "(#0)->toLowerCase()")
    public native global def toLowerCase(): String;

    // FIXME: Locale sensitivity
    /**
     * Converts all of the Chars in this String to upper case.
     * @return this String, converted to uppercase.
     */
    @Native("java", "(#0).toUpperCase()")
    @Native("c++", "(#0)->toUpperCase()")
    public native global def toUpperCase(): String;


    /**
     * Compares this String with another String lexicographically.
     * The result is a negative integer if this String lexicographically precedes the argument String.
     * The result is a positive integer if this String lexicographically follows the argument String.
     * The result is zero if the Strings are equal; compareTo returns 0 exactly when the equals(Any) method would return true.
     * <p/>
     * This method compares the Chars in this String and the argument String at all indexes from 0 to the length of the shorter of the two strings.
     * If the Chars at some index k are not equal, the method returns the difference in ordinal values of those Chars:
     * <pre>
     * this(k).ord() - arg(k).ord()
     * </pre>
     * If there is no index position at which the Chars differ, then the method returns the difference of the lengths of the two strings:
     * <pre>
     * this.length() - arg.length()
     * </pre>
     * @param arg the argument String
     * @return 0 if the argument String is equal to this String; a negative Int if this String is lexicographically less than the argument String; and a positive Int if this String is lexicographically greater than the argument String.
     */
    @Native("java", "(#0).compareTo(#1)")
    @Native("c++", "(#0)->compareTo(#1)")
    public native global def compareTo(arg: String): Int;

    // FIXME: Locale sensitivity
    /**
     * Compares this String with another String lexicographically, ignoring case differences.
     * This method returns an integer whose sign is that of calling {@link #compareTo(String)}
     * with normalized versions of the Strings where case differences have been eliminated,
     * e.g., by calling s.toLowerCase().toUpperCase() on each String.
     * @param arg the argument String
     * @return a negative Int, zero, or a positive Int as the argument String is greater than, equal to, or less than this String, ignoring case considerations.
     */
    @Native("java", "(#0).compareToIgnoreCase(#1)")
    @Native("c++", "(#0)->compareToIgnoreCase(#1)")
    public native global def compareToIgnoreCase(arg: String): Int;


    // FIXME: Locale sensitivity
    /**
     * A less-than operator.
     * Compares this String with another String and returns true if this String is
     * strictly before the other String in dictionary order.
     * @param x the other String
     * @return true if this String is strictly before the other String.
     */
    @Native("java", "((#0).compareTo(#1) < 0)")
    @Native("c++",  "((#0)->compareTo(#1) < 0)")
    public native global safe operator this < (x:String): Boolean;

    // FIXME: Locale sensitivity
    /**
     * A greater-than operator.
     * Compares this String with another String and returns true if this String is
     * strictly after the other String in dictionary order.
     * @param x the other String
     * @return true if this String is strictly after the other String.
     */
    @Native("java", "((#0).compareTo(#1) > 0)")
    @Native("c++",  "((#0)->compareTo(#1) > 0)")
    public native global safe operator this > (x:String): Boolean;

    // FIXME: Locale sensitivity
    /**
     * A less-than-or-equal-to operator.
     * Compares this String with another String and returns true if this String is
     * equal to the other String or is before it in dictionary order.
     * @param x the other String
     * @return true if this String is before or equal to the other String.
     */
    @Native("java", "((#0).compareTo(#1) <= 0)")
    @Native("c++",  "((#0)->compareTo(#1) <= 0)")
    public native global safe operator this <= (x:String): Boolean;

    // FIXME: Locale sensitivity
    /**
     * A greater-than-or-equal-to operator.
     * Compares this String with another String and returns true if this String is
     * equal to the other String or is after it in dictionary order.
     * @param x the other String
     * @return true if this String is after or equal to the other String.
     */
    @Native("java", "((#0).compareTo(#1) >= 0)")
    @Native("c++",  "((#0)->compareTo(#1) >= 0)")
    public native global safe operator this >= (x:String): Boolean;

    /**
     * A string concatenation operator.
     * Appends the given entity to the given String by calling the entity's
     * {@link x10.lang.Any#toString()} method.
     * @param x the given String
     * @param y the given entity
     * @return the resulting String
     */
    @Native("java", "((#4) + (#5))")
    @Native("c++",  "((#4) + (#5))")
    public native static safe operator[T] (x:String) + (y:T): String;

    /**
     * A string concatenation operator.
     * Prepends the given entity to the given String by calling the entity's
     * {@link x10.lang.Any#toString()} method.
     * @param x the given entity
     * @param y the given String
     * @return the resulting String
     */
    @Native("java", "((#4) + (#5))")
    @Native("c++",  "((#4) + (#5))")
    public native static safe operator[T] (x:T) + (y:String): String;
}
