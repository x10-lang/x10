class Convolve {
    static def run(w:ValRail[Int], x:ValRail[Int]) {
	finish async {
	    val c = Clock.make();
        val n = x.length;
	    val yi= Rail.makeVar[Int](w.length, (Int)=>0); 
	    val xz = Rail.makeVar[Int](1, (Int)=> 0);
	    for ((i) in 0..w.length-1) async clocked(c)  {
		    for ((j) in 1..n) {
			next; // end of write phase, now you can read the values
			val v = (i==0?0:yi(i-1)) + w(i)*xz(0);
			next; // end of read phase, now you can write the values
			yi(i)=v;
		    }
		}
	    async clocked(c) 
		for (v in x) {
		xz(0)= v;
		next; // end of write phase, now you can read the values
                // no clocked variables to read
		next; // end of read phase, now you can write the values
	    }
	    doNext(2*w.length);
	    Console.ERR.print("y = "); 
	    for ((j) in 1..n) {
		next; // end of write phase, now you can read the values
		Console.ERR.print(yi(w.length-1)+ " " );
		next; // end of read phase, now you can write the values
	    }
	    Console.ERR.println();
	}
    }
    static def doNext(n:Int) {
	for ((j) in 1..n) next;
    }
    public static def main(args: Rail[String]) {
    	Console.ERR.print("Should get "); for (a in [14,20,26,32,32,32]) Console.ERR.print(a+ " ");
    	Console.ERR.println();
	run([1,2,3], [1,2,3,4,5,6]);
	
    }
}