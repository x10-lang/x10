import clocked.*;

public class Prefix {
    const N = 8;
    val c = Clock.make();
    val op = Int.+;
    global val a = Rail.make[int @ Clocked[Int](c, op, 0)](N, (i:Int)=> i);

   
    public def run(lo:int, hi:int) @ClockedM (c) {
        if (lo+1 == hi) {
       		val e = a(lo);
       		a(hi)= e + a(hi);
        	return;
        }
        val mid = lo + ((hi-lo+1)/2);
         async clocked(c) run(lo, mid-1);
         run(mid, hi);
         next;
        { //expand
            val e = a(mid-1);
           for ((p) in mid..hi)
                async clocked(c)
                    a(p) = e + a(p);
            next;
        }
    }
    public def print() @ ClockedM(c) {
        for ((p) in 0..N-1)
             Console.OUT.println("a(" + p+ ")= " + a(p));
    }

    public static def main(Rail[String]) {
 
        val s = new Prefix();
        s.run(0, N-1);
        s.print();
    }
}