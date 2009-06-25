public class ArrayCtor1 {
    public static void main(String[] args) {
        new ArrayCtor1().foo();
    }
    public void foo() {
	region ar= [1:10];
	int[.] a= new int[ar] (point p) { return p[0]; };
	for(point p2: a) {
	    int x= a[p2];
	    int y= x + 5;
	    a[p2]= y * 2;
	}
	for(point p3: a) {
	    System.out.println("a[" + p3 + "] = " + a[p3]);
	}
    }
}
