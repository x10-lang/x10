package x10.array;

import x10.lang.*;


public class U {

    public static void pr(String s) {
        System.out.println(s);
    }

    public static void xxx(String s) {
        System.out.println("xxx " + s);
    }

    public static void where(String s) {
        new java.lang.Exception(s).printStackTrace();
    }

    public static RuntimeException unsupported() {
        return unsupported("unsupported operation");
    }

    public static RuntimeException illegal() {
        return illegal("illegal operation");
    }

    public static RuntimeException unsupported(String msg) {
        return new UnsupportedOperationException(msg);
    }

    public static RuntimeException illegal(String msg) {
        return new IllegalOperationException(msg);
    }
}
