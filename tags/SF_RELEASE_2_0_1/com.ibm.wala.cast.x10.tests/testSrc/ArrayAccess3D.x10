public class ArrayAccess3D {
    public static void main(String[] args) {
	int[.] a= new int[[0:5,0:5,0:5]] (point p) { return p[0] + p[1] + p[2]; };
	new ArrayAccess3D().sum(a);
    }
    public int sum(int[.] a) {
	int result= 0;
        for(point p: a) {
            result += a[p];
        }
        for(point p: a) {
            result += a[p[0],p[1],p[2]];
        }
        return result;
    }
}
