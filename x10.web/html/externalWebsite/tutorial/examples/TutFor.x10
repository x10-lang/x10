public class TutFor {
    public static void main(String[] args) {
        region R = [0:1,0:2];
        System.out.print("Points in region " + R + " =");
        for ( point p : R ) System.out.print(" " + p);
        System.out.println();
        // Use exploded syntax instead
        System.out.print("(i,j) pairs in region " + R + " =");
        for ( point[i,j] : R ) 
            System.out.print("(" + i + "," + j + ")");
        System.out.println();
    } // main()
} // TutFor

/* 

Output:

Points in region {0:1,0:2} = [0,0] [0,1] [0,2] [1,0] [1,1] [1,2]
(i,j) pairs in region {0:1,0:2} =(0,0)(0,1)(0,2)(1,0)(1,1)(1,2)

*/
