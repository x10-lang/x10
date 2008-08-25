package x10.types;

import x10.core.Ref;
import x10.core.Value;

public class Equality {
    public static boolean equalsequals(boolean a, boolean b) { return a == b; }
    public static boolean equalsequals(byte a, byte b) { return a == b; }
    public static boolean equalsequals(short a, short b) { return a == b; }
    public static boolean equalsequals(char a, char b) { return a == b; }
    public static boolean equalsequals(int a, int b) { return a == b; }
    public static boolean equalsequals(long a, long b) { return a == b; }
    public static boolean equalsequals(float a, float b) { return a == b; }
    public static boolean equalsequals(double a, double b) { return a == b; }
    public static boolean equalsequals(String a, String b) { return a.equals(b); }
    public static boolean equalsequals(Value a, Value b) { return a.equals(b); }
    public static boolean equalsequals(Ref a, Ref b) { return a == b; }

    public static boolean equalsequals(Object a, Object b) {
        if (a == null) return b == null;
        if (b == null) return false;
        if (a instanceof Ref) return a == b;
        if (b instanceof Ref) return false;
        if (a instanceof String) return a.equals(b);
        if (a instanceof Comparable) return ((Comparable) a).compareTo(b) == 0;
        if (a instanceof Value) return a.equals(b);
        return false;
    }
}
