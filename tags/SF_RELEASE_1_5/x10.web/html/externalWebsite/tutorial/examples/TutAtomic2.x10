public class TutAtomic2 {
    int a = 100;
    int b = 100;
    int sum;

    atomic void incr_a() { a++ ; b-- ; }
    atomic void decr_a() { a-- ; b++ ; }

    public static void main(String args[]) {
	new TutAtomic2().run();
    }
    
    void run() {
        finish {
            async for (int i=1 ; i<=10 ; i++ ) incr_a();
            for (int i=1 ; i<=10 ; i++ ) decr_a();
        }
        atomic sum = a + b;
        System.out.println("a+b = " + sum);
    } 
} // TutAtomic2


/* 

Output:

a+b = 200

*/
