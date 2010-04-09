


public class PipelineOrig {


   public def pipeline() {
  		val c = Clock.make(); 
   		val op = Int.+;
     	val a = Rail.make[int] (1, (Int) => 0);
   		val b = Rail.make[int] (1, (Int)=> 0);
 	    async clocked(c)  {
                        var i: int;
                        for (i = 0; i < 10; i++)  {
                                a(0) = i;
                                next;  /*write phase over */
                        }
                }
                async clocked (c) {
                        var i: int;
                        for (i = 0; i < 10; i++)  {
                                next; /*write phase over */
                                b(0) = a(0) + 1;
                        }
                }
                var i: int;
                next; /*write phase over */
                for (i = 0; i < 10; i++)  {
                        next; /*write phase over */
                        val o = b(0) + 1;
                        Console.OUT.println(o);
                 }
        }

   

    public static def main(args:Rail[String]!) {
         val h = new Pipeline();  // final variable
         h.pipeline();
    }

}
