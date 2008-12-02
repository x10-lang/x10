public class ArrayAccess1 {
    public static void main(String[] args) {
	int[.] a= new int[[0:5]] (point p) { return 3; };
	new ArrayAccess1().sum(a);
    }
    public int sum(int[.] a) {
	int result= 0;
        for(point p: a) {
            result += a[p];
        }
        return result;
    }
}
