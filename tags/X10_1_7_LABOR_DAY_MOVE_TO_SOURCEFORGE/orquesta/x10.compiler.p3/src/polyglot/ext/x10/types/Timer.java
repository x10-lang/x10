package polyglot.ext.x10.types;

public class Timer {
    static class C { }

    static Class k = C.class;

    public static void main(String[] a) throws InstantiationException, IllegalAccessException {
	long t1, t2;
	C c;

	t1 = System.currentTimeMillis();

	for (int i = 0; i < 10000000; i++) {
	    c = new C();
	}
	t2 = System.currentTimeMillis();
	System.out.println("new " + (t2-t1));

	t1 = System.currentTimeMillis();

	for (int i = 0; i < 10000000; i++) {
	    c = (C) k.newInstance();
	}
	t2 = System.currentTimeMillis();
	System.out.println("newInstance " + (t2-t1));
    }
}
