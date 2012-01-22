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

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

final public class String extends x10.core.Ref implements
    x10.core.fun.Fun_0_1<x10.core.Int, x10.core.Char>,
    java.lang.Comparable<java.lang.String>, x10.util.Ordered
{

    private static final long serialVersionUID = 1L;
    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, String.class);

    public static final RuntimeType<java.lang.String> $RTT = Types.STRING;
    @Override
    public RuntimeType<?> $getRTT() {return $RTT;}
    @Override
    public Type<?> $getParam(int i) {return null;}
    
    // dispatcher for method abstract public (a1:Z1)=> U.$apply(a1:Z1): U
    public x10.core.Char $apply(final x10.core.Int a1, final Type t1) {
        return x10.core.Char.$box($apply(x10.core.Int.$unbox(a1)));
    }

    // dispatcher for method abstract public x10.lang.Comparable.compareTo(that:T): x10.lang.Int
    public java.lang.Object compareTo(final java.lang.Object a1, final Type t1) {
        return compareTo((java.lang.String)a1);
    }

    // implements Ordered<String>
    // dispatcher for method abstract public x10.util.Ordered.operator<(that:T):x10.lang.Boolean
    public java.lang.Object $lt(final java.lang.Object a1, final Type t1) {
        return x10.core.Boolean.$box($lt$O((java.lang.String) a1));
    }

    // dispatcher for method abstract public x10.util.Ordered.operator>(that:T):x10.lang.Boolean
    public java.lang.Object $gt(final java.lang.Object a1, final Type t1) {
        return x10.core.Boolean.$box($gt$O((java.lang.String) a1));
    }

    // dispatcher for method abstract public x10.util.Ordered.operator<=(that:T):x10.lang.Boolean
    public java.lang.Object $le(final java.lang.Object a1, final Type t1) {
        return x10.core.Boolean.$box($le$O((java.lang.String) a1));
    }

    // dispatcher for method abstract public x10.util.Ordered.operator>=(that:T):x10.lang.Boolean
    public java.lang.Object $ge(final java.lang.Object a1, final Type t1) {
        return x10.core.Boolean.$box($ge$O((java.lang.String) a1));
    }
    // for X10PrettyPrinterVisitor.returnSpecialTypeFromDispatcher
    // dispatcher for method abstract public x10.util.Ordered.operator<(that:T):x10.lang.Boolean
    public boolean $lt$O(java.lang.Object a1, Type t1) { return $lt$O((java.lang.String) a1); }
    // dispatcher for method abstract public x10.util.Ordered.operator>(that:T):x10.lang.Boolean
    public boolean $gt$O(java.lang.Object a1, Type t1) { return $gt$O((java.lang.String) a1); }
    // dispatcher for method abstract public x10.util.Ordered.operator<=(that:T):x10.lang.Boolean
    public boolean $le$O(java.lang.Object a1, Type t1) { return $le$O((java.lang.String) a1); }
    // dispatcher for method abstract public x10.util.Ordered.operator>=(that:T):x10.lang.Boolean
    public boolean $ge$O(java.lang.Object a1, Type t1) { return $ge$O((java.lang.String) a1); }


    public java.lang.String $value;

    public String(java.lang.String value) {
        $value = value;
    }

    // constructor just for allocation
    public String(java.lang.System[] $dummy) {
        super($dummy);
    }

    public String $init(java.lang.String value) {
        $value = value;
        return this;
    }
    
    public String() {
        $value = "";
    }

    public String $init() {
        $value = "";
        return this;
    }
    
    public static String $box(java.lang.String value) {
        return value == null ? null : new String(value);
    }
    
    public static java.lang.String $unbox(String obj) {
        return obj.$value;
    }

    // make $box/$unbox idempotent
    public static String $box(String obj) {
    	return obj;
    }
    
    public static java.lang.String $unbox(java.lang.String value) {
    	return value;
    }

//    public String(final x10.core.String id$1) {
//        $value = id$1.$value;
//    }
//
//    public String(final x10.array.Array<java.lang.Character> r,
//                  final int offset,
//                  final int length,
//                  java.lang.Class $dummy0)
//    {
//        $value = new java.lang.String(r.raw().getCharArray(), offset, length);
//    }
//
//    public String(final x10.core.Rail<java.lang.Character> r,
//                  final int offset,
//                  final int length,
//                  java.lang.Class[] $dummy0)
//    {
//        $value = new java.lang.String(r.getCharArray(), offset, length);
//    }

    @Override
    public boolean equals(final java.lang.Object id$2) {
        if (id$2 instanceof java.lang.String) {
            return $value.equals(id$2);
        }
        if (id$2 instanceof x10.core.String) {
            return $value.equals(((x10.core.String)id$2).$value);
        }
        return false;
    }

//    public boolean equalsIgnoreCase(final java.lang.String id$3) {
//        return $value.equalsIgnoreCase(id$3);
//    }

    @Override
    public int hashCode() {
        return $value.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return $value;
    }

//    public int length() {
//        return $value.length();
//    }

    public char $apply(final int index) {
        return $value.charAt(index);
    }

	public boolean $lt$O(final java.lang.String x) {
		return $value.compareTo(x) < 0;
	}

	public boolean $gt$O(final java.lang.String x) {
		return $value.compareTo(x) > 0;
	}

	public boolean $le$O(final java.lang.String x) {
		return $value.compareTo(x) <= 0;
	}

	public boolean $ge$O(final java.lang.String x) {
		return $value.compareTo(x) >= 0;
	}

//    public char charAt(final int index) {
//        return $value.charAt(index);
//    }
//
//    public x10.core.Rail<java.lang.Character> chars() {
//        return x10.core.ArrayFactory.<java.lang.Character>makeRailFromJavaArray(Types.CHAR, $value.toCharArray());
//    }
//
//    public x10.core.Rail<java.lang.Byte> bytes() {
//        return x10.core.ArrayFactory.<java.lang.Byte>makeRailFromJavaArray(Types.BYTE, $value.getBytes());
//    }
//
//    public x10.core.String substring(final int fromIndex, final int toIndex) {
//        return new x10.core.String($value.substring(fromIndex, toIndex));
//    }
//
//    public x10.core.String substring(final int fromIndex) {
//        return new x10.core.String($value.substring(fromIndex));
//    }
//
//    public int indexOf(final char ch) {
//        return $value.indexOf(ch);
//    }
//
//    public int indexOf(final char ch, final int i) {
//        return $value.indexOf(ch, i);
//    }
//
//    public int indexOf(final x10.core.String str) {
//        return $value.indexOf(str.$value);
//    }
//
//    public int indexOf(final x10.core.String str, final int i) {
//        return $value.indexOf(str.$value, i);
//    }
//
//    public int lastIndexOf(final char ch) {
//        return $value.lastIndexOf(ch);
//    }
//
//    public int lastIndexOf(final char ch, final int i) {
//        return $value.lastIndexOf(ch, i);
//    }
//
//    public int lastIndexOf(final x10.core.String str) {
//        return $value.lastIndexOf(str.$value);
//    }
//
//    public int lastIndexOf(final x10.core.String str, final int i) {
//        return $value.lastIndexOf(str.$value, i);
//    }
//
//    public x10.core.Rail<x10.core.String> split(final x10.core.String regex) {
//        java.lang.String[] split = $value.split(regex.$value);
//        x10.core.String[] res = new x10.core.String[split.length];
//        for (int i = 0; i < split.length; i++) {
//            res[i] = new x10.core.String(split[i]);
//        }
//        return x10.core.ArrayFactory.<x10.core.String>makeRailFromJavaArray(Types.STRING, res);
//    }
//
//    public x10.core.String trim() {
//        return new x10.core.String($value.trim());
//    }

//    public static <T>x10.core.String
//        valueOf_0_$$x10$lang$String_T(final Type T, final T v)
//    {
//        return new x10.core.String(java.lang.String.valueOf(v));
//    }
//
//    public static x10.core.String
//        format_1_$_x10$lang$Any_$(final x10.core.String fmt,
//                                  final x10.array.Array<java.lang.Object> args)
//    {
//        return new x10.core.String(java.lang.String.format(fmt.$value, args.raw().getObjectArray()));
//    }
	
	public static java.lang.String format(java.lang.String format, Object[] args) {
	    Object[] copy = new Object[args.length];
	    // rebox x10.core.Int objects into java.lang.Integers
	    for (int i = 0; i < args.length; i++) {
    	    if (args[i] instanceof x10.core.Byte)
	            copy[i] = new java.lang.Byte(x10.core.Byte.$unbox((x10.core.Byte)args[i]));
    	    else if (args[i] instanceof x10.core.Short)
	            copy[i] = new java.lang.Short(x10.core.Short.$unbox((x10.core.Short)args[i]));
    	    else if (args[i] instanceof x10.core.Int)
	            copy[i] = new java.lang.Integer(x10.core.Int.$unbox((x10.core.Int)args[i]));
    	    else if (args[i] instanceof x10.core.Long)
	            copy[i] = new java.lang.Long(x10.core.Long.$unbox((x10.core.Long)args[i]));
    	    else if (args[i] instanceof x10.core.Float)
	            copy[i] = new java.lang.Float(x10.core.Float.$unbox((x10.core.Float)args[i]));
    	    else if (args[i] instanceof x10.core.Double)
	            copy[i] = new java.lang.Double(x10.core.Double.$unbox((x10.core.Double)args[i]));
	        else
    	        copy[i] = args[i];
	    }
	    return java.lang.String.format(format, copy);
	}

//    public x10.core.String toLowerCase() {
//        return new x10.core.String($value.toLowerCase());
//    }
//
//    public x10.core.String toUpperCase() {
//        return new x10.core.String($value.toUpperCase());
//    }
//
    public int compareTo(final java.lang.String arg) {
        return $value.compareTo(arg);
    }
//
//    public int compareToIgnoreCase(final x10.core.String arg) {
//        return $value.compareToIgnoreCase(arg.$value);
//    }
//
//    public boolean startsWith(final x10.core.String arg) {
//        return $value.startsWith(arg.$value);
//    }
//
//    public boolean endsWith(final x10.core.String arg) {
//        return $value.endsWith(arg.$value);
//    }
//
//    public boolean $lt(final x10.core.String x) {
//        return $value.compareTo(x.$value) < 0;
//    }
//
//    public boolean $gt(final x10.core.String x) {
//        return $value.compareTo(x.$value) > 0;
//    }
//
//    public boolean $le(final x10.core.String x) {
//        return $value.compareTo(x.$value) <= 0;
//    }
//
//    public boolean $ge(final x10.core.String x) {
//        return $value.compareTo(x.$value) >= 0;
//    }
//
//    final public <T>x10.core.String
//        $plus_0_$$x10$lang$String_T(final Type T, final T x)
//    {
//        return new x10.core.String($value + x);
//    }
//
//    public static <T>x10.core.String
//        $plus_0_$$x10$lang$String_T(final Type T, final T x, final x10.core.String y)
//    {
//        return new x10.core.String(x + y.$value);
//    }

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write($value);
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        String str = new String((java.lang.System[]) null);
        deserializer.record_reference(str);
        return $_deserialize_body(str, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(x10.core.String string, X10JavaDeserializer deserializer) throws IOException {
        java.lang.String str  = deserializer.readString();
        string.$value = str;
        return string;
    }

    public short $_get_serialization_id() {
		return _serialization_id;
	}
}
