


public class PipelineOrig {

   val N = 64;
   public def pipeline() {
  		val c = Clock.make(); 
   		val op = Int.+;
     	val a = Rail.make[int] (1, (Int) => 0);
   		val b = Rail.make[int] (N, (Int)=> 0);
 	    async clocked(c)  {
                        var i: int;
                        for (i = 0; i < N; i++)  {
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
                        for (i = 0; i < N; i++)  {
                                next; /*write phase over */                   
                                b(jj) = b(jj - 1) + 1;
                                next;
                        }
                }
		}
                var i: int;
              
                for (i = 0; i < N; i++)  {
                        val o = b(N-1) + 1;
                        Console.OUT.println(o);
                        next; /*write phase over */
                        next; /*read phase over */
                 }
        }

   

    public static def main(args:Rail[String]!) {
         val h = new PipelineOrig();  // final variable
         h.pipeline();
    }

}
