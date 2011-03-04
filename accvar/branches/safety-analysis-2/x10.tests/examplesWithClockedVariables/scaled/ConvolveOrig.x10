


public class ConvolveOrig {

   val N = 64;
   val REPEAT = 64;
   public def pipeline() {
  		val c = Clock.make(); 
   		val op = Int.+;
     		val a = Rail.make[int] (1, (Int) => 0);
   		val b = Rail.make[int] (N, (Int)=> 0);
         	val w = Rail.make[Double](N, (i:int)=> (i/1000.0));
 	    	async clocked(c)  {
                        var i: int;
                        for (i = 0; i < REPEAT*N; i++)  {
                                b(0) = i;
                                next;  /*write phase over */
                                next; /*read phase over */
                        }
                }
		var j: int = 0;
		for (j = 1; j <= N-1; j++) {
		val jj = j; 
                async clocked (c) {
                        var i: int;
                        for (i = 0; i < REPEAT*N; i++)  {
                                next; /*write phase over */                   
                                val tmp  = b(jj - 1);
                                next;
				b(jj) = tmp + (b(jj) * w(jj) as Int);
                        }
                }
		}
                var i: int;
              
                for (i = 0; i < REPEAT*N; i++)  {
                        next; /*write phase over */
                        val o = b(N-1);
                        Console.OUT.println(o);
                        next; /*read phase over */
                 }
        }

   

    public static def main(args:Rail[String]!) {
    	val start_time = System.currentTimeMillis(); 
         val h = new ConvolveOrig();  // final variable
         h.pipeline();
    	val compute_time = (System.currentTimeMillis() - start_time);
    	Console.OUT.println( compute_time + " ");
    }

}
