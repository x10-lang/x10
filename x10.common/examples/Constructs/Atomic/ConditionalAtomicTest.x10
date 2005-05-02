
/**
 * Minimal test for conditional atomics.
 */
public class ConditionalAtomicTest {

    int value1=0;
    int value2=0;

    public boolean run() {
    	  final clock c =  clock.factory.clock();
          async(this) clocked(c) { 
                // this activity waits until value1 and 
                // value2 are equal, then atomically makes 
                // value1 two higher then value2
                while (true) {
                    int temp;
                    atomic temp=value1; 
                    if (temp >= 42) break;
                    when (value1 == value2) { value1++; value2--; }
                }
          }
          async(this) clocked(c) {
                // this activity waits until value1 is
                // two higher than value2, then atomically raises
                // value2 to value1's level so they become equal
                while (true) {
                     int temp;
                     atomic temp=value2; 
                     if(temp>=42) break;
                     when (value1 == value2 + 2) 
                         { value2 = value1; }
                     or (value1 != value2+2 &&
                         value1 != value2) //something went wrong 
                         { value1 = value2 = 43; /* error */ };
                }
           }
           next; // wait until both activities end

           int temp;
           atomic temp=value1;
           return temp == 42;
    }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ConditionalAtomicTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}
