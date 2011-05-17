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

package x10.core;

import x10.rtt.Types;

final public class String extends x10.core.Ref implements
    x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Character>,
    java.lang.Comparable<java.lang.String>, x10.util.Ordered
{

	private static final long serialVersionUID = 1L;

    public static final x10.rtt.RuntimeType<java.lang.String> $RTT = Types.STRING;
    
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    public x10.rtt.Type<?> $getParam(int i) {return null;}
    
    // dispatcher for method abstract public (a1:Z1)=> U.$apply(a1:Z1): U
    public java.lang.Character $apply(final java.lang.Integer a1, final x10.rtt.Type t1) {
        return $apply((int)(java.lang.Integer)a1);
    }
    // dispatcher for method abstract public x10.lang.Comparable.compareTo(that:T): x10.lang.Int
    public java.lang.Object compareTo(final java.lang.Object a1, final x10.rtt.Type t1) {
        return compareTo((java.lang.String)a1);
    }

	// dispatcher for method abstract public x10.util.Ordered.operator<(that:T):x10.lang.Boolean
	public java.lang.Object $lt(final java.lang.Object a1, final x10.rtt.Type t1) {
		return $lt$O((java.lang.String) a1);
	}

	// dispatcher for method abstract public x10.util.Ordered.operator>(that:T):x10.lang.Boolean
	public java.lang.Object $gt(final java.lang.Object a1, final x10.rtt.Type t1) {
		return $gt$O((java.lang.String) a1);
	}

	// dispatcher for method abstract public x10.util.Ordered.operator<=(that:T):x10.lang.Boolean
	public java.lang.Object $le(final java.lang.Object a1, final x10.rtt.Type t1) {
		return $le$O((java.lang.String) a1);
	}

	// dispatcher for method abstract public x10.util.Ordered.operator>=(that:T):x10.lang.Boolean
	public java.lang.Object $ge(final java.lang.Object a1, final x10.rtt.Type t1) {
		return $ge$O((java.lang.String) a1);
	}

    public java.lang.String $str;

    public String(java.lang.System[] $dummy) {
        super($dummy);
    }

    public String $init() {
        $str = "";
        return this;
    }
    
    public String() {
        $str = "";
    }

    public static String box(java.lang.String str) {
        return str == null ? null : new String(str);
    }
    public static java.lang.String unbox(Object obj) {
        return obj instanceof x10.core.String ? ((x10.core.String) obj).$str : (java.lang.String) obj;
    }
    public String $init(final java.lang.String str) {
        $str = str;
        return this;
    }
    
    public String(final java.lang.String str) {
        $str = str;
    }

//    public String(final x10.core.String id$1) {
//        $str = id$1.$str;
//    }
//
//    public String(final x10.array.Array<java.lang.Character> r,
//                  final int offset,
//                  final int length,
//                  java.lang.Class $dummy0)
//    {
//        $str = new java.lang.String(r.raw().getCharArray(), offset, length);
//    }
//
//    public String(final x10.core.Rail<java.lang.Character> r,
//                  final int offset,
//                  final int length,
//                  java.lang.Class[] $dummy0)
//    {
//        $str = new java.lang.String(r.getCharArray(), offset, length);
//    }

    public boolean equals(final java.lang.Object id$2) {
        if (id$2 instanceof java.lang.String) {
            return $str.equals(id$2);
        }
        if (id$2 instanceof x10.core.String) {
            return $str.equals(((x10.core.String)id$2).$str);
        }
        return false;
    }

//    public boolean equalsIgnoreCase(final java.lang.String id$3) {
//        return $str.equalsIgnoreCase(id$3);
//    }

    public int hashCode() {
        return $str.hashCode();
    }

    public java.lang.String toString() {
        return $str;
    }

//    public int length() {
//        return $str.length();
//    }

    public char $apply(final int index) {
        return $str.charAt(index);
    }

	public boolean $lt$O(final java.lang.String x) {
		return $str.compareTo(x) < 0;
	}

	public boolean $gt$O(final java.lang.String x) {
		return $str.compareTo(x) > 0;
	}

	public boolean $le$O(final java.lang.String x) {
		return $str.compareTo(x) <= 0;
	}

	public boolean $ge$O(final java.lang.String x) {
		return $str.compareTo(x) >= 0;
	}

//    public char charAt(final int index) {
//        return $str.charAt(index);
//    }
//
//    public x10.core.Rail<java.lang.Character> chars() {
//        return x10.core.ArrayFactory.<java.lang.Character>makeRailFromJavaArray(x10.rtt.Types.CHAR, $str.toCharArray());
//    }
//
//    public x10.core.Rail<java.lang.Byte> bytes() {
//        return x10.core.ArrayFactory.<java.lang.Byte>makeRailFromJavaArray(x10.rtt.Types.BYTE, $str.getBytes());
//    }
//
//    public x10.core.String substring(final int fromIndex, final int toIndex) {
//        return new x10.core.String($str.substring(fromIndex, toIndex));
//    }
//
//    public x10.core.String substring(final int fromIndex) {
//        return new x10.core.String($str.substring(fromIndex));
//    }
//
//    public int indexOf(final char ch) {
//        return $str.indexOf(ch);
//    }
//
//    public int indexOf(final char ch, final int i) {
//        return $str.indexOf(ch, i);
//    }
//
//    public int indexOf(final x10.core.String str) {
//        return $str.indexOf(str.$str);
//    }
//
//    public int indexOf(final x10.core.String str, final int i) {
//        return $str.indexOf(str.$str, i);
//    }
//
//    public int lastIndexOf(final char ch) {
//        return $str.lastIndexOf(ch);
//    }
//
//    public int lastIndexOf(final char ch, final int i) {
//        return $str.lastIndexOf(ch, i);
//    }
//
//    public int lastIndexOf(final x10.core.String str) {
//        return $str.lastIndexOf(str.$str);
//    }
//
//    public int lastIndexOf(final x10.core.String str, final int i) {
//        return $str.lastIndexOf(str.$str, i);
//    }
//
//    public x10.core.Rail<x10.core.String> split(final x10.core.String regex) {
//        java.lang.String[] split = $str.split(regex.$str);
//        x10.core.String[] res = new x10.core.String[split.length];
//        for (int i = 0; i < split.length; i++) {
//            res[i] = new x10.core.String(split[i]);
//        }
//        return x10.core.ArrayFactory.<x10.core.String>makeRailFromJavaArray(x10.rtt.Types.STRING, res);
//    }
//
//    public x10.core.String trim() {
//        return new x10.core.String($str.trim());
//    }

//    public static <T>x10.core.String
//        valueOf_0_$$x10$lang$String_T(final x10.rtt.Type T, final T v)
//    {
//        return new x10.core.String(java.lang.String.valueOf(v));
//    }
//
//    public static x10.core.String
//        format_1_$_x10$lang$Any_$(final x10.core.String fmt,
//                                  final x10.array.Array<java.lang.Object> args)
//    {
//        return new x10.core.String(java.lang.String.format(fmt.$str, args.raw().getObjectArray()));
//    }

//    public x10.core.String toLowerCase() {
//        return new x10.core.String($str.toLowerCase());
//    }
//
//    public x10.core.String toUpperCase() {
//        return new x10.core.String($str.toUpperCase());
//    }
//
    public int compareTo(final java.lang.String arg) {
        return $str.compareTo(arg);
    }
//
//    public int compareToIgnoreCase(final x10.core.String arg) {
//        return $str.compareToIgnoreCase(arg.$str);
//    }
//
//    public boolean startsWith(final x10.core.String arg) {
//        return $str.startsWith(arg.$str);
//    }
//
//    public boolean endsWith(final x10.core.String arg) {
//        return $str.endsWith(arg.$str);
//    }
//
//    public boolean $lt(final x10.core.String x) {
//        return $str.compareTo(x.$str) < 0;
//    }
//
//    public boolean $gt(final x10.core.String x) {
//        return $str.compareTo(x.$str) > 0;
//    }
//
//    public boolean $le(final x10.core.String x) {
//        return $str.compareTo(x.$str) <= 0;
//    }
//
//    public boolean $ge(final x10.core.String x) {
//        return $str.compareTo(x.$str) >= 0;
//    }
//
//    final public <T>x10.core.String
//        $plus_0_$$x10$lang$String_T(final x10.rtt.Type T, final T x)
//    {
//        return new x10.core.String($str + x);
//    }
//
//    public static <T>x10.core.String
//        $plus_0_$$x10$lang$String_T(final x10.rtt.Type T, final T x, final x10.core.String y)
//    {
//        return new x10.core.String(x + y.$str);
//    }
}
