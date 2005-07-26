public class Test {
    public static void main(String[] s) {
	Object o = null;
	System.out.println(o + " ");
	float[] a = new float[2];

	for (int i=0; i < 2; i++) {
	    System.out.println("a["+i+"] is |" + a[i]+"|");
	}

	Integer[] A = new Integer[2];

	for (int i=0; i < 2; i++) {
	    System.out.println("A["+i+"] is |" + A[i]+"|");
	}

    }

}
