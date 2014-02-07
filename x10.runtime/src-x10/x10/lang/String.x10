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
import x10.util.Ordered;
import x10.util.GrowableRail;

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
@NativeRep("java", "java.lang.String", null, "x10.rtt.Types.STRING")
@NativeRep("c++", "x10::lang::String*", "x10::lang::String", null)
public final class String implements Comparable[String], CharSequence {

    /**
     * Construct the empty string.
     */
    @Native("java", "new java.lang.String()")
    public native def this():String;

    /**
     * Copy constructor.
     */
    @Native("java", "new java.lang.String(#original)")
    public native def this(original:String):String;

    /**
     * Construct a String from a Rail[Byte].
     */
    @Native("java", "new java.lang.String((#r).getByteArray())")
    public native def this(r:Rail[Byte]):String;

    /**
     * Construct a String from a GrowableRail[Byte].
     */
    @Native("java", "new java.lang.String((#r).rail().getByteArray(), 0, (int)(#r).size)")
    public native def this(r:x10.util.GrowableRail[Byte]):String;

    /**
     * Construct a String from a Rail[Byte], offset and length.
     * LONG_RAIL: unsafe int cast
     */
    @Native("java", "new java.lang.String((#r).getByteArray(),(int)#offset,(int)#length)")
    public native def this(r:Rail[Byte], offset:Long, length:Long):String;

    /**
     * Construct a String from a Rail[Char].
     */
    @Native("java", "new java.lang.String((#r).getCharArray())")
    public native def this(r:Rail[Char]):String;

    /**
     * Construct a String from a Rail[Char], offset and length.
     * LONG_RAIL: unsafe int cast
     */
    @Native("java", "new java.lang.String((#r).getCharArray(),(int)#offset,(int)#length)")
    public native def this(r:Rail[Char], offset:Long, length:Long):String;

    /**
     * Return true if the given entity is a String, and this String is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this String is equal to the given entity.
     */
    @Native("java", "(#this).equals(#that)")
    @Native("c++", "::x10aux::equals(#this,#that)")
    public native def equals(that:Any):Boolean;

    /**
     * Return true if the given entity is a String, and this String is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this String is equal to the given entity.
     */
    @Native("java", "(#this).equals(#that)")
    @Native("c++", "::x10aux::equals(#this,#that)")
    public native def equals(that:String):Boolean;

    // FIXME: Locale sensitivity
    /**
     * Returns true if this String is equal to the given String, ignoring case considerations.
     * @param x the given String
     * @return true if this String is equal to the given String ignoring case.
     */
    @Native("java", "(#this).equalsIgnoreCase(#that)")
    public native def equalsIgnoreCase(that:String):Boolean;

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
    @Native("java", "(#this).hashCode()")
    @Native("c++", "::x10aux::hash_code(#this)")
    public native def hashCode():Int;


    /**
     * Returns this String.
     * @return the String itself.
     */
    @Native("java", "(#this).toString()")
    public native def toString():String;


    /**
     * Returns the length of this String.
     * @return the length of this String.
     */
    @Native("java", "(#this).length()")
    public native def length():Int;

    /**
     * Returns the Char at the specified index in this String.
     * An index ranges from 0 to length()-1.
     * @param index the index of the Char
     * @return the Char at the specified (0-based) index of this String.
     * @see #charAt(Int)
     */
    @Native("java", "(#this).charAt(#index)")
    @Native("c++", "(#this)->charAt(#index)")
    public native operator this(index:Int):Char;

    /**
     * Returns the Char at the specified index in this String.
     * An index ranges from 0 to length()-1.
     * @param index the index of the Char
     * @return the Char at the specified (0-based) index of this String.
     * @see #operator(Int)
     */
    @Native("java", "(#this).charAt(#index)")
    public native def charAt(index:Int):Char;

    /**
     * Converts this String to a Rail of Chars.
     * @return a Rail of Chars whose length is the length of this String and
     *         whose contents are initialized to contain the Chars in this String.
     * @see #bytes()
     */
    @Native("java", "x10.runtime.impl.java.ArrayUtils.<x10.core.Char>makeRailFromJavaArray(x10.rtt.Types.CHAR, (#this).toCharArray(), false)")
    public native def chars():Rail[Char];

    /**
     * Encodes this String into a sequence of Bytes using the platform's default charset.
     * @return the Rail of Bytes representing this String in the default charset.
     * @see #chars()
     */
    @Native("java", "x10.runtime.impl.java.ArrayUtils.<x10.core.Byte>makeRailFromJavaArray(x10.rtt.Types.BYTE, (#this).getBytes(), false)")
    public native def bytes():Rail[Byte];

    /**
     * Has the same result as substring, but is present so that String may implement CharSequence.
     * @see #substring(Int,Int)
     */
    @Native("java", "(#this).substring(#fromIndex, #toIndex)")
    public native def subSequence(fromIndex:Int, toIndex:Int):CharSequence;

    /**
     * Returns a new String that is a substring of this String.
     * The substring begins at the specified fromIndex and extends to the Char at index toIndex-1.
     * Thus the length of the substring is toIndex-fromIndex.
     * @param fromIndex the starting index, inclusive
     * @param toIndex the ending index, exclusive
     * @return the specified substring.
     */
    @Native("java", "(#this).substring(#fromIndex, #toIndex)")
    public native def substring(fromIndex:Int, toIndex:Int):String;

    /**
     * Returns a new String that is a substring of this String.
     * The substring begins at the specified fromIndex and extends to last Char in the String.
     * Thus the length of the substring is length()-fromIndex-1.
     * @param fromIndex the starting index, inclusive
     * @return the specified substring.
     */
    @Native("java", "(#this).substring(#fromIndex)")
    public native def substring(fromIndex:Int):String;

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
     * @see #indexOf(Char,Int)
     * @see #indexOf(String)
     * @see #lastIndexOf(Char)
     */
    @Native("java", "(#this).indexOf(#ch)")
    public native def indexOf(ch:Char):Int;

    /**
     * Returns the index within this String of the first occurrence of the specified Char ch after
     * the given index i.  If the Char ch occurs in this String after the index i, then the index
     * of the first such occurrence is returned.
     * This index is the smallest value k&gt;=i such that:
     * <pre>
     * this(k) == ch
     * </pre>
     * is true.
     * If no such Char occurs in this String, then -1 is returned.
     * @param ch the given Char
     * @param i the given index
     * @return the index of the first occurrence of the Char in this String after the given index,
     *         or -1 if the Char does not occur.
     * @see #indexOf(Char)
     * @see #indexOf(String,Int)
     * @see #lastIndexOf(Char,Int)
     */
    @Native("java", "(#this).indexOf(#ch, #i)")
    public native def indexOf(ch:Char, i:Int):Int;

    /**
     * Returns the index within this String of the first occurrence of the specified substring.
     * The Int returned is the smallest value k such that:
     * <pre>
     * this.substring(k, k+str.length()).equals(str)
     * </pre>
     * is true.
     * @param str the substring to search for
     * @return if the String argument occurs as a substring within this String,
     *         then the index of the first character of the first such substring
     *         is returned. If it does not occur as a substring, -1 is returned.
     * @see #indexOf(String,Int)
     * @see #indexOf(Char)
     * @see #lastIndexOf(String)
     */
    @Native("java", "(#this).indexOf(#str)")
    public native def indexOf(str:String):Int;

    /**
     * Returns the index within this String of the first occurrence of the specified substring after
     * the given index i.
     * The Int returned is the smallest value k&gt;=i such that:
     * <pre>
     * this.substring(k, k+str.length()).equals(str)
     * </pre>
     * is true.
     * @param str the substring to search for
     * @param i the given index
     * @return if the String argument occurs as a substring within this String after the given index,
     *         then the index of the first character of the first such substring
     *         is returned. If it does not occur as a substring, -1 is returned.
     * @see #indexOf(String)
     * @see #indexOf(Char,Int)
     * @see #lastIndexOf(String,Int)
     */
    @Native("java", "(#this).indexOf(#str, #i)")
    public native def indexOf(str:String, i:Int):Int;

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
     * @see #lastIndexOf(Char,Int)
     * @see #lastIndexOf(String)
     * @see #indexOf(Char)
     */
    @Native("java", "(#this).lastIndexOf(#ch)")
    public native def lastIndexOf(ch:Char):Int;

    /**
     * Returns the index within this String of the last occurrence of the specified Char ch before
     * the given index i.  If the Char ch occurs in this String before the index i, then the index
     * of the last such occurrence is returned.
     * This index is the largest value k&lt;=i such that:
     * <pre>
     * this(k) == ch
     * </pre>
     * is true.
     * If no such Char occurs in this String, then -1 is returned.
     * The String is searched backwards starting at index i.
     * @param ch the given Char
     * @param i the given index
     * @return the index of the last occurrence of the Char in this String before the given index,
     *         or -1 if the Char does not occur.
     * @see #lastIndexOf(Char)
     * @see #lastIndexOf(String,Int)
     * @see #indexOf(Char,Int)
     */
    @Native("java", "(#this).lastIndexOf(#ch, #i)")
    public native def lastIndexOf(ch:Char, i:Int):Int;

    /**
     * Returns the index within this String of the rightmost occurrence of the specified substring.
     * The rightmost empty string "" is considered to occur at the index value this.length().
     * The returned index is the largest value k such that:
     * <pre>
     * this.substring(k, k+str.length()).equals(str)
     * </pre>
     * is true.
     * @param str the substring to search for
     * @return if the String argument occurs one or more times as a substring
     *         within this String, then the index of the first character of the
     *         last such substring is returned. If it does not occur as a
     *         substring, -1 is returned.
     * @see #lastIndexOf(String,Int)
     * @see #lastIndexOf(Char)
     * @see #indexOf(String)
     */
    @Native("java", "(#this).lastIndexOf(#str)")
    public native def lastIndexOf(str:String):Int;

    /**
     * Returns the index within this String of the rightmost occurrence of the specified substring
     * before the given index i.
     * The rightmost empty string "" is considered to occur at the index value this.length().
     * The returned index is the largest value k&lt;=i such that:
     * <pre>
     * this.substring(k, k+str.length()).equals(str)
     * </pre>
     * is true.
     * @param str the substring to search for
     * @param i the given index
     * @return if the String argument occurs one or more times as a substring
     *         within this String before the given index, then the index of the first character of the
     *         last such substring is returned. If it does not occur as a
     *         substring, -1 is returned.
     * @see #lastIndexOf(String)
     * @see #lastIndexOf(Char,Int)
     * @see #indexOf(String,Int)
     */
    @Native("java", "(#this).lastIndexOf(#str, #i)")
    public native def lastIndexOf(str:String, i:Int):Int;


    /**
     * Splits this String around matches of the given string; 
     * unlike in Java the splitting String is treated as a simple String
     * to be matched character by character (as in indexOf), not as 
     * a regular expression.
     * Trailing empty strings are not included in the resulting Rail.
     *
     * @param split the String to use as a delimiter.
     * @return the Rail of Strings computed by splitting this String around matches of the delimiter.
     */
    @Native("java", "x10.lang.StringHelper.split(#regex, #this)")
    @Native("c++", "::x10::lang::StringHelper::split(#regex, #this)")
    public native def split(regex:String):Rail[String];


    /**
     * Returns a copy of the string with leading and trailing whitespace removed.
     * @return The new string with no leading/trailing whitespace.
     */
    @Native("java", "(#this).trim()")
    public native def trim():String;


    /**
     * Returns the String representation of the given entity.
     * The representation is exactly the one returned by the toString() method of the entity.
     * @param v the given entity
     * @return a String representation of the given entity.
     */
    @Native("java", "java.lang.String.valueOf(#v)")
    @Native("c++", "::x10aux::safe_to_string(#v)")
    public native static def valueOf[T](v:T):String;


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
    @Native("java", "x10.runtime.impl.java.StringUtils.format(#fmt,(java.lang.Object[]) (#args).value)")
    @Native("c++", "::x10::lang::String::format(#fmt,#args)")
    public native static def format(fmt:String, args:Rail[Any]):String;


    // FIXME: Locale sensitivity
    /**
     * Converts all of the Chars in this String to lower case.
     * @return this String, converted to lowercase.
     */
    @Native("java", "(#this).toLowerCase()")
    public native def toLowerCase():String;

    // FIXME: Locale sensitivity
    /**
     * Converts all of the Chars in this String to upper case.
     * @return this String, converted to uppercase.
     */
    @Native("java", "(#this).toUpperCase()")
    public native def toUpperCase():String;


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
    @Native("java", "(#this).compareTo(#arg)")
    public native def compareTo(arg:String):Int;

    // FIXME: Locale sensitivity
    /**
     * Compares this String with another String lexicographically, ignoring case differences.
     * This method returns an integer whose sign is that of calling {@link #compareTo(String)}
     * with normalized versions of the Strings where case differences have been eliminated,
     * e.g., by calling s.toLowerCase().toUpperCase() on each String.
     * @param arg the argument String
     * @return a negative Int, zero, or a positive Int as the argument String is greater than, equal to, or less than this String, ignoring case considerations.
     */
    @Native("java", "(#this).compareToIgnoreCase(#arg)")
    public native def compareToIgnoreCase(arg:String):Int;

    /**
     * Checks if this String has another String as its head.
     * @param arg The argument string.
     * @return true if the argument string appears at the head of this String.
     *         The method returns false otherwise.
     */
    @Native("java", "(#this).startsWith(#arg)")
    public native def startsWith(arg:String):Boolean;

    /**
     * Checks if this String has another String as its tail.
     * @param arg The argument string.
     * @return true if the argument string appears at the tail of this String.
     *         The method returns false otherwise.
     */
    @Native("java", "(#this).endsWith(#arg)")
    public native def endsWith(arg:String):Boolean;

    // FIXME: Locale sensitivity
    /**
     * A less-than operator.
     * Compares this String with another String and returns true if this String is
     * strictly before the other String in dictionary order.
     * @param x the other String
     * @return true if this String is strictly before the other String.
     */
    @Native("java", "((#this).compareTo(#x) < 0)")
    @Native("c++",  "((#this)->compareTo(#x) < 0)")
    public native operator this < (x:String):Boolean;

    // FIXME: Locale sensitivity
    /**
     * A greater-than operator.
     * Compares this String with another String and returns true if this String is
     * strictly after the other String in dictionary order.
     * @param x the other String
     * @return true if this String is strictly after the other String.
     */
    @Native("java", "((#this).compareTo(#x) > 0)")
    @Native("c++",  "(::x10aux::nullCheck(#this)->compareTo(#x) > 0)")
    public native operator this > (x:String):Boolean;

    // FIXME: Locale sensitivity
    /**
     * A less-than-or-equal-to operator.
     * Compares this String with another String and returns true if this String is
     * equal to the other String or is before it in dictionary order.
     * @param x the other String
     * @return true if this String is before or equal to the other String.
     */
    @Native("java", "((#this).compareTo(#x) <= 0)")
    @Native("c++",  "(::x10aux::nullCheck(#this)->compareTo(#x) <= 0)")
    public native operator this <= (x:String):Boolean;

    // FIXME: Locale sensitivity
    /**
     * A greater-than-or-equal-to operator.
     * Compares this String with another String and returns true if this String is
     * equal to the other String or is after it in dictionary order.
     * @param x the other String
     * @return true if this String is after or equal to the other String.
     */
    @Native("java", "((#this).compareTo(#x) >= 0)")
    @Native("c++",  "(::x10aux::nullCheck(#this)->compareTo(#x) >= 0)")
    public native operator this >= (x:String):Boolean;

    /**
     * A string concatenation operator.
     * Appends the given entity to this String by calling the entity's
     * {@link x10.lang.Any#toString()} method.
     * @param x the given String
     * @param y the given entity
     * @return the resulting String
     */
    @Native("java", "((#x) + (#y))")
    @Native("c++",  "::x10::lang::String::__plus(#x, #y)")
    public native static operator[T] (x:String) + (y:T):String;

    /**
     * A string concatenation operator.
     * Prepends the given entity to the given String by calling the entity's
     * {@link x10.lang.Any#toString()} method.
     * @param x the given entity
     * @param y the given String
     * @return the resulting String
     */
    @Native("java", "((#x) + (#y))")
    @Native("c++",  "::x10::lang::String::__plus(#x, #y)")
    public native static operator[T] (x:T) + (y:String):String;

    /**
     * A string concatenation operator.
     * Appends the second String to the first String (disambiguation).
     * @param x the first String
     * @param y the second String
     * @return the resulting String
     */
    @Native("java", "((#x) + (#y))")
    @Native("c++",  "::x10::lang::String::__plus(#x, #y)")
    public native static operator (x:String) + (y:String):String;
}

public type String(s:String) = String{self==s};

class StringHelper {
    static def split(delim:String, str:String):Rail[String] {
        if (delim.equals("")) {
            return new Rail[String](str.length(), (i:Long)=>str.substring(i as Int, (i+1) as Int));
        }
        val ans = new GrowableRail[String]();
        var pos:Int = 0n;
        var nextMatch:Int = str.indexOf(delim, pos);
        while (nextMatch != -1n) {
          ans.add(str.substring(pos, nextMatch));
          pos = nextMatch+delim.length();
          nextMatch = str.indexOf(delim, pos);
        }
        if (pos < str.length()) {
            ans.add(str.substring(pos, str.length()));
        }
        return ans.toRail();
    }
}
