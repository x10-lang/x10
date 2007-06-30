public class TutArray1 {
    public static void main(String[] args) {
	int[.] A = new int[ [1:10,1:10] ] (point [i,j]) { return i+j;} ;
        System.out.println("A.rank = " + A.rank + " ; A.region = " + A.region);
	int[.] B = A | [1:5,1:5];
	System.out.println("B.max() = " + B.max());
    } // main()
} // TutArray1

/* 

Output:

A.rank = 2 ; A.region = {1:10,1:10}
B.max() = 10

*/
