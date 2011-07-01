package x10.runtime.impl.java;

public class TestClassLoading {
    public static void main(String[] args) {
        try {
            String name = args[0];
            Class.forName(name);
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
