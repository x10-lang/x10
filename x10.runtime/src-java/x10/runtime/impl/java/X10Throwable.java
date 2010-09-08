package x10.runtime.impl.java;

public class X10Throwable extends RuntimeException {

    public X10Throwable() {
        super();
    }

    public X10Throwable(String message) {
        super(message);
    }

    public X10Throwable(Throwable cause) {
        super(cause);
    }

    public X10Throwable(String message, Throwable cause) {
        super(message, cause);
    }

    static public X10Throwable getCorrespondingX10Exception(RuntimeException e) {
        String newExcName = "x10.lang.RuntimeException";
        if (e instanceof java.lang.ArithmeticException) {
            newExcName = "x10.lang.ArithmeticException";
        } else if (e instanceof java.lang.ArrayIndexOutOfBoundsException) {
            newExcName = "x10.lang.ArrayIndexOutOfBoundsException";
        } else if (e instanceof java.lang.ClassCastException) {
            newExcName = "x10.lang.ClassCastException";
        } else if (e instanceof java.lang.IllegalArgumentException) {
            newExcName = "x10.lang.IllegalArgumentException";
        } else if (e instanceof java.util.NoSuchElementException) {
            newExcName = "x10.util.NoSuchElementException";
        } else if (e instanceof java.lang.NullPointerException) {
            newExcName = "x10.lang.NullPointerException";
        } else if (e instanceof java.lang.UnsupportedOperationException) {
            newExcName = "x10.lang.UnsupportedOperationException";
        } else {
            // no corresponding x10 exceptions defined
        }

        try {
            return (X10Throwable)(Class.forName(newExcName).newInstance());
        } catch (ClassNotFoundException e1) {
        } catch (InstantiationException e2) {
        } catch (IllegalAccessException e3) {
        }
        throw new Error();
    }

    static public X10Throwable getCorrespondingX10Error(Error e) {
        String newExcName = "x10.lang.Error";
        if (e instanceof java.lang.OutOfMemoryError) {
            newExcName = "x10.lang.OutOfMemoryError";
        } else {
            // no corresponding x10 errors defined
        }

        try {
            return (X10Throwable)(Class.forName(newExcName).newInstance());
        } catch (ClassNotFoundException e1) {
        } catch (InstantiationException e2) {
        } catch (IllegalAccessException e3) {
        }
        throw new Error();
    }
}
