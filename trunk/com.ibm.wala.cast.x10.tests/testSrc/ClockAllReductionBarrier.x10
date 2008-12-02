/***************************************************************************************
An example of implementing all reduction using X10's clock

In this implementation, a barrier is needed after each phase which is
implemented using X10's clock. The length of the array on which the
all reduction is performed is equal to the number of places used to
run this code. For simplicity, it is assumed that the number of places
is a power of 2.

Also, the explicit and implicit syntax for accessing a remote array
element are presented.
    

Date:   11/09/06

Author: Tong Wen @IBM Research
* Modified by T.W. 11/29/2007: comment out the import statements;
*                              replace dist.factory.unique() with dist.UNIQUE;
*                              add more dependent type properties.
*****************************************************************************************/
import java.lang.Math;

public class  ClockAllReductionBarrier {
        final dist(:rail) ALLPLACES=(dist(:rail))dist.UNIQUE;
        final int numPlaces=place.MAX_PLACES;
    public boolean run() {
        //assert powerOf2(numPlaces); //the size must be a power of 2
        final double [:rail] A=new double [ALLPLACES] (point[i]){return 1;};
        reduce(A);
        return A[0] == numPlaces;
    }
    void reduce(final double[:rail] A) {
        // B is a temporary global array created for red/black iteration.
        final double [:rail] B=new double [ALLPLACES] (point[i]){return 0;};
        final int factor=numPlaces, phases=(int)java.lang.Math.log(factor) + 1;

        finish async{
            final clock clk=clock.factory.clock();
            ateach (point [i]:ALLPLACES) clocked(clk){
                boolean red=true;
                int Factor=factor, shift=0;
                 for (int j=0;j<phases;++j){
                    shift=Factor/2;
                    final int destProcID=(i+shift)%Factor+i/Factor*Factor;
                    if (red)
                        B[i]=A[i]+A[destProcID];
                    else
                        A[i]=B[i]+B[destProcID];
                    clk.doNext(); // wait until all activities have updated current array.
                    Factor/=2;
                    red=!red;
                }
                if (!red) A[i]=B[i];
                if (A[i] != numPlaces)
                    System.out.println("Error at i=" + i + "A[i] = " + A[i] + " instead of " + numPlaces);
            }
        }

    }

    public static void main(String[] args) {
    
    	async{
           new ClockAllReductionBarrier().run();
           }
    }
}
    
                                                  
                                                 
                                                 