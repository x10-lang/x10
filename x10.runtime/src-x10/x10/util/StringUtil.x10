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

package x10.util;

/**
 * A collection of static methods that probably should be instance methods of
 * the String class or static methods of the various numeric classes and 
 * Boolean.  The problem is that x10.lang's numeric class's string parsers
 * separate the radix from the literal: they do not parse a valid hex literal
 * in the source format, like 0x2f, for example.  The 8 methods "parseByte",
 * "parseInt", etc. below all expect a string literal in the usual C/Java/X10
 * form without the trailing size and unsigned indicators (eg no trailing "Y"
 * for Byte literals).  The radix is 
 *     16 if, after an optional '-' in the case of signed integers, it begins
 *        with 0x or 0X,
 *      8 if it begins with 0, and
 *     10 otherwise.
 * Only X10's fixed point types allow for a radix other than 10: Float and
 * Double expect decimals, although this could change.  So that one can handle
 * true X10 integer (fixed point) literals more easily, we also provide a 
 * method that strips the trailing type indicator.
 * 
 * We also include a few methods for recognizing some common literals that are
 * used to mean roughly "true" or "false".
 */
public class StringUtil {
    /**
     * return the radix to be used in parsing an options' value.  This is used
     * internally to call the correct parse when looking up an integer value for
     * an option of any type from Byte to ULong.
     * @return 10 unless the string begins with 0, 16 if it begins with 0x or 0X,
     *    and 8 otherwise.
     */
    public static def radix(var v: String): Int {
        if (v(0n) == '-') v = v.substring(1n);
        if (v.length() < 2n || v(0n) != '0') return 10n;
        else if (v(1n) == 'x' || v(1n) == 'X') return 16n;
        else return 8n;
    }
    
    // X10's numeric parse methods expect a string with no leading indicator of the
    //  radix in the case of hex literals.  We strip the radix off here.
    private static def stripRadix(var v: String) {
        if (v(0n) == '-') v = v.substring(1n);
        switch(radix(v)) {
        case 10n: return v;
        case 16n: return v.substring(2n);
        case  8n: return v.substring(1n);
        default: throw new IllegalArgumentException("Unexpected radix, "+radix(v));
        }
    }

    /**
     * Strips the trailing non-digits from an integral (fixed point) numeric
     * literal.  The point here is not type checking: it is simply a question
     * of tokenization: stripping off any characters at the end of the string
     * that are not part of the number itself, but might be type information.
     * 
     * @return a Pair whose first entry is the numeric part of the literal
     * and whose second entry is the type indicator we've stripped or 
     * computed.  E.g. "-21L" returns ["-21", "L"], while "0x30" returns
     * ["0x30", "I"]. The "I" means "implicit type integer".
     */
    public static def stripNumericType(s: String): Pair[String, String] {
        val sLength = s.length();
        var n: Int = sLength - 1n;
        var typeLit: String;
        val sUpper = s.toUpperCase();
        val xIndex = sUpper.indexOf("X");
        val dotIndex = sUpper.indexOf(".");
        if (xIndex < 0n) { // is base 10: cannot have trailing hex digits as such
            val lastN = dotIndex > 0n ? dotIndex : 0n; // may have a trailing dot
            // if it is float, there's trailing 'F'.  If it's a Double, there
            // must be at least one digit before the exponent starting 'E'.
            while(n > lastN && !sUpper(n).isDigit()) n--; 
        }
        else while(n > 0n && !isHexDigit(sUpper(n))) n--;
        if(n < sLength-1n) typeLit = sUpper.substring(n+1n); // is explicit
        else if (xIndex>0n) typeLit = "I";               //
        else if (dotIndex>=0n) typeLit = "D"; // need trailing F for float
        else if (sUpper.indexOf("E")>0n)  typeLit = "D"; // not hex, so must be the exponent
        else typeLit = "I";                             // implicit integer
        return Pair[String, String](sUpper.substring(0n,n+1n), typeLit) ; 
    }

    private static def isHexDigit(c: Char) {
        val ordC = c.ord();
        return (ordC >= 0x30 && ordC <= 0x39) || (ordC >= 0x41 && ordC <= 0x46);
    }
    
    public static def parseByte(s: String) = (s(0n)=='-'?-1Y:1Y)*Byte.parse(stripRadix(s), radix(s));
    public static def parseShort(s: String) = (s(0n)=='-'?-1S:1S)*Short.parse(stripRadix(s), radix(s));
    public static def parseInt(s: String) = (s(0n)=='-'?-1n:1n)*Int.parse(stripRadix(s), radix(s));
    public static def parseLong(s: String) = (s(0n)=='-'?-1:1)*Long.parse(stripRadix(s), radix(s));
    public static def parseUByte(s: String) = UByte.parse(stripRadix(s), radix(s));
    public static def parseUShort(s: String) = UShort.parse(stripRadix(s), radix(s));
    public static def parseUInt(s: String) = UInt.parse(stripRadix(s), radix(s));
    public static def parseULong(s: String) = ULong.parse(stripRadix(s), radix(s));
    
    public static def formatArray[T](a: Rail[T]) = formatArray[T](a, ", ", "    ", 80n);
    public static def formatArray[T](a: Rail[T], separator:String, leftPad: String, maxLength: Int) {
        if(a.size == 0) return "";
        val lines = new StringBuilder();
        var aLine: StringBuilder = new StringBuilder();
        aLine.add(leftPad);
        for(n in 0..(a.size-2)) {
            aLine.add(a(n).toString());
            aLine.add(separator);
            if(aLine.length() >= maxLength) {
                lines.add(aLine.result());
                lines.add("\n");
                aLine = new StringBuilder();
                aLine.add(leftPad);
            }
        }
        if (aLine.length() >= maxLength) {
            lines.add(aLine.result());
            lines.add("\n");
            aLine = new StringBuilder();
            aLine.add(leftPad);
        }
        aLine.add(a(a.size-2));
        if (aLine.length() >= maxLength) aLine.add("\n");
        lines.add(aLine);
        return lines.result();
    }
    
    private static def makeSet[T](a: Rail[T]) {
        return makeSet(a, (3n*(a.size as Int))/2n); // assume no duplicates, allow for slop
    }
    
    private static def makeSet[T](a: Rail[T], hashTableSize: Int) {
        val set = new HashSet[T](hashTableSize);
        for(v in a) set.add(v);
        return set;
    }
    
    private static TRUE_STRINGS 
    = ["1","yea","yes","si","sim","oui","ja","ok","okay","true","t","on"];
    /** the set of strings that are to be interpreted as meaning "yes" or "true" */
    public static trueStrings = makeSet[String](TRUE_STRINGS);
    private static FALSE_STRINGS
    = ["0", "no", "non", "nao", "nay", "nein", "false", "f", "off"];
    /** the set of strings that are to be interpreted as meaning "no" or "false" */
    public static falseStrings = makeSet[String](FALSE_STRINGS);
    private static def makeBooleanMap() {
        val map = new HashMap[String, Int]();
        for(s in trueStrings) map.put(s, 1n);
        for(s in falseStrings) map.put(s, 0n);
        return map;
    }
    /** maps the true strings to true and the false strings to false */
    public static booleans = makeBooleanMap();

    /**
     * the argument is first translated to lower case and then is checked
     * against common words meaning "yes", "true" or "ok", or the opposite,
     * as collected in the hash map "booleans".  The "true" entries in
     * booleans have value 1, the "false" have value 0.  You are free to
     * clear the table and insert your own mapping, but be careful to 
     * to use lower case and to observe the rule true=>1, false=>0.
     * 
     * If the argument v, lower-cased, is not a key in the booleans map, an
     * IllegalArgumentException is thrown.
     * @param v a String that is either in trueStrings or falseStrings.
     * @returns a Boolean: true if v means "true", false if v means "false". 
     */
    public static def checkBoolean(v: String) {
        val vlow = v.toLowerCase();
        val fromMap = booleans.getOrElse(vlow, -1n);
        if (fromMap == 1n) return true;
        else if(fromMap == 0n) return false;
        else {
            throw new IllegalArgumentException("Expected boolean string literal, got \""+v+"\"");
        }
    }
}