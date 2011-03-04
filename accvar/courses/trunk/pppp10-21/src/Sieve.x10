/** An implementation of the Sieve of Eratosthenes,
    using clocked variables.

    <p> Prints out prime numbers upto N.
    @author vj
 */

public class Sieve {
    public static def main(s:Array[String](1)) {
        val N = s.size > 0 ? Int.parseInt(s(0)) : 100;
        val time = System.nanoTime();
        finish async
            primes(gen(N));
        Console.OUT.println("Time: "
                   + ((System.nanoTime()-time)*1.0)/(1000*1000*1000)
                   + " s.");
    }
    /**
       Generate the clocked stream of values 2..N.
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
    /**
       Print out the numbers in the input stream that are not
       multiples of the previous numbers in the stream.
     */
        static def primes(nums:Clocked[Int]) {
        async clocked(nums.clock) {
            val n = nums();
            if (n > 0) {
                Console.OUT.print(" " + n);
                nums.next();
                primes(sieve(n, nums));
            } else {
                Console.OUT.println();
            }
        }
    }
    /**
       Output a stream which contains all numbers in nums
       that are not multiples of prime.
     */
    static def sieve(prime:Int, nums:Clocked[Int]):Clocked[Int] {
        val result:Clocked[Int];
        // Determine the value n to initialize the output Clocked[Int].
        var n:Int = nums();
        while (true) {
            if (n < 0) 
                return new Clocked[Int](-1);
            if (n % prime != 0) {
                result = new Clocked[Int](n);
                break;
            }
            nums.next();
            n = nums();
        }
        nums.next();
        // Leave an activity behind to move values from the input to the
        // output, if they are not multiples of prime.
        async clocked (nums.clock, result.clock) {
            var m:Int=nums();
            while (true) {
                if (m < 0)
                    break;
                if (m % prime != 0) {
                    result() = m;
                    result.next();
                }
                nums.next();
                m = nums();
            }
            result() = -1;
        }
        return result;
    }
}