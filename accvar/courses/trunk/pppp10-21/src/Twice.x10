/** 
    A simple program that reads values v_i from a Clocked[Int] and emits
    2*v_i.
    
    @author vj
 */
public class Twice {
    public static def main(s:Array[String](1)) {
        val N = s.size > 0 ? Int.parseInt(s(0)) : 100;
        val time = System.nanoTime();
        finish async
            twice(gen(N));
        Console.OUT.println("Time: " + ((System.nanoTime()-time)*1.0)/(1000*1000*1000) + " s.");
    }
    /** Generates the sequence of values 2..N on the return Clocked[Int].
     */
    static def gen(N:Int):Clocked[Int] {
        val x = new Clocked[Int](2);
        async clocked (x.clock) {
            for ([i] in 3..N) {
                x() = i;
                x.next();
            }
            x() = -1; // terminate
            x.next();
        }
        return x;
    }
    /** Prints out 2*x, for every value x read from nums.
     */    
    static def twice(nums:Clocked[Int]) {
        async clocked(nums.clock) {
            var n:Int = nums();
            while (n > 0) {
                Console.OUT.print(" " + (2*n));
                nums.next();
                n = nums();
            } 
            Console.OUT.println();
            Console.OUT.println("Done! Go home!");
        }
    }
}