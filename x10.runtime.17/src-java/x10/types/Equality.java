/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import x10.core.Box;
import x10.core.Ref;
import x10.core.Value;

public class Equality {
    public static boolean equalsequals(boolean a, boolean b) { return a == b; }
    public static boolean equalsequals(boolean a, Object b) { return equalsequals((Object) a, b); }
    
    public static boolean equalsequals(byte a, byte b) { return a == b; }
    public static boolean equalsequals(byte a, short b) { return a == b; }
    public static boolean equalsequals(byte a, int b) { return a == b; }
    public static boolean equalsequals(byte a, long b) { return a == b; }
    public static boolean equalsequals(byte a, float b) { return a == b; }
    public static boolean equalsequals(byte a, double b) { return a == b; }
    public static boolean equalsequals(byte a, Object b) { return equalsequals((Object) a, b); }

    public static boolean equalsequals(short a, byte b) { return a == b; }
    public static boolean equalsequals(short a, short b) { return a == b; }
    public static boolean equalsequals(short a, int b) { return a == b; }
    public static boolean equalsequals(short a, long b) { return a == b; }
    public static boolean equalsequals(short a, float b) { return a == b; }
    public static boolean equalsequals(short a, double b) { return a == b; }
    public static boolean equalsequals(short a, Object b) { return equalsequals((Object) a, b); }
    
//    public static boolean equalsequals(char a, byte b) { return a == b; }
//    public static boolean equalsequals(char a, short b) { return a == b; }
//    public static boolean equalsequals(char a, int b) { return a == b; }
//    public static boolean equalsequals(char a, long b) { return a == b; }
//    public static boolean equalsequals(char a, float b) { return a == b; }
//    public static boolean equalsequals(char a, double b) { return a == b; }
    public static boolean equalsequals(char a, char b) { return a == b; }
    public static boolean equalsequals(char a, Object b) { return equalsequals((Object) a, b); }
    
    public static boolean equalsequals(int a, byte b) { return a == b; }
    public static boolean equalsequals(int a, short b) { return a == b; }
    public static boolean equalsequals(int a, int b) { return a == b; }
    public static boolean equalsequals(int a, long b) { return a == b; }
    public static boolean equalsequals(int a, float b) { return a == b; }
    public static boolean equalsequals(int a, double b) { return a == b; }
    public static boolean equalsequals(int a, Object b) { return equalsequals((Object) a, b); }
    
    public static boolean equalsequals(long a, byte b) { return a == b; }
    public static boolean equalsequals(long a, short b) { return a == b; }
    public static boolean equalsequals(long a, int b) { return a == b; }
    public static boolean equalsequals(long a, long b) { return a == b; }
    public static boolean equalsequals(long a, float b) { return a == b; }
    public static boolean equalsequals(long a, double b) { return a == b; }
    public static boolean equalsequals(long a, Object b) { return equalsequals((Object) a, b); }
    
    public static boolean equalsequals(float a, byte b) { return a == b; }
    public static boolean equalsequals(float a, short b) { return a == b; }
    public static boolean equalsequals(float a, int b) { return a == b; }
    public static boolean equalsequals(float a, long b) { return a == b; }
    public static boolean equalsequals(float a, float b) { return a == b; }
    public static boolean equalsequals(float a, double b) { return a == b; }
    public static boolean equalsequals(float a, Object b) { return equalsequals((Object) a, b); }
    
    public static boolean equalsequals(double a, byte b) { return a == b; }
    public static boolean equalsequals(double a, short b) { return a == b; }
    public static boolean equalsequals(double a, int b) { return a == b; }
    public static boolean equalsequals(double a, long b) { return a == b; }
    public static boolean equalsequals(double a, float b) { return a == b; }
    public static boolean equalsequals(double a, double b) { return a == b; }
    public static boolean equalsequals(double a, Object b) { return equalsequals((Object) a, b); }
    
    public static boolean equalsequals(String a, String b) { return a.equals(b); }
    public static boolean equalsequals(Value a, Value b) { return a.structEquals(b); }
    public static boolean equalsequals(Ref a, Ref b) { return a == b; }

    public static boolean equalsequals(Object a, boolean b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, byte b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, short b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, char b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, int b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, float b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, long b) { return equalsequals(a, (Object) b); }
    public static boolean equalsequals(Object a, double b) { return equalsequals(a, (Object) b); }

    public static boolean equalsequals(Object a, Object b) {
        if (a == b) return true;
        
        // Ref equality is pointer equality, which we already tested.
        if (a == null || b == null) return false;
        
        /*
        // Except for boxed values...
        if (a instanceof Box) return a.equals(b);
        if (b instanceof Box) return b.equals(a);
        */
        
        if (a instanceof Ref || b instanceof Ref) return false;
        
        // Value equality is structural equality.
        if (a instanceof String) return a.equals(b);
        if (a instanceof Character && b instanceof Character)
            return (char) (Character) a == (char) (Character) b;
        if (a instanceof Number && b instanceof Number)
            return equalsNumbers(a, b);
        if (a instanceof Comparable) return ((Comparable) a).compareTo(b) == 0;
        if (a instanceof Value) return ((Value) a).structEquals(b);
        
        return false;
    }
    
    public static boolean equalsNumbers(Object a, Object b) {
        if (a instanceof Double && b instanceof Double) {
            return (double) (Double) a == (double) (Double) b;
        }
        if (a instanceof Double && b instanceof Float) {
            return (double) (Double) a == (float) (Float) b;
        }
        if (a instanceof Double && b instanceof Long) {
            return (double) (Double) a == (long) (Long) b;
        }
        if (a instanceof Double && b instanceof Integer) {
            return (double) (Double) a == (int) (Integer) b;
        }
        if (a instanceof Double && b instanceof Short) {
            return (double) (Double) a == (short) (Short) b;
        }
        if (a instanceof Double && b instanceof Byte) {
            return (double) (Double) a == (byte) (Byte) b;
        }
        
        if (a instanceof Float && b instanceof Double) {
            return (float) (Float) a == (double) (Double) b;
        }
        if (a instanceof Float && b instanceof Float) {
            return (float) (Float) a == (float) (Float) b;
        }
        if (a instanceof Float && b instanceof Long) {
            return (float) (Float) a == (long) (Long) b;
        }
        if (a instanceof Float && b instanceof Integer) {
            return (float) (Float) a == (int) (Integer) b;
        }
        if (a instanceof Float && b instanceof Short) {
            return (float) (Float) a == (short) (Short) b;
        }
        if (a instanceof Float && b instanceof Byte) {
            return (float) (Float) a == (byte) (Byte) b;
        }
        
        if (a instanceof Long && b instanceof Double) {
            return (long) (Long) a == (double) (Double) b;
        }
        if (a instanceof Long && b instanceof Float) {
            return (long) (Long) a == (float) (Float) b;
        }
        if (a instanceof Long && b instanceof Long) {
            return (long) (Long) a == (long) (Long) b;
        }
        if (a instanceof Long && b instanceof Integer) {
            return (long) (Long) a == (int) (Integer) b;
        }
        if (a instanceof Long && b instanceof Short) {
            return (long) (Long) a == (short) (Short) b;
        }
        if (a instanceof Long && b instanceof Byte) {
            return (long) (Long) a == (byte) (Byte) b;
        }
        
        if (a instanceof Integer && b instanceof Double) {
            return (int) (Integer) a == (double) (Double) b;
        }
        if (a instanceof Integer && b instanceof Float) {
            return (int) (Integer) a == (float) (Float) b;
        }
        if (a instanceof Integer && b instanceof Long) {
            return (int) (Integer) a == (long) (Long) b;
        }
        if (a instanceof Integer && b instanceof Integer) {
            return (int) (Integer) a == (int) (Integer) b;
        }
        if (a instanceof Integer && b instanceof Short) {
            return (int) (Integer) a == (short) (Short) b;
        }
        if (a instanceof Integer && b instanceof Byte) {
            return (int) (Integer) a == (byte) (Byte) b;
        }
        
        if (a instanceof Short && b instanceof Double) {
            return (short) (Short) a == (double) (Double) b;
        }
        if (a instanceof Short && b instanceof Float) {
            return (short) (Short) a == (float) (Float) b;
        }
        if (a instanceof Short && b instanceof Long) {
            return (short) (Short) a == (long) (Long) b;
        }
        if (a instanceof Short && b instanceof Integer) {
            return (short) (Short) a == (int) (Integer) b;
        }
        if (a instanceof Short && b instanceof Short) {
            return (short) (Short) a == (short) (Short) b;
        }
        if (a instanceof Short && b instanceof Byte) {
            return (short) (Short) a == (byte) (Byte) b;
        }
        
        if (a instanceof Byte && b instanceof Double) {
            return (byte) (Byte) a == (double) (Double) b;
        }
        if (a instanceof Byte && b instanceof Float) {
            return (byte) (Byte) a == (float) (Float) b;
        }
        if (a instanceof Byte && b instanceof Long) {
            return (byte) (Byte) a == (long) (Long) b;
        }
        if (a instanceof Byte && b instanceof Integer) {
            return (byte) (Byte) a == (int) (Integer) b;
        }
        if (a instanceof Byte && b instanceof Short) {
            return (byte) (Byte) a == (short) (Short) b;
        }
        if (a instanceof Byte && b instanceof Byte) {
            return (byte) (Byte) a == (byte) (Byte) b;
        }
        
        return false;
    }
}
