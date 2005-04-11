import x10.lang.*;

/**
 * Minimal test for conditional atomics.
 */
public class ConditionalAtomicTest {

    int value1=0;
    int value2=0;

    public boolean run() {
    clock c =  clock.factory.clock();
            now(c) { 
                // this activity waits until value1 and 
                // value2 are equal, then atomically makes 
                // value1 two higher then value2
                async(here) 
                while (true) {
                    int temp;
                    atomic{temp=value1;}
                    if (temp >= 42) break;
                    when (value1 == value2) { value1++; value2--; }
                }
            }
            now(c) {
                // this activity waits until value1 is
                // two higher than value2, then atomically raises
                // value2 to value1's level so they become equal
                async(here)
                while (true) {
                     int temp;
                     atomic{temp=value2;}
                     if(temp>=42) break;
                     when (value1 == value2 + 2) 
                         { value2 = value1; }
                     or (value1 != value2+2 &&
                         value1 != value2) 
                         { value1 = value2 = 43; /* error */ };
                }
            }
            next;

        int temp;
        atomic{temp=value1;}
        return temp == 42;
    }
	public static void main(String args[]) {
		boolean b= (new ConditionalAtomicTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
