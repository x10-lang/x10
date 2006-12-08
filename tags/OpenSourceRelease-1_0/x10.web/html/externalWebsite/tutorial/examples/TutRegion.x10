public class TutRegion {
    public static void main(String[] args) {
	region R1 = [1:10, -100:100]; 
	System.out.println("R1 = " + R1 + " ; R1.rank = " + R1.rank + " ; R1.size() = " + R1.size() + " ; R1.ordinal([10,100]) = " + R1.ordinal([10,100]));
	region R2 = [1:10,90:100]; 
	System.out.println("R2 = " + R2 + " ; R1.contains(R2) = " + R1.contains(R2) + " ; R2.rank(1).low() = " + R2.rank(1).low() + " ; R2.coord(0) = " + R2.coord(0));
    } // main()
} // TutRegion

/* 

Output:

R1 = {1:10,-100:100} ; R1.rank = 2 ; R1.size() = 2010 ; R1.ordinal([10,100]) = 2009
R2 = {1:10,90:100} ; R1.contains(R2) = true ; R2.rank(1).low() = 90 ; R2.coord(0) = [1,90]

*/
