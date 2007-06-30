public class TutPoint {
    public static void main(String[] args) {
	point p1 = [1,2,3,4,5];
	point p2 = [1,2];
	point p3 = [2,1];
	System.out.println("p1 = " + p1 + " ; p1.rank = " + p1.rank + " ; p1.get(2) = " + p1.get(2));
	System.out.println("p2 = " + p2 + " ; p3 = " + p3 + " ; p2.lt(p3) = " + p2.lt(p3));
    } // main()
} // TutPoint

/* 

Output:

p1 = [1,2,3,4,5] ; p1.rank = 5 ; p1.get(2) = 3
p2 = [1,2] ; p3 = [2,1] ; p2.lt(p3) = true

*/
